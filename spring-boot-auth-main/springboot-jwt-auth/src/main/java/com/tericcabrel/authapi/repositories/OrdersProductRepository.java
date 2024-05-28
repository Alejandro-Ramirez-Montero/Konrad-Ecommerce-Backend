package com.tericcabrel.authapi.repositories;

import com.tericcabrel.authapi.entities.OrdersProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersProductRepository extends JpaRepository<OrdersProduct, Long> {
    Optional<OrdersProduct> findByProductId(Long id);
}