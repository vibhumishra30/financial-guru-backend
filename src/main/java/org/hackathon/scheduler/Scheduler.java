package org.hackathon.scheduler;

import org.hackathon.service.ContactNumberService;
import org.hackathon.service.NewsService;
import org.hackathon.service.WhatsappService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import java.util.List;

@Component
@AllArgsConstructor
public class Scheduler {

    @Autowired
    private NewsService newsService;    

    private final WhatsappService whatsAppService;
    private final ContactNumberService contactNumberService;

    // Run every day at 7 AM IST
    @Scheduled(cron = "0 0 7 * * *", zone = "Asia/Kolkata")
    public void sendDailyMessage() {
        String content = "Good Morning! ☀️ Here's your daily update/message.";
        String newsletterMessage = newsService.getFormattedNewsletterForWhatsApp();
        List<String> numbers = contactNumberService.getNumbers();

        for (String number : numbers) {
            whatsAppService.sendMessage(number, newsletterMessage);
        }
    }
}