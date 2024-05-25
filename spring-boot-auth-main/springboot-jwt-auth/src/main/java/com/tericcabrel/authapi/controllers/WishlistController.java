package com.tericcabrel.authapi.controllers;

import com.tericcabrel.authapi.dtos.WishlistDto;
import com.tericcabrel.authapi.dtos.WishlistProductDto;
import com.tericcabrel.authapi.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {
    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WishlistDto> getWishlist() {
        WishlistDto wishlistDto = wishlistService.getWishlist();
        return new ResponseEntity<>(wishlistDto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> addProductToWishlist(@RequestBody WishlistProductDto wishlistProductDto) {
        boolean operationSuccess = wishlistService.addProductToWishlist(wishlistProductDto);
        return new ResponseEntity<>(operationSuccess, HttpStatus.CREATED);
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> editProductQuantity(@RequestBody WishlistProductDto wishlistProductDto) {
        boolean operationSuccess = wishlistService.editProductQuantity(wishlistProductDto);
        return new ResponseEntity<>(operationSuccess, HttpStatus.OK);
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> deleteProduct(@RequestBody WishlistProductDto wishlistProductDto) {
        boolean operationSuccess = wishlistService.deleteProduct(wishlistProductDto);
        return new ResponseEntity<>(operationSuccess, HttpStatus.OK);
    }

    @DeleteMapping("clearWishlist")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> clearWishlist() {
        boolean operationSuccess = wishlistService.clearWishlist();
        return new ResponseEntity<>(operationSuccess, HttpStatus.NO_CONTENT);
    }
}
