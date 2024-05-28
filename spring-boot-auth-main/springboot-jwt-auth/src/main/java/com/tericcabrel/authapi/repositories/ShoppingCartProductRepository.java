package com.tericcabrel.authapi.repositories;

import com.tericcabrel.authapi.entities.ShoppingCart;
import com.tericcabrel.authapi.entities.ShoppingCartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartProductRepository extends JpaRepository<ShoppingCartProduct, Long> {
    Optional<ShoppingCartProduct> findByProductId(Long id);

    @Modifying
    @Query("DELETE FROM ShoppingCartProduct sp WHERE sp.shoppingCart = :shoppingCart")
    void deleteAllByCart(ShoppingCart shoppingCart);
}