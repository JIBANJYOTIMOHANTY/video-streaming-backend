package com.videoStream.ApiGateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import java.util.Map;

@Component
public class JwtAuthenticationFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final RestTemplate restTemplate;

    public JwtAuthenticationFilter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ServerResponse filter(ServerRequest request,
            org.springframework.web.servlet.function.HandlerFunction<ServerResponse> next) throws Exception {
        String authHeader = request.headers().firstHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", 1, "error", "Missing or invalid Authorization header"));
        }

        String token = authHeader.substring(7);
        try {
            // Validate token via auth-service
            String validateUrl = "http://AUTH-SERVICE/api/v1/auth/validate?token=" + token;
            restTemplate.getForEntity(validateUrl, Map.class);
            return next.handle(request);
        } catch (Exception e) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", 1, "error", "Token is expired or invalid"));
        }
    }
}
