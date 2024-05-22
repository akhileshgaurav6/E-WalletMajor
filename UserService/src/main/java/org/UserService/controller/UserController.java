package org.UserService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import org.UserService.model.User;
import org.UserService.request.CreateUserRequest;
import org.UserService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public User createUser(@RequestBody @Valid CreateUserRequest createUserRequest) throws JsonProcessingException {
        return userService.createUser(createUserRequest);
    }

    @GetMapping("/getUser")    // we want to call userService method in txnService
    public User findUser(@RequestParam("contact") String contact){
        return userService.loadUserByUsername(contact);
    }
}
