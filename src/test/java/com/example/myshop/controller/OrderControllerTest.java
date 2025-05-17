package com.example.myshop.controller;

import com.example.myshop.Status;
import com.example.myshop.entity.Order;
import com.example.myshop.entity.OrderProductMap;
import com.example.myshop.entity.Product;
import com.example.myshop.entity.User;
import com.example.myshop.repository.OrderProductMapRepository;
import com.example.myshop.repository.OrderRepository;
import com.example.myshop.repository.ProductRepository;
import com.example.myshop.service.OrderService;
import com.example.myshop.service.ShoppingCartService;
import com.example.myshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private ShoppingCartService shoppingCartService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProductMapRepository orderProductMapRepository;

    @Mock
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        OrderController orderController = new OrderController(shoppingCartService, productRepository, orderRepository, orderProductMapRepository, orderService);
        orderController.service = userService; // Inject the UserService

        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void testCartToOrder() throws Exception {
        // Mock security context
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn("testuser");

        // Mock user
        User user = new User();
        user.setId(1L);
        user.setLogin("testuser");
        Mockito.when(userService.findByLogin("testuser")).thenReturn(user);

        // Mock cart
        Map<Product, Integer> cart = new HashMap<>();
        Product product = new Product();
        product.setId(1L);
        product.setPrice(new BigDecimal("10.00"));
        cart.put(product, 2);
        Mockito.when(shoppingCartService.getCart()).thenReturn(cart);
        Mockito.when(shoppingCartService.totalPrice()).thenReturn(new BigDecimal("20.00"));

        mockMvc.perform(get("/user/carttoorder"))
                .andExpect(status().isOk())
                .andExpect(view().name("ordersuccess"))
                .andDo(print());
    }

    @Test
    public void testOrderList() throws Exception {
        // Mock security context
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        
        User user = new User();
        user.setId(1L);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);

        List<Order> orders = Arrays.asList(new Order(), new Order());
        Mockito.when(orderRepository.findByUser(user)).thenReturn(orders);

        mockMvc.perform(get("/user/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("/order"))
                .andExpect(model().attributeExists("OrdersUser"))
                .andDo(print());
    }

    @Test
    public void testViewOrder() throws Exception {
        Order order = new Order();
        order.setId(1L);
        Mockito.when(orderService.findOrder(1L)).thenReturn(Optional.of(order));

        List<OrderProductMap> orderProducts = Arrays.asList(new OrderProductMap(), new OrderProductMap());
        Mockito.when(orderProductMapRepository.findAllByOrderid(1L)).thenReturn(orderProducts);

        mockMvc.perform(get("/user/orders/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("order/order"))
                .andExpect(model().attributeExists("OrdersProducts"))
                .andDo(print());
    }

    @Test
    public void testDeleteOrder() throws Exception {
        mockMvc.perform(get("/user/delete/order/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/orders"))
                .andDo(print());
    }
} 