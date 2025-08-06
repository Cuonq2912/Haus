package org.example.haus.constant;

public class UrlConstant {

    public static class Auth {
        public static final String PRE_FIX = "auth";

        public static final String LOGIN = PRE_FIX + "/login";
        public static final String LOGOUT = PRE_FIX + "/logout";

        private Auth(){
        }
    }

    public static class User {
        public static final String PRE_FIX = "user";

        public static final String GET_USERS = PRE_FIX;
        public static final String GET_USER = PRE_FIX + "/{userId}";
        public static final String GET_CURRENT_USER = PRE_FIX + "/current";

        private User() {
        }
    }

}
