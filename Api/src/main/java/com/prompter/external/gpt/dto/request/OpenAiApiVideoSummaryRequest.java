package com.prompter.external.gpt.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpenAiApiVideoSummaryRequest {
    private String text;
    private int type;

    public static OpenAiApiVideoSummaryRequest of(String url, int type) {
        return OpenAiApiVideoSummaryRequest.builder()
                .text(url)
                .type(type)
                .build();
    }
}
