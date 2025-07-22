package org.hackathon.scheduler;

import org.hackathon.service.ContactNumberService;
import org.hackathon.service.WhatsappService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Scheduler {

    private final WhatsappService whatsAppService;
    private final ContactNumberService contactNumberService;

    public Scheduler(WhatsappService service, ContactNumberService contactNumberService) {
        this.whatsAppService = service;
        this.contactNumberService = contactNumberService;
    }

    // Run every day at 7 AM IST
    @Scheduled(cron = "0 0 7 * * *", zone = "Asia/Kolkata")
    public void sendDailyMessage() {
        String content = "Good Morning! ☀️ Here's your daily update/message.";
        List<String> numbers = contactNumberService.getNumbers();

        for (String number : numbers) {
            whatsAppService.sendMessage(number, content);
        }
    }
}