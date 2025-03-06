package vn.duclan.candlelight_be.service.custom;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.duclan.candlelight_be.model.Image;
import vn.duclan.candlelight_be.model.Product;
import vn.duclan.candlelight_be.repository.ImageRepository;
import vn.duclan.candlelight_be.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {
    final ProductRepository productRepository;
    final ImageRepository imageRepository;

    public void insertImage(Image image) {
        System.out.println(image.getImageData());
        Product product = productRepository
                .findById(Integer.parseInt(image.getProductId()))
                .get();
        System.out.println(product);
        image.setProduct(product);
        image.setImageData("data:image/png;base64," + image.getImageData());
        imageRepository.save(image);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public Product findTopByOrderByProductIdDesc() {
        return productRepository.findTopByOrderByProductIdDesc()
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}
