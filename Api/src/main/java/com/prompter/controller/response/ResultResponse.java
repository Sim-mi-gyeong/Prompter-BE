package com.prompter.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
public class ResultResponse {

    private final String summaryContent;
    private List<String> tags;
    private List<Keyword> keywords;
    private int adsPercent;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keyword {
        private String keyword;
        private String wikiContent;
        private String wikiUrl;
    }

    public static ResultResponse of(String summaryContent, List<String> tags, List<Keyword> keywords, int adsPercent) {
        return ResultResponse
                .builder()
                .summaryContent(summaryContent)
                .tags(tags)
                .keywords(keywords)
                .adsPercent(adsPercent)
                .build();
    }
}