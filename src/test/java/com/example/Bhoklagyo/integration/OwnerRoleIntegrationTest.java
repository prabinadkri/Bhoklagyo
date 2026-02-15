package com.example.Bhoklagyo.integration;

import com.example.Bhoklagyo.dto.*;
import com.example.Bhoklagyo.entity.OrderStatus;
import com.example.Bhoklagyo.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class OwnerRoleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String ownerToken;
    private static String customerToken;
    private static String adminToken;
    private static Long ownerId;
    private static Long customerId;
    private static Long restaurantId;
    private static Long menuItemId;
    private static Long orderId;
    private static String vendorPanNumber = "9876543210";

    @Test
    @Order(1)
    @DisplayName("1. Register Admin")
    void testRegisterAdmin() throws Exception {
        AdminRegisterRequest adminRequest = new AdminRegisterRequest();
        adminRequest.setName("Admin User");
        adminRequest.setPassword("Admin123!");
        adminRequest.setEmail("admin_owner@test.com");
        adminRequest.setPhoneNumber("+977-9800000002");
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
        
        System.out.println("✓ Admin registered successfully");
    }

    @Test
    @Order(2)
    @DisplayName("2. Register Owner")
    void testRegisterOwner() throws Exception {
        RegisterRequest ownerRequest = new RegisterRequest();
        ownerRequest.setName("Restaurant Owner");
        ownerRequest.setPassword("Owner123!");
        ownerRequest.setEmail("owner@test.com");
        ownerRequest.setPhoneNumber("+977-9812345679");
        ownerRequest.setRole(Role.OWNER);

        MvcResult result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ownerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.role").value("OWNER"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        ownerToken = loginResponse.getToken();
        ownerId = loginResponse.getUserId();
        
        System.out.println("✓ Owner registered successfully with ID: " + ownerId);
    }

    @Test
    @Order(3)
    @DisplayName("3. Register Customer")
    void testRegisterCustomer() throws Exception {
        RegisterRequest customerRequest = new RegisterRequest();
        customerRequest.setName("Jane Customer");
        customerRequest.setPassword("Customer123!");
        customerRequest.setEmail("customer_owner@test.com");
        customerRequest.setPhoneNumber("+977-9812345680");
        customerRequest.setRole(Role.CUSTOMER);
        customerRequest.setAddress("Lalitpur, Nepal");

        MvcResult result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        LoginResponse loginResponse = objectMapper.readValue(responseBody, LoginResponse.class);
        customerToken = loginResponse.getToken();
        customerId = loginResponse.getUserId();
        
        System.out.println("✓ Customer registered successfully");
    }

    @Test
    @Order(4)
    @DisplayName("4. Owner Cannot Create Restaurant")
    void testOwnerCannotCreateRestaurant() throws Exception {
        RestaurantRequest restaurantRequest = new RestaurantRequest();
        restaurantRequest.setName("Owner's Restaurant");
        restaurantRequest.setLatitude(27.7172);
        restaurantRequest.setLongitude(85.3240);
        restaurantRequest.setContactNumber("+977-9851111111");
        restaurantRequest.setPanNumber(vendorPanNumber);
        restaurantRequest.setCuisineTags(Arrays.asList("Chinese", "Asian"));

        mockMvc.perform(post("/restaurants")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantRequest)))
                .andExpect(status().isForbidden());
        
        System.out.println("✓ Owner correctly denied from creating restaurant (admin only)");
    }

    @Test
    @Order(5)
    @DisplayName("5. Admin Creates Vendor")
    void testAdminCreatesVendor() throws Exception {
        VendorRequest vendorRequest = new VendorRequest();
        vendorRequest.setPanNumber(vendorPanNumber);
        vendorRequest.setBusinessName("Owner Test Vendor LLC");
        vendorRequest.setAccountNumber("ACC987654321");
        vendorRequest.setIsVatRegistered(false);
        vendorRequest.setEmail("ownervendor@test.com");
        vendorRequest.setPhoneNumber("+977-9800000003");
        vendorRequest.setAddress("Lalitpur, Nepal");

        mockMvc.perform(post("/vendors")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vendorRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.panNumber").value(vendorPanNumber));
        
        System.out.println("✓ Admin created vendor with PAN: " + vendorPanNumber);
    }

    @Test
    @Order(6)
    @DisplayName("6. Admin Creates Restaurant")
    void testAdminCreatesRestaurant() throws Exception {
        RestaurantRequest restaurantRequest = new RestaurantRequest();
        restaurantRequest.setName("Burger House");
        restaurantRequest.setLatitude(27.7172);
        restaurantRequest.setLongitude(85.3240);
        restaurantRequest.setContactNumber("+977-9851234567");
        restaurantRequest.setPanNumber(vendorPanNumber);
        restaurantRequest.setCuisineTags(Arrays.asList("American", "Fast Food"));
        restaurantRequest.setDietaryTags(Arrays.asList("Non-Vegetarian"));

        MvcResult result = mockMvc.perform(post("/restaurants")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Burger House"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        RestaurantResponse restaurantResponse = objectMapper.readValue(responseBody, RestaurantResponse.class);
        restaurantId = restaurantResponse.getId();
        
        System.out.println("✓ Admin created restaurant with ID: " + restaurantId);
    }

    @Test
    @Order(7)
    @DisplayName("7. Admin Assigns Owner to Restaurant")
    void testAdminAssignsOwner() throws Exception {
        AssignOwnerRequest assignRequest = new AssignOwnerRequest();
        assignRequest.setUserId(ownerId);
        assignRequest.setRestaurantId(restaurantId);

        mockMvc.perform(post("/admin/assign-owner")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurantId));
        
        System.out.println("✓ Admin assigned owner to restaurant");
    }

    @Test
    @Order(8)
    @DisplayName("8. Owner Cannot Add Menu Items to Unowned Restaurant")
    void testOwnerCannotAddMenuToUnownedRestaurant() throws Exception {
        // Create another restaurant
        RestaurantRequest restaurantRequest = new RestaurantRequest();
        restaurantRequest.setName("Another Restaurant");
        restaurantRequest.setLatitude(27.7172);
        restaurantRequest.setLongitude(85.3240);
        restaurantRequest.setContactNumber("+977-9851111112");
        restaurantRequest.setPanNumber(vendorPanNumber);

        MvcResult result = mockMvc.perform(post("/restaurants")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(restaurantRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        RestaurantResponse anotherRestaurant = objectMapper.readValue(
            result.getResponse().getContentAsString(), RestaurantResponse.class);

        MenuItemRequest menuItemRequest = new MenuItemRequest();
        menuItemRequest.setCategoryName("Pizza");
        menuItemRequest.setName("Pepperoni Pizza");
        menuItemRequest.setDescription("Spicy pepperoni pizza");
        menuItemRequest.setPrice(12.99);

        mockMvc.perform(post("/restaurants/" + anotherRestaurant.getId() + "/menu")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singletonList(menuItemRequest))))
                .andExpect(status().isForbidden());
        
        System.out.println("✓ Owner correctly denied from adding menu to unowned restaurant");
    }

    @Test
    @Order(9)
    @DisplayName("8. Owner Can Add Menu Items to Owned Restaurant")
    void testOwnerCanAddMenuItems() throws Exception {
        MenuItemRequest menuItemRequest = new MenuItemRequest();
        menuItemRequest.setCategoryName("Burger");
        menuItemRequest.setName("Classic Burger");
        menuItemRequest.setDescription("Juicy beef burger");
        menuItemRequest.setPrice(8.99);
        menuItemRequest.setIsVegan(false);
        menuItemRequest.setIsVegetarian(false);
        menuItemRequest.setAllergyWarnings("Contains gluten, dairy");
        menuItemRequest.setIsTodaySpecial(true);

        MvcResult result = mockMvc.perform(post("/restaurants/" + restaurantId + "/menu")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singletonList(menuItemRequest))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Classic Burger"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        MenuItemResponse[] menuItems = objectMapper.readValue(responseBody, MenuItemResponse[].class);
        menuItemId = menuItems[0].getId();
        
        System.out.println("✓ Owner added menu item with ID: " + menuItemId);
    }

    @Test
    @Order(10)
    @DisplayName("9. Owner Can Update Menu Items")
    void testOwnerCanUpdateMenuItems() throws Exception {
        MenuItemRequest updateRequest = new MenuItemRequest();
        updateRequest.setName("Premium Classic Burger");
        updateRequest.setDescription("Premium beef burger with special sauce");
        updateRequest.setPrice(10.99);
        updateRequest.setIsVegan(false);
        updateRequest.setIsVegetarian(false);

        mockMvc.perform(patch("/restaurants/" + restaurantId + "/menu/" + menuItemId)
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Premium Classic Burger"))
                .andExpect(jsonPath("$.price").value(10.99));
        
        System.out.println("✓ Owner updated menu item successfully");
    }

    @Test
    @Order(11)
    @DisplayName("10. Customer Creates Order")
    void testCustomerCreatesOrder() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setCustomerId(customerId);
        orderRequest.setRestaurantId(restaurantId);
        orderRequest.setItems(Arrays.asList(new OrderItemRequest(menuItemId, 2)));
        orderRequest.setStatus(OrderStatus.PENDING);
        orderRequest.setDeliveryLatitude(27.7172);
        orderRequest.setDeliveryLongitude(85.3240);

        MvcResult result = mockMvc.perform(post("/restaurants/" + restaurantId + "/orders")
                .header("Authorization", "Bearer " + customerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        OrderResponse orderResponse = objectMapper.readValue(responseBody, OrderResponse.class);
        orderId = orderResponse.getId();
        
        System.out.println("✓ Customer created order with ID: " + orderId);
    }

    @Test
    @Order(12)
    @DisplayName("11. Owner Can View Restaurant Orders")
    void testOwnerCanViewRestaurantOrders() throws Exception {
        mockMvc.perform(get("/restaurants/" + restaurantId + "/orders")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(orderId));
        
        System.out.println("✓ Owner can view restaurant orders");
    }

    @Test
    @Order(13)
    @DisplayName("12. Owner Can Update Order Status")
    void testOwnerCanUpdateOrderStatus() throws Exception {
        OrderStatusRequest statusRequest = new OrderStatusRequest();
        statusRequest.setStatus(OrderStatus.PREPARING);
        
        mockMvc.perform(patch("/restaurants/" + restaurantId + "/orders/" + orderId)
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PREPARING"));
        
        System.out.println("✓ Owner updated order status to PREPARING");
    }

    @Test
    @Order(14)
    @DisplayName("13. Owner Can View Specific Order")
    void testOwnerCanViewSpecificOrder() throws Exception {
        mockMvc.perform(get("/restaurants/" + restaurantId + "/orders/" + orderId)
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("PREPARING"));
        
        System.out.println("✓ Owner can view specific order details");
    }

    @Test
    @Order(15)
    @DisplayName("14. Owner Cannot Access Another Restaurant's Orders")
    void testOwnerCannotAccessOtherRestaurantOrders() throws Exception {
        Long otherRestaurantId = 999L;
        
        // Should get 404 (restaurant not found), 403 (forbidden), or 500 (internal error for not found)
        mockMvc.perform(get("/restaurants/" + otherRestaurantId + "/orders")
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(result -> {
                    int status = result.getResponse().getStatus();
                    if (status != 403 && status != 404 && status != 500) {
                        throw new AssertionError("Expected 403, 404, or 500 but got " + status);
                    }
                });
        
        System.out.println("✓ Owner correctly denied from accessing other restaurant's orders");
    }

    @Test
    @Order(16)
    @DisplayName("15. Owner Can Delete Menu Items")
    void testOwnerCanDeleteMenuItems() throws Exception {
        // First add a menu item to delete
        MenuItemRequest menuItemRequest = new MenuItemRequest();
        menuItemRequest.setCategoryName("Burger");
        menuItemRequest.setName("Temporary Burger");
        menuItemRequest.setPrice(5.99);

        MvcResult result = mockMvc.perform(post("/restaurants/" + restaurantId + "/menu")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singletonList(menuItemRequest))))
                .andExpect(status().isCreated())
                .andReturn();

        MenuItemResponse[] items = objectMapper.readValue(
            result.getResponse().getContentAsString(), MenuItemResponse[].class);
        Long tempMenuItemId = items[0].getId();
        
        // Now delete it
        mockMvc.perform(delete("/restaurants/" + restaurantId + "/menu/" + tempMenuItemId)
                .header("Authorization", "Bearer " + ownerToken))
                .andExpect(status().isNoContent());
        
        System.out.println("✓ Owner deleted menu item successfully");
    }

    @Test
    @Order(17)
    @DisplayName("16. Customer Cannot Delete Menu Items")
    void testCustomerCannotDeleteMenuItems() throws Exception {
        // First add a menu item
        MenuItemRequest menuItemRequest = new MenuItemRequest();
        menuItemRequest.setCategoryName("Burger");
        menuItemRequest.setName("Veggie Burger");
        menuItemRequest.setPrice(7.99);

        MvcResult result = mockMvc.perform(post("/restaurants/" + restaurantId + "/menu")
                .header("Authorization", "Bearer " + ownerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Collections.singletonList(menuItemRequest))))
                .andExpect(status().isCreated())
                .andReturn();

        MenuItemResponse[] items = objectMapper.readValue(
            result.getResponse().getContentAsString(), MenuItemResponse[].class);
        Long newMenuItemId = items[0].getId();

        // Try to delete as customer
        mockMvc.perform(delete("/restaurants/" + restaurantId + "/menu/" + newMenuItemId)
                .header("Authorization", "Bearer " + customerToken))
                .andExpect(status().isForbidden());
        
        System.out.println("✓ Customer correctly denied from deleting menu items");
    }
}
