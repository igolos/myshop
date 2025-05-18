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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ProductControllerTest {

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

        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService, productRepository))
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void testViewProducts() throws Exception {
        List<Product> products = Arrays.asList(
            createProduct(1L, "Product 1"),
            createProduct(2L, "Product 2")
        );
        Mockito.when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get("/admin/product"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/product"))
                .andExpect(model().attributeExists("allproducts"))
                .andDo(print());
    }

    @Test
    public void testNewProduct() throws Exception {
        mockMvc.perform(get("/admin/product/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/newproduct"))
                .andExpect(model().attributeExists("newProductForm"))
                .andDo(print());
    }

    @Test
    public void testCreateNewProductSuccess() throws Exception {
        Mockito.when(productService.saveProduct(Mockito.any(Product.class))).thenReturn(true);

        mockMvc.perform(post("/admin/product/new")
                .param("name", "New Product")
                .param("price", "10.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product"))
                .andDo(print());
    }

    @Test
    public void testCreateNewProductFailure() throws Exception {
        Mockito.when(productService.saveProduct(Mockito.any(Product.class))).thenReturn(false);

        mockMvc.perform(post("/admin/product/new")
                .param("name", "Existing Product")
                .param("price", "10.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/newproduct"))
                .andExpect(model().attributeExists("productNameError"))
                .andDo(print());
    }

    @Test
    public void testDeleteProduct() throws Exception {
        mockMvc.perform(get("/admin/product/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product"))
                .andDo(print());
    }

    @Test
    public void testEditProduct() throws Exception {
        Product product = createProduct(1L, "Test Product");
        Mockito.when(productRepository.existsById(1L)).thenReturn(true);
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        mockMvc.perform(get("/admin/product/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/editproduct"))
                .andExpect(model().attributeExists("editProduct"))
                .andDo(print());
    }

    @Test
    public void testUpdateProduct() throws Exception {
        Product product = createProduct(1L, "Updated Product");
        Mockito.when(productRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(post("/admin/product/edit/1")
                .param("name", "Updated Product")
                .param("price", "20.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product"))
                .andDo(print());
    }

    private Product createProduct(Long id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        return product;
    }
} 