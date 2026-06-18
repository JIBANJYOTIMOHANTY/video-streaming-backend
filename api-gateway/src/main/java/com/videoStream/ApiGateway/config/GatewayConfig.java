package com.videoStream.ApiGateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class GatewayConfig implements WebMvcConfigurer {

        // ─── Load-balanced RestTemplate (used by SwaggerProxyController) ───────────
        @Bean
        @LoadBalanced
        public RestTemplate restTemplate() {
                return new RestTemplate();
        }

        // ─── CORS ───────────────────────────────────────────────────────────────────
        @Override
        public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                                .allowedOrigins("http://localhost:4200", "http://localhost:4201")
                                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                                .allowedHeaders("*")
                                .allowCredentials(true);
        }

        // ─── API Proxy Routes ────────────────────────────────────────────────────────
        @Bean
        public RouterFunction<ServerResponse> apiGatewayRoutes() {
                return route("auth-service")
                                .route(RequestPredicates.path("/api/v1/auth/**"), http())
                                .before(uri("lb://AUTH-SERVICE"))
                                .build()
                                .and(route("user-service")
                                                .route(RequestPredicates.path("/api/v1/users/**"), http())
                                                .before(uri("lb://USER-SERVICE"))
                                                .build())
                                .and(route("video-service")
                                                .route(RequestPredicates.path("/api/v1/videos/**"), http())
                                                .before(uri("lb://VIDEO-SERVICE"))
                                                .build())
                                .and(route("upload-service")
                                                .route(RequestPredicates.path("/api/v1/upload/**"), http())
                                                .before(uri("lb://UPLOAD-SERVICE"))
                                                .build())
                                .and(route("streaming-service")
                                                .route(RequestPredicates.path("/api/v1/stream/**"), http())
                                                .before(uri("lb://STREAMING-SERVICE"))
                                                .build())
                                .and(route("analytics-service")
                                                .route(RequestPredicates.path("/api/v1/analytics/**"), http())
                                                .before(uri("lb://ANALYTICS-SERVICE"))
                                                .build());
        }
}
