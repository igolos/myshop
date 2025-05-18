package com.example.myshop.controller;

import com.example.myshop.Status;
import com.example.myshop.entity.Order;
import com.example.myshop.entity.OrderProductMap;
import com.example.myshop.entity.Product;
import com.example.myshop.entity.User;
import com.example.myshop.repository.OrderProductMapRepository;
import com.example.myshop.repository.OrderRepository;
import com.example.myshop.repository.ProductRepository;
import com.example.myshop.service.OrderService;
import com.example.myshop.service.ShoppingCartService;
import com.example.myshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.Map;

import static com.example.myshop.Status.ACCEPTED;

@RequestMapping("/user")
@Controller
public class OrderController {
    @Autowired
    protected UserService service;
    @Autowired
    protected ShoppingCartService shoppingCartServiceImpl;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
  protected OrderProductMapRepository orderProductMapRepository;

    @Autowired
    protected OrderService orderServiceImpl;

    public OrderController(ShoppingCartService shoppingCartServiceImpl, ProductRepository productRepository, OrderRepository orderRepository, OrderProductMapRepository orderProductMapRepository, OrderService orderServiceImpl) {
        this.shoppingCartServiceImpl = shoppingCartServiceImpl;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderProductMapRepository = orderProductMapRepository;
        this.orderServiceImpl = orderServiceImpl;

    }

    @GetMapping("/carttoorder")
    public String carttoorder() {
       String login = SecurityContextHolder.getContext().getAuthentication().getName();
       // User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       User user = service.findByLogin(login);
        Map<Product, Integer> mapcart = shoppingCartServiceImpl.getCart();
        System.out.println("MAPA=" + mapcart);
        Order newOrder = new Order();
        Status newStatus = ACCEPTED;
        newOrder.setStatus(newStatus.toString());
        newOrder.setUser(user);
        newOrder.setGrand_total(shoppingCartServiceImpl.totalPrice());
        newOrder.setDateoforder(LocalDate.now());
        newOrder.setDescription("My order");
        System.out.println("NEWORDER=" + newOrder);
        orderServiceImpl.saveOrder(newOrder);
        System.out.println("OrderAfterSave=" + newOrder);

        for (Map.Entry<Product, Integer> entry : mapcart.entrySet()) {
            OrderProductMap orderProductMap = new OrderProductMap();
            System.out.println("ID =  " + entry.getKey() + " Значение = " + entry.getValue());
            orderProductMap.setOrder(newOrder);
            orderProductMap.setOrderid(newOrder.getId());
            orderProductMap.setProduct(entry.getKey());
            orderProductMap.setQuantity(entry.getValue());
            orderProductMap.setPrice(entry.getKey().getPrice());
            orderProductMapRepository.save(orderProductMap);
        }
        shoppingCartServiceImpl.clearProducts();
        return "ordersuccess";
    }


    @GetMapping("/orders")
    public String orderList(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("MYORDER" + user + orderRepository.findById(user.getId()));
        model.addAttribute("OrdersUser", orderRepository.findByUser(user));

        return "/order";
    }

    @GetMapping("/orders/view/{id}")
    public String userid(@PathVariable("id") Long id, Model model) {
        Order order = orderServiceImpl.findOrder(id).get();
        System.out.println("Order=" + order);
        model.addAttribute("OrdersProducts", orderProductMapRepository.findAllByOrderid(order.getId()));

        return "order/order";
    }

    @GetMapping("/delete/order/{id}")
    public String deleteOrder(@PathVariable("id") long id) {
        orderServiceImpl.deleteOrder(id);
        return "redirect:/user/orders";
    }


}
