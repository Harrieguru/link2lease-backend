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
                // Original users
                User tenant1 = new User("John Doe", "john@email.com", "password123",
                        UserRole.TENANT, "+1234567890", LocalDate.now());

                User landlord1 = new User("Jane Smith", "jane@email.com", "password456",
                        UserRole.LANDLORD, "+1987654321", LocalDate.now());

                User admin = new User("Admin User", "admin@email.com", "adminpass",
                        UserRole.ADMIN, "+1111111111", LocalDate.now());

                // Additional landlords
                User landlord2 = new User("Mike Johnson", "mike@email.com", "pass789",
                        UserRole.LANDLORD, "+2223334444", LocalDate.now());

                User landlord3 = new User("Sara Williams", "sara@email.com", "pass101",
                        UserRole.LANDLORD, "+5556667777", LocalDate.now());

                // Additional tenants
                User tenant2 = new User("Emily Brown", "emily@email.com", "pass202",
                        UserRole.TENANT, "+8889990000", LocalDate.now());

                repository.saveAll(List.of(
                        tenant1, tenant2,
                        landlord1, landlord2, landlord3,
                        admin
                ));

                System.out.println("Sample users created: 3 landlords, 2 tenants, 1 admin.");
            } else {
                System.out.println("Users already exist. Skipping creation.");
            }
        };
    }
}
