package com.prompter.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SummaryResponse {

    private final String summaryContent;
    private final List<String> tags;

    public static SummaryResponse of(String summaryContent, List<String> tags) {
        return SummaryResponse
                .builder()
                .summaryContent(summaryContent)
                .tags(tags)
                .build();
    }
}
