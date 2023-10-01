package com.prompter.external.gpt.dto.response.gpt;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OpenAiApiResultResponse {

    private String title;

    private String summary;

    @JsonProperty(value = "summary_language")
    private String summaryLanguage;

    private String content;

    private String tags;

    @JsonProperty(value = "tags_language")
    private String tagsLanguage;

    private String adYn;
}
