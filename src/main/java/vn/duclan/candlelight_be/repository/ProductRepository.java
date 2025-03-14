package vn.duclan.candlelight_be.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import vn.duclan.candlelight_be.model.Product;

@RepositoryRestResource(path = "products")

public interface ProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByProductNameContaining(@RequestParam("productName") String productName, Pageable pageable);

    Page<Product> findByCategoryList_CategoryId(@RequestParam("categoryId") int categoryId, Pageable pageable);

    Page<Product> findByProductNameContainingAndCategoryList_CategoryId(
            @RequestParam("productName") String productName,
            @RequestParam("categoryId") int categoryId,
            Pageable pageable);

    Optional<Product> findTopByOrderByProductIdDesc();

    @Query("SELECT COUNT(p) FROM Product p")
    public long countProducts();
}
