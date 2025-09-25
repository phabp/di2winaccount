package br.com.di2win.account.dto;

import java.math.BigDecimal;

public class BalanceResponseDTO {
    private String number;
    private BigDecimal balance;

    public BalanceResponseDTO() {}

    public BalanceResponseDTO(String number, BigDecimal balance) {
        this.number = number;
        this.balance = balance;
    }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
