package com.example.myshop.controller;

import com.example.myshop.entity.Product;
import com.example.myshop.repository.ProductRepository;
import com.example.myshop.service.ProductService;
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
class HomeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(new HomeController(productService, productRepository))
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void testHome() throws Exception {
        List<Product> products = Arrays.asList(
            createProduct(1L, "Product 1"),
            createProduct(2L, "Product 2")
        );
        Mockito.when(productService.findAll()).thenReturn(products);
        Mockito.when(productService.productsCount()).thenReturn(2L);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("productsCount"))
                .andDo(print());
    }

    @Test
    public void testAbout() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"))
                .andDo(print());
    }

    @Test
    public void testSearchByProduct() throws Exception {
        Product product = createProduct(1L, "Test Product");
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(productService.productsCount()).thenReturn(1L);

        mockMvc.perform(get("/searchByProduct/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("productfromcategory"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("productsCount"))
                .andDo(print());
    }

    private Product createProduct(Long id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        return product;
    }
} 