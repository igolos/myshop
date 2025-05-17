package com.example.myshop.repository;

import com.example.myshop.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void whenFindById_thenReturnRole() {
        // given
        Role role = new Role();
        role.setName("ROLE_USER");
        entityManager.persist(role);
        entityManager.flush();

        // when
        Role found = roleRepository.findById(role.getId()).orElse(null);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("ROLE_USER");
    }

    @Test
    public void whenSaveRole_thenReturnSavedRole() {
        // given
        Role role = new Role();
        role.setName("ROLE_ADMIN");

        // when
        Role saved = roleRepository.save(role);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    public void whenDeleteRole_thenRoleShouldNotExist() {
        // given
        Role role = new Role();
        role.setName("ROLE_TEST");
        entityManager.persist(role);
        entityManager.flush();

        // when
        roleRepository.deleteById(role.getId());

        // then
        assertThat(roleRepository.findById(role.getId())).isEmpty();
    }
} 