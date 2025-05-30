package org.example.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service-public", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f
                                .rewritePath("/api/auth/(?<segment>.*)", "/api/auth/${segment}")
                                .addRequestHeader("X-Request-Source", "gateway")
                        )
                        .uri("http://user-service:8081")
                )
                .route("user-service-secure", r -> r
                        .path("/api/users/**")
                        .filters(f -> f
                                .addRequestHeader("X-Request-Source", "gateway")
                                .rewritePath("/api/users/(?<segment>.*)", "/api/users/${segment}")
                        )
                        .uri("http://user-service:8081")
                )
                .route("order-service", r -> r
                        .path("/api/orders/**")
                        .filters(f -> f
                                .addRequestHeader("X-Request-Source", "gateway")
                                .rewritePath("/api/orders/(?<segment>.*)", "/api/orders/${segment}")
                        )
                        .uri("http://order-service:8082")
                )
                .build();
    }
}
