package br.com.di2win.account.entities;

import br.com.di2win.account.enums.OperationType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "operations", indexes = {
    @Index(name = "idx_operations_account_date", columnList = "account_id, operation_date")
})
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OperationType type;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal value;

    @Column(name = "operation_date", nullable = false)
    private LocalDateTime operationDate;

    // NOVOS CAMPOS
    /** Saldo da conta imediatamente após a operação */
    @Column(name = "balance_after", precision = 18, scale = 2)
    private BigDecimal balanceAfter;

    /** Número da conta contraparte (em transferências); null para depósito/saque */
    @Column(name = "counterparty_number", length = 20)
    private String counterpartyNumber;

    @PrePersist
    protected void onCreate() {
        if (operationDate == null) {
            operationDate = LocalDateTime.now();
        }
    }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }

    public OperationType getType() { return type; }
    public void setType(OperationType type) { this.type = type; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public LocalDateTime getOperationDate() { return operationDate; }
    public void setOperationDate(LocalDateTime operationDate) { this.operationDate = operationDate; }

    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public void setBalanceAfter(BigDecimal balanceAfter) { this.balanceAfter = balanceAfter; }

    public String getCounterpartyNumber() { return counterpartyNumber; }
    public void setCounterpartyNumber(String counterpartyNumber) { this.counterpartyNumber = counterpartyNumber; }
}

