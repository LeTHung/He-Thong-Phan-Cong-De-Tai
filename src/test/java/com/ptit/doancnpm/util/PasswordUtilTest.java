package com.ptit.doancnpm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilTest {

    @Test
    void hashesAndVerifiesPasswordWithPbkdf2() {
        String hash = PasswordUtil.hash("123456");

        assertTrue(hash.startsWith("pbkdf2$"));
        assertTrue(PasswordUtil.matches("123456", hash));
        assertFalse(PasswordUtil.matches("wrong-password", hash));
        assertFalse(PasswordUtil.needsRehash(hash));
    }

    @Test
    void usesDifferentSaltForEveryHash() {
        assertNotEquals(PasswordUtil.hash("123456"), PasswordUtil.hash("123456"));
    }

    @Test
    void acceptsLegacyPlaintextAndSha256ForAutomaticUpgrade() {
        assertTrue(PasswordUtil.matches("123456", "123456"));
        assertTrue(PasswordUtil.matches("123456", PasswordUtil.sha256("123456")));
        assertTrue(PasswordUtil.needsRehash("123456"));
        assertTrue(PasswordUtil.needsRehash(PasswordUtil.sha256("123456")));
    }

    @Test
    void rejectsMalformedPbkdf2Value() {
        assertFalse(PasswordUtil.matches("123456", "pbkdf2$bad$value"));
    }
}
