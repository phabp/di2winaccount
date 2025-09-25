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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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

    @Operation(description = "Open a new account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Account created")
    })
    @PostMapping("/open")
    public ResponseEntity<AccountResponseDTO> open(@RequestBody @Valid OpenAccountRequestDTO dto) {
        AccountResponseDTO created = service.openAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(description = "Deposit into account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deposit successful")
    })
    @PostMapping("/{number}/deposit")
    public ResponseEntity<Void> deposit(@PathVariable String number,
                                        @RequestBody @Valid OperationValueDTO dto) {
        service.deposit(number, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Withdraw from account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Withdraw successful")
    })
    @PostMapping("/{number}/withdraw")
    public ResponseEntity<Void> withdraw(@PathVariable String number,
                                         @RequestBody @Valid OperationValueDTO dto) {
        service.withdraw(number, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Transfer between accounts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Transfer successful")
    })
    @PostMapping("/{origin}/transfer")
    public ResponseEntity<Void> transfer(@PathVariable("origin") String originNumber,
                                         @RequestBody @Valid TransferDTO dto) {
        service.transfer(originNumber, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get balance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Balance returned")
    })
    @GetMapping("/{number}/balance")
    public ResponseEntity<BalanceResponseDTO> balance(@PathVariable String number) {
        BalanceResponseDTO bal = service.getBalance(number);
        return ResponseEntity.ok(bal);
    }

    @Operation(description = "Block account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account blocked")
    })
    @PostMapping("/{number}/block")
    public ResponseEntity<AccountResponseDTO> block(@PathVariable String number) {
        service.block(number);
        Account acc = service.getAccountEntityOr404(number);
        return ResponseEntity.ok(toResponse(acc));
    }

    @Operation(description = "Unblock account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account unblocked")
    })
    @PostMapping("/{number}/unblock")
    public ResponseEntity<AccountResponseDTO> unblock(@PathVariable String number) {
        service.unblock(number);
        Account acc = service.getAccountEntityOr404(number);
        return ResponseEntity.ok(toResponse(acc));
    }

    @Operation(description = "List accounts by CPF")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accounts returned")
    })
    @GetMapping("/by-cpf/{cpf}")
    public ResponseEntity<List<AccountResponseDTO>> byCpf(@PathVariable String cpf) {
        List<AccountResponseDTO> accounts = service.findByCpf(cpf);
        return ResponseEntity.ok(accounts);
    }

    @Operation(description = "Extract by period")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operations returned")
    })
    @GetMapping("/{number}/extract")
    public List<OperationResponseDTO> extract(
            @PathVariable String number,
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime end
    ) {
        Account acc = service.getAccountEntityOr404(number);
        return operationService.extractSimple(acc, start, end);
    }

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
