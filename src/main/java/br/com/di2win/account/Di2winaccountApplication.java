package br.com.di2win.account;

import br.com.di2win.account.clientMenu.MenuService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class Di2winaccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(Di2winaccountApplication.class, args);
    }

    @Bean
    @Profile("cli")
    CommandLineRunner cliRunner(MenuService menu) {
        return args -> menu.start();
    }
}
