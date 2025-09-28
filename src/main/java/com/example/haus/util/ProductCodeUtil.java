package com.example.haus.util;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProductCodeUtil {

    private ProductCodeUtil() {}

    private static final String PREFIX = "SKU";
    private static final SecureRandom random = new SecureRandom();

    public static String generateProductCode() {
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            digits.append(random.nextInt(10));
        }
        return PREFIX + digits.toString();
    }

//    public static String generateUniqueProductCode() {
//        LocalDateTime now = LocalDateTime.now();
//        String timestamp = now.format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
//        return PREFIX + timestamp;
//    }
}