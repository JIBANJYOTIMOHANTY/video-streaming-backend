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
    public ResponseEntity<Object> proxySwaggerDocs(@PathVariable String serviceName) {
        String eurekaId = SERVICE_MAP.get(serviceName);
        if (eurekaId == null) {
            return ResponseEntity.notFound().build();
        }
        try {
            return restTemplate.getForEntity(
                    "http://" + eurekaId + "/v3/api-docs",
                    Object.class);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
