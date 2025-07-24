package org.hackathon.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hackathon.entity.User;
import org.hackathon.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public String setSubscriptionStatus(String email){

        Optional<User> receivedUser = userRepository.findByEmailIgnoreCase(email);
        User user = receivedUser.orElse(null);
        if(user != null){
            user.setSubscription(!user.getSubscription());
            userRepository.save(user);
            if(user.getSubscription()) return "Subscribed!";
            else return "Unsubscribed!";
        }
        else return "No such User!";
    }

}
