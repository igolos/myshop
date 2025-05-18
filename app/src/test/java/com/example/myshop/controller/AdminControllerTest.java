package com.example.myshop.controller;

import com.example.myshop.entity.Order;
import com.example.myshop.entity.OrderProductMap;
import com.example.myshop.entity.User;
import com.example.myshop.repository.OrderProductMapRepository;
import com.example.myshop.repository.OrderRepository;
import com.example.myshop.repository.UserRepository;
import com.example.myshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProductMapRepository orderProductMapRepository;

    @BeforeEach
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(new AdminController(userService, userRepository, orderRepository, orderProductMapRepository))
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void testAdminHome() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin"))
                .andDo(print());
    }

    @Test
    public void testUserList() throws Exception {
        List<User> users = Arrays.asList(
            createUser(1L, "User 1"),
            createUser(2L, "User 2")
        );
        Mockito.when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get("/admin/editusers"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/editusers"))
                .andExpect(model().attributeExists("allUsers"))
                .andExpect(model().attributeExists("roleUsers"))
                .andDo(print());
    }

    @Test
    public void testUserCard() throws Exception {
        User user = createUser(1L, "Test User");
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/admin/editusers/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/usercard"))
                .andExpect(model().attributeExists("usercard"))
                .andDo(print());
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(get("/admin/user/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/editusers"))
                .andDo(print());
    }

    @Test
    public void testOrdersList() throws Exception {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        Mockito.when(orderRepository.findAll()).thenReturn(orders);

        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/adminordersuser"))
                .andExpect(model().attributeExists("AllOrders"))
                .andDo(print());
    }

    @Test
    public void testViewOrder() throws Exception {
        List<OrderProductMap> orderProducts = Arrays.asList(new OrderProductMap(), new OrderProductMap());
        Mockito.when(orderProductMapRepository.findAllByOrderid(1L)).thenReturn(orderProducts);

        mockMvc.perform(get("/admin/orders/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/productoforder"))
                .andExpect(model().attributeExists("OrdersProducts"))
                .andDo(print());
    }

    private User createUser(Long id, String name) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        return user;
    }
} 