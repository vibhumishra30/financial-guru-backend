package org.hackathon.controller;

import lombok.AllArgsConstructor;
import org.hackathon.entity.LoginRequest;
import org.hackathon.entity.SubscriptionRequest;
import org.hackathon.entity.User;
import org.hackathon.repository.UserRepository;
import org.hackathon.scheduler.Scheduler;
import org.hackathon.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final Scheduler scheduler;
    private final BCryptPasswordEncoder passwordEncoder;

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

    @PostMapping("/subscription")
    public String setSubscriptionFlag(@RequestBody SubscriptionRequest request){
        return userService.setSubscriptionStatus(request.getEmail());
    }

}

