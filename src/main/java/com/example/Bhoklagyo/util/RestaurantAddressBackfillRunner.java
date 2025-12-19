package com.example.Bhoklagyo.util;

import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Backfill runner to populate `address_label` for existing restaurants.
 *
 * Usage: run the app with `-Dspring.profiles.active=backfill` once.
 * It will query restaurants with NULL address_label and reverse-geocode them
 * using Nominatim (respect rate limits: 1 request/second).
 */
@Component
@Profile("backfill")
public class RestaurantAddressBackfillRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(RestaurantAddressBackfillRunner.class);

    private final RestaurantRepository restaurantRepository;
    private final RestTemplate rest;

    public RestaurantAddressBackfillRunner(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
        this.rest = new RestTemplate();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Starting restaurant address_label backfill...");

        List<Restaurant> rows = restaurantRepository.findAll();
        int total = rows.size();
        int updated = 0;

        for (int i = 0; i < rows.size(); i++) {
            Restaurant r = rows.get(i);
            // If overwrite requested, update even if addressLabel exists
            boolean overwrite = false;
            String overwriteEnv = System.getenv("BACKFILL_OVERWRITE");
            if (overwriteEnv != null && (overwriteEnv.equalsIgnoreCase("1") || overwriteEnv.equalsIgnoreCase("true"))) {
                overwrite = true;
            }
            if (r.getAddressLabel() != null && !r.getAddressLabel().isBlank() && !overwrite) continue;
            if (r.getLocation() == null) continue;

            double lon = r.getLocation().getX();
            double lat = r.getLocation().getY();

            try {
                // Request results in English and include address details
                URI uri = UriComponentsBuilder.fromHttpUrl("https://nominatim.openstreetmap.org/reverse")
                    .queryParam("lat", lat)
                    .queryParam("lon", lon)
                    .queryParam("format", "json")
                    .queryParam("addressdetails", 1)
                    .queryParam("accept-language", "en")
                    .build()
                    .toUri();

                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.USER_AGENT, "Bhoklagyo-backfill/1.0 (your-email@example.com)");
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));

                @SuppressWarnings("unchecked")
                var resp = rest.getForObject(uri, java.util.Map.class);

                if (resp != null) {
                    Object display = resp.get("display_name");
                    Object addrObj = resp.get("address");
                    String label = null;
                    if (addrObj instanceof java.util.Map) {
                        java.util.Map addr = (java.util.Map) addrObj;
                        Object road = addr.getOrDefault("road", addr.getOrDefault("pedestrian", addr.get("residential")));
                        Object city = addr.getOrDefault("city", addr.getOrDefault("town", addr.getOrDefault("village", addr.get("county"))));
                        if (road != null && city != null) label = road.toString() + ", " + city.toString();
                        else if (road != null) label = road.toString();
                        else if (city != null) label = city.toString();
                    }
                    if (label == null && display instanceof String) {
                        String[] parts = ((String) display).split(",");
                        for (int k = 0; k < parts.length; k++) parts[k] = parts[k].trim();
                        if (parts.length >= 2) label = parts[0] + ", " + parts[1];
                        else if (parts.length == 1) label = parts[0];
                    }

                    if (label != null && !label.isBlank()) {
                        r.setAddressLabel(label);
                        restaurantRepository.save(r);
                        updated++;
                        log.info("Updated restaurant id={} label='{}'", r.getId(), label);
                    } else {
                        log.debug("No label for restaurant id={} from nominatim", r.getId());
                    }
                }

            } catch (Exception ex) {
                log.warn("Reverse geocode failed for restaurant id={} lat={},lon={}: {}", r.getId(), lat, lon, ex.getMessage());
            }

            // Nominatim rate limit: be polite and sleep ~1s between requests
            try { Thread.sleep(1100L); } catch (InterruptedException ignored) {}
        }

        log.info("Backfill complete: processed={}, updated={}", total, updated);
    }
}
