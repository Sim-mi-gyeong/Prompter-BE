package com.prompter.common;


import lombok.Getter;

@Getter
public enum LanguageCode {
    KO(0, "ko"),
    EN(1, "en");

    LanguageCode(final int code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final int code;
    private final String desc;
}
