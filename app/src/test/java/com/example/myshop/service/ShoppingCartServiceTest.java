package com.example.myshop.service;

import com.example.myshop.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ShoppingCartServiceTest {

    private ShoppingCartService shoppingCartService;

    @BeforeEach
    void setUp() {
        shoppingCartService = new ShoppingCartService();
    }

    @Test
    void addProduct_WhenProductNotInCart_AddsProduct() {
        // Arrange
        Product product = createProduct(1L, "Test Product", BigDecimal.valueOf(100));

        // Act
        shoppingCartService.addProduct(product);

        // Assert
        Map<Product, Integer> cart = shoppingCartService.getCart();
        assertEquals(1, cart.size());
        assertEquals(1, cart.get(product));
    }

    @Test
    void addProduct_WhenProductAlreadyInCart_IncrementsQuantity() {
        // Arrange
        Product product = createProduct(1L, "Test Product", BigDecimal.valueOf(100));
        shoppingCartService.addProduct(product);

        // Act
        shoppingCartService.addProduct(product);

        // Assert
        Map<Product, Integer> cart = shoppingCartService.getCart();
        assertEquals(1, cart.size());
        assertEquals(2, cart.get(product));
    }

    @Test
    void removeProduct_WhenQuantityGreaterThanOne_DecrementsQuantity() {
        // Arrange
        Product product = createProduct(1L, "Test Product", BigDecimal.valueOf(100));
        shoppingCartService.addProduct(product);
        shoppingCartService.addProduct(product);

        // Act
        shoppingCartService.removeProduct(product);

        // Assert
        Map<Product, Integer> cart = shoppingCartService.getCart();
        assertEquals(1, cart.size());
        assertEquals(1, cart.get(product));
    }

    @Test
    void removeProduct_WhenQuantityEqualsOne_RemovesProduct() {
        // Arrange
        Product product = createProduct(1L, "Test Product", BigDecimal.valueOf(100));
        shoppingCartService.addProduct(product);

        // Act
        shoppingCartService.removeProduct(product);

        // Assert
        Map<Product, Integer> cart = shoppingCartService.getCart();
        assertTrue(cart.isEmpty());
    }

    @Test
    void clearProducts_RemovesAllProducts() {
        // Arrange
        Product product1 = createProduct(1L, "Product 1", BigDecimal.valueOf(100));
        Product product2 = createProduct(2L, "Product 2", BigDecimal.valueOf(200));
        shoppingCartService.addProduct(product1);
        shoppingCartService.addProduct(product2);

        // Act
        shoppingCartService.clearProducts();

        // Assert
        Map<Product, Integer> cart = shoppingCartService.getCart();
        assertTrue(cart.isEmpty());
    }

    @Test
    void productsInCart_ReturnsUnmodifiableMap() {
        // Arrange
        Product product = createProduct(1L, "Test Product", BigDecimal.valueOf(100));
        shoppingCartService.addProduct(product);

        // Act
        Map<Product, Integer> cart = shoppingCartService.productsInCart();

        // Assert
        assertThrows(UnsupportedOperationException.class, () -> cart.put(product, 2));
    }

    @Test
    void totalPrice_CalculatesCorrectTotal() {
        // Arrange
        Product product1 = createProduct(1L, "Product 1", BigDecimal.valueOf(100));
        Product product2 = createProduct(2L, "Product 2", BigDecimal.valueOf(200));
        shoppingCartService.addProduct(product1);
        shoppingCartService.addProduct(product1); // Quantity: 2
        shoppingCartService.addProduct(product2); // Quantity: 1

        // Act
        BigDecimal total = shoppingCartService.totalPrice();

        // Assert
        // Expected: (100 * 2) + (200 * 1) = 400
        assertEquals(BigDecimal.valueOf(400), total);
    }

    @Test
    void totalPrice_WhenCartEmpty_ReturnsZero() {
        // Act
        BigDecimal total = shoppingCartService.totalPrice();

        // Assert
        assertEquals(BigDecimal.ZERO, total);
    }

    private Product createProduct(Long id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(10);
        return product;
    }
} 