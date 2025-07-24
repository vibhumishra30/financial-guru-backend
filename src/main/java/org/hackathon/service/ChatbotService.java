package org.hackathon.service;

import org.hackathon.models.UserType;
import org.hackathon.util.BotResponseUtil;
import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

    private final ContextMemoryService contextMemoryService;
    private final UserProfileService userProfileService;

    public ChatbotService(ContextMemoryService contextMemoryService, UserProfileService userProfileService) {
        this.contextMemoryService = contextMemoryService;
        this.userProfileService = userProfileService;
    }

    public UserType detectUserType(String translatedInput) {
        String clean = translatedInput.trim().toLowerCase();

        if (clean.contains("student") || clean.contains("i am a student")) {
            return UserType.STUDENT;
        } else if (clean.contains("farmer") || clean.contains("i am a farmer")) {
            return UserType.FARMER;
        } else if (clean.contains("unemployed woman") || clean.contains("i am unemployed woman")) {
            return UserType.UNEMPLOYED_WOMAN;
        }

        return UserType.UNKNOWN;
    }


    public String handleIncomingMessage(String message, String fromUser) {
        String cleanMsg = message.trim().toLowerCase();

        switch (cleanMsg) {
            case "i am a student":
            case "student":
                userProfileService.setUserType(fromUser, UserType.STUDENT);
                return BotResponseUtil.withIntro("🎓 Noted! You are a student. We'll suggest student-focused loans and scholarships.");

            case "i am a farmer":
            case "farmer":
                userProfileService.setUserType(fromUser, UserType.FARMER);
                return BotResponseUtil.withIntro("🌾 Got it! You're a farmer. You’ll now get crop insurance and PM Kisan suggestions.");

            case "i am unemployed woman":
            case "unemployed woman":
            case "woman":
                userProfileService.setUserType(fromUser, UserType.UNEMPLOYED_WOMAN);
                return BotResponseUtil.withIntro("👩‍🌾 Thanks! We’ll prioritize self-employment training and subsidies for women.");

            case "my profile":
                UserType type = userProfileService.getUserType(fromUser);
                return BotResponseUtil.withIntro("👤 Your registered type is: " + type.name());

            case "help":
            case "menu":
                return BotResponseUtil.withIntro("""
                        🗂️ Main Menu:
                        1️⃣ Loan Info
                        2️⃣ Insurance
                        3️⃣ Govt Schemes
                        4️⃣ Speak to Guru
                        Please type the number (1–4).
                        """);

            case "1":
            case "loan info":
                return BotResponseUtil.withIntro("""
                        💸 Loan Info:
                        - Student Loan: low interest, no collateral
                        - Farmer Loan: seasonal repayment
                        - Home Loan: up to ₹40L, 6.5% interest
                        Want help applying? Type 'apply loan'
                        """);

            case "2":
            case "insurance":
                return BotResponseUtil.withIntro("""
                        📜 Insurance Info:
                        - Health Insurance
                        - Life Insurance
                        - Crop Insurance
                        Want to compare policies? Type 'compare insurance'
                        """);

            case "3":
            case "govt schemes":
                return BotResponseUtil.withIntro("""
                        🏛️ Govt Schemes:
                        - PM Kisan Yojana
                        - Mudra Loans
                        - Skill Development Training
                        Want help finding eligible schemes? Type 'check eligibility'
                        """);

            case "4":
            case "speak to guru":
                return null; // Fallback to Gemini

            default:
                return null; // Unknown, fallback to Gemini
        }
    }
}