package me.kmaxi.wynnmounts.data;

import java.util.List;

public record FeedPlan(
        List<MaterialCount> materials,
        int totalFeeds,
        int estimatedHours,
        /** Null for current-tier plan. Non-null describes the training step needed, e.g. "Train Speed to 90 (8 feeds)" */
        String trainingNote,
        int tier
) {
    public record MaterialCount(Material material, int count) {}

    public static FeedPlan empty(int tier) {
        return new FeedPlan(List.of(), 0, 0, null, tier);
    }
}
