package com.tericcabrel.authapi.services;

import com.tericcabrel.authapi.dtos.ProductDto;
import com.tericcabrel.authapi.dtos.ShoppingCartDto;
import com.tericcabrel.authapi.dtos.ShoppingCartProductDto;
import com.tericcabrel.authapi.entities.Product;
import com.tericcabrel.authapi.entities.ShoppingCart;
import com.tericcabrel.authapi.entities.ShoppingCartProduct;
import com.tericcabrel.authapi.entities.User;
import com.tericcabrel.authapi.repositories.ShoppingCartProductRepository;
import com.tericcabrel.authapi.repositories.ShoppingCartRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShoppingCartService {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ShoppingCartProductRepository shoppingCartProductRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ModelMapper modelMapper;

    public ShoppingCartDto getShoppingCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<ShoppingCart> shoppingCart = this.shoppingCartRepository.findByUserId(currentUser.getId());

        return shoppingCart.map(cart -> this.modelMapper.map(cart, ShoppingCartDto.class)).orElse(null);
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
                shoppingCartProduct.setPrice(quantity * product.getPrice());
                shoppingCartProductRepository.save(shoppingCartProduct);
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
                cartProduct.setPrice(quantity * cartProduct.getProduct().getPrice());
                shoppingCartProductRepository.save(cartProduct);
                return true;
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
}
