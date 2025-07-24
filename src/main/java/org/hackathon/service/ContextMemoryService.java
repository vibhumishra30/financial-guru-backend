package org.hackathon.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ContextMemoryService {

    // Map of phoneNumber -> list of past messages
    private final Map<String, Deque<String>> userContextMap = new HashMap<>();

    private static final int MAX_CONTEXT_SIZE = 5;

    public void saveUserMessage(String userId, String message) {
        Deque<String> history = userContextMap.computeIfAbsent(userId, k -> new LinkedList<>());
        if (history.size() >= MAX_CONTEXT_SIZE) {
            history.removeFirst(); // remove oldest
        }
        history.addLast(message); // add latest
    }

    public List<String> getUserHistory(String userId) {
        return new ArrayList<>(userContextMap.getOrDefault(userId, new LinkedList<>()));
    }

    public void clearHistory(String userId) {
        userContextMap.remove(userId);
    }
}
