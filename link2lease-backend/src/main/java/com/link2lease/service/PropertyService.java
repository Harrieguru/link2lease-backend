package com.link2lease.service;

import com.link2lease.dto.PropertyDto;
import com.link2lease.model.Property;
import com.link2lease.model.User;
import com.link2lease.repository.PropertyRepository;
import com.link2lease.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PropertyService {
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Autowired
    public PropertyService(PropertyRepository propertyRepository,UserRepository userRepository){
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    // --- Utility to convert entities -> DTOs ---
    private PropertyDto toDto(Property property) {
        return new PropertyDto(property);
    }

    private List<PropertyDto> toDtoList(List<Property> properties) {
        return properties.stream().map(PropertyDto::new).collect(Collectors.toList());
    }

    // --- Basic CRUD with entities (still used internally) ---
    public Property saveProperty(Property property){
        validateProperty(property);
        return propertyRepository.save(property);
    }

    public PropertyDto savePropertyDto(Property property){
        if (property.getLandlord() == null || property.getLandlord().getId() == null) {
            throw new IllegalArgumentException("Property must have a landlord id");
        }

        // Fetch the managed landlord entity
        Long landlordId = property.getLandlord().getId();
        User landlord = userRepository.findById(landlordId)
                .orElseThrow(() -> new IllegalArgumentException("Landlord not found with id: " + landlordId));

        property.setLandlord(landlord); // assign managed entity

        Property savedProperty = propertyRepository.save(property);
        return new PropertyDto(savedProperty);
    }

    public Property updateProperty(Long id, Property propertyDetails){
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Property with id " + id + " does not exist"));

        validateProperty(propertyDetails);

        property.setTitle(propertyDetails.getTitle());
        property.setDescription(propertyDetails.getDescription());
        property.setAddress(propertyDetails.getAddress());
        property.setRentAmount(propertyDetails.getRentAmount());
        property.setAvailableFrom(propertyDetails.getAvailableFrom());

        return propertyRepository.save(property);
    }


    public PropertyDto updatePropertyDto(Long id, PropertyDto propertyDetailsDto) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Property not found"));

        property.setTitle(propertyDetailsDto.getTitle());
        property.setDescription(propertyDetailsDto.getDescription());
        property.setAddress(propertyDetailsDto.getAddress());
        property.setRentAmount(propertyDetailsDto.getRentAmount());
        property.setAvailableFrom(propertyDetailsDto.getAvailableFrom());

        // Now this will work because PropertyDto has getLandlordEmail()
        if (propertyDetailsDto.getLandlordEmail() != null) {
            User landlord = userRepository.findUserByEmail(propertyDetailsDto.getLandlordEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Landlord not found with email: "
                            + propertyDetailsDto.getLandlordEmail()));
            property.setLandlord(landlord);
        } else {
            throw new IllegalArgumentException("Property must have a landlord email");
        }

        Property saved = propertyRepository.save(property);
        return new PropertyDto(saved);
    }


    public void deleteProperty(Long id){
        boolean exists = propertyRepository.existsById(id);
        if(!exists){
            throw new IllegalStateException("Property with id " + id + " does not exist");
        }
        propertyRepository.deleteById(id);
    }

    // --- DTO-based methods for Controller responses ---
    public List<PropertyDto> getAllProperties(){
        return toDtoList(propertyRepository.findAll());
    }

    public Optional<PropertyDto> getPropertyById(Long id){
        return propertyRepository.findById(id).map(this::toDto);
    }

    // REMOVED: getPropertyId method (was causing confusion)
    // The controller now correctly calls getPropertyById

    public List<PropertyDto> getPropertiesByLandlord(User landlord){
        return toDtoList(propertyRepository.findByLandlord(landlord));
    }

    public List<PropertyDto> getPropertiesByLandlordId(Long landlordId){
        return toDtoList(propertyRepository.findByLandlordId(landlordId));
    }

    public List<PropertyDto> getAvailablePropertiesFromDate(LocalDate date){
        return toDtoList(propertyRepository.findAvailableProperties(date));
    }

    public List<PropertyDto> getAvailableProperties() {
        return toDtoList(propertyRepository.findAvailableProperties(LocalDate.now()));
    }

    public List<PropertyDto> getPropertiesByRentRange(double minRent, double maxRent){
        if(minRent < 0 || maxRent < 0){
            throw new IllegalArgumentException("Rent amounts cannot be negative");
        }
        if(minRent > maxRent){
            throw new IllegalArgumentException("Minimum rent cannot be greater than maximum rent");
        }
        return toDtoList(propertyRepository.findByRentAmountBetween(minRent, maxRent));
    }

    public List<PropertyDto> getPropertiesByMaxRent(double maxRent){
        if(maxRent < 0){
            throw new IllegalArgumentException("Rent amount cannot be negative");
        }
        return toDtoList(propertyRepository.findByRentAmountLessThanEqual(maxRent));
    }

    public List<PropertyDto> searchPropertiesByAddress(String address){
        if(address == null || address.trim().isEmpty()){
            throw new IllegalArgumentException("Address search term cannot be empty");
        }
        return toDtoList(propertyRepository.findByAddressContaining(address.trim()));
    }

    public List<PropertyDto> searchPropertiesByTitle(String title){
        if(title == null || title.trim().isEmpty()){
            throw new IllegalArgumentException("Title search term cannot be empty");
        }
        return toDtoList(propertyRepository.findByTitleContaining(title.trim()));
    }

    public List<PropertyDto> searchPropertiesWithFilters(
            String title,
            String address,
            double minRent,
            double maxRent,
            LocalDate availableFrom
    ){
        if(minRent < 0){
            throw new IllegalArgumentException("Minimum rent cannot be negative");
        }
        if(maxRent < 0){
            throw new IllegalArgumentException("Maximum rent cannot be negative");
        }
        if(minRent > maxRent){
            throw new IllegalArgumentException("Minimum rent cannot be greater than maximum rent");
        }

        return toDtoList(propertyRepository.findPropertiesWithFilters(
                title != null && !title.trim().isEmpty() ? title.trim() : null,
                address != null && !address.trim().isEmpty() ? address.trim() : null,
                minRent,
                maxRent,
                availableFrom
        ));
    }

    // --- Validation ---
    private void validateProperty(Property property){
        if(property.getTitle() == null || property.getTitle().trim().isEmpty()){
            throw new IllegalArgumentException("Property title cannot be empty");
        }
        if(property.getAddress() == null || property.getAddress().trim().isEmpty()){
            throw new IllegalArgumentException("Property address cannot be empty");
        }
        if(property.getRentAmount() < 0){
            throw new IllegalArgumentException("Rent amount cannot be negative");
        }
        if(property.getAvailableFrom() == null){
            throw new IllegalArgumentException("Available from date cannot be null");
        }
        if(property.getLandlord() == null){
            throw new IllegalArgumentException("Property must have a landlord");
        }
    }
}