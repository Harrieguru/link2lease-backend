package com.link2lease.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.link2lease.model.Lease;
import com.link2lease.model.User;
import java.util.List;

public interface LeaseRepository extends JpaRepository<Lease,Long> {
    List<Lease> findByTenant(User tenant); // gets lease by tenant
    List<Lease> findByPropertyLandlord(User Landlord); //gets lease by landlord
}
