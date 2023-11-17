package com.mes.techdebt.config.security;

import com.azure.spring.cloud.autoconfigure.aad.AadResourceServerWebSecurityConfigurerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class AadOAuth2ResourceServerSecurityConfig extends AadResourceServerWebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.info("Activating Azure AD Configuration");
        super.configure(http);

        http.cors();

        // This is related to calls from another internal api endpoint
        http.csrf().ignoringAntMatchers("/api/v1/location/**");

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.headers().frameOptions().disable();

        http.headers().httpStrictTransportSecurity()
                .maxAgeInSeconds(0)
                .includeSubDomains(true);

        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointHandler())
                .accessDeniedHandler(accessDeniedHandler());

        http.authorizeRequests()
                .antMatchers(
                        "/swagger-ui.html**",
                        "/swagger-ui/index.html#/**",
                        "/swagger-ui/**",
                        "/swagger-ui**",
                        "/swagger-resources/**",
                        "/api-docs/**",
                        "/api-docs**",
                        "/v3/api-docs/**",
                        "/configuration/security",
                        "/webjars/**",
                        "/health")
                .permitAll()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPointHandler() {
        return new CustomAuthenticationErrorHandler();
    }
}