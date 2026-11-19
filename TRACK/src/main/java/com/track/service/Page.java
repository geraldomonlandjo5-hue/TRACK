package com.track.service;

import java.util.List;

/** Page de résultats (getters pour l'EL des JSP). */
public final class Page<T> {
    private final List<T> items;
    private final int page;
    private final int size;
    private final long total;

    public Page(List<T> items, int page, int size, long total) {
        this.items = items; this.page = page; this.size = size; this.total = total;
    }

    public static <T> Page<T> empty(int size) { return new Page<>(List.of(), 0, size, 0); }

    public List<T> getItems() { return items; }
    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotal() { return total; }
    public int getTotalPages() { return (int) Math.max(1, (total + size - 1) / size); }
    public boolean isHasPrevious() { return page > 0; }
    public boolean isHasNext() { return page + 1 < getTotalPages(); }
}
