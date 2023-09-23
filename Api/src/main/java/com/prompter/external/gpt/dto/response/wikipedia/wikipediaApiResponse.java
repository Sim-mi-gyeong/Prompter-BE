package com.prompter.external.gpt.dto.response.wikipedia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@Getter
public class wikipediaApiResponse {
    /*
        {
            "batchcomplete": "",
            "query": {
                "pages": {
                    "261765": {
                        "pageid": 261765,
                        "ns": 0,
                        "title": "영양",
                        "extract": "영양은 다음과 같은 뜻이 있다.\n\n영양(營養)은 생물이 살아가는 데에 필요한 물질을 말한다.\n영양(羚羊)은 소과에 속하는 야생동물 가운데 일부를 부르는 말이다.\n영양군(英陽郡)은 대한민국 경상북도에 있는 행정 구역이다.\n영양읍(英陽邑)은 대한민국 경상북도 영양군에 있는 행정 구역이다."
                    }
                }
            }
        }
     */
    private String batchcomplete;
    private Query query;

    @Getter
    public class Query {

//        private Pages pages;
        Map<String, PageData> pages;

        @Getter
        public static class PageData {

            @JsonProperty(value = "pageid")
            private long pageId;

            private int ns;

            private String title;

            private String extract;
        }

//        @Getter
//        public static class Pages {
//
//            private Map<String, PageData> pageData;
//
//            public static class PageData {
//
//                @JsonProperty(value = "pageid")
//                private long pageId;
//
//                private int ns;
//
//                private String title;
//
//                private String extract;
//            }
    }
}
