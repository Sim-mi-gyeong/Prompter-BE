package com.prompter.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CrawlingResponse {
    private final String text;

    public static CrawlingResponse from(String text){
        return CrawlingResponse.builder().text(text).build();
    }
}
