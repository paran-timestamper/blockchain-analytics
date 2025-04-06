package com.timestamper.blockchain_analytics.dto.main.news;

import lombok.Data;

@Data
public class NewsDto {
    private String title;
    private String description;
    private String content;
    private String url;
    private String image;
    private String publishedAt;
    private SourceDto source;
}
