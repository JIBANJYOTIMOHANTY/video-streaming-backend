package com.videoStream.ApiGateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.servlet.function.HandlerFilterFunction;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class GatewayConfig implements WebMvcConfigurer {

        private static final Logger logger = LoggerFactory.getLogger(GatewayConfig.class);
        private final JwtAuthenticationFilter jwtAuthenticationFilter;

        public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

        // Helper filter to pause and retry when services are not yet registered
        private static HandlerFilterFunction<ServerResponse, ServerResponse> retryOnServiceUnavailable(int maxAttempts,
                        long delayMs) {
                return (request, next) -> {
                        int attempts = 0;
                        while (true) {
                                try {
                                        attempts++;
                                        return next.handle(request);
                                } catch (Exception e) {
                                        boolean is503 = false;
                                        if (e instanceof HttpServerErrorException &&
                                                        ((HttpServerErrorException) e).getStatusCode().value() == 503) {
                                                is503 = true;
                                        } else if (e.getMessage() != null
                                                        && e.getMessage().contains("Unable to find instance")) {
                                                is503 = true;
                                        }

                                        if (is503 && attempts < maxAttempts) {
                                                logger.warn("Target service not available for {}. Retrying attempt {}/{} in {}ms...",
                                                                request.path(), attempts, maxAttempts, delayMs);
                                                try {
                                                        Thread.sleep(delayMs);
                                                } catch (InterruptedException ie) {
                                                        Thread.currentThread().interrupt();
                                                        throw e;
                                                }
                                        } else {
                                                throw e;
                                        }
                                }
                        }
                };
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
                                .filter(retryOnServiceUnavailable(5, 2000))
                                .filter(lb("AUTH-SERVICE"))
                                .build()
                                .and(route("user-service")
                                                .route(RequestPredicates.path("/api/v1/users/**"), http())
                                                .filter(retryOnServiceUnavailable(5, 2000))
                                                .filter(lb("USER-SERVICE"))
                                                .build())
                                .and(route("video-service")
                                                .route(RequestPredicates.path("/api/v1/videos/**"), http())
                                                .filter(retryOnServiceUnavailable(5, 2000))
                                                .filter(lb("VIDEO-SERVICE"))
                                                .build())
                                .and(route("upload-service")
                                                .route(RequestPredicates.path("/api/v1/upload/**"), http())
                                                .filter(jwtAuthenticationFilter)
                                                .filter(retryOnServiceUnavailable(5, 2000))
                                                .filter(lb("UPLOAD-SERVICE"))
                                                .build())
                                .and(route("streaming-service")
                                                .route(RequestPredicates.path("/api/v1/stream/**"), http())
                                                .filter(retryOnServiceUnavailable(5, 2000))
                                                .filter(lb("STREAMING-SERVICE"))
                                                .build())
                                .and(route("analytics-service")
                                                .route(RequestPredicates.path("/api/v1/analytics/**"), http())
                                                .filter(retryOnServiceUnavailable(5, 2000))
                                                .filter(lb("ANALYTICS-SERVICE"))
                                                .build());
        }
}
