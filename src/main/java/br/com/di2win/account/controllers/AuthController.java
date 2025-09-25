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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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

    @Operation(description = "Register new user with CPF and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created"),
        @ApiResponse(responseCode = "400", description = "Invalid data"),
        @ApiResponse(responseCode = "409", description = "This user already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterUserDTO dto) {
        userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(description = "Login with CPF and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Authenticated"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        final String cpf = dto.getCpf();
        final String raw = dto.getPassword();

        User u = userRepository.findByCpf(cpf).orElse(null);
        if (u == null || !u.isActive() || !encoder.matches(raw, u.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = UUID.randomUUID().toString();
        return ResponseEntity.ok(new LoginResponseDTO(token, u.getCpf()));
    }
}

