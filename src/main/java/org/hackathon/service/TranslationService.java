package org.hackathon.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
public class TranslationService {

    private final Translate translate;

    public TranslationService(@Value("${google.cloud.credentials.location}") String credentialsLocation) throws IOException {
        InputStream credentialsStream = new ClassPathResource("translation.json").getInputStream();
        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
        this.translate = TranslateOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    public String translateToEnglish(String input) {
        return translate.translate(
                input,
                Translate.TranslateOption.targetLanguage("en"),
                Translate.TranslateOption.model("nmt")
        ).getTranslatedText();
    }

    public String detectLanguage(String input) {
        return translate.detect(input).getLanguage();
    }
}