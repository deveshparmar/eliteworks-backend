package com.eliteworks.eliteworks.controllers;

import com.eliteworks.eliteworks.models.User;
import com.eliteworks.eliteworks.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
public class UserController {
    public UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/api/registerUser")
    public String registerUser(@RequestBody User user) throws ExecutionException, InterruptedException {
        return userService.createUser(user);
    }

    @GetMapping("/api/getUser")
    public User getUser(@RequestParam String id) throws ExecutionException, InterruptedException {
        return userService.getUser(id);
    }

    @PutMapping("/api/updateUser")
    public String updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    @DeleteMapping("/api/deleteUser")
    public String deleteUser(@RequestParam String id){
        return userService.deleteUser(id);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndPoint(){
        return ResponseEntity.ok("Working!");
    }
}
