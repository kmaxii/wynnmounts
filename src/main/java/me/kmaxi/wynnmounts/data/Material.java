package me.kmaxi.wynnmounts.data;

/**
 * A single feeding material.
 * bonuses[i] maps to stat index (0=Speed, 1=Acceleration, 2=Altitude, 3=Energy,
 * 4=Handling, 5=Toughness, 6=Boost, 7=Training).
 */
public record Material(int tier, String name, int[] bonuses) {

    /** Total stat points this material provides per feed. */
    public int totalPoints() {
        int sum = 0;
        for (int b : bonuses) sum += b;
        return sum;
    }
}
