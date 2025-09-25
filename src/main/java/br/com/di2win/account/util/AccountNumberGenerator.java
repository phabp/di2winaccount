package br.com.di2win.account.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import br.com.di2win.account.repositories.AccountRepository;

@Component
public class AccountNumberGenerator {

    private final SecureRandom random = new SecureRandom();
    private final AccountRepository accountRepository;

    public AccountNumberGenerator(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    
    public String generateAgency() {
        int n = random.nextInt(9999) + 1;
        return String.format("%04d", n);
    }

    
    public String generateUniqueAccountNumber() {
        String candidate;
        int attempts = 0;
        do {
            int n = random.nextInt(99_999_999) + 1; 
            candidate = String.format("%08d", n);
            attempts++;
            
            if (attempts > 100) {
                throw new IllegalStateException("failed to generate account number after too many attempts!");
            }
        } while (accountRepository.existsByNumber(candidate));
        return candidate;
    }
}
