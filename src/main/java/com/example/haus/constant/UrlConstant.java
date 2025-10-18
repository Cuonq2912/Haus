package com.example.haus.constant;

public class UrlConstant {

    private UrlConstant() {
    }

    public static class Auth {
        private static final String PRE_FIX = "/auth";

        public static final String LOGIN = PRE_FIX + "/login";
        // public static final String LOGIN_WITH_GOOGLE = PRE_FIX + "/google";
        public static final String REGISTER = PRE_FIX + "/register";
        public static final String VERIFY_OTP = PRE_FIX + "/verify-otp";
        public static final String FORGOT_PASSWORD = PRE_FIX + "/forgot-password";
        public static final String VERIFY_OTP_TO_RESET_PASSWORD = PRE_FIX + "/verify-otp-to-reset-password";
        public static final String RESET_PASSWORD = PRE_FIX + "/reset-password";
        public static final String REFRESH_TOKEN = PRE_FIX + "/refresh";
        public static final String LOGOUT = PRE_FIX + "/logout";

        private Auth() {
        }
    }

    public static class User {
        private static final String PRE_FIX = "/user";

        // public static final String GET_USERS = PRE_FIX;
        // public static final String GET_USER = PRE_FIX + "/{userId}";
        // public static final String GET_CURRENT_USER = PRE_FIX + "/current";
        // public static final String FILL_PERSONAL_INFORMATION = PRE_FIX +
        // "/personal-information";
        public static final String UPLOAD_AVATAR = PRE_FIX + "/upload-avatar";
        public static final String DELETE_MY_ACCOUNT = PRE_FIX + "/delete-my-account";
        public static final String GET_PROFILE = PRE_FIX + "/profile";
        public static final String UPDATE_PROFILE = PRE_FIX + "/update-profile";
        public static final String UPDATE_PASSWORD = PRE_FIX + "/update-password";

        private User() {
        }
    }

    public static class Category {

        private Category() {
        }

        private static final String PRE_FIX = "/category";

        public static final String ADD_CATEGORY = PRE_FIX;
        public static final String UPDATE_CATEGORY = PRE_FIX + "/{categoryId}";
        public static final String DELETE_CATEGORY = PRE_FIX + "/{categoryId}";
        public static final String GET_CATEGORY_BY_ID = PRE_FIX + "/{categoryId}";
        public static final String GET_ALL_CATEGORY = PRE_FIX;
        public static final String GET_ALL_SUB_CATEGORY = PRE_FIX + "/sub";
        public static final String GET_CATEGORY_BY_NAME = PRE_FIX + "/name/{categoryName}";
        public static final String SEARCH_CATEGORY_BY_NAME_AND_SORT_BY_KEYWORD = PRE_FIX + "/search";
    }

    public static class Cart {

        private Cart() {
        }

        private static final String PRE_FIX = "/cart";

        public static final String ADD_CART = PRE_FIX ;
        public static final String UPDATE_CART = PRE_FIX;
        public static final String DELETE_CART = PRE_FIX;
        public static final String GET_CART_BY_USER_ID = PRE_FIX;
        public static final String GET_ALL_CART= PRE_FIX + "/list";
        public static final String REMOVE_CART_ITEM_FROM_CART= PRE_FIX + "/{variantId}";
    }

    public static class Promotion {

        private Promotion() {
        }

        private static final String PRE_FIX = "/promotion";

        public static final String ADD_PROMOTION = PRE_FIX;
        public static final String UPDATE_PROMOTION = PRE_FIX + "/{promotionId}";
        public static final String DELETE_PROMOTION = PRE_FIX + "/{promotionId}";
        public static final String GET_PROMOTION_BY_ID = PRE_FIX + "/{promotionId}";
        public static final String GET_ALL_PROMOTION = PRE_FIX;
        public static final String GET_PROMOTION_BY_CODE = PRE_FIX + "/code/{promotionCode}";
        public static final String FILTER_PROMOTION = PRE_FIX + "/filter";
    }

    public static class Product {
        private static final String PRE_FIX = "/product";

        public static final String GET_PRODUCT_BY_ID = PRE_FIX + "/{id}";
        public static final String CREATE_PRODUCT = PRE_FIX;
        public static final String UPDATE_PRODUCT = PRE_FIX + "/{id}";
        public static final String DELETE_PRODUCT = PRE_FIX + "/{id}";
        public static final String GET_ALL_PRODUCTS = PRE_FIX;

        public static final String GET_PRODUCT_VARIATIONS_BY_PRODUCT_ID = PRE_FIX + "/{productId}/variations";
        public static final String GET_PRODUCT_VARIATION_BY_ID = PRE_FIX + "/variation/{variationId}";
        public static final String CREATE_PRODUCT_VARIATION = PRE_FIX + "/variation";
        public static final String UPDATE_PRODUCT_VARIATION = PRE_FIX + "/variation";
        public static final String DELETE_PRODUCT_VARIATION = PRE_FIX + "/variation/{variationId}";

        public static final String GET_PRODUCTS_BY_CATEGORY = PRE_FIX + "/category/{categoryName}";
        public static final String GET_PRODUCTS_BY_CATEGORY_ID = PRE_FIX + "/category-id/{categoryId}";

        public static final String SEARCH_PRODUCTS_BY_KEYWORD = PRE_FIX + "/search";
        public static final String FILTER_PRODUCTS = PRE_FIX + "/filter";

        private Product() {
        }
    }

    public static class Payment {
        private static final String PRE_FIX = "/payment";

        public static final String GET_PAYMENT_URL = PRE_FIX + "/vnpay/payment_url";
        public static final String VNPAY_RETURN = PRE_FIX + "/vnpay/vnpay-return";
        public static final String VNPAY_IPN = PRE_FIX + "/vnpay/vnpay-ipn";

        public static final String MOMO_CREATE_ORDER = PRE_FIX + "/momo/create";
        public static final String MOMO_IPN = PRE_FIX + "/momo/ipn-handler";
        public static final String MOMO_CALLBACK = PRE_FIX + "/momo/callback";

        public static final String COD_PAYMENT = PRE_FIX + "/cod";

        private Payment() {
        }
    }


    public static class Order {
        private static final String PRE_FIX = "/order";

        public static final String GET_ALL_ORDERS = PRE_FIX;
        public static final String GET_ORDER_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE_STATUS_ORDER_BY_ID = PRE_FIX + "/{id}";

        private Order() {

        }
    }

    public static class Address {
        private static final String PRE_FIX = "/address";

        public static final String ADD_ADDRESS = PRE_FIX;
        public static final String UPDATE_ADDRESS = PRE_FIX + "/{id}";
        public static final String DELETE_ADDRESS = PRE_FIX + "/{id}";
        public static final String GET_ADDRESSES_BY_USER_ID = PRE_FIX + "/user";
        public static final String GET_ADDRESS = PRE_FIX + "/{id}";
    }
}
