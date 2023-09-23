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
    private final PapaoApi papaoApi;

    @Getter
    @AllArgsConstructor
    public static class OpenAiApi {
        private final String baseUrl;
    }

    @Getter
    @AllArgsConstructor
    public static class PapaoApi {
        private final String baseUrl;
        private final String clientId;
        private final String clientSecret;
    }
}
