package org.hackathon.util;

public class BotResponseUtil {

    private static final String BOT_INTRO = "🤖 Hello! I'm your FourOFourNotFound personal financial guru.\n";

    public static String withIntro(String response) {
        return BOT_INTRO + response;
    }
}