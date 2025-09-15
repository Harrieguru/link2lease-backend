package com.link2lease.service;

import com.link2lease.dto.LeaseDto;
import com.link2lease.enums.LeaseStatus;
import com.link2lease.model.Lease;
import com.link2lease.model.Property;
import com.link2lease.model.User;
import com.link2lease.repository.LeaseRepository;
import com.link2lease.repository.PropertyRepository;
import com.link2lease.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class LeaseService {

    private final LeaseRepository leaseRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public LeaseService(LeaseRepository leaseRepository,
                        PropertyRepository propertyRepository,
                        UserRepository userRepository){
        this.leaseRepository = leaseRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public LeaseDto applyLease(Long propertyId, Long tenantId){
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("property not found"));

        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("tenant not found"));

        Lease lease = new Lease();
        lease.setProperty(property);
        lease.setTenant(tenant);
        lease.setStatus(LeaseStatus.PENDING);
        // start of date to today
        lease.setStartDate(LocalDate.now());

        //set end date to 1 year after start
        lease.setEndDate(LocalDate.now().plusYears(1));
        Lease savedLease = leaseRepository.save(lease);

        return LeaseDto.fromEntity(savedLease);
    }

    @Transactional
    public LeaseDto approveLease(Long leaseId, Long landlordId){
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new RuntimeException("Lease not found"));

        if(!lease.getProperty().getLandlord().getId().equals(landlordId)){
            throw new RuntimeException("Only landlord can approve this lease");
        }

        lease.setStatus(LeaseStatus.ACTIVE);
        return LeaseDto.fromEntity(leaseRepository.save(lease));

    }

    @Transactional
    public LeaseDto terminateLease(Long leaseId, Long userId){
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new RuntimeException("Lease not found"));

        //either tenant or landlord can terminate
        boolean isTenant = lease.getTenant().getId().equals(userId);
        boolean isLandlord = lease.getProperty().getLandlord().getId().equals(userId);

        if(!isTenant && !isLandlord){
            throw new RuntimeException("Unauthorized termination");
        }

        lease.setStatus(LeaseStatus.TERMINATED);
        return  LeaseDto.fromEntity(leaseRepository.save(lease));
    }

    public LeaseDto getLease(Long leaseId){
        Lease lease = leaseRepository.findById(leaseId)
                .orElseThrow(() -> new RuntimeException("Lease not found"));

        return LeaseDto.fromEntity(lease);
    }
}