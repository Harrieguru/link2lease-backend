package com.link2lease.controller;

import com.link2lease.dto.PropertyDto;
import com.link2lease.model.Property;
import com.link2lease.service.PropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "*")
public class PropertyController {
    private final PropertyService propertyService;

    @Autowired
    public PropertyController(PropertyService propertyService){
        this.propertyService = propertyService;
    }

    // Get all properties (DTOs)
    @GetMapping
    public ResponseEntity<List<PropertyDto>> getAllProperties(){
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    // Get property by ID (DTO) - FIXED METHOD NAME
    @GetMapping("/{id}")
    public ResponseEntity<?> getPropertyById(@PathVariable Long id){
        return propertyService.getPropertyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new property (accept entity, return DTO) - FIXED TO RETURN DTO
    @PostMapping
    public ResponseEntity<?> createProperty(@RequestBody Property property){
        try {
            PropertyDto savedProperty = propertyService.savePropertyDto(property);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProperty);
        } catch(IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }



    // Update existing property (return DTO) - FIXED TO RETURN DTO
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateProperty(@PathVariable Long id, @RequestBody Property propertyDetails){
//        try {
//            PropertyDto updatedProperty = propertyService.updatePropertyDto(id, propertyDetails);
//            return ResponseEntity.ok(updatedProperty);
//        } catch (IllegalStateException e) {
//            return ResponseEntity.notFound().build();
//        } catch(IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
//        }
//    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDto> updateProperty(@PathVariable Long id,
                                                      @RequestBody PropertyDto propertyDto) {
        PropertyDto updatedProperty = propertyService.updatePropertyDto(id, propertyDto);
        return ResponseEntity.ok(updatedProperty);
    }

    // Delete property
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long id){
        try {
            propertyService.deleteProperty(id);
            return ResponseEntity.ok().body("Property deleted successfully");
        } catch(IllegalStateException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get properties by landlord ID
    @GetMapping("/landlord/{landlordId}")
    public ResponseEntity<List<PropertyDto>> getPropertiesByLandlord(@PathVariable Long landlordId){
        return ResponseEntity.ok(propertyService.getPropertiesByLandlordId(landlordId));
    }

    // Get available properties
    @GetMapping("/available")
    public ResponseEntity<List<PropertyDto>> getAvailableProperties(){
        return ResponseEntity.ok(propertyService.getAvailableProperties());
    }

    // Get available properties from specific date
    @GetMapping("/available/{date}")
    public ResponseEntity<?> getAvailablePropertiesFromDate(@PathVariable String date){
        try {
            LocalDate availableDate = LocalDate.parse(date);
            return ResponseEntity.ok(propertyService.getAvailablePropertiesFromDate(availableDate));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body("Error: Invalid date format. Use YYYY-MM-DD");
        }
    }

    // Search properties with filters
    @GetMapping("/search")
    public ResponseEntity<?> searchProperties(@RequestParam(required = false) String title,
                                              @RequestParam(required = false) String address,
                                              @RequestParam(required = false) Double minRent,
                                              @RequestParam(required = false) Double maxRent,
                                              @RequestParam(required = false) String availableFrom){
        try {
            LocalDate availableDate = null;
            if(availableFrom != null && !availableFrom.trim().isEmpty()){
                availableDate = LocalDate.parse(availableFrom);
            }

            List<PropertyDto> properties = propertyService.searchPropertiesWithFilters(
                    title, address,
                    minRent != null ? minRent : 0,
                    maxRent != null ? maxRent : Double.MAX_VALUE,
                    availableDate
            );
            return ResponseEntity.ok(properties);
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Get properties by rent range
    @GetMapping("/rent")
    public ResponseEntity<?> getPropertiesByRentRange(@RequestParam(required = false) Double minRent,
                                                      @RequestParam(required = false) Double maxRent){
        try {
            List<PropertyDto> properties;
            if(minRent != null && maxRent != null){
                properties = propertyService.getPropertiesByRentRange(minRent, maxRent);
            } else if(maxRent != null){
                properties = propertyService.getPropertiesByMaxRent(maxRent);
            } else {
                return ResponseEntity.badRequest().body("Error: Please provide at least maxRent parameter");
            }

            return ResponseEntity.ok(properties);
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Search by address
    @GetMapping("/search/address")
    public ResponseEntity<?> searchByAddress(@RequestParam String address){
        try {
            return ResponseEntity.ok(propertyService.searchPropertiesByAddress(address));
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // Search by title
    @GetMapping("/search/title")
    public ResponseEntity<?> searchByTitle(@RequestParam String title){
        try {
            return ResponseEntity.ok(propertyService.searchPropertiesByTitle(title));
        } catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}