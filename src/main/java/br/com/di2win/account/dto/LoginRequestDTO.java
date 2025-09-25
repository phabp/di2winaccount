package br.com.di2win.account.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public class LoginRequestDTO {
    @NotBlank
    @CPF(message = "This cpf is not valid !! ")
    private String cpf;

    @NotBlank
    private String password;

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = (cpf == null) ? null : cpf.replaceAll("\\D", ""); }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = (password == null) ? null : password.trim(); }
}
