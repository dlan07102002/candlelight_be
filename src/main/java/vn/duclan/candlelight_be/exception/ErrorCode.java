package vn.duclan.candlelight_be.exception;

public enum ErrorCode {
    // Định nghĩa các hằng số
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception"),
    USER_EXISTED(1001, "Username or email already exists."),
    VALIDATION_ERROR(1002, "Validation Failed"),
    ACTIVATION_ERROR(1003, "Activation Failed"),
    AUTHENTICATION_ERROR(1005, "Account is not activated"),
    PASSWORD_TOO_SHORT(1004, "\"Password must be at least 8 characters long"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    // Thuộc tính
    private final int code;
    private final String message;

    // Constructor (private)
    private ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // Getter
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
