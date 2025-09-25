package br.com.di2win.account.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private String agency;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Boolean blocked = false;

    @Column(nullable = false)
    private LocalDateTime openDate; 

    @PrePersist
    protected void onCreate() {
        if (openDate == null) {
            openDate = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getAgency() { return agency; }
    public void setAgency(String agency) { this.agency = agency; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Boolean getBlocked() { return blocked; }
    public void setBlocked(Boolean blocked) { this.blocked = blocked; }

    public LocalDateTime getOpenDate() { return openDate; }
    public void setOpenDate(LocalDateTime openDate) { this.openDate = openDate; }
}
