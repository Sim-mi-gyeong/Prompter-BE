package com.prompter.controller.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummaryResponse {

    private String summaryContent;
    //    private List<String> tags;
    //    private List<Stream<Object>> tags;

    //    public static SummaryResponse of(String summaryContent, List<Stream<Object>> tags) {
    //        return SummaryResponse
    //                .builder()
    //                .summaryContent(summaryContent)
    ////                .tags(tags)
    //                .build();
    //    }

    public static SummaryResponse of(String summaryContent) {
        return SummaryResponse.builder().summaryContent(summaryContent).build();
    }
}
