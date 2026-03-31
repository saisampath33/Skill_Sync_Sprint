package com.skillsync.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

    /**
     * Fallback routing for the Circuit Breaker. 
     * Handles both GET and POST failure redirects.
     */
    @Bean
    public RouterFunction<ServerResponse> fallbackRoute() {
        return RouterFunctions
            .route(GET("/fallback"), this::handleFallback)
            .andRoute(POST("/fallback"), this::handleFallback);
    }

    private Mono<ServerResponse> handleFallback(org.springframework.web.reactive.function.server.ServerRequest request) {
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(Map.of(
                "status", "Service Temporarily Unavailable",
                "message", "The requested service is currently down or taking too long (TIMEOUT).",
                "error", "Circuit Breaker Triggered"
            ));
    }
}
