package com.prompter.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CrawlingResponse {
    private final String content;

    public static CrawlingResponse from(String content){
        return CrawlingResponse.builder().content(content).build();
    }
}
