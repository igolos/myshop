package com.example.myshop.controller;

import com.example.myshop.entity.Product;
import com.example.myshop.repository.ProductRepository;
import com.example.myshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Log
public class HomeController {

    private final ProductService service;


    private final ProductRepository repository;

    @GetMapping(value = {"/", "/index", "/home"})
    public String home(Model model) {
        List<Product> uniqueProducts = service.findAll().stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Product::getName))), ArrayList::new
                ));
        model.addAttribute("products", uniqueProducts);
        model.addAttribute("productsCount", service.productsCount());
        return "index";
    }

    @GetMapping(value = {"/about"})
    public String about() {

        return "about";
    }

    @RequestMapping("/searchByProduct/{id}")
    public String homePost(@PathVariable("id") long productId, Model model) {
        Product product = repository.findById(productId).get();
        model.addAttribute("productsCount", service.productsCount());
        model.addAttribute("product", product);
        return "productfromcategory";
    }

}
