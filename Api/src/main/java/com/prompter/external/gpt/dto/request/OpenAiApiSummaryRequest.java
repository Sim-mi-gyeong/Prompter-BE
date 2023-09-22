package com.prompter.external.gpt.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpenAiApiSummaryRequest {

    private String text;
    private int type;

    public static OpenAiApiSummaryRequest of(String text, int type) {
        return OpenAiApiSummaryRequest.builder().text(text).type(type).build();
    }
}
