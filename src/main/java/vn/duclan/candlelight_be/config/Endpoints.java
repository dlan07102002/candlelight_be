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
                        "/categories/**",
                        "/reviews",
                        "/orders",
                        "/orders/**",
                        "/order-details",
                        "/order-details/**",
                        "/wishlists",
                        "/users/search/existsByUsername",
                        "/users/search/existsByEmail",
                        "/account/activate"
        };

        public static final String[] PUBLIC_POST_ENDPOINTS = {
                        "/account/register",
                        "/account/login",
                        "/account/introspect",
                        "/account/logout",
                        "/account/refresh",
                        "/api/order-detail",
                        "/api/order-detail/**",
                        "/images",
                        "/images/**",
                        // "/admin/images",
                        // "/admin/images/**",

        };

        public static final String[] USER_PATCH_ENDPOINTS = {
                        "/order-details/", "/order-details/**", "/users", "/users/**", "/account/**",
        };

        public static final String[] USER_DELETE_ENDPOINTS = {
                        "/order-details/", "/order-details/**",
        };

        public static final String[] ADMIN_GET_ENDPOINTS = {
                        "/", "/users", "/users/**",
        };

        public static final String[] ADMIN_POST_ENDPOINTS = {
                        "/products",
                        "/products/**",
                        "/users",
                        "/users/**",
                        "/admin/products",
                        "/admin/products/**",
                        "/admin/images",
                        "/admin/images/**",
                        "/api/order-detail",
                        "/api/order-detail/**",
                        "/order-details/",
                        "/order-details/**",
        };
}
