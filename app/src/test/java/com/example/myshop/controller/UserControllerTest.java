package com.example.myshop.controller;

import com.example.myshop.entity.User;
import com.example.myshop.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    public void testUserHome() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/userhome"))
                .andDo(print());
    }

    @Test
    public void testUpdateProfile() throws Exception {
        // Mock security context
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setLogin("testuser");
        Mockito.when(authentication.getPrincipal()).thenReturn(user);

        mockMvc.perform(get("/user/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/updateprofile"))
                .andExpect(model().attributeExists("userUpdate"))
                .andDo(print());
    }

    @Test
    public void testUpdateProfilePost() throws Exception {
        // Mock security context
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        
        User user = new User();
        user.setId(1L);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);

        mockMvc.perform(post("/user/update")
                .param("name", "Updated User")
                .param("login", "updateduser")
                .param("password", "NewPass123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user"))
                .andDo(print());
    }
} 