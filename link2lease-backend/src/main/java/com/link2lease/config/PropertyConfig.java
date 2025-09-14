package com.link2lease.config;

import com.link2lease.enums.UserRole;
import com.link2lease.model.Property;
import com.link2lease.model.User;
import com.link2lease.repository.PropertyRepository;
import com.link2lease.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class PropertyConfig {

    @Bean
    @Order(2)
    CommandLineRunner propertyCommandLineRunner(PropertyRepository propertyRepository, UserRepository userRepository){
        return args -> {
            List<User> landlords = userRepository.findAllByRole(UserRole.LANDLORD);


            if(landlords.isEmpty()){
                System.out.println("No landlords found. Skipping property creation.");
                return;
            }

            if(propertyRepository.count() == 0){
                Property property1 = new Property(
                        "Modern Downtown Apartment",
                        "Beautiful 2-bedroom apartment in the heart of downtown",
                        "123 Main Street, Downtown, City",
                        1800.00,
                        LocalDate.of(2025, 9, 1),
                        landlords.get(0) // Jane Smith
                );

                Property property2 = new Property(
                        "Cozy Suburban House",
                        "Charming 3-bedroom house in quiet neighborhood",
                        "456 Oak Avenue, Suburbia, City",
                        2200.00,
                        LocalDate.of(2025, 9, 15),
                        landlords.get(1) // Mike Johnson
                );

                Property property3 = new Property(
                        "Luxury Penthouse",
                        "Exclusive penthouse with panoramic city views",
                        "789 Elite Boulevard, Uptown, City",
                        4500.00,
                        LocalDate.of(2025, 10, 1),
                        landlords.get(2) // Sara Williams
                );

                propertyRepository.saveAll(List.of(property1, property2, property3));
                System.out.println("Sample properties created for multiple landlords.");
            } else {
                System.out.println("Properties already exist. Skipping creation.");
            }
        };
    }
}
