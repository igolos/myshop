package com.example.myshop.repository;

import com.example.myshop.Status;
import com.example.myshop.entity.Order;
import com.example.myshop.entity.Role;
import com.example.myshop.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    private User createAndPersistUser(String name, String login) {
        Role role = new Role();
        role.setName("ROLE_USER");
        entityManager.persist(role);

        User user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setPassword("Test123!@#"); // Valid password that meets all requirements
        user.setRoles(Collections.singleton(role));
        return entityManager.persist(user);
    }

    @Test
    public void whenFindById_thenReturnOrder() {
        // given
        User user = createAndPersistUser("Test User", "testuser");

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.ACCEPTED.toString());
        order.setDateoforder(LocalDate.now());
        order.setGrand_total(new BigDecimal("99.99"));
        entityManager.persist(order);
        entityManager.flush();

        // when
        Order found = orderRepository.findById(order.getId()).orElse(null);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getUser()).isEqualTo(user);
        assertThat(found.getStatus()).isEqualTo(Status.ACCEPTED.toString());
    }

    @Test
    public void whenFindByUser_thenReturnUserOrders() {
        // given
        User user = createAndPersistUser("Order User", "orderuser");

        Order order1 = new Order();
        order1.setUser(user);
        order1.setStatus(Status.ACCEPTED.toString());
        order1.setDateoforder(LocalDate.now());
        order1.setGrand_total(new BigDecimal("99.99"));

        Order order2 = new Order();
        order2.setUser(user);
        order2.setStatus(Status.PAID.toString());
        order2.setDateoforder(LocalDate.now());
        order2.setGrand_total(new BigDecimal("149.99"));

        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();

        // when
        List<Order> orders = orderRepository.findByUser(user);

        // then
        assertThat(orders).hasSize(2);
        assertThat(orders).extracting(Order::getStatus)
                .containsExactlyInAnyOrder(Status.ACCEPTED.toString(), Status.PAID.toString());
    }

    @Test
    public void whenSaveOrder_thenReturnSavedOrder() {
        // given
        User user = createAndPersistUser("New User", "newuser");

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.ACCEPTED.toString());
        order.setDateoforder(LocalDate.now());
        order.setGrand_total(new BigDecimal("199.99"));

        // when
        Order saved = orderRepository.save(order);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser()).isEqualTo(user);
        assertThat(saved.getStatus()).isEqualTo(Status.ACCEPTED.toString());
    }

    @Test
    public void whenDeleteOrder_thenOrderShouldNotExist() {
        // given
        User user = createAndPersistUser("Delete User", "deleteuser");

        Order order = new Order();
        order.setUser(user);
        order.setStatus(Status.ACCEPTED.toString());
        order.setDateoforder(LocalDate.now());
        order.setGrand_total(new BigDecimal("299.99"));
        entityManager.persist(order);
        entityManager.flush();

        // when
        orderRepository.deleteById(order.getId());

        // then
        assertThat(orderRepository.findById(order.getId())).isEmpty();
    }
} 