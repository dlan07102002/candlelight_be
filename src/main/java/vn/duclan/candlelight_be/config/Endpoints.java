package vn.duclan.candlelight_be.config;

// Endpoint management
public class Endpoints {
        public static final String FE_HOST = "http://localhost:5173";
        public static final String[] PUBLIC_GET_ENDPOINTS = {
                        "/users",
                        "/users/**",
                        "/products",
                        "/products/**",
                        "/images",
                        "/images/**",
                        "/categories",
                        "/categories/**",
                        "/reviews",
                        "/orders",
                        "/order-details",

                        "/wishlists",
                        "/users/search/existsByUsername",
                        "/users/search/existsByEmail",
                        "/account/activate"
        };

        public static final String[] PUBLIC_POST_ENDPOINTS = {

                        "/account/register",
                        "/account/login",
                        "/admin/products",
                        "/admin/products/**",
                        "/images",
                        "/images/**",
                        "/admin/images",
                        "/admin/images/**",

        };

        public static final String[] ADMIN_GET_ENDPOINTS = {
                        "/",
                        "/users",
                        "/users/**",
                        "/orders",
                        "/orders/**"

        };

        public static final String[] ADMIN_POST_ENDPOINTS = {
                        "/products",
                        "/products/**",
                        "/users",
                        "/users/**",

        };

}
