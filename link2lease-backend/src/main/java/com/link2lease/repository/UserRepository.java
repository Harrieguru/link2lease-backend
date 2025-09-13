package com.link2lease.repository;

import com.link2lease.enums.UserRole;
import com.link2lease.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findUserByEmail(String email);

    List<User> findAllByRole(UserRole userRole);
}
