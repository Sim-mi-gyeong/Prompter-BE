package com.prompter.external.gpt.dto.response.gpt;

import lombok.Getter;

@Getter
public class OpenAiApiResultResponse {
    private String title;
    private String summary;
    private String content;
    private String tags;
    private String adYn;
}
