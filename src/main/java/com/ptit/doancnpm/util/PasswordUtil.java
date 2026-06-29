package com.ptit.doancnpm.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public final class PasswordUtil {

    private static final String PREFIX = "pbkdf2";
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 210_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BYTES = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    /**
     * Tạo chuỗi mật khẩu PBKDF2 có salt riêng cho từng tài khoản.
     */
    public static String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }

        byte[] salt = new byte[SALT_BYTES];
        SECURE_RANDOM.nextBytes(salt);
        byte[] derivedKey = derive(rawPassword.trim(), salt, ITERATIONS);
        return PREFIX + "$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(derivedKey);
    }

    /**
     * Hỗ trợ cả PBKDF2 mới, SHA-256 cũ và mật khẩu rõ cũ để nâng cấp không làm
     * người dùng hiện tại mất khả năng đăng nhập.
     */
    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }

        String raw = rawPassword.trim();
        String stored = storedPassword.trim();
        if (stored.startsWith(PREFIX + "$")) {
            return matchesPbkdf2(raw, stored);
        }

        if (isSha256(stored)) {
            return MessageDigest.isEqual(
                    sha256(raw).toLowerCase().getBytes(StandardCharsets.US_ASCII),
                    stored.toLowerCase().getBytes(StandardCharsets.US_ASCII));
        }

        return MessageDigest.isEqual(
                raw.getBytes(StandardCharsets.UTF_8),
                stored.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean needsRehash(String storedPassword) {
        if (storedPassword == null || !storedPassword.startsWith(PREFIX + "$")) {
            return true;
        }
        String[] parts = storedPassword.split("\\$", -1);
        if (parts.length != 4) {
            return true;
        }
        try {
            return Integer.parseInt(parts[1]) < ITERATIONS;
        } catch (NumberFormatException exception) {
            return true;
        }
    }

    /** Giữ lại để xác thực dữ liệu SHA-256 cũ. */
    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();
            for (byte b : encoded) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Không hỗ trợ SHA-256.", exception);
        }
    }

    private static boolean matchesPbkdf2(String rawPassword, String encodedPassword) {
        try {
            String[] parts = encodedPassword.split("\\$", -1);
            if (parts.length != 4 || !PREFIX.equals(parts[0])) {
                return false;
            }
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expected = Base64.getDecoder().decode(parts[3]);
            byte[] actual = derive(rawPassword, salt, iterations, expected.length);
            return MessageDigest.isEqual(expected, actual);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static boolean isSha256(String value) {
        return value.length() == 64 && value.matches("[0-9a-fA-F]{64}");
    }

    private static byte[] derive(String password, byte[] salt, int iterations) {
        return derive(password, salt, iterations, KEY_BYTES);
    }

    private static byte[] derive(String password, byte[] salt, int iterations, int keyBytes) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyBytes * 8);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new IllegalStateException("Không hỗ trợ mã hóa mật khẩu PBKDF2.", exception);
        } finally {
            spec.clearPassword();
        }
    }
}
