package br.com.di2win.account.services;

import br.com.di2win.account.dto.*;
import br.com.di2win.account.entities.Account;
import br.com.di2win.account.entities.Client;
import br.com.di2win.account.entities.User;
import br.com.di2win.account.enums.OperationType;
import br.com.di2win.account.exceptions.BusinessRuleException;
import br.com.di2win.account.exceptions.ConflictException;
import br.com.di2win.account.exceptions.NotFoundException;
import br.com.di2win.account.repositories.AccountRepository;
import br.com.di2win.account.repositories.ClientRepository;
import br.com.di2win.account.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepo;
    private final ClientRepository clientRepo;
    private final UserRepository userRepo;
    private final OperationService operationService;
    private final BCryptPasswordEncoder encoder;

    private static final SecureRandom RNG = new SecureRandom();

    @Value("${withdraw.daily.limit:2000.00}")
    private BigDecimal dailyWithdrawLimit;

    public AccountService(AccountRepository accountRepo,
                          ClientRepository clientRepo,
                          UserRepository userRepo,
                          OperationService operationService,
                          BCryptPasswordEncoder encoder) {
        this.accountRepo = accountRepo;
        this.clientRepo = clientRepo;
        this.userRepo = userRepo;
        this.operationService = operationService;
        this.encoder = encoder;
    }

    // =================== OPEN ACCOUNT (Client + User + Account) ===================
    @Transactional
    public AccountResponseDTO openAccount(OpenAccountRequestDTO dto) {
        final String cpf = normalizeCpf(dto.getCpf());
        final String name = safeTrim(dto.getName());
        final String password = safeTrim(dto.getPassword());
        validatePasswordStrength(password);

        Client client = clientRepo.findByCpf(cpf).orElseGet(() -> {
            Client c = new Client();
            c.setName(name);
            c.setCpf(cpf);
            c.setBirthDate(dto.getBirthDate());
            return clientRepo.save(c);
        });

        userRepo.findByCpf(cpf).ifPresent(u -> {
            throw new ConflictException("User already exists for this CPF");
        });
        User user = new User();
        user.setCpf(cpf);
        user.setPasswordHash(encoder.encode(password));
        user.setClient(client);
        userRepo.save(user);

        String number = generateUniqueNumber();
        String agency = generateAgency();

        Account acc = new Account();
        acc.setClient(client);
        acc.setNumber(number);
        acc.setAgency(agency);
        acc.setBalance(BigDecimal.ZERO);
        acc.setActive(true);
        acc.setBlocked(false);
        acc.setOpenDate(LocalDateTime.now());
        acc = accountRepo.save(acc);

        return toAccountResponseDTO(acc);
    }

    // =================== LIST/READ ===================
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> findByCpf(String cpf) {
        String normCpf = normalizeCpf(cpf);
        return accountRepo.findByClientCpf(normCpf).stream()
                .map(this::toAccountResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public BalanceResponseDTO getBalance(String number) {
        Account acc = findByNumberOr404Internal(number);
        return new BalanceResponseDTO(acc.getNumber(), acc.getBalance());
    }

    /** Helper público para controllers pegarem a entidade (404 se não existir) */
    @Transactional(readOnly = true)
    public Account getAccountEntityOr404(String number) {
        return findByNumberOr404Internal(number);
    }

    // =================== BLOCK / UNBLOCK ===================
    @Transactional
    public void block(String number) {
        Account acc = findByNumberOr404Internal(number);
        acc.setBlocked(true);
        accountRepo.save(acc);
    }

    @Transactional
    public void unblock(String number) {
        Account acc = findByNumberOr404Internal(number);
        acc.setBlocked(false);
        accountRepo.save(acc);
    }

    // =================== OPERATIONS ===================
    @Transactional
    public void deposit(String number, OperationValueDTO dto) {
        Account acc = findByNumberOr404Internal(number);
        ensureActiveAndUnblocked(acc);

        if (dto.getValue() == null || dto.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Invalid deposit amount.");
        }

        acc.setBalance(acc.getBalance().add(dto.getValue()));
        accountRepo.save(acc);

        // registra com saldo pós-operação e sem contraparte
        operationService.register(acc, OperationType.DEPOSIT, dto.getValue(), acc.getBalance(), null);
    }

    @Transactional
    public void withdraw(String number, OperationValueDTO dto) {
        Account acc = findByNumberOr404Internal(number);
        ensureActiveAndUnblocked(acc);

        if (dto.getValue() == null || dto.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Invalid withdrawal amount.");
        }
        if (acc.getBalance().compareTo(dto.getValue()) < 0) {
            throw new BusinessRuleException("Insufficient funds.");
        }

        BigDecimal totalToday = operationService.totalWithdrawnToday(acc, LocalDate.now());
        if (totalToday.add(dto.getValue()).compareTo(dailyWithdrawLimit) > 0) {
            throw new BusinessRuleException("Daily withdrawal limit exceeded.");
        }

        acc.setBalance(acc.getBalance().subtract(dto.getValue()));
        accountRepo.save(acc);

        // registra com saldo pós-operação e sem contraparte
        operationService.register(acc, OperationType.WITHDRAW, dto.getValue(), acc.getBalance(), null);
    }

    @Transactional
    public void transfer(String originNumber, TransferDTO dto) {
        Account origin = findByNumberOr404Internal(originNumber);
        ensureActiveAndUnblocked(origin);

        Account destination = accountRepo.findByNumber(dto.getDestinationNumber())
                .orElseThrow(() -> new NotFoundException("Destination account not found."));
        ensureActiveAndUnblocked(destination);

        if (origin.getId().equals(destination.getId())) {
            throw new BusinessRuleException("Origin and destination accounts cannot be the same.");
        }
        if (dto.getValue() == null || dto.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException("Invalid transfer amount.");
        }
        if (origin.getBalance().compareTo(dto.getValue()) < 0) {
            throw new BusinessRuleException("Insufficient funds for transfer.");
        }

        // Atualiza saldos
        origin.setBalance(origin.getBalance().subtract(dto.getValue()));
        destination.setBalance(destination.getBalance().add(dto.getValue()));
        accountRepo.save(origin);
        accountRepo.save(destination);
        accountRepo.flush(); // garante persistência antes dos registros

        // Registra operações com saldo pós-operação e contraparte
        operationService.register(
                origin,
                OperationType.TRANSFER_MADE,
                dto.getValue(),
                origin.getBalance(),
                destination.getNumber()
        );

        operationService.register(
                destination,
                OperationType.TRANSFER_RECEIVED,
                dto.getValue(),
                destination.getBalance(),
                origin.getNumber()
        );
    }

    // =================== HELPERS ===================
    private Account findByNumberOr404Internal(String number) {
        return accountRepo.findByNumber(number)
                .orElseThrow(() -> new NotFoundException("Account not found."));
    }

    private void ensureActiveAndUnblocked(Account acc) {
        if (acc.getActive() == null || !acc.getActive()) {
            throw new BusinessRuleException("Account is inactive.");
        }
        if (Boolean.TRUE.equals(acc.getBlocked())) {
            throw new BusinessRuleException("Account is blocked.");
        }
    }

    private AccountResponseDTO toAccountResponseDTO(Account acc) {
        return new AccountResponseDTO(
                acc.getId(),
                acc.getClient().getId(),
                acc.getNumber(),
                acc.getAgency(),
                acc.getBalance(),
                acc.getActive(),
                acc.getBlocked(),
                acc.getOpenDate()
        );
    }

    // =================== NUMBER/AGENCY GENERATION (embutidos) ===================
    private String generateAgency() {
        int n = RNG.nextInt(10_000);
        return String.format("%04d", n == 0 ? 1 : n);
    }

    private String generateUniqueAccountNumberCandidate() {
        long n = Math.abs(RNG.nextLong()) % 1_000_000_0000L;
        return String.format("%010d", n);
    }

    private String generateUniqueNumber() {
        String number;
        int guard = 0;
        do {
            number = generateUniqueAccountNumberCandidate();
            guard++;
            if (guard > 50) {
                throw new IllegalStateException("Failed to generate a unique account number.");
            }
        } while (accountRepo.existsByNumber(number));
        return number;
    }

    // =================== NORMALIZATION & PASSWORD RULES ===================
    private static String safeTrim(String s) {
        return s == null ? null : s.trim();
    }

    private static String normalizeCpf(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }

    private static void validatePasswordStrength(String password) {
        if (password == null || password.length() < 6) {
            throw new BusinessRuleException("Password must have at least 6 characters");
        }
        if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")) {
            throw new BusinessRuleException("Password must contain letters and numbers");
        }
    }
}
