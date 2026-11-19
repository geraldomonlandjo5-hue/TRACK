package com.track.security;

import jakarta.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** Jeton CSRF stocké en session (un attribut par usage : formulaires connectés, formulaire de login). */
public final class Csrf {
    public static final String APP_TOKEN = "csrfToken";
    public static final String LOGIN_TOKEN = "loginCsrfToken";
    public static final String PARAM = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private Csrf() {}

    public static String ensure(HttpSession session, String attribute) {
        String token = (String) session.getAttribute(attribute);
        if (token == null) {
            byte[] bytes = new byte[32];
            RANDOM.nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(attribute, token);
        }
        return token;
    }

    public static boolean valid(HttpSession session, String attribute, String provided) {
        if (session == null || provided == null) return false;
        String expected = (String) session.getAttribute(attribute);
        return expected != null && MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8), provided.getBytes(StandardCharsets.UTF_8));
    }
}
