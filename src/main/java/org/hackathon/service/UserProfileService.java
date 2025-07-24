package org.hackathon.service;

import org.hackathon.models.UserType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserProfileService {

    private final Map<String, UserType> userTypeMap = new HashMap<>();

    public void setUserType(String userId, UserType type) {
        userTypeMap.put(userId, type);
    }

    public UserType getUserType(String userId) {
        return userTypeMap.getOrDefault(userId, UserType.UNKNOWN);
    }
}
