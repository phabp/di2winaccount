
package br.com.di2win.account.dto;

import br.com.di2win.account.enums.OperationType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OperationResponseDTO {
    private Long id;
    private String accountNumber;
    private OperationType type;
    private BigDecimal value;
    private LocalDateTime operationDate;
    private BigDecimal balanceAfter;       // saldo da conta após a operação
    private String counterpartyNumber;     // conta de/para quem (em transferências)

    public OperationResponseDTO() {}

    // Construtor existente (mantido para compatibilidade)
    public OperationResponseDTO(Long id, String accountNumber, OperationType type,
                                BigDecimal value, LocalDateTime operationDate,
                                BigDecimal balanceAfter) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.value = value;
        this.operationDate = operationDate;
        this.balanceAfter = balanceAfter;
    }

    // Novo construtor incluindo counterpartyNumber
    public OperationResponseDTO(Long id, String accountNumber, OperationType type,
                                BigDecimal value, LocalDateTime operationDate,
                                BigDecimal balanceAfter, String counterpartyNumber) {
        this(id, accountNumber, type, value, operationDate, balanceAfter);
        this.counterpartyNumber = counterpartyNumber;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public OperationType getType() { return type; }
    public void setType(OperationType type) { this.type = type; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public LocalDateTime getOperationDate() { return operationDate; }
    public void setOperationDate(LocalDateTime operationDate) { this.operationDate = operationDate; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getCounterpartyNumber() { return counterpartyNumber; }
    public void setCounterpartyNumber(String counterpartyNumber) { this.counterpartyNumber = counterpartyNumber; }
}
