package org.hackathon.controller;

import org.hackathon.service.GeminiService;
import org.hackathon.service.TwilioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WhatsAppController {

    private final GeminiService geminiService;
    private final TwilioService twilioService;

    public WhatsAppController(GeminiService geminiService, TwilioService twilioService) {
        this.geminiService = geminiService;
        this.twilioService = twilioService;
    }

    @PostMapping("/whatsapp-webhook")
    public ResponseEntity<String> receiveMessage(@RequestParam("Body") String body,
                                                 @RequestParam("From") String from) {

        // Forward message to Gemini
        String reply = geminiService.getReplyFromGemini(body);

        System.out.println("Starting message");
        // Send reply via Twilio
        twilioService.sendWhatsAppMessage(from, reply);
        System.out.println("Ending message");

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/twilio/status")
    public ResponseEntity<Void> messageStatus(@RequestParam Map<String, String> statusParams) {
        System.out.println("📦 Delivery Update: " + statusParams);
        return ResponseEntity.ok().build();
    }

}