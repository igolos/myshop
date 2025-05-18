package com.example.myshop.repository;

import com.example.myshop.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void cleanUp() {
        productRepository.deleteAll();
    }

    @Test
    public void whenFindById_thenReturnProduct() {
        // given
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setQuantity(10);
        entityManager.persist(product);
        entityManager.flush();

        // when
        Product found = productRepository.findById(product.getId()).orElse(null);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Test Product");
        assertThat(found.getPrice()).isEqualTo(new BigDecimal("99.99"));
    }

    @Test
    public void whenFindByName_thenReturnProduct() {
        // given
        Product product = new Product();
        product.setName("Unique Product");
        product.setDescription("Unique Description");
        product.setPrice(new BigDecimal("149.99"));
        product.setQuantity(5);
        entityManager.persist(product);
        entityManager.flush();

        // when
        Product found = productRepository.findByName("Unique Product");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Unique Product");
        assertThat(found.getPrice()).isEqualTo(new BigDecimal("149.99"));
    }

    @Test
    public void whenSaveProduct_thenReturnSavedProduct() {
        // given
        Product product = new Product();
        product.setName("New Product");
        product.setDescription("New Description");
        product.setPrice(new BigDecimal("199.99"));
        product.setQuantity(15);

        // when
        Product saved = productRepository.save(product);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New Product");
        assertThat(saved.getPrice()).isEqualTo(new BigDecimal("199.99"));
    }

    @Test
    public void whenFindAll_thenReturnAllProducts() {
        // given
        Product product1 = new Product();
        product1.setName("Product 1");
        product1.setPrice(new BigDecimal("99.99"));
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setName("Product 2");
        product2.setPrice(new BigDecimal("199.99"));
        product2.setQuantity(20);

        entityManager.persist(product1);
        entityManager.persist(product2);
        entityManager.flush();

        // when
        List<Product> products = productRepository.findAll();

        // then
        assertThat(products).hasSize(2);
        assertThat(products).extracting(Product::getName)
                .containsExactlyInAnyOrder("Product 1", "Product 2");
    }

    @Test
    public void whenDeleteProduct_thenProductShouldNotExist() {
        // given
        Product product = new Product();
        product.setName("To Delete");
        product.setPrice(new BigDecimal("299.99"));
        product.setQuantity(5);
        entityManager.persist(product);
        entityManager.flush();

        // when
        productRepository.deleteById(product.getId());

        // then
        assertThat(productRepository.findById(product.getId())).isEmpty();
    }
} 