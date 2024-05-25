package com.tericcabrel.authapi.repositories;

import com.tericcabrel.authapi.entities.Wishlist;
import com.tericcabrel.authapi.entities.WishlistProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistProductRepository extends JpaRepository<WishlistProduct, Long> {
    @Modifying
    @Query("DELETE FROM WishlistProduct wp WHERE wp.wishlist = :wishlist")
    void deleteAllByWishlist(Wishlist wishlist);

    @Modifying
    @Query("DELETE FROM WishlistProduct wp WHERE wp.wishlist = :wishlist AND wp NOT IN :wishlistProducts")
    void deleteNotInWishlist(Wishlist wishlist, List<WishlistProduct> wishlistProducts);
}
