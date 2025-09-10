package com.aster.cloud.utils;
import java.security.SecureRandom;
public class RandomTokenGenerator {
    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomToken(int length) {
        StringBuilder token = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARSET.length());
            token.append(CHARSET.charAt(index));
        }
        return token.toString();
    }
}
