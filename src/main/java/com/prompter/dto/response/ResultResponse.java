package com.prompter.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ResultResponse {

    private final String summaryContent;
    private List<String> tags;
    private List<Word> words;
    private boolean isAds;

    /*
        type Word = {
       text:string;
       value:number;
        }
     */

    @Getter
    @Builder
    public static class Word {
        private String text;
        private long number;
    }

    public static ResultResponse of(String summaryContent, List<String> tags, List<Word> words, boolean isAds) {
        return ResultResponse.builder()
                .summaryContent(summaryContent)
                .tags(tags)
                .words(words)
                .isAds(isAds)
                .build();
    }
}
