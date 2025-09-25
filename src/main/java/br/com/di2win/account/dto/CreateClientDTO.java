package br.com.di2win.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.hibernate.validator.constraints.br.CPF;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CreateClient", description = "Request payload to create a new client.")
public class CreateClientDTO {

    @Schema(description = "Full name of the client", example = "João Pedro da Silva")
    @NotBlank(message = "Name is required ")
    private String name;

    @Schema(description = " CPF (digits only)", example = "12345678909")
    @NotBlank(message = "CPF is required")
    @CPF(message = "This is an invalid CPF")
    private String cpf;

    @Schema(description = "Birth date in format YYYY-MM-DD)", example = "1995-08-17")
    @NotNull(message = "Birth date is required ")
    private LocalDate birthDate;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}
