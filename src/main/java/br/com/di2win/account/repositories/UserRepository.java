package br.com.di2win.account.repositories;

import br.com.di2win.account.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}
