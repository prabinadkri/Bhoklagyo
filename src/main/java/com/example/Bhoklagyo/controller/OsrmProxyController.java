package com.example.Bhoklagyo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.*;

/**
 * Simple OSRM proxy controller.
 *
 * For development you can set VITE_API_BASE_URL to point to your backend (e.g. http://localhost:8080)
 * and the frontend will call backend:/osrm/route/v1/.... The backend will forward requests to
 * https://router.project-osrm.org/route/v1/...
 *
 * NOTE: In production you should tighten CORS and consider rate-limiting / caching.
 */
// CORS is handled globally by `CorsConfig` — do not add @CrossOrigin here
@RestController
@RequestMapping("/osrm")
public class OsrmProxyController {

    private static final Logger log = LoggerFactory.getLogger(OsrmProxyController.class);

    private final RestTemplate rest;

    public OsrmProxyController(RestTemplateBuilder builder) {
        // Configure timeouts for outgoing requests to OSRM
        this.rest = builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }

    // Hop-by-hop headers that should NOT be forwarded
    private static final Set<String> HOP_BY_HOP = new HashSet<>(Arrays.asList(
            "connection", "keep-alive", "proxy-authenticate", "proxy-authorization",
            "te", "trailer", "transfer-encoding", "upgrade", "content-length"
    ));

    @GetMapping("/**")
    public ResponseEntity<byte[]> proxyGet(HttpServletRequest request) {
        // Reconstruct the target URL by removing the /osrm prefix
        String requestUri = request.getRequestURI(); // e.g. /osrm/route/v1/...
        String contextPath = Optional.ofNullable(request.getContextPath()).orElse("");
        String pathWithoutContext = requestUri.substring(contextPath.length());
        String pathToForward = pathWithoutContext.replaceFirst("^/osrm", ""); // leaves /route/v1/...
        String query = request.getQueryString();

        // Try alternative OSRM servers (router.project-osrm.org is currently returning 502)
        // You can also set up your own OSRM instance locally
        String target = "http://router.project-osrm.org" + pathToForward + (query != null ? "?" + query : "");

        log.debug("Proxying OSRM GET: {} -> {}", requestUri, target);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> resp = rest.exchange(target, HttpMethod.GET, entity, byte[].class);

            // Build response headers copying relevant ones (avoid hop-by-hop)
            HttpHeaders out = new HttpHeaders();
            for (Map.Entry<String, List<String>> e : resp.getHeaders().entrySet()) {
                String name = e.getKey();
                if (name == null) continue;
                String lower = name.toLowerCase();
                // Skip hop-by-hop headers and any CORS response headers from the upstream service
                if (HOP_BY_HOP.contains(lower)) continue;
                if (lower.startsWith("access-control-")) continue; // avoid duplicating CORS headers
                out.put(name, e.getValue());
            }

            // Ensure content-type is set if present
            MediaType ct = resp.getHeaders().getContentType();
            if (ct != null) out.setContentType(ct);

            // CORS headers are handled by global CorsConfig

            return new ResponseEntity<>(resp.getBody(), out, resp.getStatusCode());
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            log.warn("OSRM proxy received upstream error: {} {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            // Return upstream status and body
            HttpHeaders errHeaders = new HttpHeaders();
            errHeaders.setContentType(MediaType.APPLICATION_JSON);
            // CORS headers are handled by global CorsConfig
            return new ResponseEntity<>(ex.getResponseBodyAsByteArray(), errHeaders, ex.getStatusCode());
        } catch (Exception ex) {
            log.error("OSRM proxy unexpected error", ex);
            // CORS headers are handled by global CorsConfig
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("OSRM proxy error: " + ex.getMessage()).getBytes());
        }
    }

    // OPTIONS preflight requests are handled by Spring Security + global CorsConfig
}