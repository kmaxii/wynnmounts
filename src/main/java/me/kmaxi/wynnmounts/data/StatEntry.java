package me.kmaxi.wynnmounts.data;

public record StatEntry(int current, int limit, int max) {
    public int needed() {
        return Math.max(max - limit, 0);
    }
}
