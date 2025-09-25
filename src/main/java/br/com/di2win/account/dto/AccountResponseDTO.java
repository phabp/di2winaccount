package br.com.di2win.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponseDTO {
    private Long id;
    private Long clientId;
    private String number;
    private String agency;
    private BigDecimal balance;
    private Boolean active;
    private Boolean blocked;
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
