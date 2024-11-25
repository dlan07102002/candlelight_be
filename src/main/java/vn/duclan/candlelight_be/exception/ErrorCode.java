package vn.duclan.candlelight_be.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // Định nghĩa các hằng số
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    USER_EXISTED(1001, "Username or email already exists.", HttpStatus.CONFLICT),
    VALIDATION_ERROR(1002, "Validation Failed", HttpStatus.BAD_REQUEST),
    ACTIVATION_ERROR(1003, "Activation Failed", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters long", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATION(1005, "Invalid Username or Password", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1006, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(1006, "Invalid token or token is expired", HttpStatus.BAD_REQUEST),

    BAD_REQUEST(400, "Bad Request", HttpStatus.BAD_REQUEST);

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
