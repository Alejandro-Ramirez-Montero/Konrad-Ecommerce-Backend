package com.tericcabrel.authapi.controllers;

import com.tericcabrel.authapi.dtos.OrdersDto;
import com.tericcabrel.authapi.dtos.ShoppingCartDto;
import com.tericcabrel.authapi.dtos.ShoppingCartProductDto;
import com.tericcabrel.authapi.services.OrdersService;
import com.tericcabrel.authapi.services.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @GetMapping("getAllOrders")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<List<OrdersDto>> getAllOrders() {
        List<OrdersDto> ordersDtos = ordersService.getAllOrders();
        return new ResponseEntity<>(ordersDtos, HttpStatus.OK);
    }

    @GetMapping("getAllOrdersByUser")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrdersDto>> getAllOrdersByUser() {
        List<OrdersDto> ordersDtos = ordersService.getAllOrdersByUser();
        return new ResponseEntity<>(ordersDtos, HttpStatus.OK);
    }

    @GetMapping("getOrderById/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<OrdersDto> getOrderById(@PathVariable Long id) {
        OrdersDto ordersDto = ordersService.getOrderById(id);
        return new ResponseEntity<>(ordersDto, HttpStatus.OK);
    }

    @GetMapping("getOrderByIdAsUser/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrdersDto> getOrderByIdAsUser(@PathVariable Long id) {
        OrdersDto ordersDto = ordersService.getOrderByIdAsUser(id);
        return new ResponseEntity<>(ordersDto, HttpStatus.OK);
    }


    @PatchMapping("editStatus")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<Boolean> editStatus(@RequestBody OrdersDto ordersDto) {
        boolean operationSuccess = ordersService.editOrderStatus(ordersDto);
        return new ResponseEntity<>(operationSuccess, HttpStatus.OK);
    }

    @PostMapping("convertCartToOrder")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> addWishlistToCart(@RequestBody OrdersDto ordersDto) {
        boolean operationSuccess = ordersService.convertCartToOrder(ordersDto);
        return new ResponseEntity<>(operationSuccess, HttpStatus.CREATED);
    }
}
