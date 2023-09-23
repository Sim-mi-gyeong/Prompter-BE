package com.prompter.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Getter
@Builder
public class ResultResponse {

    private String title;
    private final String summaryContent;
    private List<String> tags;
    private  List<Keyword> keywords;
    private double adsPercent;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keyword {
        private String keyword;
        private String wikiContent;
        private String wikiUrl;
    }

    public static ResultResponse of(String title, String summaryContent, List<String> tags, List<Keyword> keywords, double adsPercent) {
        return ResultResponse
                .builder()
                .title(title)
                .summaryContent(summaryContent)
                .tags(tags)
                .keywords(keywords)
                .adsPercent(adsPercent)
                .build();
    }
}
