package com.tericcabrel.authapi.services;

import com.tericcabrel.authapi.dtos.*;
import com.tericcabrel.authapi.entities.*;
import com.tericcabrel.authapi.repositories.ProductRepository;
import com.tericcabrel.authapi.repositories.ShoppingCartProductRepository;
import com.tericcabrel.authapi.repositories.ShoppingCartRepository;
import com.tericcabrel.authapi.repositories.WishlistRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
public class ShoppingCartService {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ShoppingCartProductRepository shoppingCartProductRepository;
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private WishlistService wishlistService;
    @Autowired
    private ModelMapper modelMapper;

    public ShoppingCartDto getShoppingCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<ShoppingCart> shoppingCart = this.shoppingCartRepository.findByUserId(currentUser.getId());

        return shoppingCart.map(cart -> this.modelMapper.map(cart, ShoppingCartDto.class)).orElse(null);
    }

    public boolean isProductAlreadyInCart(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<ShoppingCart> shoppingCart = this.shoppingCartRepository.findByUserId(currentUser.getId());
        return shoppingCart.map(cart -> cart.getShoppingCartProducts().stream().anyMatch(cartProduct -> cartProduct.getProduct().getId().equals(productId))).orElse(false);
    }

    public boolean addProductToCart(ShoppingCartProductDto shoppingCartProductDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<ShoppingCart> shoppingCart = this.shoppingCartRepository.findByUserId(currentUser.getId());
        ProductDto productDto = productService.getProductById(shoppingCartProductDto.getProduct().getId());

        boolean productInCart = false;
        int quantity = shoppingCartProductDto.getQuantity();

        if(shoppingCart.isPresent() && productDto != null && quantity > 0 && quantity < 100){
            Product product = this.modelMapper.map(productService.getProductById(shoppingCartProductDto.getProduct().getId()), Product.class);

            for (ShoppingCartProduct cartProduct : shoppingCart.get().getShoppingCartProducts()){
                if (cartProduct.getProduct().getId().equals(shoppingCartProductDto.getProduct().getId())) {
                    productInCart = true;
                    break;
                }
            }
            if(!productInCart){
                ShoppingCartProduct shoppingCartProduct = new ShoppingCartProduct();
                shoppingCartProduct.setShoppingCart(shoppingCart.get());
                shoppingCartProduct.setProduct(product);
                shoppingCartProduct.setQuantity(quantity);
                BigDecimal totalPrice = BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(product.getPrice()));
                shoppingCartProduct.setPrice(totalPrice.doubleValue());
                shoppingCartProductRepository.saveAndFlush(shoppingCartProduct);
                return true;
            }
        }
        return false;
    }

    public boolean editProductQuantity(ShoppingCartProductDto shoppingCartProductDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<ShoppingCart> shoppingCart = this.shoppingCartRepository.findByUserId(currentUser.getId());
        int quantity = shoppingCartProductDto.getQuantity();
        if(shoppingCart.isPresent() && quantity > 0 && quantity < 100){
            Optional<ShoppingCartProduct> matchedShoppingCartProduct =  shoppingCart.get().getShoppingCartProducts().stream().filter(cartProductEntity -> cartProductEntity.getProduct().getId().equals(shoppingCartProductDto.getProduct().getId())).findFirst();
            if(matchedShoppingCartProduct.isPresent()){
                ShoppingCartProduct cartProduct = matchedShoppingCartProduct.get();
                cartProduct.setQuantity(quantity);
                BigDecimal totalPrice = BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(cartProduct.getProduct().getPrice()));
                cartProduct.setPrice(totalPrice.doubleValue());
                shoppingCartProductRepository.saveAndFlush(cartProduct);
                return true;
            }
        }
        return false;
    }

    public boolean addProductQuantity(ShoppingCartProductDto shoppingCartProductDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<ShoppingCart> shoppingCart = this.shoppingCartRepository.findByUserId(currentUser.getId());
        if(shoppingCart.isPresent()){
            Optional<ShoppingCartProduct> matchedShoppingCartProduct =  shoppingCart.get().getShoppingCartProducts().stream().filter(cartProductEntity -> cartProductEntity.getProduct().getId().equals(shoppingCartProductDto.getProduct().getId())).findFirst();
            if(matchedShoppingCartProduct.isPresent()){
                ShoppingCartProduct cartProduct = matchedShoppingCartProduct.get();
                int quantity = shoppingCartProductDto.getQuantity() + cartProduct.getQuantity();
                if(quantity > 0 && quantity < 100){
                    cartProduct.setQuantity(quantity);
                    BigDecimal totalPrice = BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(cartProduct.getProduct().getPrice()));
                    cartProduct.setPrice(totalPrice.doubleValue());
                    shoppingCartProductRepository.saveAndFlush(cartProduct);
                return true;
                }
            }
        }
        return false;
    }

    @Transactional
    public boolean deleteProduct(ShoppingCartProductDto shoppingCartProductDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<ShoppingCart> shoppingCart = this.shoppingCartRepository.findByUserId(currentUser.getId());
        if(shoppingCart.isPresent()){
            Optional<ShoppingCartProduct> matchedShoppingCartProduct =  shoppingCart.get().getShoppingCartProducts().stream().filter(cartProductEntity -> cartProductEntity.getProduct().getId().equals(shoppingCartProductDto.getProduct().getId())).findFirst();
            if(matchedShoppingCartProduct.isPresent()){
                ShoppingCart cart = shoppingCart.get();
                cart.getShoppingCartProducts().remove(matchedShoppingCartProduct.get());
                shoppingCartRepository.save(cart);
                return  true;
            }
        }
        return false;
    }

    @Transactional
    public boolean clearShoppingCart(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<ShoppingCart> shoppingCart = this.shoppingCartRepository.findByUserId(currentUser.getId());
        if(shoppingCart.isPresent()){
            ShoppingCart cart = shoppingCart.get();
            cart.getShoppingCartProducts().clear();
            shoppingCartRepository.save(cart);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean addWishlistToCart(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<ShoppingCart> shoppingCart = this.shoppingCartRepository.findByUserId(currentUser.getId());
        Optional<Wishlist> wishlist = this.wishlistRepository.findByUserId(currentUser.getId());

        if(shoppingCart.isPresent() && wishlist.isPresent()){
            for(WishlistProduct wishlistProduct : wishlist.get().getWishlistProducts()){
                Optional<ShoppingCartProduct> matchedShoppingCartProduct =  shoppingCart.get().getShoppingCartProducts().stream().filter(cartProductEntity -> cartProductEntity.getProduct().getId().equals(wishlistProduct.getProduct().getId())).findFirst();
                int quantity = wishlistProduct.getQuantity();
                if(matchedShoppingCartProduct.isPresent()){
                    quantity += matchedShoppingCartProduct.get().getQuantity();
                    if(quantity > 0 && quantity < 100){
                        matchedShoppingCartProduct.get().setQuantity(quantity);
                        matchedShoppingCartProduct.get().setPrice(BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(matchedShoppingCartProduct.get().getProduct().getPrice())).doubleValue());
                    }
                }
                else{
                    ShoppingCartProduct cartProduct = new ShoppingCartProduct();
                    cartProduct.setProduct(this.productRepository.findById(wishlistProduct.getProduct().getId()).get());
                    cartProduct.setShoppingCart(shoppingCart.get());
                    cartProduct.setQuantity(quantity);
                    cartProduct.setPrice(wishlistProduct.getPrice());
                    shoppingCart.get().getShoppingCartProducts().add(cartProduct);
                }
            }
            this.shoppingCartRepository.saveAndFlush(shoppingCart.get());
            //this.wishlistService.clearWishlist();
            return true;
        }
        return false;
    }

    public double getShoppingCartTotal(){
        double total = 0;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<ShoppingCart> shoppingCart = this.shoppingCartRepository.findByUserId(currentUser.getId());

        if(shoppingCart.isPresent()){
            for(ShoppingCartProduct cartProduct : shoppingCart.get().getShoppingCartProducts()){
                total += cartProduct.getPrice();
            }
            double shippingPrice = 5;
            double taxes = 0.13;
            double totalTax = BigDecimal.valueOf(total).multiply(BigDecimal.valueOf(0.13)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            total += shippingPrice + totalTax;
        }

        return total;
    }
}
