package com.eliteworks.eliteworks.controllers;

import com.eliteworks.eliteworks.config.EmailSender;
import com.eliteworks.eliteworks.models.User;
import com.eliteworks.eliteworks.services.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Controller
public class UserController {
    @Autowired
    public UserService userService;
    public FirebaseAuth firebaseAuth;

    public UserController(UserService userService){
        this.userService = userService;
        this.firebaseAuth = FirebaseAuth.getInstance() ;
    }

    @PostMapping("/api/registerUser")
    public ResponseEntity<String> registerUser(@RequestBody User user) throws ExecutionException, InterruptedException {
        try{
            firebaseAuth.getUserByEmail(user.getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Already exists");
        } catch (FirebaseAuthException e) {

        }

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setDisplayName(user.getName());

        try{
            UserRecord userRecord = firebaseAuth.createUser(request);
            UserRecord.UpdateRequest updateRequest = new UserRecord.UpdateRequest(userRecord.getUid())
                    .setEmailVerified(true);
            firebaseAuth.updateUser(updateRequest);
            String response = userService.createUser(user);
            return ResponseEntity.ok(response);
        }catch (FirebaseAuthException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user");
        }
    }

    @GetMapping("/api/getUser")
    public ResponseEntity<User> getUser(@RequestParam String id) throws ExecutionException, InterruptedException {
        User user =userService.getUser(id);

        if(user==null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        else return ResponseEntity.ok(user);
    }

    @PutMapping("/api/updateUser")
    public ResponseEntity<String> updateUser(@RequestBody User user){
        String response =  userService.updateUser(user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam String id){
        String response = userService.deleteUser(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndPoint(){
        return ResponseEntity.ok("Working!");
    }

    @PostMapping("/api/loginUser")
    public ResponseEntity<String> loginUser(@RequestBody User user)throws ExecutionException,InterruptedException{
        String response = userService.loginUser(user);
        if(response.equals("Invalid credentials") || response.equals("User does not exist")) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) throws ExecutionException, InterruptedException {
        String response = userService.forgotPassword(email);
        String userId = userService.getIdOfUser(email);

        if (!response.startsWith("Invalid")) {
            String resetUrl = "https://eliteworks.azurewebsites.net/api/reset-password?token=" + response + "&id=" + userId;
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
