package com.example.Bhoklagyo.controller;

import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Admin-only endpoints for cache management.
 * Allows monitoring and clearing Redis caches.
 */
@RestController
@RequestMapping("/admin/cache")
@PreAuthorize("hasRole('ADMIN')")
public class CacheManagementController {

    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;

    public CacheManagementController(CacheManager cacheManager,
                                     RedisTemplate<String, Object> redisTemplate) {
        this.cacheManager = cacheManager;
        this.redisTemplate = redisTemplate;
    }

    /**
     * List all known cache names.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCacheInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("cacheNames", cacheManager.getCacheNames());

        try {
            Properties redisInfo = Objects.requireNonNull(
                    redisTemplate.getConnectionFactory()).getConnection().serverCommands().info("memory");
            if (redisInfo != null) {
                info.put("usedMemory", redisInfo.getProperty("used_memory_human"));
                info.put("maxMemory", redisInfo.getProperty("maxmemory_human"));
            }
        } catch (Exception e) {
            info.put("redisStatus", "unavailable: " + e.getMessage());
        }

        return ResponseEntity.ok(info);
    }

    /**
     * Evict a specific cache by name.
     */
    @DeleteMapping("/{cacheName}")
    public ResponseEntity<Map<String, String>> evictCache(@PathVariable String cacheName) {
        var cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.clear();
            return ResponseEntity.ok(Map.of("message", "Cache '" + cacheName + "' cleared"));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Evict all caches.
     */
    @DeleteMapping
    public ResponseEntity<Map<String, String>> evictAllCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
        return ResponseEntity.ok(Map.of("message", "All caches cleared"));
    }
}
