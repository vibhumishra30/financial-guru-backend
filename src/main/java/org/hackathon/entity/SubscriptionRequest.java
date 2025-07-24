package org.hackathon.entity;

import lombok.Data;

@Data
public class SubscriptionRequest {
    private String email;
    private Boolean subscription;
}
