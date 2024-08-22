package com.picpay.controllers;

import com.picpay.domain.user.User;
import com.picpay.dtos.UserDTO;
import com.picpay.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(UserDTO user) {
        User newUser = userService.createUser(user);
    }
}