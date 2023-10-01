package com.prompter.external.gpt.dto.response.papago;


import lombok.Getter;

@Getter
public class PapagoTranslationErrorResponse {
    private String errorCode;
    private String errorMessage;
}
