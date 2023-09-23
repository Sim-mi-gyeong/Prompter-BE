package com.prompter.external.gpt;

import com.google.common.net.HttpHeaders;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableConfigurationProperties(ExternalClientProperties.class)
public class ExternalWebClient {

    public HttpClient httpClient(int connectTimeout, int readTimeout, int writeTimeout) {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(readTimeout))
                        .addHandlerLast(new WriteTimeoutHandler(writeTimeout))
                )
                .keepAlive(true)
                .wiretap(true)
                .compress(true);
    }

    @Bean
    public WebClient openAiApiWebClient(ExternalClientProperties externalClientProperties) {

//        final ConnectionProvider connectionProvider =
//                ConnectionProvider.builder("platform-client-connection-pool-fixed")
//                        .maxConnections(1_000)
//                        .pendingAcquireTimeout(Duration.ofMillis(30_000))
//                        .maxIdleTime(Duration.ofMillis(120))
//                        .build();
//
//        final TcpClient mainTcpClient = TcpClient.create(connectionProvider)
//                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 15_000)
//                .doOnConnected(conn -> conn
//                        .addHandlerLast(new ReadTimeoutHandler(
//                                120_000,
//                                TimeUnit.MILLISECONDS))
//                        .addHandlerLast(new WriteTimeoutHandler(
//                                500,
//                                TimeUnit.MILLISECONDS))
//                        .addHandler(new IdleStateHandler(
//                                2_000,
//                                500, 5000,
//                                TimeUnit.MILLISECONDS)));
//
//        final HttpClient httpClient = HttpClient.from(mainTcpClient)
//                .keepAlive(true)
//                .wiretap(true)
//                .compress(true);

        return WebClient.builder()
                .baseUrl(externalClientProperties.getOpenAiApi().getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(
                        new ReactorClientHttpConnector(httpClient(50000, 50000, 50000))
                )
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    @Bean
    public WebClient papagoApiWebClient(ExternalClientProperties externalClientProperties) {
        return WebClient.builder()
                .baseUrl(externalClientProperties.getPapaoApi().getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .defaultHeader("X-Naver-Client-Id", externalClientProperties.getPapaoApi().getClientId())
                .defaultHeader("X-Naver-Client-Secret", externalClientProperties.getPapaoApi().getClientSecret())
                .clientConnector(
                        new ReactorClientHttpConnector(httpClient(10000, 10000, 50000))
                )
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    @Bean
    public WebClient searchApiWebClient(ExternalClientProperties externalClientProperties) {
        return WebClient.builder()
                .baseUrl(externalClientProperties.getSearchApi().getBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("X-Naver-Client-Id", externalClientProperties.getSearchApi().getClientId())
                .defaultHeader("X-Naver-Client-Secret", externalClientProperties.getSearchApi().getClientSecret())
                .clientConnector(
                        new ReactorClientHttpConnector(httpClient(10000, 10000, 50000))
                )
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    @Bean
    public WebClient koWikipediaApiWebClient(ExternalClientProperties externalClientProperties) {
        return WebClient.builder()
                .baseUrl(externalClientProperties.getWikipediaApi().getKoBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(
                        new ReactorClientHttpConnector(httpClient(10000, 10000, 50000))
                )
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    @Bean
    public WebClient enWikipediaApiWebClient(ExternalClientProperties externalClientProperties) {
        return WebClient.builder()
                .baseUrl(externalClientProperties.getWikipediaApi().getEnBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(
                        new ReactorClientHttpConnector(httpClient(10000, 10000, 50000))
                )
                .filter(logRequest())
                .filter(logResponse())
                .build();
    }

    // 클라이언트 필터 등록
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("externalApiWebClient Request: url: {}, method: {}", clientRequest.url(), clientRequest.method());
            return Mono.just(clientRequest);
        });
    }

    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            logStatus(clientResponse);
            return logBody(clientResponse);
        });
    }

    private static void logStatus(ClientResponse clientResponse) {
        HttpStatus httpStatus = (HttpStatus) clientResponse.statusCode();
        log.info("externalApiWebClient status code {} ({})", httpStatus.value(), httpStatus.getReasonPhrase());
    }

    private static Mono<ClientResponse> logBody(ClientResponse clientResponse) {
        if (clientResponse.statusCode().is4xxClientError() || clientResponse.statusCode().is5xxServerError()) {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(body -> {
                        log.info("externalApiWebClient response body {}", body);
                        return Mono.just(clientResponse);
                    });
        } else {
            return Mono.just(clientResponse);
        }
    }
}