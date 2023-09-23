package com.prompter.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TranslationResponse {
    private String srcLanguage;
    private String dstLanguage;
    private String text;

    public static TranslationResponse of(String srcLanguage, String dstLanguage, String text) {
        return TranslationResponse.builder()
                .srcLanguage(srcLanguage)
                .dstLanguage(dstLanguage)
                .text(text)
                .build();
    }
}
