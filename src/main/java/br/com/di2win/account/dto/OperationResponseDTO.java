package br.com.di2win.account.dto;

import br.com.di2win.account.enums.OperationType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "OperationResponse", description = "Represents a single ledger entry for an account operation.")
public class OperationResponseDTO {

    @Schema(description = "An unique identifier for the operation", example = "123")
    private Long id;

    @Schema(description = "Account number related to the operation", example = "7647913839")
    private String accountNumber;

    @Schema(description = "Type of the operation", example = "TRANSFER_MADE")
    private OperationType type;

    @Schema(description = "Operation amount. ", example = "100.00")
    private BigDecimal value;

    @Schema(description = "Operation date and time (format mandatory)", example = "2025-09-24T20:30:36.481484")
    private LocalDateTime operationDate;

    @Schema(description = "Account balance after this operation", example = "81.00")
    private BigDecimal balanceAfter;

    @Schema(description = "Counterparty account number (origin or destination).", example = "7647913839")
    private String counterpartyNumber;

    public OperationResponseDTO() {}

    
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

