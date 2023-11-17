package com.mes.techdebt.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfiguration {

    @Value("${wiretap.enabled}")
    private boolean wireTapEnabled;

    @Value("${tcp.connectionTimeOutMillis}")
    private int connectionTimeoutMillis;

    @Value("${location_data.url}")
    private String url;

    @Bean
    public WebClient defaultWebClient(){
        return this.createWebClient();
    }

    private WebClient createWebClient(){
        ConnectionProvider connectionProvider = ConnectionProvider
                .builder("defaultWebClient")
                .fifo()
                .maxConnections(1000)
                .maxIdleTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .maxLifeTime(Duration.ofSeconds(60))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .wiretap(wireTapEnabled)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMillis)
                .doOnConnected(
                        c -> c.addHandlerLast(new ReadTimeoutHandler(connectionTimeoutMillis, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(connectionTimeoutMillis, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new IdleStateHandler(connectionTimeoutMillis, connectionTimeoutMillis,
                                        6000, TimeUnit.MILLISECONDS))).compress(true).followRedirect(true);

        WebClient client = WebClient.builder()
                .baseUrl(url)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(logRequest())
                .build();
        return client;
    }

    // This method returns filter function which will log request data
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }
}
