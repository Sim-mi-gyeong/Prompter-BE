package com.prompter.external;

import lombok.Getter;

import java.util.List;

@Getter
public class OpenAiApiSummaryResponse {

    private String summary;
//    private List<String> tag;
    private String tag;   // , 로 구분된 문자열
}
