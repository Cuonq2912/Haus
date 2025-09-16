package com.example.haus.util;

import java.util.Random;

public final class OtpUtil {

    private static final Random random = new Random();

    public static String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private OtpUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
