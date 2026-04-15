package me.kmaxi.wynnmounts.data;

import java.util.List;

public record FeedPlan(
        List<MaterialCount> materials,
        int totalFeeds,
        int estimatedHours,
        /** Null for current-tier plan. Non-null describes the training steps needed. */
        String trainingNote,
        int tier,
        /**
         * For Phase C cascading plans: how many MaterialCount *entries* (not individual
         * feeds) at the start of materials[] are pre-feeds at an intermediate tier.
         * Always 0 (Phase A/B) or 1 (Phase C — one entry for the single pre-feed material).
         * The renderer uses this index to split the list:
         *   [0..preFeedCount)  → shown after the first "train to tMid" line
         *   [preFeedCount..)   → shown after the second "train to tTarget" line
         */
        int preFeedCount
) {
    public record MaterialCount(Material material, int count) {}

    public static FeedPlan empty(int tier) {
        return new FeedPlan(List.of(), 0, 0, null, tier, 0);
    }
}
