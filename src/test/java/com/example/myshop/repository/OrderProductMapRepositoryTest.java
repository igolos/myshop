package com.example.myshop.repository;

import com.example.myshop.entity.Order;
import com.example.myshop.entity.OrderProductMap;
import com.example.myshop.entity.Product;
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
public class OrderProductMapRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderProductMapRepository orderProductMapRepository;

    private User createAndPersistUser(String name, String login) {
        Role role = new Role();
        role.setName("ROLE_USER");
        entityManager.persist(role);

        User user = new User();
        user.setName(name);
        user.setLogin(login);
        user.setPassword("Test123!@#");
        user.setRoles(Collections.singleton(role));
        return entityManager.persist(user);
    }

    @Test
    public void whenFindById_thenReturnOrderProductMap() {
        // given
        User user = createAndPersistUser("Test User", "testuser");

        Order order = new Order();
        order.setUser(user);
        order.setStatus("ACCEPTED");
        order.setDateoforder(LocalDate.now());
        order.setGrand_total(new BigDecimal("99.99"));
        entityManager.persist(order);

        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(10);
        entityManager.persist(product);

        OrderProductMap orderProductMap = new OrderProductMap();
        orderProductMap.setOrder(order);
        orderProductMap.setOrderid(order.getId());
        orderProductMap.setProduct(product);
        orderProductMap.setQuantity(1);
        orderProductMap.setPrice(product.getPrice());
        entityManager.persist(orderProductMap);
        entityManager.flush();

        // when
        OrderProductMap found = orderProductMapRepository.findById(orderProductMap.getId()).orElse(null);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getOrder()).isEqualTo(order);
        assertThat(found.getProduct()).isEqualTo(product);
        assertThat(found.getQuantity()).isEqualTo(1);
    }

    @Test
    public void whenFindAllByOrderid_thenReturnOrderProductMaps() {
        // given
        User user = createAndPersistUser("Order User", "orderuser");

        Order order = new Order();
        order.setUser(user);
        order.setStatus("ACCEPTED");
        order.setDateoforder(LocalDate.now());
        order.setGrand_total(new BigDecimal("199.99"));
        entityManager.persist(order);

        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("99.99"));
        product1.setQuantity(10);
        entityManager.persist(product1);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("99.99"));
        product2.setQuantity(5);
        entityManager.persist(product2);

        OrderProductMap orderProductMap1 = new OrderProductMap();
        orderProductMap1.setOrder(order);
        orderProductMap1.setOrderid(order.getId());
        orderProductMap1.setProduct(product1);
        orderProductMap1.setQuantity(1);
        orderProductMap1.setPrice(product1.getPrice());

        OrderProductMap orderProductMap2 = new OrderProductMap();
        orderProductMap2.setOrder(order);
        orderProductMap2.setOrderid(order.getId());
        orderProductMap2.setProduct(product2);
        orderProductMap2.setQuantity(1);
        orderProductMap2.setPrice(product2.getPrice());

        entityManager.persist(orderProductMap1);
        entityManager.persist(orderProductMap2);
        entityManager.flush();

        // when
        List<OrderProductMap> orderProductMaps = orderProductMapRepository.findAllByOrderid(order.getId());

        // then
        assertThat(orderProductMaps).hasSize(2);
        assertThat(orderProductMaps).extracting(OrderProductMap::getProduct)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Product 1", "Product 2");
    }

    @Test
    public void whenSaveOrderProductMap_thenReturnSavedOrderProductMap() {
        // given
        User user = createAndPersistUser("New User", "newuser");

        Order order = new Order();
        order.setUser(user);
        order.setStatus("ACCEPTED");
        order.setDateoforder(LocalDate.now());
        order.setGrand_total(new BigDecimal("99.99"));
        entityManager.persist(order);

        Product product = new Product();
        product.setName("New Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(10);
        entityManager.persist(product);

        OrderProductMap orderProductMap = new OrderProductMap();
        orderProductMap.setOrder(order);
        orderProductMap.setOrderid(order.getId());
        orderProductMap.setProduct(product);
        orderProductMap.setQuantity(1);
        orderProductMap.setPrice(product.getPrice());

        // when
        OrderProductMap saved = orderProductMapRepository.save(orderProductMap);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getOrder()).isEqualTo(order);
        assertThat(saved.getProduct()).isEqualTo(product);
        assertThat(saved.getQuantity()).isEqualTo(1);
    }

    @Test
    public void whenDeleteOrderProductMap_thenOrderProductMapShouldNotExist() {
        // given
        User user = createAndPersistUser("Delete User", "deleteuser");

        Order order = new Order();
        order.setUser(user);
        order.setStatus("ACCEPTED");
        order.setDateoforder(LocalDate.now());
        order.setGrand_total(new BigDecimal("99.99"));
        entityManager.persist(order);

        Product product = new Product();
        product.setName("Delete Product");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(10);
        entityManager.persist(product);

        OrderProductMap orderProductMap = new OrderProductMap();
        orderProductMap.setOrder(order);
        orderProductMap.setOrderid(order.getId());
        orderProductMap.setProduct(product);
        orderProductMap.setQuantity(1);
        orderProductMap.setPrice(product.getPrice());
        entityManager.persist(orderProductMap);
        entityManager.flush();

        // when
        orderProductMapRepository.deleteById(orderProductMap.getId());

        // then
        assertThat(orderProductMapRepository.findById(orderProductMap.getId())).isEmpty();
    }
} 