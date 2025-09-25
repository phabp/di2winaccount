package br.com.di2win.account.dto;

public class LoginResponseDTO {
    private String token;
    private String cpf;

    public LoginResponseDTO(String token, String cpf) {
        this.token = token;
        this.cpf = cpf;
    }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
}
