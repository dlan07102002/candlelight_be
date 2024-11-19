package vn.duclan.candlelight_be.repository;

import vn.duclan.candlelight_be.model.Wishlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "wishlists")
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

}