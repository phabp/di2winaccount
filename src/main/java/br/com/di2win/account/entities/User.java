package br.com.di2win.account.entities;

import jakarta.persistence.*;

@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(columnNames = "cpf")
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

 
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    
    @Column(nullable = false)
    private String passwordHash;

    
    @Column(nullable = false, length = 30)
    private String role = "USER";

    
    @Column(nullable = false)
    private boolean active = true;

    
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }
}
