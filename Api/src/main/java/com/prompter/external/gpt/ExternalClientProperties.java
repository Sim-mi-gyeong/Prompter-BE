package com.prompter.external.gpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
//@ConstructorBinding
@ConfigurationProperties("client")
public class ExternalClientProperties {

    private final OpenAiApi openAiApi;

    @Getter
    @AllArgsConstructor
    public static class OpenAiApi {
        private final String baseUrl;
    }
}
