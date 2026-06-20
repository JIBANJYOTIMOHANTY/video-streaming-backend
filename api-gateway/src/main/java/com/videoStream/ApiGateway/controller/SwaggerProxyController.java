package com.videoStream.ApiGateway.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Proxies Swagger/OpenAPI JSON from each downstream service through the
 * gateway.
 * The Swagger UI (served at /swagger-ui.html) fetches
 * /v3/api-docs/{service-name},
 * which this controller resolves via Eureka and forwards to the real service's
 * /v3/api-docs.
 *
 * @Hidden prevents this controller from being included in the gateway's own
 *         OpenAPI spec.
 */
@Hidden
@RestController
@RequestMapping("/v3/api-docs")
public class SwaggerProxyController {

    /** Maps URL path segments to Eureka service IDs */
    private static final Map<String, String> SERVICE_MAP = Map.of(
            "auth-service", "AUTH-SERVICE",
            "user-service", "USER-SERVICE",
            "video-service", "VIDEO-SERVICE",
            "upload-service", "UPLOAD-SERVICE",
            "streaming-service", "STREAMING-SERVICE",
            "analytics-service", "ANALYTICS-SERVICE");

    private final RestTemplate restTemplate;

    public SwaggerProxyController(@Autowired RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{serviceName}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<Object> proxySwaggerDocs(@PathVariable String serviceName) {
        String eurekaId = SERVICE_MAP.get(serviceName);
        if (eurekaId == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            Map<String, Object> docs = restTemplate.getForObject(
                    "http://" + eurekaId + "/v3/api-docs",
                    Map.class);
            if (docs != null) {
                docs.put("servers", java.util.List.of(Map.of("url", "/api/v1")));
                
                // Rewrite paths to strip "/api/v1" prefix so that /api/v1 serves as the base URL
                @SuppressWarnings("unchecked")
                Map<String, Object> paths = (Map<String, Object>) docs.get("paths");
                if (paths != null) {
                    java.util.Map<String, Object> newPaths = new java.util.LinkedHashMap<>();
                    for (Map.Entry<String, Object> entry : paths.entrySet()) {
                        String oldPath = entry.getKey();
                        String newPath = oldPath;
                        if (oldPath.startsWith("/api/v1/")) {
                            newPath = oldPath.substring(7); // "/api/v1/foo" -> "/foo"
                        } else if (oldPath.equals("/api/v1")) {
                            newPath = "/";
                        }
                        newPaths.put(newPath, entry.getValue());
                    }
                    docs.put("paths", newPaths);
                }
                
                
                // Inject JWT Bearer authorization components into Swagger UI dynamically
                @SuppressWarnings("unchecked")
                Map<String, Object> components = (Map<String, Object>) docs.computeIfAbsent("components", k -> new java.util.HashMap<String, Object>());
                
                @SuppressWarnings("unchecked")
                Map<String, Object> securitySchemes = (Map<String, Object>) components.computeIfAbsent("securitySchemes", k -> new java.util.HashMap<String, Object>());
                
                securitySchemes.put("BearerAuthentication", Map.of(
                        "type", "http",
                        "scheme", "bearer",
                        "bearerFormat", "JWT"
                ));
                
                docs.put("security", java.util.List.of(Map.of("BearerAuthentication", java.util.List.of())));
            }
            return ResponseEntity.ok(docs);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
