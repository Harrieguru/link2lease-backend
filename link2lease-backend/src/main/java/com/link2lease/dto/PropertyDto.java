package com.link2lease.dto;

import com.link2lease.model.Property;

import java.time.LocalDate;

public class PropertyDto {
    private Long id;
    private String title;
    private String description;
    private String address;
    private double rentAmount;
    private LocalDate availableFrom;
    private String landlordName;
    private String landlordEmail;

    public PropertyDto(Property property){
        this.id = property.getId();
        this.title = property.getTitle();
        this.description = property.getDescription();
        this.address = property.getAddress();
        this.rentAmount = property.getRentAmount();
        this.availableFrom = property.getAvailableFrom();

        if(property.getLandlord() != null){
            this.landlordName = property.getLandlord().getFullName();
            this.landlordEmail = property.getLandlord().getEmail();
        }
    }

    public Long getId() { return id; }
    public String getTitle(){ return title; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public double getRentAmount() { return rentAmount; }
    public LocalDate getAvailableFrom() { return availableFrom; }
    public String getLandlordName() { return landlordName; }
    public String getLandlordEmail() { return landlordEmail; }


}
