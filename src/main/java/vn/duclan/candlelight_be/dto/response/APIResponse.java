package vn.duclan.candlelight_be.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL) // skip null fields
public class APIResponse<T> {
    // Builder.Default to set default value to code
    // Or else, Lombok will skip it
    @Builder.Default
    int code = 1000;

    String message;
    // The return type change based API, so should use generic type
    T result;
}
