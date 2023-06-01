package com.eliteworks.eliteworks.controllers;

import com.eliteworks.eliteworks.config.EmailSender;
import com.eliteworks.eliteworks.models.User;
import com.eliteworks.eliteworks.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Controller
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

    @PostMapping("/api/loginUser")
    public String loginUser(@RequestBody User user)throws ExecutionException,InterruptedException{
        return userService.loginUser(user);
    }

    @PostMapping("/api/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws ExecutionException, InterruptedException {
        String response = userService.forgotPassword(email);
        String userId = userService.getIdOfUser(email);

        if (!response.startsWith("Invalid")) {
            String resetUrl = "http://localhost:8080/api/reset-password?token=" + response + "&id=" + userId;
             EmailSender.sendPasswordResetEmail(email, resetUrl);
            return ResponseEntity.ok(resetUrl);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/api/reset-password")
    public String resetPassword(@RequestParam String token,
                                @RequestParam String id, Model model) throws ExecutionException, InterruptedException {
        model.addAttribute("id",id);
        model.addAttribute("token",token);
        return "reset";
    }

    @PostMapping("/api/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestParam String id,
                                                @RequestParam String password) throws ExecutionException, InterruptedException {

        String response = userService.resetPassword(token, id, password);

        if (response.equals("Success")) {
            return ResponseEntity.ok("Password reset successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
