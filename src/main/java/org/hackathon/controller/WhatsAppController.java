package org.hackathon.controller;

import org.hackathon.models.UserType;
import org.hackathon.service.*;
import org.hackathon.util.BotResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class WhatsAppController {

    @Autowired
    private ChatbotService chatbotService;

    @Autowired
    private TranslationService translationService;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private TwilioService twilioService;

    @Autowired
    private ContextMemoryService contextMemoryService;

    @Autowired
    private UserProfileService userProfileService;

    @PostMapping("/whatsapp-webhook")
    public ResponseEntity<String> receiveMessage(@RequestParam("Body") String body,
                                                 @RequestParam("From") String from) {

        String trimmedBody = body.trim();
        String reply;

        // 1. Check for predefined keyword or menu command
        reply = chatbotService.handleIncomingMessage(trimmedBody, from);
        if (reply != null) {
            twilioService.sendWhatsAppMessage(from, reply);
            return ResponseEntity.ok("OK");
        }

        // 2. Translate user message to English
        String translatedInput = translationService.translateToEnglish(trimmedBody);

        // 3. Check for user type declaration in translated message
        UserType guessedUserType = chatbotService.detectUserType(translatedInput);
        if (guessedUserType != UserType.UNKNOWN) {
            userProfileService.setUserType(from, guessedUserType);
            reply = BotResponseUtil.withIntro("✅ User type detected and saved as: " + guessedUserType.name());
            twilioService.sendWhatsAppMessage(from, reply);
            return ResponseEntity.ok("OK");
        }

        // 4. Store and fetch conversation history for context
        contextMemoryService.saveUserMessage(from, translatedInput);
        var history = contextMemoryService.getUserHistory(from);

        // 5. Build contextual Gemini prompt
        String prompt = "User Type: " + userProfileService.getUserType(from).name() + "\n";
        for (String past : history) {
            prompt += "User: " + past + "\n";
        }

        // 6. Get response from Gemini and format with bot identity
        String geminiReply = geminiService.getReplyFromGemini(prompt);
        reply = BotResponseUtil.withIntro(geminiReply);

        // 7. Send reply back via Twilio
        twilioService.sendWhatsAppMessage(from, reply);
        return ResponseEntity.ok("OK");
    }


    @PostMapping("/twilio/status")
    public ResponseEntity<Void> messageStatus(@RequestParam Map<String, String> statusParams) {
        System.out.println("📦 Delivery Update: " + statusParams);
        return ResponseEntity.ok().build();
    }
}