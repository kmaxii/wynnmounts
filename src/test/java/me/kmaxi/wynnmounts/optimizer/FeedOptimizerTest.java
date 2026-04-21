package me.kmaxi.wynnmounts.optimizer;

import me.kmaxi.wynnmounts.data.FeedResult;
import me.kmaxi.wynnmounts.data.MountStats;
import me.kmaxi.wynnmounts.data.MountType;
import me.kmaxi.wynnmounts.data.StatEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeedOptimizerTest {

    // ── helpers ──────────────────────────────────────────────────────────────

    /** Build MountStats where every stat has the same StatEntry. */
    private static MountStats uniform(int current, int limit, int max) {
        StatEntry e = new StatEntry(current, limit, max);
        return new MountStats(MountType.UNKNOWN, e, e, e, e, e, e, e, e);
    }

    // ── Test 1: bestPointsAtLevel lookup table ────────────────────────────────

    @Test
    void testBestPointsAtLevel() {
        assertEquals(42, FeedOptimizer.bestPointsAtLevel(115));
        assertEquals(41, FeedOptimizer.bestPointsAtLevel(114)); // [110,41] bucket
        assertEquals(41, FeedOptimizer.bestPointsAtLevel(110));
        assertEquals(39, FeedOptimizer.bestPointsAtLevel(105));
        assertEquals(38, FeedOptimizer.bestPointsAtLevel(100));
        assertEquals(36, FeedOptimizer.bestPointsAtLevel(90));
        assertEquals(33, FeedOptimizer.bestPointsAtLevel(80));
        assertEquals(31, FeedOptimizer.bestPointsAtLevel(70));
        assertEquals(28, FeedOptimizer.bestPointsAtLevel(60));
        assertEquals(26, FeedOptimizer.bestPointsAtLevel(50));
        assertEquals(22, FeedOptimizer.bestPointsAtLevel(40));
        assertEquals(20, FeedOptimizer.bestPointsAtLevel(30));
        assertEquals(18, FeedOptimizer.bestPointsAtLevel(20));
        assertEquals(15, FeedOptimizer.bestPointsAtLevel(10));
        assertEquals(12, FeedOptimizer.bestPointsAtLevel(1));
        assertEquals(12, FeedOptimizer.bestPointsAtLevel(0)); // below minimum → 12
    }

    // ── Test 2: already maxed ─────────────────────────────────────────────────

    @Test
    void testAlreadyMaxed() {
        // needed = max(50-50, 0) = 0 for all stats
        FeedResult r = FeedOptimizer.solve(uniform(50, 50, 50));
        assertEquals(0, r.currentTierPlan().totalFeeds());
        assertEquals(0, r.optimalPlan().totalFeeds());
        assertFalse(r.hasImprovement());
    }

    // ── Test 3: single stat needs feeding ("ignore training" path) ────────────

    @Test
    void testSingleStatNeedsFeeding() {
        // Training: current=1, limit=1, max=10 → needed=9
        // All stats limit=1 < every tier threshold above 1 → Phase B never unlocks anything
        StatEntry zero = new StatEntry(1, 1, 1);
        StatEntry trn  = new StatEntry(1, 1, 10);
        MountStats stats = new MountStats(MountType.UNKNOWN, zero, zero, zero, zero, zero, zero, zero, trn);

        FeedResult r = FeedOptimizer.solve(stats);

        assertEquals(1, r.currentTierPlan().tier());
        // Copper Gem gives 6 Training/feed. ceil(9/6) = 2 feeds.
        assertEquals(2, r.currentTierPlan().totalFeeds());
        assertFalse(r.hasImprovement());
    }

    // ── Test 4: single-stat Training mount where training to T10 saves feeds ──

    @Test
    void testTrainingStat_TrainingBetterThanCurrentTier() {
        // Training: current=1, limit=10, max=50 → needed=40; highestCurrent=1 → T1 baseline
        // All other stats: limit=1, max=1 → needed=0 and cannot be used to unlock higher tiers
        //
        // Phase A (T1): best T1 material for Training is Copper Gem (6/feed).
        //   ceil(40/6) = 7 feeds.
        //
        // Phase B: only Training can be trained (others have limit=1 < any tier > 1).
        //   Training is free (limit=10 >= T10 threshold) — no material cost.
        //   T10 greedySolve on original needed=40: Granite Gem (8/feed) → ceil(40/8) = 5 feeds.
        //   Total = 5 < 7. Improvement!

        StatEntry zero = new StatEntry(1, 1, 1);
        StatEntry trn  = new StatEntry(1, 10, 50);
        MountStats stats = new MountStats(MountType.UNKNOWN, zero, zero, zero, zero, zero, zero, zero, trn);

        FeedResult r = FeedOptimizer.solve(stats);

        assertTrue(r.hasImprovement());
        assertEquals(1,  r.currentTierPlan().tier());
        assertEquals(7,  r.currentTierPlan().totalFeeds());
        assertEquals(10, r.optimalPlan().tier());
        assertEquals(5,  r.optimalPlan().totalFeeds());
        assertNotNull(r.optimalPlan().trainingNote());
        assertTrue(r.optimalPlan().trainingNote().contains("Training"));
    }

    // ── Test 5: high current stat unlocks a higher tier ───────────────────────

    @Test
    void testHighCurrentUnlocksHigherTier() {
        // Training current=50 → highestCurrent=50 → Tier 50 materials available
        // Speed: current=1, limit=20, max=40 → needed=20; all others needed=0
        StatEntry maxed = new StatEntry(50, 50, 50);
        StatEntry spd   = new StatEntry(1, 20, 40);
        MountStats stats = new MountStats(MountType.UNKNOWN, spd, maxed, maxed, maxed, maxed, maxed, maxed, maxed);

        FeedResult r = FeedOptimizer.solve(stats);

        assertEquals(50, r.currentTierPlan().tier(),
                "highestCurrent=50 should select Tier 50 materials");
        // Rye Grains (T50) gives 18 Speed/feed. ceil(20/18) = 2 feeds.
        assertEquals(2, r.currentTierPlan().totalFeeds());
    }

    // ── Test 6: tier 1 all-stats case — regression for greedy over-allocation ──

    @Test
    void testTier1AllStats_noOverAllocation() {
        // All stats: current=1, limit=10, max=30 → needed=20 each; highestCurrent=1 → T1
        // Phase A: current-tier (T1) plan must be 14 feeds (not 15 — greedy must not over-allocate).
        // Phase C may now find improvement via cascading tier unlock.
        FeedResult r = FeedOptimizer.solve(uniform(1, 10, 30));

        assertEquals(1,  r.currentTierPlan().tier());
        assertEquals(14, r.currentTierPlan().totalFeeds());
    }

    // ── Test 8: Phase C cascading tier unlock ────────────────────────────────────
    //
    // altitude: current=1, limit=10, max=40 → needed=30
    // handling: current=1, limit=1,  max=34 → needed=33  (no limit to train above T1)
    // all others: maxed (needed=0)
    //
    // Phase A (T1):  greedy picks Oak Paper + Gudgeon Oil → 9 feeds.
    //
    // Phase B (T10, unlocked via altitude.limit=10):
    //   greedySolve with T10 mats → 2×Birch Paper + 5×Trout Oil = 7 feeds.
    //
    // Phase C finds 6 feeds via either:
    //   T_mid=10, T_target=20, stat=Altitude: 1 pre-feed + 5 T20 = 6 feeds, OR
    //   T_mid=10, T_target=30, stat=Altitude: 2 pre-feeds + 4 T30 = 6 feeds.
    // Either is a valid optimal result — we assert 6 feeds and that Altitude is the
    // bootstrapped stat, but not a specific tier (both 20 and 30 are correct).

    @Test
    void testCascadingTierUnlock_PhaseC() {
        StatEntry zero = new StatEntry(1, 1, 1);
        StatEntry alt  = new StatEntry(1, 10, 40);
        StatEntry han  = new StatEntry(1, 1, 34);
        MountStats stats = new MountStats(MountType.UNKNOWN,
                zero, zero, alt, zero, han, zero, zero, zero);

        FeedResult r = FeedOptimizer.solve(stats);

        assertTrue(r.hasImprovement(), "Phase C should find a 6-feed plan better than Phase A/B");
        assertTrue(r.optimalPlan().tier() >= 20, "Optimal tier should be ≥ 20 (Phase C unlock)");
        assertEquals(6, r.optimalPlan().totalFeeds(), "Phase C: pre-feeds + target-tier feeds = 6 total");
        assertNotNull(r.optimalPlan().trainingNote());
        assertTrue(r.optimalPlan().trainingNote().contains("Altitude"),
                "Training note should mention the bootstrapped stat");
    }

    // ── Test 9: all stats 1/10/30 — full cascading T10→T30 path ─────────────────
    //
    // Screenshot scenario: gen-0 horse saddle, every stat current=1, limit=10, max=30.
    //
    // Phase A (T1): 14 feeds (verified in Test 6).
    //
    // Phase C (tMid=10, tTarget=30, stat=Altitude):
    //   Train Altitude to 10 (free — limit=10 meets threshold)
    //   Feed 2× Birch Paper (T10, alt+10 each) → altitude limit 10→30
    //   Train Altitude to 30 (free — limit now=30)
    //   T30 greedySolve on remaining [20,20,0,20,20,20,10,20]:
    //     picks Sandstone Ingot, Acacia Plank, Carp Meat, Malt String,
    //           Sandstone Gem, Carp Oil×2, Malt Grains = 8 feeds
    //   total = 2 + 8 = 10 < 14  →  Phase C wins!
    //
    // Expected optimal path the player should follow:
    //   train altitude to 10
    //   feed birch paper ×2
    //   train altitude to 30
    //   feed malt grains, sandstone gem, acacia plank, malt string,
    //         carp meat, sandstone ingot, carp oil ×2

    @Test
    void testAllStats_1_10_30_cascadingUnlockOptimal() {
        FeedResult r = FeedOptimizer.solve(uniform(1, 10, 30));

        assertTrue(r.hasImprovement(), "Phase C should beat 14-feed T1 plan");
        assertEquals(30, r.optimalPlan().tier(),
                "Optimal plan should use Tier 30 materials (cascading T10→T30 via Altitude)");
        assertEquals(10, r.optimalPlan().totalFeeds(),
                "2 Birch Paper pre-feeds + 8 T30 feeds = 10 total");
        assertNotNull(r.optimalPlan().trainingNote());
        assertTrue(r.optimalPlan().trainingNote().contains("Altitude"),
                "Training note should name the bootstrapped stat (Altitude)");
        // preFeedCount must be 1 (one MaterialCount entry = Birch Paper ×2),
        // not 2 (feed count). The renderer uses it as a list-entry index.
        assertEquals(1, r.optimalPlan().preFeedCount(),
                "preFeedCount is a list-entry count, not a feed count: only Birch Paper is a pre-feed");
        assertEquals("Birch Paper", r.optimalPlan().materials().get(0).material().name(),
                "First material entry must be the pre-feed material (Birch Paper)");
    }

    // ── Test 10: Phase C with tMid = currentTier (Wyvern screenshot scenario) ──────────────
    //
    // Wyvern Reins from in-game screenshot:
    //   Speed: 36/38/69 (needed=31), Acc: 38/39/51 (needed=12), Alt: 15/43/58 (needed=15),
    //   Ene: 20/38/47 (needed=9),   Han: 25/42/98 (needed=56), Tou: 40/42/97 (needed=55),
    //   Bst: 37/44/95 (needed=51),  Trn: 35/44/92 (needed=48)
    //
    // highestCurrent = 40 (Toughness) → currentTier = T40
    //
    // Phase A (T40, no training): 16 feeds.
    //
    // Phase B: no stat has limit ≥ 50, so no free training to T50+ → no Phase B improvement.
    //
    // Phase C bug (current code): loop starts at tMid > currentTier=40, so T40 pre-feeds
    //   are never considered as bootstrapping material. bestOptimal stays at planA.
    //
    // Phase C fix (tMid >= currentTier=40): tries tMid=40, tTarget=60, stat=Boost.
    //   Boost: limit=44, max=95. gap to T60 = 60-44 = 16.
    //   Best T40 Boost material: Hops String (+12 Boost). preFeeds = ceil(16/12) = 2.
    //   After 2×Hops String: Boost limit 44→68 ≥ 60 → train Boost to 60 (free).
    //   Remaining: [31, 6, 15, 9, 42, 55, 27, 48]
    //   T60 greedy: 10 feeds (3×Cobalt Ingot, 2×Cobalt Gem, 2×Millet String, 2×Koi Oil,
    //                         1×Millet Grains)
    //   Total: 2 + 10 = 12 < 16 → improvement!
    //   (verified manually against optimal plan)

    @Test
    void testWyvernMount_PhaseCWithCurrentTier() {
        StatEntry spd = new StatEntry(36, 38, 69);
        StatEntry acc = new StatEntry(38, 39, 51);
        StatEntry alt = new StatEntry(15, 43, 58);
        StatEntry ene = new StatEntry(20, 38, 47);
        StatEntry han = new StatEntry(25, 42, 98);
        StatEntry tou = new StatEntry(40, 42, 97);
        StatEntry bst = new StatEntry(37, 44, 95);
        StatEntry trn = new StatEntry(35, 44, 92);
        MountStats stats = new MountStats(MountType.UNKNOWN, spd, acc, alt, ene, han, tou, bst, trn);

        FeedResult r = FeedOptimizer.solve(stats);

        assertTrue(r.hasImprovement(),
                "Phase C (tMid=currentTier=40) should find a 12-feed T60 plan better than Phase A");
        assertEquals(40, r.currentTierPlan().tier());
        assertEquals(60, r.optimalPlan().tier(),
                "Optimal plan should use Tier 60 materials (pre-feed Boost to 60 via Hops String)");
        assertEquals(12, r.optimalPlan().totalFeeds(),
                "2 Hops String pre-feeds + 10 T60 feeds = 12 total (vs 16 No Training)");
        assertNotNull(r.optimalPlan().trainingNote());
        assertTrue(r.optimalPlan().trainingNote().contains("Boost"),
                "Training note should mention Boost as the bootstrapped stat");
    }

    // ── Test 7: fully max-level mount at T115 ─────────────────────────────────

    @Test
    void testMaxLevelMount_T115Materials() {
        // All stats: current=115, limit=115, max=146 → needed=31; highestCurrent=115 → T115
        //
        // The greedy solver picks exactly 1 of each T115 material (8 feeds total):
        //   Cinnabar Ingot  → Energy(+11),      Toughness(+31)
        //   Redwood Paper   → Altitude(+31),     Boost(+11)
        //   Heather Grains  → Speed(+31),        Altitude(+11)
        //   Mahseer Meat    → Acceleration(+11), Energy(+31)
        //   Cinnabar Gem    → Speed(+13),        Energy(+5),       Training(+23)
        //   Redwood Plank   → Speed(+5),         Acceleration(+23),Toughness(+13)
        //   Heather String  → Acceleration(+5),  Handling(+13),    Boost(+23)
        //   Mahseer Oil     → Altitude(+5),      Handling(+23),    Training(+13)
        //
        // T115 is already the highest tier, so no training improvement exists.

        FeedResult r = FeedOptimizer.solve(uniform(115, 115, 146));

        assertEquals(115, r.currentTierPlan().tier());
        assertEquals(8,   r.currentTierPlan().totalFeeds());
        assertFalse(r.hasImprovement());
    }
}
