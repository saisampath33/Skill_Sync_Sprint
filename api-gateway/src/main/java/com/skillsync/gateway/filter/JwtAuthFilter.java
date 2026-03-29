package com.skillsync.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Public paths that don't need JWT
    private static final String[] PUBLIC_PATHS = {
            "/auth/login", "/auth/register", "/auth/refresh",
            "/actuator", "/v3/api-docs", "/swagger-ui"
    };

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        for (String pub : PUBLIC_PATHS) {
            if (path.startsWith(pub)) {
                return chain.filter(exchange);
            }
        }
        
        if (path.contains("/v3/api-docs") || path.contains("/swagger-ui")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            String token = authHeader.substring(7);
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(token).getBody();

            String userId = claims.getSubject();
            String role   = String.valueOf(claims.get("role"));
            String username = String.valueOf(claims.get("fullName"));

            // IMPORTANT: RBAC Enforcement at Gateway level
            // 1. Block anyone except ADMIN from access /all or user lists
            if ((path.contains("/users/all") || path.contains("/auth/users") || path.contains("/profiles/all")) 
                && !"ROLE_ADMIN".equals(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // 2. Block non-ADMIN from modifying skills
            if (path.startsWith("/skills") && !exchange.getRequest().getMethod().name().equals("GET") 
                && !"ROLE_ADMIN".equals(role)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return exchange.getResponse().setComplete();
            }

            // Standard mutate - ensure we REMOVE existing headers to prevent spoofing
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                    .header("X-User-Id",   userId)
                    .header("X-User-Role", role)
                    .header("X-User-Name", username)
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build());

        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
