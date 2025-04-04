package com.timestamper.blockchain_analytics.dto.news;

import lombok.Data;

import java.util.List;

@Data
public class NewsResponseDto {
    private int totalArticles;
    private List<NewsDto> articles;
}
