package com.track.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

/** Hachage de mot de passe PBKDF2-HMAC-SHA256, sans dépendance tierce. */
public final class PasswordUtil {
    private static final int ITERATIONS = 210_000;
    private static final int SALT_BYTES = 16;
    private static final int KEY_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();
    private PasswordUtil() {}

    public static String hash(char[] password) {
        byte[] salt = new byte[SALT_BYTES]; RANDOM.nextBytes(salt);
        byte[] derived = derive(password, salt, ITERATIONS);
        return "pbkdf2_sha256$" + ITERATIONS + "$" + HexFormat.of().formatHex(salt)
                + "$" + HexFormat.of().formatHex(derived);
    }

    public static boolean verify(char[] password, String encoded) {
        if (encoded == null) return false;
        try {
            String[] parts = encoded.split("\\$");
            if (parts.length != 4 || !"pbkdf2_sha256".equals(parts[0])) return false;
            int iterations = Integer.parseInt(parts[1]);
            if (iterations < 100_000 || iterations > 2_000_000) return false;
            byte[] expected = HexFormat.of().parseHex(parts[3]);
            byte[] actual = derive(password, HexFormat.of().parseHex(parts[2]), iterations);
            return MessageDigest.isEqual(expected, actual);
        } catch (RuntimeException ex) { return false; }
    }

    private static byte[] derive(char[] password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, KEY_BITS);
        try { return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(); }
        catch (Exception ex) { throw new IllegalStateException("Hachage indisponible", ex); }
        finally { spec.clearPassword(); }
    }
}
