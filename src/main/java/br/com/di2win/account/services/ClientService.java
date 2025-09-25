package br.com.di2win.account.services;

import br.com.di2win.account.dto.ClientResponseDTO;
import br.com.di2win.account.dto.CreateClientDTO;
import br.com.di2win.account.entities.Client;
import br.com.di2win.account.exceptions.BusinessRuleException;
import br.com.di2win.account.exceptions.ConflictException;
import br.com.di2win.account.exceptions.NotFoundException;
import br.com.di2win.account.repositories.AccountRepository;
import br.com.di2win.account.repositories.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;

    public ClientService(ClientRepository clientRepository,
                         AccountRepository accountRepository) {
        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public ClientResponseDTO create(CreateClientDTO dto) {
        if (clientRepository.existsByCpf(dto.getCpf())) {
            throw new ConflictException("A client with this CPF already exists.");
        }

        Client c = new Client();
        c.setName(dto.getName());
        c.setCpf(dto.getCpf());
        c.setBirthDate(dto.getBirthDate()); 

        Client saved = clientRepository.save(c);
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found."));

        var accounts = accountRepository.findByClientId(client.getId());
        boolean hasActive = accounts.stream().anyMatch(a -> Boolean.TRUE.equals(a.getActive()));
        if (hasActive) {
            throw new BusinessRuleException("Client has an active account. Close/block it before deleting.");
        }

        clientRepository.delete(client);
    }

    @Transactional(readOnly = true)
    public ClientResponseDTO getById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found."));
        return toResponse(client);
    }

    @Transactional(readOnly = true)
    public List<ClientResponseDTO> listAll() {
        return clientRepository.findAll().stream().map(this::toResponse).toList();
    }

    private ClientResponseDTO toResponse(Client c) {
        return new ClientResponseDTO(
                c.getId(),
                c.getName(),
                c.getCpf(),
                c.getBirthDate() 
        );
    }
}
