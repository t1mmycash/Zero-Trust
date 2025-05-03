package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.WebFilter;

@Configuration
public class GatewayFilterConfig {

    @Bean
    public WebFilter gatewayHeaderFilter() {
        return (exchange, chain) -> {
            // Проверяем, что запрос пришел через Gateway
            String header = exchange.getRequest().getHeaders().getFirst("X-Request-Source");
            if (!"gateway".equals(header)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        };
    }
}
