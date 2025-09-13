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

import java.util.List;

import java.time.LocalDate;

@Configuration
public class PropertyConfig {
    @Bean
    @Order(2)
    CommandLineRunner propertyCommandLineRunner(PropertyRepository propertyRepository, UserRepository userRepository){
        return args -> {
            // Find the landlord user (Jane Smith) from the database
            User landlord = userRepository.findUserByEmail("jane@email.com")
                    .orElseThrow(() -> new IllegalStateException("Landlord not found"));

            // Verify it's actually a landlord
            if (landlord.getRole() != UserRole.LANDLORD) {
                throw new IllegalStateException("User is not a landlord");
            }

            Property property1 = new Property(
                    "Modern Downtown Apartment",
                    "Beautiful 2-bedroom apartment in the heart of downtown with stunning city views. Features include hardwood floors, stainless steel appliances, and in-unit laundry.",
                    "123 Main Street, Downtown, City",
                    1800.00,
                    LocalDate.of(2025, 9, 1),
                    landlord
            );

            Property property2 = new Property(
                    "Cozy Suburban House",
                    "Charming 3-bedroom house in quiet neighborhood. Perfect for families with a large backyard, garage, and close proximity to schools.",
                    "456 Oak Avenue, Suburbia, City",
                    2200.00,
                    LocalDate.of(2025, 9, 15),
                    landlord
            );

            Property property3 = new Property(
                    "Student-Friendly Studio",
                    "Affordable studio apartment near university campus. Furnished with desk, bed, and kitchenette. All utilities included.",
                    "789 University Drive, Campus Area, City",
                    900.00,
                    LocalDate.of(2025, 8, 30),
                    landlord
            );

            Property property4 = new Property(
                    "Luxury Penthouse",
                    "Exclusive penthouse with panoramic city views, rooftop terrace, and premium finishes throughout. Building amenities include gym and concierge.",
                    "321 Elite Boulevard, Uptown, City",
                    4500.00,
                    LocalDate.of(2025, 10, 1),
                    landlord
            );

            // Only save if no properties exist to avoid duplicates
            if (propertyRepository.count() == 0) {
                propertyRepository.saveAll(List.of(property1, property2, property3, property4));
            }
        };
    }
}
