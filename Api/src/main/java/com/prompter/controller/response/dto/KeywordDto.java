package com.prompter.controller.response.dto;


import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordDto {

    private List<Keyword> keywords;

    @Getter
    @Setter
    @Builderㄸ
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keyword {

        private String keyword;
        private String wikiContent;
        private String wikiUrl;
    }
}
