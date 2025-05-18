package com.example.myshop.controller;

import com.example.myshop.entity.User;
import com.example.myshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "user")
@RequiredArgsConstructor
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    private final UserService service;
    @GetMapping
    public String userhome(Model model) {
        return "user/userhome";
    }

    @GetMapping("/update")
    public String editProduct(Model model) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("userUpdate", user);

        return "user/updateprofile";

    }

    /***
     *
     * @param user
     * @return
     */
    @PostMapping("/update")
    @Transactional
    public String updateProduct(@ModelAttribute("userUpdate") User user) {

        Long id = ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        service.updateUser(id, user);

        return "redirect:/user";
    }

//    @GetMapping
//    public ResponseEntity<List<User>> getAllUsers() {
//        List<User> users = service.findAll();
//        logger.info("getting car list: {}", users);
//        return new ResponseEntity<>(users, HttpStatus.OK);
//    }

    /**
     *
     * @param userId
     * @return
     */
//    @GetMapping("/{login}")
//    public User getUserByLogin(@PathVariable("login") String login) {
//        return service.findByLogin(login);
//    }

//    @PostMapping
//    public ResponseEntity<User> createUser(@RequestBody User user) {
//        service.saveUser(user);
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Location", "/api/v1/users/" + user.getId());
//        return new ResponseEntity<>(null, headers, HttpStatus.CREATED);
//    }

//    @PutMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<User> updateUser(@RequestBody User user,@RequestBody Long id) {
//        service.updateUser(id,user);
//        return ResponseEntity.ok().build();
//    }
//
//    @DeleteMapping("/{userId}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<User> deleteUserById(@PathVariable long userId) {
//        service.deleteUser(userId);
//        return ResponseEntity.ok().build();
//    }




}
