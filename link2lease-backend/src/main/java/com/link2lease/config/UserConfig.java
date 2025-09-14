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
    @Order(1) // Run before properties
    CommandLineRunner userCommandLineRunner(UserRepository userRepository) {
        return args -> {
            // Create 3 landlords if they don't exist
            if (userRepository.findUserByEmail("jane.smith@email.com").isEmpty()) {
                User jane = new User(
                        "Jane Smith",
                        "jane.smith@email.com",
                        "password123",
                        UserRole.LANDLORD,
                        "1234567890"
                );
                userRepository.save(jane);
            }
            if (userRepository.findUserByEmail("mike.johnson@email.com").isEmpty()) {
                User mike = new User(
                        "Mike Johnson",
                        "mike.johnson@email.com",
                        "password123",
                        UserRole.LANDLORD,
                        "1234567890"
                );
                userRepository.save(mike);
            }
            if (userRepository.findUserByEmail("sara.williams@email.com").isEmpty()) {
                User sara = new User(
                        "Sara Williams",
                        "sara.williams@email.com",
                        "password123",
                        UserRole.LANDLORD,
                        "1234567890"
                );
                userRepository.save(sara);
            }

            // Optionally, create 3 tenants as well
            if (userRepository.findUserByEmail("alice.tenant@email.com").isEmpty()) {
                User alice = new User(
                        "Alice Tenant",
                        "alice.tenant@email.com",
                        "password123",
                        UserRole.TENANT,
                        "0987654321"
                );
                userRepository.save(alice);
            }
            if (userRepository.findUserByEmail("bob.tenant@email.com").isEmpty()) {
                User bob = new User(
                        "Bob Tenant",
                        "bob.tenant@email.com",
                        "password123",
                        UserRole.TENANT,
                        "0987654322"
                );
                userRepository.save(bob);
            }
            if (userRepository.findUserByEmail("carol.tenant@email.com").isEmpty()) {
                User carol = new User(
                        "Carol Tenant",
                        "carol.tenant@email.com",
                        "password123",
                        UserRole.TENANT,
                        "0987654323"
                );
                userRepository.save(carol);
            }

            System.out.println("Sample users (landlords & tenants) created.");
        };
    }
}
