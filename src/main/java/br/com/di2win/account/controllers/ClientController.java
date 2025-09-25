package br.com.di2win.account.controllers;

import br.com.di2win.account.dto.CreateClientDTO;
import br.com.di2win.account.dto.ClientResponseDTO;
import br.com.di2win.account.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    // Create a new client
    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(@RequestBody @Valid CreateClientDTO dto) {
        ClientResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Delete a client by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Get a client by ID
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // List all clients
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }
}
