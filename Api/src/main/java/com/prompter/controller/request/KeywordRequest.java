package com.prompter.controller.request;

import lombok.Getter;

import java.util.List;

@Getter
public class KeywordRequest {
    private List<String> tags;
}
