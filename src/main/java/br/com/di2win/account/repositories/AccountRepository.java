package br.com.di2win.account.repositories;

import br.com.di2win.account.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByNumber(String number);

    boolean existsByNumber(String number);

    // usado em regras de negócio (ex.: transferência por CPF)
    Optional<Account> findFirstByClientCpfAndActiveTrueAndBlockedFalse(String cpf);

    // usado no ClientService.delete(...) para listar contas do cliente
    List<Account> findByClientId(Long clientId);

    // NOVO: listar contas diretamente por CPF do cliente (para o menu pós-login)
    List<Account> findByClientCpf(String cpf);
}
