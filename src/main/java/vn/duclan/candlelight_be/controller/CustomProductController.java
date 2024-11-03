package vn.duclan.candlelight_be.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.duclan.candlelight_be.dao.ImageRepository;
import vn.duclan.candlelight_be.dao.ProductRepository;
import vn.duclan.candlelight_be.model.Image;
import vn.duclan.candlelight_be.model.Product;

@RestController
@RequestMapping("/admin/products")
public class CustomProductController {

    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public CustomProductController(ProductRepository productRepository,
            ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
    }

    @PostMapping("")
    @CrossOrigin(origins = "http://localhost:5173") // Allow request from FE(Port 5173)
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        productRepository.save(product);
        return ResponseEntity.ok(productRepository.findTopByOrderByProductIdDesc().getProductId()
                + "");
    }

    @PostMapping("/images")
    @CrossOrigin(origins = "http://localhost:5173")
    // Allow request from FE(Port 5173)
    public ResponseEntity<?> addImage(@RequestBody Image image) {
        Product product = productRepository.findById(Integer.parseInt(image.getProductId())).get();
        image.setProduct(product);
        image.setImageData("data:image/png;base64," + image.getImageData());
        imageRepository.save(image);
        // imageRepository.save(image);
        return ResponseEntity.ok("Successful added image");

    }
}
