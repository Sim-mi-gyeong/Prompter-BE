package com.prompter.external.gpt.dto.response.papago;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PapagoTranslationResponse {

    private Message message;

    @Getter
    public static class Message {
        @JsonProperty(value = "@type")
        private String type;

        @JsonProperty(value = "@service")
        private String service;

        @JsonProperty(value = "@version")
        private String version;

        private Result result;

        @Getter
        public static class Result {
            private String srcLangType;
            private String tarLangType;
            private String translatedText;
            private String engineType;
        }
    }
}
