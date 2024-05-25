package com.tericcabrel.authapi.dtos;

import java.util.List;

public class WishlistDto {
    private Long id;
    private UserDto user;
    private List<WishlistProductDto> wishlistProducts;

    public WishlistDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public List<WishlistProductDto> getWishlistProducts() {
        return wishlistProducts;
    }

    public void setWishlistProducts(List<WishlistProductDto> wishlistProducts) {
        this.wishlistProducts = wishlistProducts;
    }
}
