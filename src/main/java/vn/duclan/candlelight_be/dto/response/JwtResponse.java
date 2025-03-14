package vn.duclan.candlelight_be.dto.response;

import lombok.Builder;

@Builder
public class JwtResponse {

    private final String jwt;

    public JwtResponse(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }
}
