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

   
    Optional<Account> findFirstByClientCpfAndActiveTrueAndBlockedFalse(String cpf);

  
    List<Account> findByClientId(Long clientId);

 
    List<Account> findByClientCpf(String cpf);
}
