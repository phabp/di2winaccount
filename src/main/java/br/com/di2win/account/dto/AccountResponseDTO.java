package br.com.di2win.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AccountResponse", description = "Public representation of a bank account.")
public class AccountResponseDTO {

    @Schema(description = "Unique identifier for the account", example = "42", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Identifier of the client (owner of the account)", example = "101", accessMode = Schema.AccessMode.READ_ONLY)
    private Long clientId;

    @Schema(description = "Account number", example = "7647913839")
    private String number;

    @Schema(description = "agency code", example = "0001")
    private String agency;

    @Schema(description = "Current balance", example = "1250.75", accessMode = Schema.AccessMode.READ_ONLY)
    private BigDecimal balance;

    @Schema(description = "When the account is active", example = "true")
    private Boolean active;

    @Schema(description = "When the account is temporarily blocked for operations", example = "false")
    private Boolean blocked;

    @Schema(description = "Date and time when the account was opened", example = "2025-09-24T20:30:36.481484", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime openDate;

    public AccountResponseDTO() {}

    public AccountResponseDTO(Long id, Long clientId, String number, String agency,
                              BigDecimal balance, Boolean active, Boolean blocked,
                              LocalDateTime openDate) {
        this.id = id;
        this.clientId = clientId;
        this.number = number;
        this.agency = agency;
        this.balance = balance;
        this.active = active;
        this.blocked = blocked;
        this.openDate = openDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getAgency() { return agency; }
    public void setAgency(String agency) { this.agency = agency; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Boolean getBlocked() { return blocked; }
    public void setBlocked(Boolean blocked) { this.blocked = blocked; }

    public LocalDateTime getOpenDate() { return openDate; }
    public void setOpenDate(LocalDateTime openDate) { this.openDate = openDate; }
}
