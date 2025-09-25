package br.com.di2win.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.hibernate.validator.constraints.br.CPF;

public class CreateClientDTO {

    @NotBlank(message = "Name is required (mandatory)")
    private String name;

    @NotBlank(message = "CPF is required (mandatory)")
    @CPF(message = "This is a invalid cpf")
    private String cpf;

    @NotNull(message = "Birth date is required (mandatory)")
    private LocalDate birthDate;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}
