package org.hackathon.entity;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NewsApiResponse {
    
    private String status;
    private int totalResults;
    private List<NewsArticle> articles;
}
