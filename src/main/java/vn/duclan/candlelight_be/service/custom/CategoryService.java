package vn.duclan.candlelight_be.service.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.model.Category;
import vn.duclan.candlelight_be.model.Product;
import vn.duclan.candlelight_be.repository.CategoryRepository;

import vn.duclan.candlelight_be.repository.ProductRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public CategoryService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category deleteCategoryById(int categoryId) {
        try {
            // Retrieve the category by ID
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new AppException(ErrorCode.BAD_REQUEST));

            // Break associations in the many-to-many relationship
            for (Product product : category.getProductList()) {
                product.getCategoryList().remove(category);
                productRepository.save(product);
            }

            // Delete the category
            categoryRepository.delete(category);

            return category; // Return true to indicate success
        } catch (Exception e) {
            // Log the exception (optional, for debugging purposes)
            System.err.println("Failed to delete category: " + e.getMessage());
            return null; // Return false to indicate failure
        }
    }

}
