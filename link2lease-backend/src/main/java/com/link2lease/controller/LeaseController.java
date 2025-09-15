package com.link2lease.controller;

import com.link2lease.dto.LeaseDto;
import com.link2lease.service.LeaseService;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/leases")
@CrossOrigin(origins = "*")
public class LeaseController {
    private final LeaseService leaseService;

    public LeaseController(LeaseService leaseService){
        this.leaseService = leaseService;
    }

    //tenant applies for a lease
    @PostMapping
    public ResponseEntity<?> applyLease(@RequestParam Long propertyId,
                                        @RequestParam Long tenantId){
        try{
            LeaseDto leaseDto = leaseService.applyLease(propertyId,tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(leaseDto);
        }catch(RuntimeException e){
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    //landlord approves  lease
    @PutMapping("/{leaseId}/approve")
    public ResponseEntity<?> approveLease(@PathVariable Long leaseId,
                                          @RequestParam Long landlordId){
        try{
            LeaseDto leaseDto = leaseService.approveLease(leaseId,landlordId);
            return ResponseEntity.ok(leaseDto);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: " + e.getMessage());
        }
    }

    //tenant or landlord terminate lease
    @PutMapping("/{leaseId}/terminate")
    public ResponseEntity<?> terminateLease(@PathVariable Long leaseId,
                                            @RequestParam Long userId){
        try{
            LeaseDto leaseDto = leaseService.terminateLease(leaseId, userId);
            return ResponseEntity.ok(leaseDto);
        } catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error: " + e.getMessage());
        }
    }

    //get lease details
    @GetMapping("/{leaseId}")
    public ResponseEntity<?> getLease(@PathVariable Long leaseId){
        try{
            LeaseDto leaseDto = leaseService.getLease(leaseId);
            return ResponseEntity.ok(leaseDto);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: " + e.getMessage());
        }
    }
}
