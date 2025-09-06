package com.example.haus.constant;

public class SuccessMessage {

    public static class Auth {

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

        public static final String GET_MY_PROFILE_SUCCESS = "Get my profile successful";
        public static final String UPDATE_PROFILE_SUCCESS = "Update profile successful";
        public static final String SOFT_DELETE_SUCCESS = "User account has been deleted successfully.";
        public static final String UPDATE_PASSWORD_SUCCESS = "Update password successful";
        public static final String UPDATE_AVATAR_SUCCESS = "Update avatar user successful";
    }

    public static class Category {
        public static final String ADD_CATEGORY_SUCCESS = "Add category successful";
        public static final String UPDATE_CATEGORY_SUCCESS = "Update category successful";
        public static final String GET_CATEGORY_SUCCESS = "Get category successful";
        public static final String GET_ALL_CATEGORY_SUCCESS = "Get all category successful";
        public static final String DELETE_CATEGORY_SUCCESS = "Delete category successful";
    }

    public static class Promotion {
        public static final String ADD_PROMOTION_SUCCESS = "Add promotion successful";
        public static final String UPDATE_PROMOTION_SUCCESS = "Update promotion successful";
        public static final String GET_PROMOTION_SUCCESS = "Get promotion successful";
        public static final String GET_ALL_PROMOTION_SUCCESS = "Get all promotion successful";
        public static final String DELETE_PROMOTION_SUCCESS = "Delete promotion successful";
    }
}
