package com.example.myshop.repository;

import com.example.myshop.entity.OrderProductMap;
import com.example.myshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderProductMapRepository extends JpaRepository<OrderProductMap, Long> {
    List<OrderProductMap> findAllByOrderid(Long id);
}
