package com.example.myshop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@Entity
@Data
@Table(name = "\"order\"")
public class Order {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private BigDecimal grand_total;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order")
    private List<OrderProductMap> orderproductmaps;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;


    private String status;


    private LocalDate dateoforder;


    private String description;



    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", user=" + user +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }
}
