package org.hackathon.service;

import lombok.AllArgsConstructor;
import org.hackathon.entity.SubscriptionRequest;
import org.hackathon.entity.User;
import org.hackathon.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public void setSubscriptionStatus(SubscriptionRequest request){
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        user.get().setSubscription(request.getSubscription());
        userRepository.saveAndFlush(user.get());
    }

}
