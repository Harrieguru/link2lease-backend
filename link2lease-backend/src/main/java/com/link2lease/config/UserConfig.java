package com.link2lease.config;

import com.link2lease.enums.UserRole;
import com.link2lease.model.User;
import com.link2lease.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class UserConfig {
    @Bean
    @Order(1)
    CommandLineRunner commandLineRunner(UserRepository repository){
        return args -> {
            if(repository.count() == 0){
                User tenant = new User("John Doe", "john@email.com", "password123",
                        UserRole.TENANT, "+1234567890", LocalDate.now()
                );

                User landlord = new User("Jane Smith", "jane@email.com", "password456",
                        UserRole.LANDLORD, "+1987654321", LocalDate.now()
                );

                User admin = new User("Admin User", "admin@email.com", "adminpass",
                        UserRole.ADMIN, "+1111111111", LocalDate.now()
                );

                repository.saveAll(List.of(tenant,landlord,admin));
            }

        };
    }
}
