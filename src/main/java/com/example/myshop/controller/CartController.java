package com.example.myshop.controller;

import com.example.myshop.entity.Product;
import com.example.myshop.repository.OrderProductMapRepository;
import com.example.myshop.repository.OrderRepository;
import com.example.myshop.repository.ProductRepository;
import com.example.myshop.repository.UserRepository;
import com.example.myshop.service.OrderService;
import com.example.myshop.service.ShoppingCartService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);


    private final ShoppingCartService shoppingService;


    private final ProductRepository productRepository;


    private final OrderRepository orderRepository;


    private final OrderProductMapRepository orderProductMapRepository;


    private final OrderService orderService;


    private final UserRepository userRepository;



    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("products", shoppingService.productsInCart());
        model.addAttribute("totalPrice", shoppingService.totalPrice());

        return "cart";
    }

    @GetMapping("/cart/add/{id}")
    public String addProductToCart(@PathVariable("id") long id) {
        Product product = productRepository.findById(id).get();
        if (product != null) {
            shoppingService.addProduct(product);
            logger.debug(String.format("Product with id: %s added to shopping cart.", id));
        }
        return "redirect:/index";
    }

    @GetMapping("/cartfromcateg/add/{id}")
    public String addProdToCart(@PathVariable("id") long id) {
        Product product = productRepository.findById(id).get();
        if (product != null) {
            shoppingService.addProduct(product);
            logger.debug(String.format("Product with id: %s added to shopping cart.", id));
        }
        String red = "redirect:/searchById/" + product.getId();
        return red;
    }

    @GetMapping("/cart/remove/{id}")
    public String removeProductFromCart(@PathVariable("id") long id) {
        Product product = productRepository.findById(id).get();

        if (product != null) {
            shoppingService.removeProduct(product);
            logger.debug(String.format("Product with id: %s removed from shopping cart.", id));
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart/clear")
    public String clearProductsInCart() {
        shoppingService.clearProducts();

        return "redirect:/cart";
    }

}