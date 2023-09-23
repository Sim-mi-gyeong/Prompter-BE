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

    private List<Keyword> keywords;

    @Getter
    public static class Keyword {
        private final String keyword;
        private final String wikiContent;
        private final String wikiUrl;

        public Keyword(ResultResponse.Keyword response) {
            this.keyword = response.getKeyword();
            this.wikiContent = response.getWikiContent();
            this.wikiUrl = response.getWikiUrl();
        }
    }
}
