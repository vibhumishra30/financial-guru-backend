package org.hackathon.controller;

import org.hackathon.entity.User;
import org.hackathon.models.UserType;
import org.hackathon.repository.UserRepository;
import org.hackathon.service.*;
import org.hackathon.util.BotResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

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
    @Autowired
    private UserRepository userRepository;

    public String extractPhoneNumber(String input) {
        if (input == null || !input.startsWith("whatsapp:")) return null;
        return input.replace("whatsapp:", "").trim();
    }
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

        long start = System.nanoTime();
        // 2. Translate user message to English
        String translatedInput = translationService.translateToEnglish(trimmedBody);

        Optional<User> userOpt = userRepository.findByContactNumber(extractPhoneNumber(from));
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok("❗ You’re not registered. Please sign up to use this service.");
        }

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
        long end = System.nanoTime();
        double durationInSeconds = (end - start) / 1_000_000_000.0;
        System.out.println("Gemini time taken: " + durationInSeconds + " seconds");

        long startT = System.nanoTime();

        // 7. Send reply back via Twilio
        twilioService.sendWhatsAppMessage(from, reply);

        long endT = System.nanoTime();
        double durationInSecondsT = (endT - startT) / 1_000_000_000.0;
        System.out.println("Time taken in twilio : " + durationInSecondsT + " seconds");
        return ResponseEntity.ok("OK");
    }


    @PostMapping("/twilio/status")
    public ResponseEntity<Void> messageStatus(@RequestParam Map<String, String> statusParams) {
        System.out.println("📦 Delivery Update: " + statusParams);
        return ResponseEntity.ok().build();
    }
}