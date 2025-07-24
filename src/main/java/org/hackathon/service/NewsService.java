package org.hackathon.service;

import java.util.List;

import org.hackathon.entity.NewsApiResponse;
import org.hackathon.entity.NewsArticle;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NewsService {
    @Value("${newsapi.apiKey}")
    private String apiKey;

    private static final String API_URL_TEMPLATE =
        "https://newsapi.org/v2/top-headlines?category=business&country=us&apiKey=%s";

        public String getFormattedNewsletterForWhatsApp() {
        String url = String.format(API_URL_TEMPLATE, apiKey);
        RestTemplate restTemplate = new RestTemplate();

        NewsApiResponse response = restTemplate.getForObject(url, NewsApiResponse.class);
        List<NewsArticle> articles = response.getArticles();

        if (articles == null || articles.isEmpty()) {
            return "⚠️ No business news available right now.";
        }

        StringBuilder msg = new StringBuilder("🗞 *Top Financial News Today:*\n\n");

        for (int i = 0; i < Math.min(5, articles.size()); i++) {
            NewsArticle article = articles.get(i);
            msg.append("🔹 *")
               .append(article.getTitle())
               .append("*\n")
               .append(article.getDescription() != null ? article.getDescription() : "")
               .append("\n🔗 ")
               .append(article.getUrl())
               .append("\n\n");
        }

        return msg.toString();
    }
}
