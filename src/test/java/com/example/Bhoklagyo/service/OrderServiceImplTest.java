package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.OrderItemRequest;
import com.example.Bhoklagyo.dto.OrderRequest;
import com.example.Bhoklagyo.dto.OrderResponse;
import com.example.Bhoklagyo.entity.*;
import com.example.Bhoklagyo.entity.Order;
import com.example.Bhoklagyo.event.EventPublisher;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.mapper.OrderMapper;
import com.example.Bhoklagyo.repository.OrderRepository;
import com.example.Bhoklagyo.repository.RestaurantMenuItemRepository;
import com.example.Bhoklagyo.repository.RestaurantRepository;
import com.example.Bhoklagyo.repository.UserRepository;
import com.example.Bhoklagyo.websocket.OrderEventSender;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private RestaurantRepository restaurantRepository;
    @Mock private RestaurantMenuItemRepository restaurantMenuItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderEventSender orderEventSender;
    @Mock private EventPublisher eventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User customer;
    private User owner;
    private Restaurant restaurant;
    private RestaurantMenuItem menuItem;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setId(1L);
        customer.setName("Customer");
        customer.setEmail("customer@example.com");
        customer.setRole(Role.CUSTOMER);

        owner = new User();
        owner.setId(2L);
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        owner.setRole(Role.OWNER);

        restaurant = new Restaurant();
        ReflectionTestUtils.setField(restaurant, "id", 10L);
        restaurant.setName("Test Restaurant");
        restaurant.setOwner(owner);
        restaurant.setTotalRating(0L);
        restaurant.setTotalCount(0L);
        restaurant.setRating(0.0);

        menuItem = new RestaurantMenuItem();
        menuItem.setId(100L);
        menuItem.setName("Momo");
        menuItem.setPrice(250.0);
        menuItem.setRestaurant(restaurant);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void mockSecurityContext(String email) {
        Authentication auth = mock(Authentication.class);
        lenient().when(auth.getName()).thenReturn(email);
        SecurityContext ctx = mock(SecurityContext.class);
        lenient().when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);
    }

    // ── createOrder ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("createOrder")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order successfully for the customer themselves")
        void createOrder_success() {
            mockSecurityContext("customer@example.com");

            OrderItemRequest itemReq = new OrderItemRequest();
            itemReq.setMenuItemId(100L);
            itemReq.setQuantity(2);

            OrderRequest request = new OrderRequest();
            request.setCustomerId(1L);
            request.setItems(List.of(itemReq));
            request.setStatus(OrderStatus.PENDING);

            when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
            when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));
            when(restaurantMenuItemRepository.findById(100L)).thenReturn(Optional.of(menuItem));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
                Order o = inv.getArgument(0);
                ReflectionTestUtils.setField(o, "id", 1000L);
                return o;
            });

            OrderResponse expectedResponse = new OrderResponse();
            expectedResponse.setId(1000L);
            when(orderMapper.toResponse(any(Order.class))).thenReturn(expectedResponse);

            OrderResponse result = orderService.createOrder(10L, request);

            assertNotNull(result);
            assertEquals(1000L, result.getId());
            verify(orderRepository).save(argThat(order ->
                    order.getCustomer().equals(customer) &&
                    order.getRestaurant().equals(restaurant) &&
                    order.getTotalPrice() > 0
            ));
        }

        @Test
        @DisplayName("Should reject order if customer id !== current user")
        void createOrder_accessDenied() {
            mockSecurityContext("other@example.com");

            User otherUser = new User();
            otherUser.setId(99L);
            otherUser.setEmail("other@example.com");

            OrderRequest request = new OrderRequest();
            request.setCustomerId(1L);
            request.setItems(List.of());

            when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));

            assertThrows(AccessDeniedException.class, () -> orderService.createOrder(10L, request));
            verify(orderRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw when customer not found")
        void createOrder_customerNotFound() {
            OrderRequest request = new OrderRequest();
            request.setCustomerId(999L);
            request.setItems(List.of());

            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(10L, request));
        }

        @Test
        @DisplayName("Should throw when menu item doesn't belong to restaurant")
        void createOrder_menuItemWrongRestaurant() {
            mockSecurityContext("customer@example.com");

            Restaurant otherRestaurant = new Restaurant();
            ReflectionTestUtils.setField(otherRestaurant, "id", 20L);
            menuItem.setRestaurant(otherRestaurant);

            OrderItemRequest itemReq = new OrderItemRequest();
            itemReq.setMenuItemId(100L);
            itemReq.setQuantity(1);

            OrderRequest request = new OrderRequest();
            request.setCustomerId(1L);
            request.setItems(List.of(itemReq));

            when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
            when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
            when(restaurantRepository.findById(10L)).thenReturn(Optional.of(restaurant));
            when(restaurantMenuItemRepository.findById(100L)).thenReturn(Optional.of(menuItem));

            assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(10L, request));
        }
    }

    // ── getOrderById ────────────────────────────────────────────────────

    @Nested
    @DisplayName("getOrderById")
    class GetOrderByIdTests {

        private Order existingOrder;

        @BeforeEach
        void setUpOrder() {
            existingOrder = new Order();
            ReflectionTestUtils.setField(existingOrder, "id", 1000L);
            existingOrder.setRestaurant(restaurant);
            existingOrder.setCustomer(customer);
            existingOrder.setStatus(OrderStatus.PENDING);
        }

        @Test
        @DisplayName("Customer can view their own order")
        void customerViewsOwnOrder() {
            mockSecurityContext("customer@example.com");
            when(orderRepository.findById(1000L)).thenReturn(Optional.of(existingOrder));
            when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));

            OrderResponse expectedResp = new OrderResponse();
            expectedResp.setId(1000L);
            when(orderMapper.toResponse(existingOrder)).thenReturn(expectedResp);

            OrderResponse result = orderService.getOrderById(10L, 1000L);
            assertEquals(1000L, result.getId());
        }

        @Test
        @DisplayName("Customer cannot view another customer's order")
        void customerCannotViewOtherOrder() {
            mockSecurityContext("other@example.com");
            User other = new User();
            other.setId(99L);
            other.setEmail("other@example.com");
            other.setRole(Role.CUSTOMER);

            when(orderRepository.findById(1000L)).thenReturn(Optional.of(existingOrder));
            when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(other));

            assertThrows(AccessDeniedException.class, () -> orderService.getOrderById(10L, 1000L));
        }

        @Test
        @DisplayName("Should throw when order does not belong to restaurant")
        void orderWrongRestaurant() {
            mockSecurityContext("customer@example.com");
            when(orderRepository.findById(1000L)).thenReturn(Optional.of(existingOrder));

            assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(999L, 1000L));
        }

        @Test
        @DisplayName("Owner can view order from their restaurant")
        void ownerViewsOrder() {
            mockSecurityContext("owner@example.com");
            when(orderRepository.findById(1000L)).thenReturn(Optional.of(existingOrder));
            when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));

            OrderResponse expectedResp = new OrderResponse();
            expectedResp.setId(1000L);
            when(orderMapper.toResponse(existingOrder)).thenReturn(expectedResp);

            OrderResponse result = orderService.getOrderById(10L, 1000L);
            assertEquals(1000L, result.getId());
        }
    }

    // ── updateOrderStatus ───────────────────────────────────────────────

    @Nested
    @DisplayName("updateOrderStatus")
    class UpdateOrderStatusTests {

        @Test
        @DisplayName("Owner can update order status")
        void ownerUpdatesStatus() {
            mockSecurityContext("owner@example.com");

            Order order = new Order();
            ReflectionTestUtils.setField(order, "id", 1000L);
            order.setRestaurant(restaurant);
            order.setCustomer(customer);
            order.setStatus(OrderStatus.PENDING);

            when(orderRepository.findById(1000L)).thenReturn(Optional.of(order));
            when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

            OrderResponse resp = new OrderResponse();
            resp.setId(1000L);
            when(orderMapper.toResponse(any(Order.class))).thenReturn(resp);

            OrderResponse result = orderService.updateOrderStatus(10L, 1000L, OrderStatus.CONFIRMED);

            assertNotNull(result);
            verify(orderRepository).save(argThat(o -> o.getStatus() == OrderStatus.CONFIRMED));
        }

        @Test
        @DisplayName("Non-restaurant-staff cannot update order status")
        void unauthorizedUpdate() {
            mockSecurityContext("customer@example.com");

            Order order = new Order();
            ReflectionTestUtils.setField(order, "id", 1000L);
            order.setRestaurant(restaurant);
            order.setCustomer(customer);
            order.setStatus(OrderStatus.PENDING);

            when(orderRepository.findById(1000L)).thenReturn(Optional.of(order));
            when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));

            assertThrows(AccessDeniedException.class,
                    () -> orderService.updateOrderStatus(10L, 1000L, OrderStatus.CONFIRMED));
        }
    }

    // ── submitOrderFeedback ─────────────────────────────────────────────

    @Nested
    @DisplayName("submitOrderFeedback")
    class SubmitFeedbackTests {

        @Test
        @DisplayName("Customer submits feedback with rating successfully")
        void submitFeedbackWithRating() {
            mockSecurityContext("customer@example.com");

            Order order = new Order();
            ReflectionTestUtils.setField(order, "id", 1000L);
            order.setCustomer(customer);
            order.setRestaurant(restaurant);
            order.setStatus(OrderStatus.DELIVERED);
            order.setTotalPrice(500.0);

            when(orderRepository.findById(1000L)).thenReturn(Optional.of(order));
            when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
            when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

            OrderResponse resp = new OrderResponse();
            resp.setId(1000L);
            when(orderMapper.toResponse(any(Order.class))).thenReturn(resp);

            OrderResponse result = orderService.submitOrderFeedback(1L, 1000L, "Great food!", 5);

            assertNotNull(result);
            verify(restaurantRepository).save(argThat(r -> {
                assertEquals(5L, r.getTotalRating());
                assertEquals(1L, r.getTotalCount());
                assertEquals(5.0, r.getRating(), 0.01);
                return true;
            }));
        }

        @Test
        @DisplayName("Updating an existing rating adjusts aggregates correctly")
        void updateExistingRating() {
            mockSecurityContext("customer@example.com");

            restaurant.setTotalRating(4L);
            restaurant.setTotalCount(1L);
            restaurant.setRating(4.0);

            Order order = new Order();
            ReflectionTestUtils.setField(order, "id", 1000L);
            order.setCustomer(customer);
            order.setRestaurant(restaurant);
            order.setStatus(OrderStatus.DELIVERED);
            order.setRating(4); // previous rating
            order.setTotalPrice(500.0);

            when(orderRepository.findById(1000L)).thenReturn(Optional.of(order));
            when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
            when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
            when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

            OrderResponse resp = new OrderResponse();
            when(orderMapper.toResponse(any())).thenReturn(resp);

            orderService.submitOrderFeedback(1L, 1000L, "Updated feedback", 2);

            // prevTotal(4) - prevOrderRating(4) + newRating(2) = 2, count stays 1
            verify(restaurantRepository).save(argThat(r -> {
                assertEquals(2L, r.getTotalRating());
                assertEquals(1L, r.getTotalCount());
                assertEquals(2.0, r.getRating(), 0.01);
                return true;
            }));
        }

        @Test
        @DisplayName("Invalid rating is rejected")
        void invalidRating() {
            mockSecurityContext("customer@example.com");

            Order order = new Order();
            ReflectionTestUtils.setField(order, "id", 1000L);
            order.setCustomer(customer);
            order.setRestaurant(restaurant);
            order.setStatus(OrderStatus.DELIVERED);

            when(orderRepository.findById(1000L)).thenReturn(Optional.of(order));
            when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));

            assertThrows(IllegalArgumentException.class,
                    () -> orderService.submitOrderFeedback(1L, 1000L, "ok", 6));
        }

        @Test
        @DisplayName("Cannot submit feedback for someone else's order")
        void feedbackAccessDenied() {
            mockSecurityContext("other@example.com");
            User other = new User();
            other.setId(99L);
            other.setEmail("other@example.com");

            Order order = new Order();
            ReflectionTestUtils.setField(order, "id", 1000L);
            order.setCustomer(customer);
            order.setRestaurant(restaurant);

            when(orderRepository.findById(1000L)).thenReturn(Optional.of(order));

            // First check: order.customer.id !== customerId param
            assertThrows(ResourceNotFoundException.class,
                    () -> orderService.submitOrderFeedback(99L, 1000L, "hack", 5));
        }
    }

    // ── getOrdersByCustomerId ─────────────────────────────────────────

    @Test
    @DisplayName("Customer can fetch their own orders")
    void getOrdersByCustomerId_success() {
        mockSecurityContext("customer@example.com");
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(orderRepository.findByCustomerId(1L)).thenReturn(Collections.emptyList());

        List<OrderResponse> result = orderService.getOrdersByCustomerId(1L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Customer cannot fetch another customer's orders")
    void getOrdersByCustomerId_accessDenied() {
        mockSecurityContext("customer@example.com");
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));

        assertThrows(AccessDeniedException.class, () -> orderService.getOrdersByCustomerId(99L));
    }
}
