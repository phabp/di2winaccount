package br.com.di2win.account.controllers;

import br.com.di2win.account.dto.LoginRequestDTO;
import br.com.di2win.account.dto.LoginResponseDTO;
import br.com.di2win.account.dto.RegisterUserDTO;
import br.com.di2win.account.entities.User;
import br.com.di2win.account.repositories.UserRepository;
import br.com.di2win.account.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public AuthController(UserService userService,
                          UserRepository userRepository,
                          BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    /** Cadastro de usuário (CPF + senha). */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterUserDTO dto) {
        userService.register(dto); // salva senha com BCrypt dentro do serviço
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** Login: recebe CPF + senha e devolve um token simples (UUID) + cpf. */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        final String cpf = dto.getCpf(); // já vem normalizado pelo setter
        final String raw = dto.getPassword(); // já vem trimado

        User u = userRepository.findByCpf(cpf).orElse(null);
        if (u == null || !u.isActive() || !encoder.matches(raw, u.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = UUID.randomUUID().toString(); // token “temporário” p/ compatibilidade
        return ResponseEntity.ok(new LoginResponseDTO(token, u.getCpf()));
    }
}

