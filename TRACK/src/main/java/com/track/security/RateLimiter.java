package com.track.security;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * Limiteur à fenêtre glissante, en mémoire (par instance du serveur).
 * Pour un déploiement en cluster, remplacer par un compteur partagé (base ou cache).
 */
public final class RateLimiter {
    private static final int PURGE_THRESHOLD = 10_000;

    private final int maxEvents;
    private final long windowMillis;
    private final LongSupplier clock;
    private final Map<String, Deque<Long>> events = new ConcurrentHashMap<>();

    public RateLimiter(int maxEvents, long windowMillis) { this(maxEvents, windowMillis, System::currentTimeMillis); }

    public RateLimiter(int maxEvents, long windowMillis, LongSupplier clock) {
        this.maxEvents = maxEvents; this.windowMillis = windowMillis; this.clock = clock;
    }

    /** Enregistre un événement si la limite n'est pas atteinte. @return false si la limite est atteinte. */
    public boolean allow(String key) {
        if (events.size() > PURGE_THRESHOLD) purge();
        Deque<Long> q = events.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (q) {
            evict(q);
            if (q.size() >= maxEvents) return false;
            q.addLast(clock.getAsLong());
            return true;
        }
    }

    /** Enregistre un événement sans condition (ex. un échec de connexion). */
    public void hit(String key) {
        Deque<Long> q = events.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (q) { evict(q); q.addLast(clock.getAsLong()); }
    }

    /** La limite est-elle atteinte pour cette clé ? (ne modifie pas le compteur) */
    public boolean isLimited(String key) {
        Deque<Long> q = events.get(key);
        if (q == null) return false;
        synchronized (q) { evict(q); return q.size() >= maxEvents; }
    }

    public void reset(String key) { events.remove(key); }

    private void evict(Deque<Long> q) {
        long limit = clock.getAsLong() - windowMillis;
        while (!q.isEmpty() && q.peekFirst() <= limit) q.pollFirst();
    }

    private void purge() {
        events.entrySet().removeIf(e -> {
            synchronized (e.getValue()) { evict(e.getValue()); return e.getValue().isEmpty(); }
        });
    }
}
