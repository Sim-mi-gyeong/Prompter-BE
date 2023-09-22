package com.prompter.external.gpt.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OpenAiApiTextSummaryRequest {

    private String text;
    private int type;

    public static OpenAiApiTextSummaryRequest of(String text, int type) {
        return OpenAiApiTextSummaryRequest.builder().text(text).type(type).build();
    }
}
