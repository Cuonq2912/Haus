package com.example.haus.constant;

public class SuccessMessage {

    private SuccessMessage() {}

    public static class Auth {

        private Auth() {}

        public static final String LOGIN_SUCCESS = "Login successful";
        public static final String LOGOUT_SUCCESS = "Logout successful";
        public static final String REFRESH_TOKEN_SUCCESS = "Take refresh token successful";
        public static final String REGISTER_SEND_OTP_SUCCESS = "Register successful. OTP has been sent to your email";
        public static final String VERIFY_OTP_REGISTER_SUCCESS = "Verify successful";
        public static final String FORGOT_PASSWORD_SUCCESS = "Forgot password request successful";
        public static final String VERIFY_OTP_TO_RESET_PASSWORD_SUCCESS = "Verify to reset password successful";
        public static final String RESET_PASSWORD_SUCCESS = "Reset password successful";

    }

    public static class User {

        private User() {}

        public static final String GET_MY_PROFILE_SUCCESS = "Get my profile successful";
        public static final String UPDATE_PROFILE_SUCCESS = "Update profile successful";
        public static final String SOFT_DELETE_SUCCESS = "User account has been deleted successfully.";
        public static final String UPDATE_PASSWORD_SUCCESS = "Update password successful";
        public static final String UPDATE_AVATAR_SUCCESS = "Update avatar user successful";
    }

    public static class Address {

        private Address() {}

        public static final String ADD_ADDRESS_SUCCESS = "Add address successful";
        public static final String UPDATE_ADDRESS_SUCCESS = "Update address successful";
        public static final String GET_ADDRESS_SUCCESS = "Get address successful";
        public static final String GET_ALL_ADDRESS_SUCCESS = "Get all address successful";
        public static final String DELETE_ADDRESS_SUCCESS = "Delete address successful";
    }

    public static class Category {

        private Category() {}

        public static final String ADD_CATEGORY_SUCCESS = "Add category successful";
        public static final String UPDATE_CATEGORY_SUCCESS = "Update category successful";
        public static final String GET_CATEGORY_SUCCESS = "Get category successful";
        public static final String GET_ALL_CATEGORY_SUCCESS = "Get all category successful";
        public static final String DELETE_CATEGORY_SUCCESS = "Delete category successful";
    }

    public static class Cart {

        private Cart() {}

        public static final String ADD_CART_SUCCESS = "Add cart successful";
        public static final String UPDATE_CART_SUCCESS = "Update cart successful";
        public static final String GET_CART_SUCCESS = "Get cart successful";
        public static final String DELETE_CART_SUCCESS = "Delete cart successful";
        public static final String DELETE_CART_ITEM_FROM_CART = "Delete cart item from cart successful";
    }

    public static class Promotion {

        private Promotion() {}

        public static final String ADD_PROMOTION_SUCCESS = "Add promotion successful";
        public static final String UPDATE_PROMOTION_SUCCESS = "Update promotion successful";
        public static final String GET_PROMOTION_SUCCESS = "Get promotion successful";
        public static final String GET_ALL_PROMOTION_SUCCESS = "Get all promotion successful";
        public static final String DELETE_PROMOTION_SUCCESS = "Delete promotion successful";
    }
    public static class Product {

        private Product() {}

        public static final String GET_PRODUCT_SUCCESS = "Get product successful";
        public static final String CREATE_PRODUCT_SUCCESS = "Create product successful";
        public static final String UPDATE_PRODUCT_SUCCESS = "Update product successful";
        public static final String DELETE_PRODUCT_SUCCESS = "Delete product successful";
        public static final String GET_ALL_PRODUCTS_SUCCESS = "Get all products successful";

        // Product Variation Messages
        public static final String GET_PRODUCT_VARIATIONS_SUCCESS = "Get product variations successful";
        public static final String GET_PRODUCT_VARIATION_SUCCESS = "Get product variation successful";
        public static final String CREATE_PRODUCT_VARIATION_SUCCESS = "Create product variation successful";
        public static final String UPDATE_PRODUCT_VARIATION_SUCCESS = "Update product variation successful";
        public static final String DELETE_PRODUCT_VARIATION_SUCCESS = "Delete product variation successful";
        
        // Favorite Messages
        public static final String ADD_FAVORITE_SUCCESS = "Added product to favorite list";
        public static final String REMOVE_FAVORITE_SUCCESS = "Deleted product to favorite list";
        public static final String GET_FAVORITES_SUCCESS = "Get favorite list successfully";
        public static final String CHECK_FAVORITE_SUCCESS = "Check status favorite successfully";

    }

    public static class Payment {

        private Payment() {}

        public static final String GET_VNPAYURL_SUCCESS = "Get VNPay url request successful";
        public static final String IPN_RECEIVED_SUCCESS = "VNPay IPN received and processed successfully";
        public static final String CALLBACK_VNPAY_SUCCESS = "VNPay return callback processed successfully";

        public static final String CREATE_MOMO_ORDER_SUCCESS = "Create MoMo order successful";
        public static final String MOMO_IPN_SUCCESS = "MoMo IPN processed successfully";
        public static final String MOMO_CALLBACK_SUCCESS = "MoMo callback processed successfully";

        public static final String COD_PAYMENT_SUCCESS = "COD payment processed successfully";

    }

    public static class Order {

        private Order() {}

        public static final String GET_ORDER_SUCCESS = "Get all order successfully";
        public static final String UPDATE_STATUS_ORDER_SUCCESS = "Update status order successfully";
        public static final String GET_INVOICE_SUCCESS = "Get invoice success";
        public static final String CREATE_ORDER_SUCCESS = "Create order success";

    }

}
