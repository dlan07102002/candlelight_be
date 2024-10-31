package vn.duclan.candlelight_be.config;

// Endpoint management
public class Endpoints {
        public static final String FE_HOST = "http://localhost:5173";
        public static final String[] PUBLIC_GET_ENDPOINTS = {
                        "/products",
                        "/products/**",
                        "/images",
                        "/images/**",
                        "/categories",
                        "/reviews",
                        "/users/search/existsByUsername",
                        "/users/search/existsByEmail",
                        "/account/activate"
        };

        public static final String[] PUBLIC_POST_ENDPOINTS = {
                        "/account/register",
                        "/account/login"
        };

        public static final String[] ADMIN_GET_ENDPOINTS = {
                        "/users",
                        "/users/**",

        };

}
