package com.prompter.external.gpt.dto.response.search;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchDictionaryResponse {

    private String lastBuildDate;
    private int total;
    private int start;
    private int display;

    private List<Item> items;

    @Getter
    public static class Item {
        private String title;
        private String link;
        private String description;
        private String thumbnail;
    }
}
