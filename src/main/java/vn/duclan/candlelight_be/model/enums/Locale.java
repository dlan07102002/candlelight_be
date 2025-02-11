package vn.duclan.candlelight_be.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Locale {

    VIETNAM("vn"),
    US("us"),
    ;

    private final String code;
}
