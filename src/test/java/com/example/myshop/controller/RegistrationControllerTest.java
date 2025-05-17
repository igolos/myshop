package com.example.myshop.controller;

import com.example.myshop.entity.User;
import com.example.myshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class RegistrationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(new RegistrationController(userService))
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andDo(print());
    }

    @Test
    public void testRegistration() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("userForm"))
                .andDo(print());
    }

    @Test
    public void testAddUserSuccess() throws Exception {
        Mockito.when(userService.saveUser(Mockito.any(User.class))).thenReturn(true);

        mockMvc.perform(post("/registration")
                .param("name", "Test User")
                .param("login", "testuser")
                .param("password", "TestPass123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andDo(print());
    }

    @Test
    public void testAddUserFailure() throws Exception {
        Mockito.when(userService.saveUser(Mockito.any(User.class))).thenReturn(false);

        mockMvc.perform(post("/registration")
                .param("name", "Test User")
                .param("login", "existinguser")
                .param("password", "TestPass123!"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("usernameError"))
                .andDo(print());
    }
} 