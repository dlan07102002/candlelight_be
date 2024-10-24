package vn.duclan.candlelight_be.dao;

import vn.duclan.candlelight_be.model.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "categories")
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}