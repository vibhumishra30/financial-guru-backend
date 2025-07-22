package org.hackathon.controller;

import org.hackathon.entity.LoginRequest;
import org.hackathon.entity.User;
import org.hackathon.repository.UserRepository;
import org.hackathon.scheduler.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public String signUp(@RequestBody User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already registered!";
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully!";
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .map(user -> {
                    if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                        return "Login successful!";
                    } else {
                        return "Incorrect password!";
                    }
                })
                .orElse("User not found!");
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/user/delete")
    public void deleteUser(@RequestBody Long id){
        userRepository.deleteById(id);
    }

    @PostMapping("/send/manual")
    public void sendManualMessage(){
        scheduler.sendDailyMessage();
    }

}

