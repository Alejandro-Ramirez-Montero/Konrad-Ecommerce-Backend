package com.tericcabrel.authapi.dtos;

import java.util.List;

public class ShoppingCartDto {
    private Long id;
    private UserDto user;
    private List<ShoppingCartProductDto> shoppingCartProducts;

    public ShoppingCartDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ShoppingCartProductDto> getShoppingCartProducts() {
        return shoppingCartProducts;
    }

    public void setShoppingCartProducts(List<ShoppingCartProductDto> shoppingCartProducts) {
        this.shoppingCartProducts = shoppingCartProducts;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
