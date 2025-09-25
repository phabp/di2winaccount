package br.com.di2win.account.repositories;

import br.com.di2win.account.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
