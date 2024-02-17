package com.example.myshop.controller;

import com.example.myshop.entity.User;
import com.example.myshop.repository.OrderProductMapRepository;
import com.example.myshop.repository.OrderRepository;
import com.example.myshop.repository.UserRepository;
import com.example.myshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userDetailsServiceImpl;

    private final UserRepository userRepository;


    private final OrderRepository orderRepository;


    private final OrderProductMapRepository orderProductMapRepository;


    @GetMapping
    public String adminhome() {

        return "admin/admin";
    }

    @GetMapping("/editusers")
    public String userList(Model model) {
        model.addAttribute("allUsers", userDetailsServiceImpl.findAll());
        model.addAttribute("roleUsers", userDetailsServiceImpl.findAll());

        return "admin/editusers";
    }

    @GetMapping("/editusers/{id}")
    public String userid(@PathVariable("id") Long id, Model model) {
        User usercard = userRepository.findById(id).get();
        model.addAttribute("usercard", usercard);
        return "admin/usercard";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        userDetailsServiceImpl.deleteUser(id);
        return "redirect:/admin/editusers";
    }

    @GetMapping("/orders")
    public String ordersUsers(Model model) {
        System.out.println(orderRepository.findAll());
        model.addAttribute("AllOrders", orderRepository.findAll());

        return "admin/adminordersuser";
    }

    @GetMapping("/orders/view/{id}")
    public String orderid(@PathVariable("id") Long id, Model model) {
        model.addAttribute("OrdersProducts", orderProductMapRepository.findAllByOrderid(id));

        return "admin/productoforder";
    }


}
