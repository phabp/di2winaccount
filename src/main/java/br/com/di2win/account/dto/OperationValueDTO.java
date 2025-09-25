package br.com.di2win.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "OperationValue", description = "Request payload carrying a monetary amount for operations")
public class OperationValueDTO {

    @Schema(description = "Amount for the operation (positive and minimum of one cent)", example = "100.00")
    @NotNull
    @DecimalMin(value = "0.01", message = "The value of this operation must be greater than zero. At least one cent.")
    private BigDecimal value;

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
}
