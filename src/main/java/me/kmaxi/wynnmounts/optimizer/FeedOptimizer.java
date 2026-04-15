package me.kmaxi.wynnmounts.optimizer;

import me.kmaxi.wynnmounts.data.*;

import java.util.ArrayList;
import java.util.List;

public final class FeedOptimizer {

    // Points-per-feed lookup: [minLevel, ptsPerFeed] — descending order
    private static final int[][] BEST_POINTS = {
            {115, 42}, {110, 41}, {105, 39}, {100, 38},
            {90,  36}, {80,  33}, {70,  31},  {60,  28},
            {50,  26}, {40,  22}, {30,  20},  {20,  18},
            {10,  15}, {1,   12}
    };

    public static int bestPointsAtLevel(int level) {
        for (int[] row : BEST_POINTS) {
            if (level >= row[0]) return row[1];
        }
        return 12;
    }

    public static FeedResult solve(MountStats stats) {
        StatEntry[] statArr = stats.toArray();
        int[] needed = new int[8];
        for (int i = 0; i < 8; i++) needed[i] = statArr[i].needed();

        int highestCurrent = stats.highestCurrent();
        int currentTier = MaterialRegistry.maxUsableTier(highestCurrent);
        List<Material> currentTierMats = MaterialRegistry.materialsAtTier(currentTier);

        // Phase A — current tier only
        FeedPlan planA = greedySolve(needed, currentTierMats, null, currentTier);

        // Phase B — try unlocking each higher tier by training one stat
        FeedPlan bestOptimal = planA;

        for (int tier : MaterialRegistry.TIER_THRESHOLDS) {
            if (tier <= currentTier) continue;

            List<Material> tierMats = MaterialRegistry.materialsAtTier(tier);
            if (tierMats.isEmpty()) continue;

            for (int s = 0; s < 8; s++) {
                if (statArr[s].current() >= tier) continue; // already meets threshold
                if (statArr[s].limit() < tier) continue;   // limit too low, can't train there

                // Training is a free in-game action: limit >= tier is guaranteed above,
                // so the player can train this stat to the threshold at no material cost.
                String note = String.format("Train %s to %d (free in-game training)",
                        MountStats.STAT_NAMES[s], tier);
                FeedPlan tierPlan = greedySolve(needed, tierMats, note, tier);

                if (tierPlan.totalFeeds() < bestOptimal.totalFeeds()) {
                    bestOptimal = new FeedPlan(
                            tierPlan.materials(),
                            tierPlan.totalFeeds(),
                            tierPlan.totalFeeds() * 6,
                            note,
                            tier
                    );
                }
            }
        }

        // Phase C — cascading unlock: pre-feed at an intermediate tier to push a stat's
        // limit high enough to unlock a higher tier, then finish with that higher tier's materials.
        // Example: altitude limit=10 → train to T10, feed 2×Birch Paper (limit→30),
        //          train to T30 for free, finish with T30 materials.
        for (int tMidIdx = 0; tMidIdx < MaterialRegistry.TIER_THRESHOLDS.length; tMidIdx++) {
            int tMid = MaterialRegistry.TIER_THRESHOLDS[tMidIdx];
            if (tMid <= currentTier) continue;

            // T_mid is reachable only if some stat's limit already meets the threshold
            boolean tMidReachable = false;
            for (StatEntry se : statArr) {
                if (se.limit() >= tMid) { tMidReachable = true; break; }
            }
            if (!tMidReachable) continue;

            List<Material> tMidMats = MaterialRegistry.materialsAtTier(tMid);
            if (tMidMats.isEmpty()) continue;

            for (int tTargetIdx = tMidIdx + 1; tTargetIdx < MaterialRegistry.TIER_THRESHOLDS.length; tTargetIdx++) {
                int tTarget = MaterialRegistry.TIER_THRESHOLDS[tTargetIdx];
                List<Material> tTargetMats = MaterialRegistry.materialsAtTier(tTarget);
                if (tTargetMats.isEmpty()) continue;

                for (int s = 0; s < 8; s++) {
                    int statLimit = statArr[s].limit();
                    int statMax   = statArr[s].max();

                    if (statLimit < tMid)    continue; // stat can't unlock T_mid via training
                    if (statLimit >= tTarget) continue; // Phase B already handles this case
                    if (statMax < tTarget)   continue; // stat can never reach T_target

                    // Find best T_mid material for stat s (minimises pre-feed count)
                    Material bestMat = null;
                    int bestBonus = 0;
                    for (Material mat : tMidMats) {
                        if (mat.bonuses()[s] > bestBonus) {
                            bestBonus = mat.bonuses()[s];
                            bestMat = mat;
                        }
                    }
                    if (bestMat == null || bestBonus == 0) continue;

                    // How many T_mid pre-feeds push stat s's limit from statLimit up to tTarget?
                    int gap = tTarget - statLimit;
                    int preFeeds = (gap + bestBonus - 1) / bestBonus;

                    // Apply pre-feeds to the remaining deficit (they also help other stats)
                    int[] remainingAfterPre = needed.clone();
                    int[] preBonus = bestMat.bonuses();
                    for (int si = 0; si < 8; si++) {
                        remainingAfterPre[si] = Math.max(remainingAfterPre[si] - preFeeds * preBonus[si], 0);
                    }

                    String note = String.format(
                            "Train %s to %d, feed %d\u00d7%s, train %s to %d",
                            MountStats.STAT_NAMES[s], tMid, preFeeds, bestMat.name(),
                            MountStats.STAT_NAMES[s], tTarget);

                    FeedPlan targetPlan = greedySolve(remainingAfterPre, tTargetMats, note, tTarget);
                    int totalFeeds = preFeeds + targetPlan.totalFeeds();

                    if (totalFeeds < bestOptimal.totalFeeds()) {
                        // Pre-feed material is T_mid; target plan materials are T_target — no overlap possible
                        List<FeedPlan.MaterialCount> combined = new ArrayList<>();
                        combined.add(new FeedPlan.MaterialCount(bestMat, preFeeds));
                        combined.addAll(targetPlan.materials());
                        bestOptimal = new FeedPlan(combined, totalFeeds, totalFeeds * 6, note, tTarget);
                    }
                }
            }
        }

        return new FeedResult(planA, bestOptimal);
    }

    /**
     * Greedy solver: repeatedly pick the material that covers the most remaining deficit.
     */
    private static FeedPlan greedySolve(int[] needed, List<Material> materials, String trainingNote, int tier) {
        if (materials.isEmpty()) return FeedPlan.empty(tier);

        int[] remaining = needed.clone();
        int[] counts = new int[materials.size()];

        boolean anyNeeded = false;
        for (int n : remaining) if (n > 0) { anyNeeded = true; break; }
        if (!anyNeeded) return FeedPlan.empty(tier);

        int maxIter = 2000;
        while (maxIter-- > 0) {
            boolean stillNeeded = false;
            for (int n : remaining) if (n > 0) { stillNeeded = true; break; }
            if (!stillNeeded) break;

            int bestIdx = -1;
            int bestReduction = 0;
            for (int mi = 0; mi < materials.size(); mi++) {
                int[] bonuses = materials.get(mi).bonuses();
                int reduction = 0;
                for (int s = 0; s < 8; s++) reduction += Math.min(remaining[s], bonuses[s]);
                if (reduction > bestReduction) {
                    bestReduction = reduction;
                    bestIdx = mi;
                }
            }

            if (bestIdx == -1 || bestReduction == 0) break;

            counts[bestIdx]++;
            int[] bonuses = materials.get(bestIdx).bonuses();
            for (int s = 0; s < 8; s++) remaining[s] = Math.max(remaining[s] - bonuses[s], 0);
        }

        // Prune over-allocated feeds: if removing one unit of a material leaves all
        // stats still satisfied (surplus >= bonus), remove it.
        // Recompute surplus from counts since remaining[] was clamped to 0.
        int[] surplus = new int[8];
        for (int mi = 0; mi < materials.size(); mi++) {
            if (counts[mi] == 0) continue;
            int[] b = materials.get(mi).bonuses();
            for (int s = 0; s < 8; s++) surplus[s] += counts[mi] * b[s];
        }
        for (int s = 0; s < 8; s++) surplus[s] -= needed[s];
        boolean changed = true;
        while (changed) {
            changed = false;
            for (int mi = 0; mi < materials.size(); mi++) {
                if (counts[mi] == 0) continue;
                int[] bonuses = materials.get(mi).bonuses();
                boolean canRemove = true;
                for (int s = 0; s < 8; s++) {
                    if (surplus[s] < bonuses[s]) { canRemove = false; break; }
                }
                if (canRemove) {
                    counts[mi]--;
                    for (int s = 0; s < 8; s++) surplus[s] -= bonuses[s];
                    changed = true;
                }
            }
        }

        List<FeedPlan.MaterialCount> result = new ArrayList<>();
        int total = 0;
        for (int mi = 0; mi < materials.size(); mi++) {
            if (counts[mi] > 0) {
                result.add(new FeedPlan.MaterialCount(materials.get(mi), counts[mi]));
                total += counts[mi];
            }
        }

        return new FeedPlan(result, total, total * 6, trainingNote, tier);
    }

    private FeedOptimizer() {}
}
