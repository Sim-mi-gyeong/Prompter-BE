package com.prompter.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SummaryResponse {

    private final String summaryContent;
//    private final String[] tags;
    private final String tags;

    public static SummaryResponse of(String summaryContent, String tags) {
        return SummaryResponse.builder()
                .summaryContent(summaryContent)
                .tags(tags)
                .build();
    }
}
