package br.com.di2win.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Transfer", description = "Request payload to transfer funds from the current account to a destination account.")
public class TransferDTO {

    @Schema(description = "Destination account number (digits only)", example = "7647913839")
    @NotBlank(message = "Destination account number is required")
    private String destinationNumber;

    @Schema(description = "Amount to transfer (minimum one cent). Always positive.", example = "150.00")
    @NotNull
    @DecimalMin(value = "0.01", message = "The value for this transaction has to be at least one cent.")
    private BigDecimal value;

    public String getDestinationNumber() { return destinationNumber; }
    public void setDestinationNumber(String destinationNumber) { this.destinationNumber = destinationNumber; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
}
