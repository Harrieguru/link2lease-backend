package com.link2lease.controller;

import com.link2lease.model.User;
import com.link2lease.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() { return userService.getUsers(); }

    @PostMapping(path = "/registration")
    public void registerUser(@RequestBody User user){ userService.addNewUser(user);}

    @DeleteMapping(path = "{userId}")
    public void deleteUser(@PathVariable("userId") Long userId){ userService.deleteUser(userId);}

    @PutMapping(path = "{userId}")
    public void updateStudent(
        @PathVariable("userId") Long userId,
        @RequestParam(required = false) String fullName,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) String password,
        @RequestParam(required = false) String phoneNumber){
        userService.updateUser(userId,fullName,email,password,phoneNumber);
    }
}

