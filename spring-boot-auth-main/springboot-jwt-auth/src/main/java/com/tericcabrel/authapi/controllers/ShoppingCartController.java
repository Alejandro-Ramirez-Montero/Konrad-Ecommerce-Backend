package com.tericcabrel.authapi.controllers;

import com.tericcabrel.authapi.dtos.ShoppingCartDto;
import com.tericcabrel.authapi.dtos.ShoppingCartProductDto;
import com.tericcabrel.authapi.services.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ShoppingCartDto> getShoppingCart() {
        ShoppingCartDto shoppingCartDto = shoppingCartService.getShoppingCart();
        return new ResponseEntity<>(shoppingCartDto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> addProductToCart(@RequestBody ShoppingCartProductDto shoppingCartProductDto) {
        boolean operationSuccess = shoppingCartService.addProductToCart(shoppingCartProductDto);
        return new ResponseEntity<>(operationSuccess, HttpStatus.CREATED);
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> editProductQuantity(@RequestBody ShoppingCartProductDto shoppingCartProductDto) {
        boolean operationSuccess = shoppingCartService.editProductQuantity(shoppingCartProductDto);
        return new ResponseEntity<>(operationSuccess, HttpStatus.OK);
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteProduct(@RequestBody ShoppingCartProductDto shoppingCartProductDto) {
        boolean operationSuccess = shoppingCartService.deleteProduct(shoppingCartProductDto);
        return new ResponseEntity<>(operationSuccess, HttpStatus.OK);
    }

    @DeleteMapping("clearShoppingCart")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> clearShoppingCart() {
        boolean operationSuccess = shoppingCartService.clearShoppingCart();
        return new ResponseEntity<>(operationSuccess, HttpStatus.NO_CONTENT);
    }
}
