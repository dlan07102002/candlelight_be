package vn.duclan.candlelight_be.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL) // skip null fields
public class APIResponse<T> {
    private int code = 1000;
    private String message;
    // The return type change based API, so should use generic type
    private T result;

}
