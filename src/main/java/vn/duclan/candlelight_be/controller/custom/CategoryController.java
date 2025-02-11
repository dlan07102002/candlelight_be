package vn.duclan.candlelight_be.controller.custom;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import vn.duclan.candlelight_be.dto.response.APIResponse;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.model.Category;
import vn.duclan.candlelight_be.service.custom.CategoryService;

@RestController
@RequestMapping("/api/category")
@Slf4j
@Tag(name = "CategoryController")
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @DeleteMapping("/{id}")
    public APIResponse<Category> deleteCategoryById(@PathVariable int id) {
        APIResponse<Category> apiResponse = new APIResponse<>();

        try {
            Category result = categoryService.deleteCategoryById(id);
            apiResponse.setResult(result);
        } catch (Exception e) {
            apiResponse.setCode(ErrorCode.BAD_REQUEST.getCode());
            apiResponse.setMessage("FAILED");
        }

        return apiResponse;
    }

}
