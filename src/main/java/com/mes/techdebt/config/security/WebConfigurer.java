package com.mes.techdebt.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;
import java.util.List;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebConfigurer implements WebMvcConfigurer {

    @Value("${cors.allowed_origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET, POST, DELETE, OPTIONS, PUT", "PATCH")
                .allowedHeaders("X-PINGOTHER","Access-Control-Allow-Origin","Origin","Authorization",
                        "X-Requested-With","X-HTTP-Method-Override", "X-XSRF-TOKEN",
                        "Content-Type","Accept","X-Auth-Token","Cache-Control", "x-capi-version")
                .exposedHeaders("Access-Control-Expose-Headers", "Authorization", "Cache-Control",
                        "Content-Type", "Access-Control-Allow-Origin", "X-XSRF-TOKEN",
                        "Access-Control-Allow-Headers", "Origin",
                        "X-Requested-With","X-HTTP-Method-Override", "Accept", "x-capi-version")
                .maxAge(60000)
                .allowCredentials(true);
        log.info("Cors Registry: {}", registry);
        log.info("Cors allowed origins: {}", allowedOrigins);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(allowedOrigins));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "OPTIONS", "PUT", "PATCH"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        log.info("Cors allowed origins: {}", allowedOrigins);
        return source;
    }
}
