package com.example.myshop.controller;

import com.example.myshop.entity.Product;
import com.example.myshop.repository.OrderProductMapRepository;
import com.example.myshop.repository.OrderRepository;
import com.example.myshop.repository.ProductRepository;
import com.example.myshop.repository.UserRepository;
import com.example.myshop.service.OrderService;
import com.example.myshop.service.ShoppingCartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CartControllerTest {

    private MockMvc mockMvc;
    @Mock
    protected ShoppingCartService shoppingCartServiceImpl;

    @Mock
    protected ProductRepository productRepository;

    @Mock
    protected OrderRepository orderRepository;

    @Mock
    protected OrderProductMapRepository orderProductMapRepository;

    @Mock
    protected OrderService orderServiceImpl;

    @Mock
    protected UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(new CartController(shoppingCartServiceImpl, productRepository, orderRepository, orderProductMapRepository, orderServiceImpl, userRepository))
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void CartControllerStatus() throws Exception {
        this.mockMvc.perform(get("/cart")).andExpect(status().isOk())
                .andExpect(view().name("cart")).andDo(print());
    }

    @Test
    public void CartControllerStatus2() throws Exception {
        Product testProduct = new Product();
        testProduct.setId(1);
        long id = testProduct.getId();
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(testProduct));
        this.mockMvc.perform(get("/cart/add/" + id)).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index")).andDo(print());
    }

    @Test
    public void CartControllerStatus4() throws Exception {
        Product testProduct = new Product();
        testProduct.setId(1);
        long id = testProduct.getId();
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(testProduct));
        this.mockMvc.perform(get("/cart/remove/" + id)).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart")).andDo(print());
    }

    @Test
    public void CartControllerStatus5() throws Exception {
        this.mockMvc.perform(get("/cart/clear")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart")).andDo(print());
    }

}