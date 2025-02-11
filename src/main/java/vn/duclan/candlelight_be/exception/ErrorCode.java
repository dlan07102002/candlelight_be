package vn.duclan.candlelight_be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // General Errors
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR(1002, "Validation failed: {details}", HttpStatus.BAD_REQUEST),
    BAD_REQUEST(1000, "Bad request", HttpStatus.BAD_REQUEST),

    // User Errors
    USER_EXISTED(1001, "Username or email already exists", HttpStatus.CONFLICT),
    ACTIVATION_ERROR(1003, "Account activation failed", HttpStatus.FORBIDDEN),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters long", HttpStatus.BAD_REQUEST),

    // Authentication & Authorization Errors
    UNAUTHENTICATION(1005, "Invalid username or password", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1006, "You do not have permission to access this resource", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(1007, "Invalid token or token is expired", HttpStatus.UNAUTHORIZED),

    // Payment
    ORDER_NOT_FOUND(1008, "Invalid order id", HttpStatus.BAD_REQUEST),
    PAYMENT_ERROR(1009, "Invalid order id or vnpay is disconnected", HttpStatus.INTERNAL_SERVER_ERROR);

    // Thuộc tính
    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    // Constructor (private)
    private ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}
