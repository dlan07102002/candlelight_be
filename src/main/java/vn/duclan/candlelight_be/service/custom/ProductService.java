package vn.duclan.candlelight_be.service.custom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.duclan.candlelight_be.repository.ImageRepository;
import vn.duclan.candlelight_be.repository.ProductRepository;
import vn.duclan.candlelight_be.model.Image;
import vn.duclan.candlelight_be.model.Product;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public ProductService(ProductRepository productRepository,
            ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
    }

    public void insertImage(Image image) {
        System.out.println(image.getImageData());
        Product product = productRepository.findById(Integer.parseInt(image.getProductId())).get();
        System.out.println(product);
        image.setProduct(product);
        image.setImageData("data:image/png;base64," + image.getImageData());
        imageRepository.save(image);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public Product findTopByOrderByProductIdDesc() {
        return productRepository.findTopByOrderByProductIdDesc();
    }
}
