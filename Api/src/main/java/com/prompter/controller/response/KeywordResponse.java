package com.prompter.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordResponse {

    private List<Keyword> keywordx;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keyword {
        private String keyword;
        private String wikiContent;
        private String wikiUrl;
    }
}
