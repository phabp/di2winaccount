package br.com.di2win.account.dto;

import br.com.di2win.account.enums.OperationType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EveryOperationDTO {
    private OperationType type;
    private BigDecimal value;
    private LocalDateTime operationDate;
    private String details; // opcional: ex. origem/destino, descrição extra

    public EveryOperationDTO() {}

    public EveryOperationDTO(OperationType type, BigDecimal value,
                             LocalDateTime operationDate, String details) {
        this.type = type;
        this.value = value;
        this.operationDate = operationDate;
        this.details = details;
    }

    public OperationType getType() { return type; }
    public void setType(OperationType type) { this.type = type; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public LocalDateTime getOperationDate() { return operationDate; }
    public void setOperationDate(LocalDateTime operationDate) { this.operationDate = operationDate; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
