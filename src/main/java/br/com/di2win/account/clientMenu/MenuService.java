package br.com.di2win.account.clientMenu;

import br.com.di2win.account.dto.*;
import br.com.di2win.account.entities.Account;
import br.com.di2win.account.entities.User;
import br.com.di2win.account.exceptions.BusinessRuleException;
import br.com.di2win.account.exceptions.ConflictException;
import br.com.di2win.account.exceptions.NotFoundException;
import br.com.di2win.account.services.AccountService;
import br.com.di2win.account.services.OperationService;
import br.com.di2win.account.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

@Component
@Profile("cli")
public class MenuService {

    private final AccountService accountService;
    private final OperationService operationService;
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder;

    private final Scanner in = new Scanner(System.in);

    public MenuService(AccountService accountService,
                       OperationService operationService,
                       UserRepository userRepo,
                       BCryptPasswordEncoder encoder) {
        this.accountService = accountService;
        this.operationService = operationService;
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    
    public void start() {

        System.out.println(" WELCOME TO DI2WIN ACCOUNT, THE PLACE WHERE YOUR MONEY IS SAFE.");

        boolean systemOn = true;
        while (systemOn) {
            System.out.println();
            System.out.println("STARTER MENU:");
            System.out.println("TYPE 1 - I already have an account");
            System.out.println("TYPE 2 - I don't have an account yet");
            System.out.println("TYPE 3 - Exit");
            String op = ask("Please select one of the given options ");

            switch (op) {
                case "1":
                    doLoginFlow();
                    break;
                case "2":
                    doOpenAccountFlow();
                    break;
                case "3":
                    System.out.println("Ok, thanks for passing by. See you next time!");
                    systemOn = false;
                    break;
                default:
                    System.out.println("You didn't chose one of the given options. Select just one of them.");
                    break;
            }
        }
    }

    private void doOpenAccountFlow() {
        System.out.println();
        System.out.println("[OPEN ACCOUNT FORM]");
        String name = ask("Name: ").trim();
        String cpf  = normalizeCpf(ask("CPF (only your numbers or with the traditional CPF format): "));
        LocalDate birth = askDate("Birth date (YYYY-MM-DD): ");
        String password = ask("Choose a password (at least 6 characters with letters and  numbers): ");

        try {
            OpenAccountRequestDTO dto = new OpenAccountRequestDTO();
            dto.setName(name);
            dto.setCpf(cpf);
            dto.setBirthDate(birth);
            dto.setPassword(password);

            AccountResponseDTO acc = accountService.openAccount(dto);
            System.out.println();
            System.out.println("Your account was successfully created! Welcome!");
            System.out.println("Take note of your account data --> Number account: " + acc.getNumber() + " , Agency: " + acc.getAgency());

            
            operationsMenu(cpf);

        } catch (ConflictException | BusinessRuleException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    private void doLoginFlow() {
        System.out.println();
        System.out.println("[LOGIN ]");
        String cpf = normalizeCpf(ask("your CPF: "));
        String password = ask("your password: ");

        try {
            User u = userRepo.findByCpf(cpf).orElse(null);
            if (u == null || !u.isActive() || !encoder.matches(password, u.getPasswordHash())) {
                System.out.println("Invalid credentials or inactive user.");
                return;
            }
            operationsMenu(cpf);
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    private void operationsMenu(String cpf) {
        String activeAccount = determineActiveAccount(cpf); 
        if (activeAccount == null) return;

        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("[MAIN MENU] (CPF " + cpf + ")");
            System.out.println("Active account: " + activeAccount);
            System.out.println("--------------------------------");
            System.out.println("1) See my account");
            System.out.println("2) Check my balance");
            System.out.println("3) Make a deposit");
            System.out.println("4) Make a withdraw");
            System.out.println("5) Transfer to another account");
            System.out.println("6) See my account statement by period");
            System.out.println("7) Block my account");
            System.out.println("8) Unblock my account");
            System.out.println("0) Logout");

            String op = ask("Choose one of the given options above. ");
            try {
                switch (op) {
                    case "1":
                        listAccounts(cpf); 
                        break;
                    case "2":
                        showBalance(activeAccount);
                        break;
                    case "3":
                        deposit(activeAccount);
                        break;
                    case "4":
                        withdraw(activeAccount);
                        break;
                    case "5":
                        transfer(activeAccount);
                        break;
                    case "6":
                        extract(activeAccount);
                        break;
                    case "7":
                        accountService.block(activeAccount);
                        System.out.println("Account blocked.");
                        break;
                    case "8":
                        accountService.unblock(activeAccount);
                        System.out.println("Account unblocked.");
                        break;
                    case "0":
                        back = true;
                        break;
                    default:
                        System.out.println("Invalid option.");
                        break;
                }
            } catch (BusinessRuleException | NotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private void listAccounts(String cpf) {
        List<AccountResponseDTO> list = accountService.findByCpf(cpf);
        if (list.isEmpty()) {
            System.out.println("Sorry! We couldn't find any account.");
            return;
        }
        System.out.println();
        System.out.println("Your account:");
        for (int i = 0; i < list.size(); i++) {
            var a = list.get(i);
            System.out.println("[" + (i + 1) + "] Number: " + a.getNumber()
                    + " | Agency: " + a.getAgency()
                    + " | Balance: " + a.getBalance()
                    + " | Blocked: " + a.getBlocked());
        }
    }


    private String determineActiveAccount(String cpf) {
        List<AccountResponseDTO> list = accountService.findByCpf(cpf);

        if (list.isEmpty()) {
            System.out.println("We couldn't find an account linked to your login.");
            System.out.println("This indicates a data inconsistency. Please contact the bank for more information.");
            return null;
        }

        AccountResponseDTO a = list.get(0);
        System.out.println();
        System.out.println("Your account: " + a.getNumber()
                + " (agency " + a.getAgency() + "), balance " + a.getBalance()
                + (Boolean.TRUE.equals(a.getBlocked()) ? " [BLOCKED ACCOUNT]" : ""));
        return a.getNumber();
    }

    private void showBalance(String number) {
        BalanceResponseDTO bal = accountService.getBalance(number);
        System.out.println("Your account " + bal.getNumber() + " balance: " + bal.getBalance());
    }

    private void deposit(String number) {
        BigDecimal value = askMoney("Please, insert the deposit value: ");
        OperationValueDTO dto = new OperationValueDTO();
        dto.setValue(value);
        accountService.deposit(number, dto);
        System.out.println("Deposit completed.");
        showBalance(number);
    }

    private void withdraw(String number) {
        BigDecimal value = askMoney("Please, insert the withdraw value: ");
        OperationValueDTO dto = new OperationValueDTO();
        dto.setValue(value);
        accountService.withdraw(number, dto);
        System.out.println("Withdraw completed!");
        showBalance(number);
    }

    private void transfer(String originNumber) {
        String dest = ask("Please, insert the destination account number: ").trim();
        BigDecimal value = askMoney("Please, insert the value to transfer: ");
        TransferDTO dto = new TransferDTO();
        dto.setDestinationNumber(dest);
        dto.setValue(value);
        accountService.transfer(originNumber, dto);
        System.out.println("Transfer completed.");
        showBalance(originNumber);
    }

    private void extract(String number) {
        System.out.println();
        System.out.println("[ACCOUNT STATEMENT]");
        LocalDateTime start = askDateTime("Start date and time: (YYYY-MM-DDTHH:MM:SS):");
        LocalDateTime end   = askDateTime("End date and time:  (YYYY-MM-DDTHH:MM:SS):");

        Account acc = accountService.getAccountEntityOr404(number);
        var list = operationService.extractSimple(acc, start, end);
        if (list.isEmpty()) {
            System.out.println("We couldn't find any operations in the given period.");
            return;
        }

        
        System.out.println();
        System.out.println("Date/Time                | Type               | Value      | Counterparty     | Balance After");
        System.out.println("-------------------------+--------------------+------------+------------------+--------------");
        list.forEach(op -> {
            String cp = (op.getCounterpartyNumber() != null) ? op.getCounterpartyNumber() : "-";
            String balAfter = (op.getBalanceAfter() != null) ? op.getBalanceAfter().toString() : "-";
            System.out.println(op.getOperationDate()
                    + " | " + op.getType()
                    + " | " + op.getValue()
                    + " | " + cp
                    + " | " + balAfter);
        });
    }



    private String ask(String prompt) {
        System.out.print(prompt);
        return in.nextLine();
    }

    private BigDecimal askMoney(String prompt) {
        while (true) {
            String s = ask(prompt).trim().replace(",", ".");
            try {
                BigDecimal v = new BigDecimal(s);
                if (v.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("The value can't be zero.");
                } else {
                    return v;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }

    private int askInt(String prompt, int min, int max) {
        while (true) {
            try {
                int v = Integer.parseInt(ask(prompt));
                if (v < min || v > max) {
                    System.out.println("Choose between " + min + " and " + max + ".");
                } else {
                    return v;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }

    private LocalDate askDate(String prompt) {
        while (true) {
            try {
                return LocalDate.parse(ask(prompt).trim());
            } catch (Exception e) {
                System.out.println("Invalid date format. Expected format: YYYY-MM-DD.");
            }
        }
    }

    private LocalDateTime askDateTime(String prompt) {
        while (true) {
            try {
                return LocalDateTime.parse(ask(prompt).trim());
            } catch (Exception e) {
                System.out.println("Invalid datetime. Expected YYYY-MM-DDTHH:MM:SS");
            }
        }
    }

    private String normalizeCpf(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
