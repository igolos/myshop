package com.example.myshop.service;

import com.example.myshop.entity.Order;
import com.example.myshop.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository);
    }

    @Test
    void saveOrder_SavesOrderSuccessfully() {
        // Arrange
        Order order = new Order();
        order.setGrand_total(BigDecimal.valueOf(100));
        order.setStatus("PENDING");
        order.setDateoforder(LocalDate.now());
        order.setDescription("Test order");

        // Act
        orderService.saveOrder(order);

        // Assert
        verify(orderRepository).save(order);
    }

    @Test
    void deleteOrder_WhenOrderExists_ReturnsTrue() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(new Order()));

        // Act
        boolean result = orderService.deleteOrder(1L);

        // Assert
        assertTrue(result);
        verify(orderRepository).deleteById(1L);
    }

    @Test
    void deleteOrder_WhenOrderDoesNotExist_ReturnsFalse() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean result = orderService.deleteOrder(1L);

        // Assert
        assertFalse(result);
        verify(orderRepository, never()).deleteById(any());
    }

    @Test
    void findOrder_WhenOrderExists_ReturnsOrder() {
        // Arrange
        Order expectedOrder = new Order();
        expectedOrder.setId(1L);
        expectedOrder.setGrand_total(BigDecimal.valueOf(100));
        expectedOrder.setStatus("PENDING");
        expectedOrder.setDateoforder(LocalDate.now());
        expectedOrder.setDescription("Test order");
        when(orderRepository.findById(1L)).thenReturn(Optional.of(expectedOrder));

        // Act
        Optional<Order> actualOrder = orderService.findOrder(1L);

        // Assert
        assertTrue(actualOrder.isPresent());
        assertEquals(expectedOrder.getId(), actualOrder.get().getId());
        verify(orderRepository).findById(1L);
    }

    @Test
    void findOrder_WhenOrderDoesNotExist_ReturnsEmpty() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Order> actualOrder = orderService.findOrder(1L);

        // Assert
        assertTrue(actualOrder.isEmpty());
        verify(orderRepository).findById(1L);
    }
} 