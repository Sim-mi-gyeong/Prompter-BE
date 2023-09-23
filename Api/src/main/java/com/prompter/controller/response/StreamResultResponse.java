package com.prompter.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Getter
@Builder
public class StreamResultResponse {

    private String title;
    private final String summaryContent;
    private List<String> tags;
    private List<Flux<Keyword>> keywords;
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

    public static StreamResultResponse of(String title, String summaryContent,
                                          List<String> tags, List<Flux<Keyword>> keywords, double adsPercent) {
        return StreamResultResponse
                .builder()
                .title(title)
                .summaryContent(summaryContent)
                .tags(tags)
                .keywords(keywords)
                .adsPercent(adsPercent)
                .build();
    }
}
