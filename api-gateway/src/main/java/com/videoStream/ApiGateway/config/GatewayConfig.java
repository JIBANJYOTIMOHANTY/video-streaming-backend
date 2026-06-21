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

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class GatewayConfig implements WebMvcConfigurer {

        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        // ─── CORS ───────────────────────────────────────────────────────────────────
        @Override
        public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                                .allowedOrigins("http://localhost:4200", "http://localhost:4201")
                                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                                .allowedHeaders("*")
                                .exposedHeaders("Content-Range", "Accept-Ranges", "Content-Length", "Content-Type")
                                .allowCredentials(true);
        }

        // ─── API Proxy Routes ────────────────────────────────────────────────────────
        @Bean
        public RouterFunction<ServerResponse> apiGatewayRoutes() {
                return route("auth-service")
                                .route(RequestPredicates.path("/api/v1/auth/**"), http())
                                .filter(lb("AUTH-SERVICE"))
                                .build()
                                .and(route("user-service")
                                                .route(RequestPredicates.path("/api/v1/users/**"), http())
                                                .filter(lb("USER-SERVICE"))
                                                .build())
                                .and(route("video-service")
                                                .route(RequestPredicates.path("/api/v1/videos/**"), http())
                                                .filter(lb("VIDEO-SERVICE"))
                                                .build())
                                .and(route("upload-service")
                                                .route(RequestPredicates.path("/api/v1/upload/**"), http())
                                                .filter(jwtAuthenticationFilter)
                                                .filter(lb("UPLOAD-SERVICE"))
                                                .build())
                                .and(route("streaming-service")
                                                .route(RequestPredicates.path("/api/v1/stream/**"), http())
                                                .filter(lb("STREAMING-SERVICE"))
                                                .build())
                                .and(route("analytics-service")
                                                .route(RequestPredicates.path("/api/v1/analytics/**"), http())
                                                .filter(lb("ANALYTICS-SERVICE"))
                                                .build());
        }
}
