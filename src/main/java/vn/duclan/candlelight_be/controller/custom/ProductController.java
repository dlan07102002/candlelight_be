package vn.duclan.candlelight_be.controller.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import vn.duclan.candlelight_be.model.Image;
import vn.duclan.candlelight_be.model.Product;
import vn.duclan.candlelight_be.service.custom.ProductService;

@RestController
@RequestMapping("/admin/products")
@Tag(name = "Product Controller")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {

        this.productService = productService;
    }

    @PostMapping("")
    @CrossOrigin(origins = "${fe.host}")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        productService.save(product);
        return ResponseEntity.ok(productService.findTopByOrderByProductIdDesc().getProductId() + "");
    }

    @PostMapping("/images")
    @CrossOrigin(origins = "${fe.host}")
    public ResponseEntity<?> addImage(@RequestBody Image image) {

        productService.insertImage(image);
        return ResponseEntity.ok("Successful added image");
    }
}
