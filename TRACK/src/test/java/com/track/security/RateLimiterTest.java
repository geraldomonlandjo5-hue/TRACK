package com.track.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.Test;

class RateLimiterTest {
    private final AtomicLong now = new AtomicLong(1_000_000);

    @Test void allowsUpToTheLimitThenBlocks() {
        RateLimiter l = new RateLimiter(3, 60_000, now::get);
        assertTrue(l.allow("a")); assertTrue(l.allow("a")); assertTrue(l.allow("a"));
        assertFalse(l.allow("a"));
        assertTrue(l.allow("b"), "une autre clé n'est pas affectée");
    }

    @Test void windowSlides() {
        RateLimiter l = new RateLimiter(2, 60_000, now::get);
        l.allow("a"); l.allow("a");
        assertFalse(l.allow("a"));
        now.addAndGet(60_001);
        assertTrue(l.allow("a"));
    }

    @Test void failuresLockUntilResetOrExpiry() {
        RateLimiter l = new RateLimiter(2, 60_000, now::get);
        assertFalse(l.isLimited("mail"));
        l.hit("mail"); l.hit("mail");
        assertTrue(l.isLimited("mail"));
        l.reset("mail");
        assertFalse(l.isLimited("mail"));
        l.hit("mail"); l.hit("mail");
        now.addAndGet(61_000);
        assertFalse(l.isLimited("mail"));
    }
}
