package br.com.di2win.account.services;

import br.com.di2win.account.dto.OperationResponseDTO;
import br.com.di2win.account.entities.Account;
import br.com.di2win.account.entities.Operation;
import br.com.di2win.account.enums.OperationType;
import br.com.di2win.account.repositories.OperationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;

@Service
public class OperationService {

    private final OperationRepository repo;

    public OperationService(OperationRepository repo) {
        this.repo = repo;
    }

    /** 
     * Mantida por compatibilidade: usa o saldo atual da conta como balanceAfter
     * e não define counterpartyNumber.
     */
    @Transactional
    public void register(Account account, OperationType type, BigDecimal value) {
        // Neste ponto, espera-se que o Account já tenha o saldo atualizado.
        register(account, type, value, account.getBalance(), null);
    }

    /**
     * Nova sobrecarga: permite informar explicitamente o saldo pós-operação
     * e (opcionalmente) o número da contraparte em transferências.
     */
    @Transactional
    public void register(Account account,
                         OperationType type,
                         BigDecimal value,
                         BigDecimal balanceAfter,
                         String counterpartyNumber) {

        Operation op = new Operation();
        op.setAccount(account);
        op.setType(type);
        op.setValue(value);
        op.setOperationDate(LocalDateTime.now());
        op.setBalanceAfter(balanceAfter);
        op.setCounterpartyNumber(counterpartyNumber);
        repo.save(op);
    }

    /** Extrato simples (lista completa) entre dois instantes, ordenado ASC por data. */
    @Transactional(readOnly = true)
    public List<OperationResponseDTO> extractSimple(Account account,
                                                    LocalDateTime start,
                                                    LocalDateTime end) {
        return repo.findByAccountAndOperationDateBetweenOrderByOperationDateAsc(account, start, end)
                   .stream()
                   .map(this::toDto)
                   .toList();
    }

    /** Soma de saques num dia específico (00:00 até 23:59:59.999…). */
    @Transactional(readOnly = true)
    public BigDecimal totalWithdrawnToday(Account account, LocalDate day) {
        LocalDateTime start = day.atStartOfDay();
        LocalDateTime end   = day.atTime(LocalTime.MAX);
        BigDecimal sum = repo.sumByPeriod(account, OperationType.WITHDRAW, start, end);
        return (sum != null) ? sum : BigDecimal.ZERO;
    }

    // ---- mapper
    private OperationResponseDTO toDto(Operation op) {
        OperationResponseDTO dto = new OperationResponseDTO();
        dto.setId(op.getId());
        dto.setAccountNumber(op.getAccount() != null ? op.getAccount().getNumber() : null);
        dto.setType(op.getType());
        dto.setValue(op.getValue());
        dto.setOperationDate(op.getOperationDate());
        dto.setBalanceAfter(op.getBalanceAfter());
        dto.setCounterpartyNumber(op.getCounterpartyNumber()); // <-- FALTAVA ESTA LINHA
        return dto;
    }
}
