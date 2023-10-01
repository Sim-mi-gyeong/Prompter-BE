package com.prompter.controller.request;


import lombok.Getter;

@Getter
public class TranslationRequest {
    private String srcLanguage;
    private String dstLanguage;
    private String text;
}
