package com.tericcabrel.authapi.repositories;

import com.tericcabrel.authapi.entities.ShoppingCartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartProductRepository extends JpaRepository<ShoppingCartProduct, Long> {
    Optional<ShoppingCartProduct> findByProductId(Long id);
}