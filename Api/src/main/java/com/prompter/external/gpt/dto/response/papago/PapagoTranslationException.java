package com.prompter.external.gpt.dto.response.papago;


import lombok.Getter;

@Getter
public class PapagoTranslationException extends RuntimeException {
    private String errorCode;
    private String errorMessage;

    public PapagoTranslationException(PapagoTranslationErrorResponse response) {
        this.errorCode = response.getErrorCode();
        this.errorMessage = response.getErrorMessage();
    }
}
