package com.example.haus.constant;

public class SuccessMessage {

    public static class Auth {

        public static final String LOGIN_SUCCESS = "Login successful";
        public static final String LOGOUT_SUCCESS = "Logout successful";
        public static final String REFRESH_TOKEN_SUCCESS = "Take refresh token successful";
        public static final String REGISTER_SEND_OTP_SUCCESS = "Register successful. OTP has been sent to your email";
        public static final String VERIFY_OTP_REGISTER_SUCCESS = "Verify successful";
        public static final String FORGOT_PASSWORD_SUCCESS = "Forgot password successful";
        public static final String VERIFY_OTP_TO_RESET_PASSWORD_SUCCESS = "Verify to reset password successful";
        public static final String RESET_PASSWORD_SUCCESS = "Reset password successful";

    }

    public static class User {

        public static final String GET_MY_PROFILE_SUCCESS = "Get my profile successful";
        public static final String UPDATE_PROFILE_SUCCESS = "Update profile successful";
        public static final String SOFT_DELETE_SUCCESS = "User account has been deleted successfully.";
        }

}
