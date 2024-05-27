package com.tericcabrel.authapi.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true, length = 100, nullable = false)
    private String path;
    @Column(length = 1000)
    private String description;
    private String image;
    private double price;
    private String category;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<WishlistProduct> wishlistProducts;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<ShoppingCartProduct> shoppingCartProducts;

    public Product() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<ShoppingCartProduct> getShoppingCartProducts() {
        return shoppingCartProducts;
    }

    public void setShoppingCartProducts(List<ShoppingCartProduct> shoppingCartProducts) {
        this.shoppingCartProducts = shoppingCartProducts;
    }

    public List<WishlistProduct> getWishlistProducts() {
        return wishlistProducts;
    }

    public void setWishlistProducts(List<WishlistProduct> wishlistProducts) {
        this.wishlistProducts = wishlistProducts;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
