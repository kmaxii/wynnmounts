package me.kmaxi.wynnmounts.data;

public record FeedResult(FeedPlan currentTierPlan, FeedPlan optimalPlan) {
    /** True if the optimal plan is actually better (fewer feeds) than staying at current tier. */
    public boolean hasImprovement() {
        return optimalPlan.totalFeeds() < currentTierPlan.totalFeeds();
    }
}
