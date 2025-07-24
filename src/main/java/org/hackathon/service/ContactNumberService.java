package org.hackathon.service;

import org.hackathon.entity.User;
import org.hackathon.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ContactNumberService {

    private final UserRepository userRepository;

    public ContactNumberService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<String> getNumbers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .filter(user -> user.getSubscription())
                .map(User::getContactNumber)
                .filter(number -> number != null && !number.trim().isEmpty())
                .collect(Collectors.toList());
    }
}
