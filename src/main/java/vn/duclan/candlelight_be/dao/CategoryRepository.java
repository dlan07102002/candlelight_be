package vn.duclan.candlelight_be.dao;

import vn.duclan.candlelight_be.model.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestResource(path = "categories")
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Page<Category> findByProductList_ProductId(@RequestParam("categoryId") int
    // categoryId, Pageable pageable);
}