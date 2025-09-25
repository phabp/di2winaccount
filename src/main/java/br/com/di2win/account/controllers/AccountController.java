package br.com.di2win.account.controllers;

import br.com.di2win.account.dto.AccountResponseDTO;
import br.com.di2win.account.dto.BalanceResponseDTO;
import br.com.di2win.account.dto.OpenAccountRequestDTO;
import br.com.di2win.account.dto.OperationResponseDTO;
import br.com.di2win.account.dto.OperationValueDTO;
import br.com.di2win.account.dto.TransferDTO;
import br.com.di2win.account.entities.Account;
import br.com.di2win.account.services.AccountService;
import br.com.di2win.account.services.OperationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import static org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService service;
    private final OperationService operationService;

    public AccountController(AccountService service, OperationService operationService) {
        this.service = service;
        this.operationService = operationService;
    }

    /** Open a new account -> generates agency & number; also creates User with password */
    @PostMapping("/open")
    public ResponseEntity<AccountResponseDTO> open(@RequestBody @Valid OpenAccountRequestDTO dto) {
        AccountResponseDTO created = service.openAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** Deposit */
    @PostMapping("/{number}/deposit")
    public ResponseEntity<Void> deposit(@PathVariable String number,
                                        @RequestBody @Valid OperationValueDTO dto) {
        service.deposit(number, dto);
        return ResponseEntity.noContent().build();
    }

    /** Withdraw */
    @PostMapping("/{number}/withdraw")
    public ResponseEntity<Void> withdraw(@PathVariable String number,
                                         @RequestBody @Valid OperationValueDTO dto) {
        service.withdraw(number, dto);
        return ResponseEntity.noContent().build();
    }

    /** Transfer */
    @PostMapping("/{origin}/transfer")
    public ResponseEntity<Void> transfer(@PathVariable("origin") String originNumber,
                                         @RequestBody @Valid TransferDTO dto) {
        service.transfer(originNumber, dto);
        return ResponseEntity.noContent().build();
    }

    /** Balance (returns only number + balance) */
    @GetMapping("/{number}/balance")
    public ResponseEntity<BalanceResponseDTO> balance(@PathVariable String number) {
        BalanceResponseDTO bal = service.getBalance(number);
        return ResponseEntity.ok(bal);
    }

    /** Block */
    @PostMapping("/{number}/block")
    public ResponseEntity<AccountResponseDTO> block(@PathVariable String number) {
        service.block(number);
        Account acc = service.getAccountEntityOr404(number);
        return ResponseEntity.ok(toResponse(acc));
    }

    /** Unblock */
    @PostMapping("/{number}/unblock")
    public ResponseEntity<AccountResponseDTO> unblock(@PathVariable String number) {
        service.unblock(number);
        Account acc = service.getAccountEntityOr404(number);
        return ResponseEntity.ok(toResponse(acc));
    }

    /** List accounts by CPF (post-login menu) */
    @GetMapping("/by-cpf/{cpf}")
    public ResponseEntity<List<AccountResponseDTO>> byCpf(@PathVariable String cpf) {
        List<AccountResponseDTO> accounts = service.findByCpf(cpf);
        return ResponseEntity.ok(accounts);
    }

    /** Extract by period (ascending order), returns OperationResponseDTO list */
    // Example: GET /api/accounts/1234567890/extract?start=2025-09-24T00:00:00&end=2025-09-24T23:59:59
    @GetMapping("/{number}/extract")
    public List<OperationResponseDTO> extract(
            @PathVariable String number,
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime end
    ) {
        Account acc = service.getAccountEntityOr404(number);
        return operationService.extractSimple(acc, start, end);
    }

    // ---- mapper (entity -> DTO) usado em block/unblock
    private AccountResponseDTO toResponse(Account a) {
        return new AccountResponseDTO(
            a.getId(),
            a.getClient() != null ? a.getClient().getId() : null,
            a.getNumber(),
            a.getAgency(),
            a.getBalance(),
            a.getActive(),
            a.getBlocked(),
            a.getOpenDate()
        );
    }
}
