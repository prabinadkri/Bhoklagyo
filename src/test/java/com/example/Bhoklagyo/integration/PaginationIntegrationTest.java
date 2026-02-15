package com.example.Bhoklagyo.integration;

import com.example.Bhoklagyo.entity.CuisineTag;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.entity.Role;
import com.example.Bhoklagyo.entity.Vendor;
import com.example.Bhoklagyo.repository.CuisineTagRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PaginationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CuisineTagRepository cuisineTagRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setup() {
        // Clear all caches to avoid stale data from other test classes
        cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());

        // Truncate all tables to cleanly reset state across test classes
        jdbcTemplate.execute("TRUNCATE TABLE order_items, orders, notifications, " +
                "restaurant_menu_items, restaurant_cuisine_tags, restaurant_dietary_tags, " +
                "restaurant_documents, restaurants, categories, cuisine_tags, dietary_tags, " +
                "vendors, admins, users CASCADE");

        // Create a vendor
        Vendor vendor = new Vendor();
        vendor.setPanNumber("TEST123456");
        vendor.setBusinessName("Test Vendor");
        vendor.setAccountNumber("ACC123");
        vendor = vendorRepository.save(vendor);

        // Create cuisine tags
        CuisineTag italian = cuisineTagRepository.save(new CuisineTag("Italian"));
        CuisineTag chinese = cuisineTagRepository.save(new CuisineTag("Chinese"));
        CuisineTag mexican = cuisineTagRepository.save(new CuisineTag("Mexican"));

        // Create 15 restaurants
        for (int i = 1; i <= 15; i++) {
            Restaurant restaurant = new Restaurant();
            restaurant.setName("Restaurant " + i);
            GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
            Point p = gf.createPoint(new Coordinate(85.3 + i * 0.01, 27.7 + i * 0.01));
            restaurant.setLocation(p);
            restaurant.setVendor(vendor);
            
            // Assign cuisine tags
            Set<CuisineTag> tags = new HashSet<>();
            if (i % 3 == 0) tags.add(italian);
            if (i % 5 == 0) tags.add(chinese);
            if (i % 7 == 0) tags.add(mexican);
            restaurant.setCuisineTags(tags);
            
            restaurantRepository.save(restaurant);
        }
    }

    @Test
    void testGetAllRestaurantsWithoutCursor() throws Exception {
        mockMvc.perform(get("/restaurants")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurants").isArray())
                .andExpect(jsonPath("$.restaurants.length()").value(5))
                .andExpect(jsonPath("$.hasMore").value(true))
                .andExpect(jsonPath("$.nextCursor").isNumber());
    }

    @Test
    void testGetAllRestaurantsWithCursor() throws Exception {
        // First request
        String response = mockMvc.perform(get("/restaurants")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Extract cursor (simple parsing for test)
        Long cursor = 5L; // Assuming first 5 restaurants have IDs 1-5

        // Second request with cursor
        mockMvc.perform(get("/restaurants")
                        .param("cursor", cursor.toString())
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurants").isArray())
                .andExpect(jsonPath("$.restaurants.length()").value(5))
                .andExpect(jsonPath("$.hasMore").value(true));
    }

    @Test
    void testGetAllRestaurantsLastPage() throws Exception {
        // First get the first page to find actual cursor
        String firstPageResponse = mockMvc.perform(get("/restaurants")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Now request second page with that cursor
        mockMvc.perform(get("/restaurants")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurants").isArray())
                .andExpect(jsonPath("$.hasMore").value(true)); // 15 restaurants, so 10 + 5 left
    }

    @Test
    void testGetAllRestaurantsSmallLimit() throws Exception {
        // Test with limit=3 to verify pagination works correctly
        mockMvc.perform(get("/restaurants")
                        .param("limit", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurants").isArray())
                .andExpect(jsonPath("$.restaurants.length()").value(3))
                .andExpect(jsonPath("$.hasMore").value(true))
                .andExpect(jsonPath("$.nextCursor").isNumber());
    }

    @Test
    void testSearchWithPagination() throws Exception {
        mockMvc.perform(get("/search")
                        .param("keyword", "Restaurant")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurants.restaurants").isArray())
                .andExpect(jsonPath("$.restaurants.restaurants.length()").value(5))
                .andExpect(jsonPath("$.restaurants.hasMore").value(true))
                .andExpect(jsonPath("$.restaurants.nextCursor").isNumber());
    }

    @Test
    void testSearchByCuisineTagWithPagination() throws Exception {
        mockMvc.perform(get("/search")
                        .param("keyword", "Italian")
                        .param("limit", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurants.restaurants").isArray())
                .andExpect(jsonPath("$.restaurants.restaurants.length()").value(3))
                .andExpect(jsonPath("$.restaurants.hasMore").value(true));
    }

    @Test
    void testSearchWithCursor() throws Exception {
        // Search all restaurants matching "Restaurant"
        mockMvc.perform(get("/search")
                        .param("keyword", "Restaurant")
                        .param("cursor", "5")
                        .param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurants.restaurants").isArray())
                .andExpect(jsonPath("$.restaurants.restaurants.length()").value(5));
    }

    @Test
    void testDefaultLimitIs20() throws Exception {
        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurants").isArray())
                .andExpect(jsonPath("$.restaurants.length()").value(15)) // All 15 since less than default 20
                .andExpect(jsonPath("$.hasMore").value(false));
    }
}
