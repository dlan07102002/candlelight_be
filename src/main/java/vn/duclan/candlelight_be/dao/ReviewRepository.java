package vn.duclan.candlelight_be.dao;

import vn.duclan.candlelight_be.model.Review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "reviews")
public interface ReviewRepository extends JpaRepository<Review, Long> {

}
