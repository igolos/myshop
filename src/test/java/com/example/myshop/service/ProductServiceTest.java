package com.example.myshop.service;

import com.example.myshop.entity.Product;
import com.example.myshop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void findAll_ReturnsAllProducts() {
        // Arrange
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setDescription("Description 1");
        product1.setPrice(BigDecimal.valueOf(100));
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setDescription("Description 2");
        product2.setPrice(BigDecimal.valueOf(200));
        product2.setQuantity(20);

        List<Product> expectedProducts = List.of(product1, product2);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // Act
        List<Product> actualProducts = productService.findAll();

        // Assert
        assertEquals(expectedProducts.size(), actualProducts.size());
        verify(productRepository).findAll();
    }

    @Test
    void saveProduct_WhenNameNotTaken_ReturnsTrue() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setDescription("Description");
        newProduct.setPrice(BigDecimal.valueOf(100));
        newProduct.setQuantity(10);
        when(productRepository.findByName("New Product")).thenReturn(null);

        // Act
        boolean result = productService.saveProduct(newProduct);

        // Assert
        assertTrue(result);
        verify(productRepository).save(newProduct);
    }

    @Test
    void saveProduct_WhenNameTaken_ReturnsFalse() {
        // Arrange
        Product newProduct = new Product();
        newProduct.setName("Existing Product");
        newProduct.setDescription("Description");
        newProduct.setPrice(BigDecimal.valueOf(100));
        newProduct.setQuantity(10);
        when(productRepository.findByName("Existing Product")).thenReturn(new Product());

        // Act
        boolean result = productService.saveProduct(newProduct);

        // Assert
        assertFalse(result);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_WhenProductExists_ReturnsTrue() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(new Product()));

        // Act
        boolean result = productService.deleteProduct(1L);

        // Assert
        assertTrue(result);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_WhenProductDoesNotExist_ReturnsFalse() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean result = productService.deleteProduct(1L);

        // Assert
        assertFalse(result);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void updateProduct_UpdatesProductSuccessfully() {
        // Arrange
        Product productToUpdate = new Product();
        productToUpdate.setName("Updated Product");
        productToUpdate.setDescription("Updated Description");
        productToUpdate.setPrice(BigDecimal.valueOf(150));
        productToUpdate.setQuantity(15);

        // Act
        productService.updateProduct(1L, productToUpdate);

        // Assert
        verify(productRepository).save(productToUpdate);
        assertEquals(1L, productToUpdate.getId());
    }

    @Test
    void productsCount_ReturnsCorrectCount() {
        // Arrange
        when(productRepository.count()).thenReturn(5L);

        // Act
        long count = productService.productsCount();

        // Assert
        assertEquals(5L, count);
        verify(productRepository).count();
    }

    @Test
    void findById_ReturnsProduct() {
        // Arrange
        Product expectedProduct = new Product();
        expectedProduct.setId(1L);
        expectedProduct.setName("Test Product");
        expectedProduct.setDescription("Description");
        expectedProduct.setPrice(BigDecimal.valueOf(100));
        expectedProduct.setQuantity(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(expectedProduct));

        // Act
        Product actualProduct = productService.findById(1L);

        // Assert
        assertNotNull(actualProduct);
        assertEquals(expectedProduct.getId(), actualProduct.getId());
        verify(productRepository).findById(1L);
    }

    @Test
    void count_ReturnsCorrectCount() {
        // Arrange
        when(productRepository.count()).thenReturn(10L);

        // Act
        long count = productService.count();

        // Assert
        assertEquals(10L, count);
        verify(productRepository).count();
    }
} 