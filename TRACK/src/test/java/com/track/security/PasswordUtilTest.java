package com.track.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PasswordUtilTest {
    @Test void hashThenVerifyWorks() {
        String hash = PasswordUtil.hash("Secret-2026!".toCharArray());
        assertTrue(PasswordUtil.verify("Secret-2026!".toCharArray(), hash));
    }

    @Test void wrongPasswordIsRejected() {
        String hash = PasswordUtil.hash("Secret-2026!".toCharArray());
        assertFalse(PasswordUtil.verify("secret-2026!".toCharArray(), hash));
    }

    @Test void saltMakesHashesDifferent() {
        assertNotEquals(PasswordUtil.hash("same".toCharArray()), PasswordUtil.hash("same".toCharArray()));
    }

    @Test void malformedHashesAreRejectedWithoutException() {
        assertFalse(PasswordUtil.verify("x".toCharArray(), null));
        assertFalse(PasswordUtil.verify("x".toCharArray(), "n'importe quoi"));
        assertFalse(PasswordUtil.verify("x".toCharArray(), "pbkdf2_sha256$10$00$00"));      // itérations trop faibles
        assertFalse(PasswordUtil.verify("x".toCharArray(), "bcrypt$210000$aa$bb"));
    }

    @Test void initialAdminSeedHashMatchesDocumentedPassword() {
        String seed = "pbkdf2_sha256$210000$6a24f3a6426748f3db0e6c7a35a6cbf1$"
                + "7fd3e27e1bb2afaf85221891f806d67f8974f8f5311360f5c3c3dd78301dcba0";
        assertTrue(PasswordUtil.verify("ChangeMe2026!".toCharArray(), seed));
    }
}
