package com.prompter.external.gpt.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpenAiApiSummaryRequest {

    private String text;

    public static OpenAiApiSummaryRequest from(String text) {
        return OpenAiApiSummaryRequest.builder().text(text).build();
    }
}
