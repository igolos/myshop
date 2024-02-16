package com.example.myshop.service;

import com.example.myshop.entity.Product;
import com.example.myshop.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    protected ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional
    public boolean saveProduct(Product product) {
        Product productFromDB = productRepository.findByName(product.getName());

        if (productFromDB != null) {
            return false;
        }
        productRepository.save(product);
        return true;
    }

    @Transactional
    public boolean deleteProduct(Long productId) {
        if (productRepository.findById(productId).isPresent()) {
            productRepository.deleteById(productId);
            return true;
        }
        return false;
    }

    @Transactional
    public void updateProduct(Long id, Product product) {
        product.setId(id);
        productRepository.save(product);
    }

    @Transactional
    public long productsCount() {
        return productRepository.count();
    }





    public Product findById(long id) {
        return productRepository.findById(id).get();
    }

    @Transactional
    public long count() {
        return productRepository.count();
    }
}
