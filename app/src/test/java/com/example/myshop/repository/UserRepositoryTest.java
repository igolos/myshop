package com.example.myshop.repository;

import com.example.myshop.entity.Role;
import com.example.myshop.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

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
    public void whenFindById_thenReturnUser() {
        // given
        User user = createAndPersistUser("Test User", "testuser");
        entityManager.flush();

        // when
        User found = userRepository.findById(user.getId()).orElse(null);

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Test User");
        assertThat(found.getLogin()).isEqualTo("testuser");
    }

    @Test
    public void whenFindByLogin_thenReturnUser() {
        // given
        User user = createAndPersistUser("Unique User", "uniqueuser");
        entityManager.flush();

        // when
        User found = userRepository.findByLogin("uniqueuser");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Unique User");
        assertThat(found.getLogin()).isEqualTo("uniqueuser");
    }

    @Test
    public void whenSaveUser_thenReturnSavedUser() {
        // given
        Role role = new Role();
        role.setName("ROLE_USER");
        entityManager.persist(role);

        User user = new User();
        user.setName("New User");
        user.setLogin("newuser");
        user.setPassword("Test123!@#"); // Valid password that meets all requirements
        user.setRoles(Collections.singleton(role));

        // when
        User saved = userRepository.save(user);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("New User");
        assertThat(saved.getLogin()).isEqualTo("newuser");
    }

    @Test
    public void whenFindAll_thenReturnAllUsers() {
        // given
        User user1 = createAndPersistUser("User 1", "user1");
        User user2 = createAndPersistUser("User 2", "user2");
        entityManager.flush();

        // when
        List<User> users = userRepository.findAll();

        // then
        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getLogin)
                .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    public void whenDeleteUser_thenUserShouldNotExist() {
        // given
        User user = createAndPersistUser("To Delete", "todelete");
        entityManager.flush();

        // when
        userRepository.deleteById(user.getId());

        // then
        assertThat(userRepository.findById(user.getId())).isEmpty();
    }
} 