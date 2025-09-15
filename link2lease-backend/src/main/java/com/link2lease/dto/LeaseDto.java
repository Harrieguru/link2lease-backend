package com.link2lease.dto;

import com.link2lease.model.Lease;

import java.time.LocalDate;

public class LeaseDto {
    private Long id;
    private Long propertyId;
    private Long tenantId;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long landlordId;

    // --- Getters and setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Long getLandlordId() { return landlordId; }
    public void setLandlordId(Long landlordId) { this.landlordId = landlordId; }

    // --- Static factory method to convert entity → DTO ---
    public static LeaseDto fromEntity(Lease lease) {
        LeaseDto dto = new LeaseDto();
        dto.setId(lease.getId());
        dto.setPropertyId(lease.getProperty().getId());
        dto.setTenantId(lease.getTenant().getId());
        dto.setStatus(lease.getStatus().name());
        dto.setStartDate(lease.getStartDate());
        dto.setEndDate(lease.getEndDate());
        dto.setLandlordId(lease.getProperty().getLandlord().getId());
        return dto;
    }

}
