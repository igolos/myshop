package com.example.myshop.controller;

import com.example.myshop.entity.Product;
import com.example.myshop.repository.ProductRepository;
import com.example.myshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);


    private final ProductService productServiceImpl;



    private final ProductRepository productRepository;



    @GetMapping
    public String viewProducts(Model model) {
        model.addAttribute("allproducts", productServiceImpl.findAll());
        return "admin/product";
    }

    @GetMapping("/new")
    public String newProduct(Model model) {
        model.addAttribute("newProductForm", new Product());

        return "admin/newproduct";
    }

    @PostMapping("/new")
    public String createNewProduct(@ModelAttribute("newProductForm") Product newProductForm, BindingResult bindingResult, Model model) {



        if (bindingResult.hasErrors()) {
            logger.error(String.valueOf(bindingResult.getFieldError()));
            return "admin/newproduct";
        }

        if (!productServiceImpl.saveProduct(newProductForm)) {
            model.addAttribute("productNameError", "This name is taken");
            return "admin/newproduct";
        }
        logger.debug(String.format("Product with id: %s successfully created.", newProductForm.getId()));
        return "redirect:/admin/product";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") long id) {
        productServiceImpl.deleteProduct(id);
        logger.debug(String.format("Product with id: %s has been successfully deleted.", id));
        return "redirect:/admin/product";
    }

    @GetMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") long id, Model model) {
        if (!productRepository.existsById(id)) {
            return "redirect:admin/product";
        }
        model.addAttribute("editProduct", productRepository.findById(id).get());

        return "admin/editproduct";
    }

    @PostMapping("/edit/{id}")
    @Transactional
    public String updateProduct(@PathVariable("id") long id, @ModelAttribute("editProduct") Product editProduct, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            logger.error(String.valueOf(bindingResult.getFieldError()));
            return "admin/editproduct";
        }
        productServiceImpl.updateProduct(id, editProduct);
        logger.debug(String.format("Product with id: %s has been successfully update.", id));
        return "redirect:/admin/product";
    }

}
