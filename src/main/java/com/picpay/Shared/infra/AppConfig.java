package com.picpay.Shared.infra;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    // CONFIGURAÇÃO DO RESTTEMPLATE
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
