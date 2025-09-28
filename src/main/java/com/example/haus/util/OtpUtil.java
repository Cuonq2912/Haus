package com.example.haus.util;

import java.security.SecureRandom;

public final class OtpUtil {

    private static final SecureRandom random = new SecureRandom();

    public static String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private OtpUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
