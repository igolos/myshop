package com.example.myshop.service;

import com.example.myshop.entity.Role;
import com.example.myshop.entity.User;
import com.example.myshop.repository.RoleRepository;
import com.example.myshop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, roleRepository, passwordEncoder);
    }

    @Test
    void findByLogin_WhenUserExists_ReturnsUser() {
        // Arrange
        User expectedUser = new User(1L, "Test User", "testuser", "password", Collections.singleton(new Role(1L, "ROLE_USER")));
        when(userRepository.findByLogin("testuser")).thenReturn(expectedUser);

        // Act
        User actualUser = userService.findByLogin("testuser");

        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser.getLogin(), actualUser.getLogin());
        verify(userRepository).findByLogin("testuser");
    }

    @Test
    void findByLogin_WhenUserDoesNotExist_ThrowsException() {
        // Arrange
        when(userRepository.findByLogin("nonexistent")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.findByLogin("nonexistent"));
        verify(userRepository).findByLogin("nonexistent");
    }

    @Test
    void deleteUser_WhenUserExists_ReturnsTrue() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        // Act
        boolean result = userService.deleteUser(1L);

        // Assert
        assertTrue(result);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_WhenUserDoesNotExist_ReturnsFalse() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean result = userService.deleteUser(1L);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void findAll_ReturnsAllUsers() {
        // Arrange
        List<User> expectedUsers = List.of(
            new User(1L, "User1", "user1", "pass1", Collections.singleton(new Role(1L, "ROLE_USER"))),
            new User(2L, "User2", "user2", "pass2", Collections.singleton(new Role(1L, "ROLE_USER")))
        );
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.findAll();

        // Assert
        assertEquals(expectedUsers.size(), actualUsers.size());
        verify(userRepository).findAll();
    }

    @Test
    void saveUser_WhenLoginNotTaken_ReturnsTrue() {
        // Arrange
        User newUser = new User(null, "New User", "newuser", "password", null);
        when(userRepository.findByLogin("newuser")).thenReturn(null);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(new Role(1L, "ROLE_USER")));

        // Act
        boolean result = userService.saveUser(newUser);

        // Assert
        assertTrue(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void saveUser_WhenLoginTaken_ReturnsFalse() {
        // Arrange
        User newUser = new User(null, "New User", "existinguser", "password", null);
        when(userRepository.findByLogin("existinguser")).thenReturn(new User());

        // Act
        boolean result = userService.saveUser(newUser);

        // Assert
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_UpdatesUserSuccessfully() {
        // Arrange
        User userToUpdate = new User(1L, "Updated User", "updateduser", "newpassword", null);
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");

        // Act
        userService.updateUser(1L, userToUpdate);

        // Assert
        verify(userRepository).save(userToUpdate);
        assertEquals(1L, userToUpdate.getId());
    }

    @Test
    void loadUserByUsername_WhenUserExists_ReturnsUser() {
        // Arrange
        User expectedUser = new User(1L, "Test User", "testuser", "password", Collections.singleton(new Role(1L, "ROLE_USER")));
        when(userRepository.findByLogin("testuser")).thenReturn(expectedUser);

        // Act
        User actualUser = (User) userService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser.getLogin(), actualUser.getLogin());
        verify(userRepository).findByLogin("testuser");
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ThrowsException() {
        // Arrange
        when(userRepository.findByLogin("nonexistent")).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("nonexistent"));
        verify(userRepository).findByLogin("nonexistent");
    }
} 