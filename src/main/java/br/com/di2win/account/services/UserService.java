package br.com.di2win.account.services;

import br.com.di2win.account.dto.RegisterUserDTO;
import br.com.di2win.account.entities.Client;
import br.com.di2win.account.entities.User;
import br.com.di2win.account.exceptions.ConflictException;
import br.com.di2win.account.exceptions.NotFoundException;
import br.com.di2win.account.repositories.ClientRepository;
import br.com.di2win.account.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final ClientRepository clientRepo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepo,
                       ClientRepository clientRepo,
                       PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.clientRepo = clientRepo;
        this.encoder = encoder;
    }

    @Transactional
    public void register(RegisterUserDTO dto) {
        String normalizedCpf = normalizeCpf(dto.getCpf());

        if (userRepo.existsByCpf(normalizedCpf)) {
            throw new ConflictException("A user with this CPF already exists.");
        }

      
        Client client = clientRepo.findByCpf(normalizedCpf)
                .orElseThrow(() -> new NotFoundException("Client not found for the given CPF. Create the client first."));

        User u = new User();
        u.setCpf(normalizedCpf);
        u.setPasswordHash(encoder.encode(dto.getPassword())); 
        u.setRole("USER");   
        u.setActive(true);
        u.setClient(client);

        userRepo.save(u);
    }

    
    private String normalizeCpf(String cpf) {
        return cpf == null ? null : cpf.replaceAll("\\D", "");
    }
}
