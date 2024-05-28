package com.tericcabrel.authapi.repositories;

import com.tericcabrel.authapi.entities.Orders;
import com.tericcabrel.authapi.entities.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUserId(Integer id);
}