package br.com.di2win.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferDTO {

    @NotBlank(message = "Destination account number is required")
    private String destinationNumber;

    @NotNull
    @DecimalMin(value = "0.01", message = "The value for this transaction has to be at least one cent!")
    private BigDecimal value;

    public String getDestinationNumber() { return destinationNumber; }
    public void setDestinationNumber(String destinationNumber) { this.destinationNumber = destinationNumber; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
}
