package vn.duclan.candlelight_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import vn.duclan.candlelight_be.model.Image;

@RepositoryRestResource(path = "images")
public interface ImageRepository extends JpaRepository<Image, Integer> {}
