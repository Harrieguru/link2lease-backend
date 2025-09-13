package com.link2lease.repository;

import com.link2lease.model.Property;
import com.link2lease.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property,Long> {

    //    find properties by landlord
    List<Property> findByLandlord(User landlord);

    //    find available properties (available from date is today or earlier)
    @Query("SELECT p FROM Property p WHERE p.availableFrom <= :date")
    List<Property> findAvailableProperties(@Param("date")LocalDate date);

    //  find properties available for a specific date
    List<Property> findByAvailableFromBefore(LocalDate date);
    List<Property> findByAvailableFromAfter(LocalDate date);

    // find properties by rent amount range
    List<Property> findByRentAmountBetween(double minRent, double maxRent);
    List<Property> findByRentAmountLessThanEqual(double maxRent);

    // find properties by address containing text
    List<Property> findByAddressContaining(String address);

    // find properties by title containing text
    List<Property> findByTitleContaining(String title);

//    find properties by specific landlord id
    List<Property> findByLandlordId(Long landlordId);

    // Custom query to find properties with complex search criteria
    @Query("SELECT p FROM Property p WHERE " +
            "(:title IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:address IS NULL OR LOWER(p.address) LIKE LOWER(CONCAT('%', :address, '%'))) AND " +
            "(:minRent IS NULL OR p.rentAmount >= :minRent) AND " +
            "(:maxRent IS NULL OR p.rentAmount <= :maxRent) AND " +
            "(:availableFrom IS NULL OR p.availableFrom <= :availableFrom)")
    List<Property> findPropertiesWithFilters(@Param("title") String title,
                                             @Param("address") String address,
                                             @Param("minRent") Double minRent,
                                             @Param("maxRent") Double maxRent,
                                             @Param("availableFrom") LocalDate availableFrom);



}
