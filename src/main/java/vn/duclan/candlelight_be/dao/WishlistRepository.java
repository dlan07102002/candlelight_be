package vn.duclan.candlelight_be.dao;

import vn.duclan.candlelight_be.model.Wishlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

}