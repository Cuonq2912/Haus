package com.example.haus.constant;

public class UrlConstant {

    public static class Auth {
        private static final String PRE_FIX = "/auth";

        public static final String LOGIN = PRE_FIX + "/login";
//        public static final String LOGIN_WITH_GOOGLE = PRE_FIX + "/google";
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

//        public static final String GET_USERS = PRE_FIX;
//        public static final String GET_USER = PRE_FIX + "/{userId}";
//        public static final String GET_CURRENT_USER = PRE_FIX + "/current";
//
//        public static final String FILL_PERSONAL_INFORMATION = PRE_FIX + "/personal-information";
//        public static final String UPLOAD_AVATAR = PRE_FIX + "/upload-avatar";
        public static final String DELETE_MY_ACCOUNT = PRE_FIX + "/delete-my-account";

        public static final String GET_PROFILE = PRE_FIX + "/profile";
        public static final String UPDATE_PROFILE = PRE_FIX + "/update-profile";

        private User() {
        }
    }

}
