package vn.duclan.candlelight_be.exception;

import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import jakarta.validation.ConstraintViolation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import vn.duclan.candlelight_be.dto.response.APIResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<APIResponse<?>> exceptionHandler(Exception exception) {
        APIResponse<?> apiResponse = new APIResponse<>();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<List<ValidationError>>> handleValidationException(
            MethodArgumentNotValidException exception) {
        // Get the error list
        List<ValidationError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> {
                    ConstraintViolation<?> constraintViolation = null;

                    try {
                        constraintViolation = fieldError.unwrap(ConstraintViolation.class);

                    } catch (ClassCastException e) {
                        // TODO: handle exception
                    }
                    Map<String, Object> attributes = Optional.ofNullable(constraintViolation)
                            .map(cv -> cv.getConstraintDescriptor().getAttributes())
                            .orElse(Collections.emptyMap());

                    return ValidationError.builder()
                            .field(fieldError.getField())
                            .message(
                                    Objects.nonNull(attributes)
                                            ? mapAttribute(fieldError.getDefaultMessage(), attributes)
                                            : fieldError.getDefaultMessage())
                            .build();
                })
                .toList();

        APIResponse<List<ValidationError>> apiResponse = new APIResponse<>();
        apiResponse.setCode(ErrorCode.VALIDATION_ERROR.getCode());
        apiResponse.setMessage(ErrorCode.VALIDATION_ERROR.getMessage());
        apiResponse.setResult(errors);
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<APIResponse<?>> exceptionHandler(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        APIResponse<?> apiResponse = new APIResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<APIResponse<?>> exceptionHandler(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(APIResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
