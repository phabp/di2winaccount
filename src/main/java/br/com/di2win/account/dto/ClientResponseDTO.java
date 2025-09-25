package br.com.di2win.account.dto;

import java.time.LocalDate;

public class ClientResponseDTO {
    private Long id;
    private String name;
    private String cpf;
    private LocalDate birthDate;

    public ClientResponseDTO() {
    }

    public ClientResponseDTO(Long id, String name, String cpf, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.cpf = cpf;
        this.birthDate = birthDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
}
