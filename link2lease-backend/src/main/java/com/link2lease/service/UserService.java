package com.link2lease.service;

import com.link2lease.enums.UserRole;
import com.link2lease.model.User;
import com.link2lease.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() { return userRepository.findAll(); }

    public void addNewUser(User user){
        Optional<User> userByEmail = userRepository.findUserByEmail(user.getEmail());
        if(userByEmail.isPresent()){
            throw new IllegalStateException("email taken");
        }

        userRepository.save(user);
    }

    public void deleteUser(Long userId){
        boolean exists =userRepository.existsById(userId);
        if(!exists){
            throw new IllegalStateException("user with Id" + userId + " does not exist");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateUser(Long userId, String fullName, String email, String password,String phoneNumber){
        User user =userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("user with Id " + userId + " does not exist"));

        if(fullName != null && fullName.length() > 0 && !Objects.equals(user.getFullName(),fullName)){
            user.setFullName(fullName);
        }

        if(email != null && email.length() > 0 && !Objects.equals(user.getEmail(),email)){
            Optional<User> userByEmail = userRepository.findUserByEmail(email);
            if(userByEmail.isPresent()){
                throw new IllegalStateException("email taken");
            }
            user.setEmail(email);
        }

        if(password != null && password.length() > 0 && !Objects.equals(user.getPassword(),password)){
            user.setPassword(password);
        }

        if(phoneNumber != null && phoneNumber.length() > 0 && !Objects.equals(user.getPhoneNumber(),phoneNumber)){
            user.setPhoneNumber(phoneNumber);
        }
    }
}
