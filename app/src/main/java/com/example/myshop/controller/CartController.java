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

/**
 * The CartController class is responsible for handling all the cart-related operations in the application.
 * It uses the ShoppingCartService to manage the products in the cart and provides endpoints to add, remove, and clear products in the cart.
 * It also uses various repositories to interact with the database.
 */
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


    /**
     * This method is mapped to the "/cart" endpoint and is responsible for displaying the cart page.
     * It adds the products in the cart and the total price of the products to the model.
     *
     * @param model the model to which the products and total price are added
     * @return the name of the cart view
     */
    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("products", shoppingService.productsInCart());
        model.addAttribute("totalPrice", shoppingService.totalPrice());

        return "cart";
    }

    /**
     * This method is mapped to the "/cart/add/{id}" endpoint and is responsible for adding a product to the cart.
     * It finds the product by its id and adds it to the cart.
     *
     * @param id the id of the product to be added to the cart
     * @return a redirect to the index page
     */
    @GetMapping("/cart/add/{id}")
    public String addProductToCart(@PathVariable("id") long id) {
        Product product = productRepository.findById(id).get();
        if (product != null) {
            shoppingService.addProduct(product);
            logger.debug(String.format("Product with id: %s added to shopping cart.", id));
        }
        return "redirect:/index";
    }

    /**
     * This method is mapped to the "/cartfromcateg/add/{id}" endpoint and is responsible for adding a product to the cart from a category.
     * It finds the product by its id and adds it to the cart.
     *
     * @param id the id of the product to be added to the cart
     * @return a redirect to the product details page
     */
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

    /**
     * This method is mapped to the "/cart/remove/{id}" endpoint and is responsible for removing a product from the cart.
     * It finds the product by its id and removes it from the cart.
     *
     * @param id the id of the product to be removed from the cart
     * @return a redirect to the cart page
     */
    @GetMapping("/cart/remove/{id}")
    public String removeProductFromCart(@PathVariable("id") long id) {
        Product product = productRepository.findById(id).get();

        if (product != null) {
            shoppingService.removeProduct(product);
            logger.debug(String.format("Product with id: %s removed from shopping cart.", id));
        }
        return "redirect:/cart";
    }

    /**
     * This method is mapped to the "/cart/clear" endpoint and is responsible for clearing all the products in the cart.
     *
     * @return a redirect to the cart page
     */
    @GetMapping("/cart/clear")
    public String clearProductsInCart() {
        shoppingService.clearProducts();

        return "redirect:/cart";
    }

}