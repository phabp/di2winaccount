package br.com.di2win.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class OperationValueDTO {

    @NotNull
    @DecimalMin(value = "0.01", message = "The value of this operation must be greater than zero. At least one cent.")
    private BigDecimal value;

    // Getter and Setter
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
} 
