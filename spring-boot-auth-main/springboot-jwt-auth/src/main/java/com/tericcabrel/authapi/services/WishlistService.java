package com.tericcabrel.authapi.services;

import com.tericcabrel.authapi.dtos.ProductDto;
import com.tericcabrel.authapi.dtos.ShoppingCartProductDto;
import com.tericcabrel.authapi.dtos.WishlistDto;
import com.tericcabrel.authapi.dtos.WishlistProductDto;
import com.tericcabrel.authapi.entities.*;
import com.tericcabrel.authapi.repositories.ProductRepository;
import com.tericcabrel.authapi.repositories.WishlistProductRepository;
import com.tericcabrel.authapi.repositories.WishlistRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class WishlistService {
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private WishlistProductRepository wishlistProductRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ModelMapper modelMapper;
    @PersistenceContext // or even @Autowired
    private EntityManager entityManager;

    public WishlistDto getWishlist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Wishlist> wishlist = this.wishlistRepository.findByUserId(currentUser.getId());

        return wishlist.map(wishlistEntity -> this.modelMapper.map(wishlistEntity, WishlistDto.class)).orElse(null);
    }

    public boolean isProductAlreadyInWishlist(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Wishlist> wishlist = this.wishlistRepository.findByUserId(currentUser.getId());
        return wishlist.map(wishlistEntity -> wishlistEntity.getWishlistProducts().stream().anyMatch(wishlistProduct -> wishlistProduct.getProduct().getId().equals(productId))).orElse(false);
    }


    public boolean addProductToWishlist(WishlistProductDto wishlistProductDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Wishlist> wishlist = this.wishlistRepository.findByUserId(currentUser.getId());
        ProductDto productDto = productService.getProductById(wishlistProductDto.getProduct().getId());

        boolean productInWishlist = false;
        int quantity = wishlistProductDto.getQuantity();

        if(wishlist.isPresent() && productDto != null && quantity > 0 && quantity < 100){
            Product product = this.modelMapper.map(productService.getProductById(wishlistProductDto.getProduct().getId()), Product.class);

            for (WishlistProduct wishlistProduct : wishlist.get().getWishlistProducts()){
                if (wishlistProduct.getProduct().getId().equals(wishlistProductDto.getProduct().getId())) {
                    productInWishlist = true;
                    break;
                }
            }
            if(!productInWishlist){
                WishlistProduct wishlistProduct = new WishlistProduct();
                wishlistProduct.setWishlist(wishlist.get());
                wishlistProduct.setProduct(product);
                wishlistProduct.setQuantity(quantity);
                BigDecimal totalPrice = BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(product.getPrice()));
                wishlistProduct.setPrice(totalPrice.doubleValue());
                wishlistProductRepository.saveAndFlush(wishlistProduct);
                return true;
            }
        }
        return false;
    }

//    @Transactional
//    public boolean addProductToWishlist(WishlistProductDto wishlistProductDto){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser = (User) authentication.getPrincipal();
//
//        Optional<Wishlist> wishlist = this.wishlistRepository.findByUserId(currentUser.getId());
//        Optional<Product> product = this.productRepository.findById((wishlistProductDto.getProduct().getId()));
//
//        boolean productInWishlist = false;
//        int quantity = wishlistProductDto.getQuantity();
//
//        if(wishlist.isPresent() && product.isPresent() && quantity > 0 && quantity < 100){
//
//            for (WishlistProduct wishlistProduct : wishlist.get().getWishlistProducts()){
//                if (wishlistProduct.getProduct().getId().equals(wishlistProductDto.getProduct().getId())) {
//                    productInWishlist = true;
//                    break;
//                }
//            }
//            if(!productInWishlist){
//                WishlistProduct wishlistProduct = new WishlistProduct();
//                wishlistProduct.setWishlist(wishlist.get());
//                wishlistProduct.setProduct(product.get());
//                wishlistProduct.setQuantity(quantity);
//                BigDecimal totalPrice = BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(product.get().getPrice()));
//                wishlistProduct.setPrice(totalPrice.doubleValue());
//                wishlist.get().getWishlistProducts().add(wishlistProduct);
//                wishlistProductRepository.saveAndFlush(wishlistProduct);
//                //wishlistProductRepository.saveAndFlush(wishlistProduct);
//                //wishlistRepository.saveAndFlush(wishlist.get());
//                return true;
//            }
//        }
//        return false;
//    }

    public boolean editProductQuantity(WishlistProductDto wishlistProductDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Wishlist> wishlist = this.wishlistRepository.findByUserId(currentUser.getId());
        int quantity = wishlistProductDto.getQuantity();
        if(wishlist.isPresent() && quantity > 0 && quantity < 100){
            Optional<WishlistProduct> matchedWishlistProduct =  wishlist.get().getWishlistProducts().stream().filter(wishlistProductEntity -> wishlistProductEntity.getProduct().getId().equals(wishlistProductDto.getProduct().getId())).findFirst();
            if(matchedWishlistProduct.isPresent()){
                WishlistProduct wishlistProduct = matchedWishlistProduct.get();
                wishlistProduct.setQuantity(quantity);
                wishlistProduct.setPrice(BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(wishlistProduct.getProduct().getPrice())).doubleValue());
                wishlistProductRepository.saveAndFlush(wishlistProduct);
                return true;
            }
        }
        return false;
    }

//    @Transactional
//    public boolean deleteProduct(WishlistProductDto wishlistProductDto){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        User currentUser = (User) authentication.getPrincipal();
//
//        Optional<Wishlist> wishlist = this.wishlistRepository.findByUserId(currentUser.getId());
//        if(wishlist.isPresent()){
//            Optional<WishlistProduct> matchedWishlistProduct =  wishlist.get().getWishlistProducts().stream().filter(wishlistProductEntity -> wishlistProductEntity.getProduct().getId().equals(wishlistProductDto.getProduct().getId())).findFirst();
//            if(matchedWishlistProduct.isPresent()){
//                Wishlist wishlist1 = wishlist.get();
//                //wishlist1.getWishlistProducts().remove(matchedWishlistProduct.get());
//                //wishlist1.setWishlistProducts(wishlist1.getWishlistProducts().remove(matchedWishlistProduct.get()));
////                this.wishlistRepository.saveAndFlush(wishlist1);
//                System.out.println(matchedWishlistProduct.get().getId());
//                this.wishlistProductRepository.delete(matchedWishlistProduct.get());
//                //this.wishlistProductRepository.flush();
////                if(wishlist1.getWishlistProducts().isEmpty()){
////                    wishlistProductRepository.deleteAllByWishlist(wishlist1);
////                }
////                else{
////                    wishlistProductRepository.deleteNotInWishlist(wishlist1, wishlist1.getWishlistProducts());
////                }
//                return  true;
//            }
//        }
//        return false;
//    }

    @Transactional
    public boolean deleteProduct(WishlistProductDto wishlistProductDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Wishlist> wishlist = this.wishlistRepository.findByUserId(currentUser.getId());
        if(wishlist.isPresent()){
            Optional<WishlistProduct> matchedWishlistProduct =  wishlist.get().getWishlistProducts().stream().filter(wishlistProductEntity -> wishlistProductEntity.getProduct().getId().equals(wishlistProductDto.getProduct().getId())).findFirst();
            if(matchedWishlistProduct.isPresent()){
                Wishlist list = wishlist.get();
                //list.getWishlistProducts().remove(matchedWishlistProduct.get());
                //wishlistRepository.save(list);
                //WishlistProduct wishlistProductToDelete = entityManager.find(WishlistProduct.class, matchedWishlistProduct.get().getId());
                //entityManager.remove(wishlistProductToDelete);
                //list.getWishlistProducts().remove(matchedWishlistProduct.get());
                return  true;
            }
        }
        return false;
    }

    @Transactional()
    public boolean clearWishlist(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Wishlist> wishlist = this.wishlistRepository.findByUserId(currentUser.getId());
        if(wishlist.isPresent()){
            Wishlist wishlist1 = wishlist.get();
            wishlist1.getWishlistProducts().clear();
//            wishlistRepository.saveAndFlush(wishlist1);
//            wishlistProductRepository.deleteAllByWishlist(wishlist1);
            return true;
        }
        return false;
    }
}
