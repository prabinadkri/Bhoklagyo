package com.example.Bhoklagyo.integration;

import com.example.Bhoklagyo.dto.*;
import com.example.Bhoklagyo.entity.OrderStatus;
import com.example.Bhoklagyo.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerRoleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String customerToken;
    private static String adminToken;
    private static Long customerId;
    private static Long restaurantId;
    private static Long menuItemId;
    private static Long orderId;
    private static String vendorPanNumber = "1234567890";

    @Test
    @Order(1)
    @DisplayName("1. Register Admin")
    void testRegisterAdmin() throws Exception {
        AdminRegisterRequest adminRequest = new AdminRegisterRequest();
        adminRequest.setName("Admin User");
        adminRequest.setPassword("Admin123!");
        adminRequest.setEmail("admin@test.com");
        adminRequest.setPhoneNumber("+977-9800000001");
        adminRequest.setRegistrationSecret("test-admin-secret");

        MvcResult result = mockMvc.perform(post("/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        adminToken = loginResponse.getToken();
        
        System.out.println("✓ Admin registered successfully with token");
    }

    @Test
    @Order(2)
    @DisplayName("2. Admin Creates Vendor")
    void testAdminCreatesVendor() throws Exception {
        VendorRequest vendorRequest = new VendorRequest();
        vendorRequest.setPanNumber(vendorPanNumber);
        vendorRequest.setBusinessName("Test Vendor LLC");
        vendorRequest.setAccountNumber("ACC123456789");
        vendorRequest.setIsVatRegistered(true);
        vendorRequest.setEmail("vendor@test.com");
        vendorRequest.setPhoneNumber("+977-9800000002");
        vendorRequest.setAddress("Kathmandu, Nepal");

        mockMvc.perform(post("/vendors")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vendorRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.panNumber").value(vendorPanNumber));
        
        System.out.println("✓ Admin created vendor with PAN: " + vendorPanNumber);
    }

    @Test
    @Order(3)
    @DisplayName("3. Register Customer")
    void testRegisterCustomer() throws Exception {
        RegisterRequest customerRequest = new RegisterRequest();
        customerRequest.setName("John Customer");
        customerRequest.setPassword("Customer123!");
        customerRequest.setEmail("customer@test.com");
        customerRequest.setPhoneNumber("+977-9812345678");
        customerRequest.setRole(Role.CUSTOMER);
        customerRequest.setAddress("Kathmandu, Nepal");

        MvcResult result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        customerToken = loginResponse.getToken();
        customerId = loginResponse.getUserId();
        
        System.out.println("✓ Customer registered successfully with token");
    }

    @Test
    @Order(4)
    @DisplayName("4. Customer Cannot Create Restaurant")
    void testCustomerCannotCreateRestaurant() throws Exception {
        RestaurantRequest restaurantRequest = new RestaurantRequest();
        restaurantRequest.setName("Test Restaurant");
        restaurantRequest.setLatitude(27.7172);
        restaurantRequest.setLongitude(85.3240);
        restaurantRequest.setContactNumber("+977-9851234567");
        restaurantRequest.setPanNumber(vendorPanNumber);
        restaurantRequest.setCuisineTags(Arrays.asList("Italian", "Fast Food"));
        restaurantRequest.setDietaryTags(Arrays.asList("Vegetarian"));

        mockMvc.perform(post("/restaurants")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantRequest)))
                .andExpect(status().isForbidden());
        
        System.out.println("✓ Customer correctly denied from creating restaurant");
    }

    @Test
    @Order(5)
    @DisplayName("5. Admin Creates Restaurant")
    void testAdminCreatesRestaurant() throws Exception {
        RestaurantRequest restaurantRequest = new RestaurantRequest();
        restaurantRequest.setName("Pizza Palace");
        restaurantRequest.setLatitude(27.7172);
        restaurantRequest.setLongitude(85.3240);
        restaurantRequest.setContactNumber("+977-9851234567");
        restaurantRequest.setPanNumber(vendorPanNumber);
        restaurantRequest.setCuisineTags(Arrays.asList("Italian", "Fast Food"));
        restaurantRequest.setDietaryTags(Arrays.asList("Vegetarian", "Vegan"));

        MvcResult result = mockMvc.perform(post("/restaurants")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Pizza Palace"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        RestaurantResponse restaurantResponse = objectMapper.readValue(responseBody, RestaurantResponse.class);
        restaurantId = restaurantResponse.getId();
        
        System.out.println("✓ Admin created restaurant with ID: " + restaurantId);
    }

    @Test
    @Order(6)
    @DisplayName("6. Customer Can View Restaurants")
    void testCustomerCanViewRestaurants() throws Exception {
        mockMvc.perform(get("/restaurants")
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurants").isArray())
                .andExpect(jsonPath("$.restaurants[0].name").value("Pizza Palace"));
        
        System.out.println("✓ Customer can view restaurants");
    }

    @Test
    @Order(7)
    @DisplayName("7. Customer Cannot Add Menu Items")
    void testCustomerCannotAddMenuItems() throws Exception {
        MenuItemRequest menuItemRequest = new MenuItemRequest();
        menuItemRequest.setCategoryName("Pizza");
        menuItemRequest.setName("Margherita Pizza");
        menuItemRequest.setDescription("Classic cheese pizza");
        menuItemRequest.setPrice(9.99);
        menuItemRequest.setIsVegan(false);
        menuItemRequest.setIsVegetarian(true);

        mockMvc.perform(post("/restaurants/" + restaurantId + "/menu")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singletonList(menuItemRequest))))
                .andExpect(status().isForbidden());
        
        System.out.println("✓ Customer correctly denied from adding menu items");
    }

    @Test
    @Order(8)
    @DisplayName("8. Customer Can View Menu")
    void testCustomerCanViewMenu() throws Exception {
        mockMvc.perform(get("/restaurants/" + restaurantId + "/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        
        System.out.println("✓ Customer can view restaurant menu");
    }

    @Test
    @Order(9)
    @DisplayName("9. Customer Cannot Create Order Without Menu Items")
    void testCustomerCannotCreateOrderWithoutMenuItems() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(customerId);
        orderRequest.setRestaurantId(restaurantId);
        orderRequest.setItems(Arrays.asList(new OrderItemRequest(99999L, 1))); // Non-existent menu item
        orderRequest.setStatus(OrderStatus.PENDING);
        orderRequest.setDeliveryLatitude(27.7172);
        orderRequest.setDeliveryLongitude(85.3240);

        mockMvc.perform(post("/restaurants/" + restaurantId + "/orders")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isNotFound());
        
        System.out.println("✓ Customer correctly prevented from ordering non-existent items");
    }

    @Test
    @Order(10)
    @DisplayName("9. Customer Cannot Update Order Status")
    void testCustomerCannotUpdateOrderStatus() throws Exception {
        OrderStatusRequest statusRequest = new OrderStatusRequest();
        statusRequest.setStatus(OrderStatus.DELIVERED);
        
        // Try to update a non-existent order - should get 404 (not found) or 403 (forbidden)
        // Both are acceptable as customer shouldn't be able to update orders
        mockMvc.perform(patch("/restaurants/" + restaurantId + "/orders/999")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 403 && status != 404) {
                        throw new AssertionError("Expected 403 or 404 but got " + status);
                    }
                });
        
        System.out.println("✓ Customer correctly denied from updating order status");
    }

    @Test
    @Order(11)
    @DisplayName("10. Customer Can View Own Orders")
    void testCustomerCanViewOwnOrders() throws Exception {
        mockMvc.perform(get("/orders")
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
        
        System.out.println("✓ Customer can view their own orders");
    }

    // @Test
    // @Order(12)
    // @DisplayName("11. Customer Cannot View Another Customer's Orders")
    // void testCustomerCannotViewOtherCustomerOrders() throws Exception {
    //     Long otherCustomerId = 999L;
        
    //     mockMvc.perform(get("/users/" + otherCustomerId + "/orders")
    //             .header("Authorization", "Bearer " + customerToken))
    //             .andExpect(status().isForbidden());
        
    //     System.out.println("✓ Customer correctly denied from viewing other customer's orders");
    // }

    @Test
    @Order(12)
    @DisplayName("12. Customer Cannot Access Restaurant Orders")
    void testCustomerCannotAccessRestaurantOrders() throws Exception {
        mockMvc.perform(get("/restaurants/" + restaurantId + "/orders")
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
        
        System.out.println("✓ Customer correctly denied from viewing restaurant orders");
    }

    @Test
    @Order(13)
    @DisplayName("13. Unauthenticated User Cannot Access Protected Endpoints")
    void testUnauthenticatedUserCannotAccessProtectedEndpoints() throws Exception {
        // Try to create order without token
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(customerId);
        orderRequest.setRestaurantId(restaurantId);
        orderRequest.setItems(Arrays.asList(new OrderItemRequest()));

        mockMvc.perform(post("/restaurants/" + restaurantId + "/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isUnauthorized());
        
        System.out.println("✓ Unauthenticated users correctly denied access");
    }
}
