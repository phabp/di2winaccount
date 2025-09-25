package br.com.di2win.account.controllers;

import br.com.di2win.account.dto.CreateClientDTO;
import br.com.di2win.account.dto.ClientResponseDTO;
import br.com.di2win.account.services.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService service;

    public ClientController(ClientService service) {
        this.service = service;
    }

    @Operation(description = "Create a new client")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Client created"),
        @ApiResponse(responseCode = "400", description = "Invalid data"),
        @ApiResponse(responseCode = "409", description = "Client already exists")
    })
    @PostMapping
    public ResponseEntity<ClientResponseDTO> create(@RequestBody @Valid CreateClientDTO dto) {
        ClientResponseDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(description = "Delete a client by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Client deleted"),
        @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get a client by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client returned"),
        @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(description = "List all clients")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clients returned")
    })
    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }
}

