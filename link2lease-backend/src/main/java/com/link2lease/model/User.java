package com.link2lease.model;

import com.link2lease.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table( name = "users")
public class User {
    public User(String fullName, String email, String password, UserRole role, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;
    @NotBlank
    private String fullName;
    @Email
    private String email;
    private String password;
    private UserRole role;
    private String phoneNumber;
    private LocalDate dateJoined;

    public User(){
        this.dateJoined = LocalDate.from(LocalDateTime.now());
    }

    public String getFullName() {
        return fullName;
    }

    public User(String fullName, String email, String password, UserRole role, String phoneNumber, LocalDate dateJoined) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.dateJoined = dateJoined;
    }
    public Long getId() {
        return id;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(LocalDate dateJoined) {
        this.dateJoined = dateJoined;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", dateJoined=" + dateJoined +
                '}';
    }


}
