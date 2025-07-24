package org.hackathon.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromNumber; // format: "whatsapp:‪+14155238886‬" for sandbox

    @PostConstruct
    public void init() {
        System.out.println("Twilio SID: " + accountSid);
        System.out.println("Twilio Token: " + authToken);
        Twilio.init(accountSid, authToken);
    }

    public void sendWhatsAppMessage(String to, String message) {
        Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(fromNumber),
                message
        ).create();
    }
}