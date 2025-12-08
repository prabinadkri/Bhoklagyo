package com.example.Bhoklagyo.integration;

import com.example.Bhoklagyo.controller.InvitationController;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.security.InviteTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.entity.Restaurant;
import com.example.Bhoklagyo.entity.Role;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@org.springframework.test.context.ActiveProfiles("test")
@AutoConfigureMockMvc
public class InvitationFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InviteTokenUtil inviteTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @MockBean
    private com.example.Bhoklagyo.service.EmailService emailService;

    private Long restaurantId;
    private Long ownerId;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        restaurantRepository.deleteAll();

        User owner = new User();
        owner.setName("Owner One");
        owner.setPassword("noop");
        owner.setEmail("owner1@example.com");
        owner.setRole(Role.OWNER);
        owner = userRepository.save(owner);
        ownerId = owner.getId();

        Restaurant restaurant = new Restaurant();
        restaurant.setName("Test Resto");
        restaurant.setOwner(owner);
        restaurant = restaurantRepository.save(restaurant);
        restaurantId = restaurant.getId();

        User employee = new User();
        employee.setName("Employee One");
        employee.setPassword("noop");
        employee.setEmail("employee1@example.com");
        employee.setRole(Role.CUSTOMER); // will be updated to EMPLOYEE on accept
        userRepository.save(employee);
    }

    @Test
    @WithMockUser(username = "owner1@example.com", roles = {"OWNER"})
    void ownerCanSendInviteEmail() throws Exception {
        String requestBody = "{\"email\":\"employee1@example.com\"}";
        mockMvc.perform(post("/restaurants/" + restaurantId + "/invite-employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Security mocked via @WithMockUser
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "employee1@example.com", roles = {"CUSTOMER"})
    void invitedUserCanAccept() throws Exception {
        String token = inviteTokenUtil.generateInviteToken(ownerId, restaurantId, "employee1@example.com");
        mockMvc.perform(get("/invitations/accept")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantId").value(restaurantId));
    }
}
