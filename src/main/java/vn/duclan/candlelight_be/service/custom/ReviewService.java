package vn.duclan.candlelight_be.service.custom;

import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import vn.duclan.candlelight_be.service.JwtService;
import vn.duclan.candlelight_be.exception.AppException;
import vn.duclan.candlelight_be.exception.ErrorCode;
import vn.duclan.candlelight_be.repository.ProductRepository;
import vn.duclan.candlelight_be.repository.ReviewRepository;
import vn.duclan.candlelight_be.repository.UserRepository;
import vn.duclan.candlelight_be.model.Product;
import vn.duclan.candlelight_be.model.Review;
import vn.duclan.candlelight_be.model.User;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewService {
    final ReviewRepository reviewRepository;
    final UserRepository userRepository;
    final ProductRepository productRepository;
    final JwtService jwtService;

    public long save(Review review, String token) {
        String username = jwtService.getUsername(token);
        User user = isUserValid(username);
        review.setUser(user);

        Product product = productRepository.findById(review.getProductId())
                .orElseThrow(() -> new RuntimeException("Product is not exist"));

        review.setProduct(product);

        Review savedReview = reviewRepository.save(review);

        return savedReview.getReviewId();
    }

    private User isUserValid(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    return new AppException(ErrorCode.UNAUTHENTICATION);
                });
    }

}
