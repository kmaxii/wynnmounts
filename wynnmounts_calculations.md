# Mount Stat Calculator — How Everything Works

This is a Wynncraft mount optimizer. It has two major systems: a **Feed Optimizer** and a **Multi-Generation Breeding Roadmap**. Here's how every calculation works.

---

## 1. Data Model

### Stats (8 total)

Speed, Acceleration, Altitude, Energy, Handling, Toughness, Boost, Training

### Materials

Each material is stored as:

```
[tier, name, speed, acceleration, altitude, energy, handling, toughness, boost, training]

// Example:
[1, "Copper Ingot", 0, 0, 0, 4, 0, 8, 0, 0]
```

- **Tier** = minimum player level required to use that material
- Each material boosts exactly 2–3 stats (the rest are 0)
- Tiers available: 1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 105, 110, 115
- Higher-tier materials give larger stat bonuses

### Inputs per stat

| Field | Meaning |
|---|---|
| Current Level | How many points the stat has right now |
| Current Level Limit | The cap the stat can train up to currently |
| Max Level | The absolute max you want to reach |

---

## 1b. Materials Reference

Each tier has 8 materials. Stats not listed are 0. The stat pattern is consistent across all tiers:

| Material Type | Stats Boosted |
|---|---|
| Ingot | Toughness + Training |
| Gem | Speed + Energy + Boost |
| Wood | Speed + Handling + Toughness |
| Paper | Altitude + Boost |
| String | Acceleration + Handling + Boost |
| Grains | Speed + Boost |
| Oil | Acceleration + Handling + Boost |
| Meat | Energy + Boost |

### Level 1

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Copper Ingot | — | — | — | — | — | 4 | — | 8 |
| Copper Gem | 4 | — | — | 2 | — | — | 6 | — |
| Oak Wood | 2 | — | — | — | 6 | 4 | — | — |
| Oak Paper | — | — | 8 | — | — | — | 4 | — |
| Wheat String | — | 2 | — | — | 4 | — | 6 | — |
| Wheat Grains | 8 | — | — | — | — | — | 4 | — |
| Gudgeon Oil | — | 2 | — | — | 6 | — | 4 | — |
| Gudgeon Meat | — | — | — | 4 | — | — | 8 | — |

### Level 10

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Granite Ingot | — | — | — | — | — | 5 | — | 10 |
| Granite Gem | 5 | — | — | 2 | — | — | 8 | — |
| Birch Wood | 2 | — | — | — | 8 | 5 | — | — |
| Birch Paper | — | — | 10 | — | — | — | 5 | — |
| Barley String | — | 2 | — | — | 5 | — | 8 | — |
| Barley Grains | 10 | — | — | — | — | — | 5 | — |
| Trout Oil | — | 2 | — | — | 8 | — | 5 | — |
| Trout Meat | — | — | — | 5 | — | — | 10 | — |

### Level 20

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Gold Ingot | — | — | — | — | — | 5 | — | 12 |
| Gold Gem | 6 | — | — | 3 | — | — | 9 | — |
| Willow Wood | 3 | — | — | — | 9 | 6 | — | — |
| Willow Paper | — | — | 12 | — | — | — | 5 | — |
| Oat String | — | 3 | — | — | 6 | — | 9 | — |
| Oat Grains | 12 | — | — | — | — | — | 5 | — |
| Salmon Oil | — | 3 | — | — | 9 | — | 6 | — |
| Salmon Meat | — | — | — | 5 | — | — | 12 | — |

### Level 30

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Sandstone Ingot | — | — | — | — | — | 6 | — | 14 |
| Sandstone Gem | 6 | — | — | 3 | — | — | 11 | — |
| Acacia Wood | 3 | — | — | — | 11 | 6 | — | — |
| Acacia Paper | — | — | 14 | — | — | — | 6 | — |
| Malt String | — | 3 | — | — | 6 | — | 11 | — |
| Malt Grains | 14 | — | — | — | — | — | 6 | — |
| Carp Oil | — | 3 | — | — | 11 | — | 6 | — |
| Carp Meat | — | — | — | 6 | — | — | 14 | — |

### Level 40

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Iron Ingot | — | — | — | — | — | 6 | — | 16 |
| Iron Gem | 7 | — | — | 3 | — | — | 12 | — |
| Spruce Wood | 3 | — | — | — | 12 | 7 | — | — |
| Spruce Paper | — | — | 16 | — | — | — | 6 | — |
| Hops String | — | 3 | — | — | 7 | — | 12 | — |
| Hops Grains | 16 | — | — | — | — | — | 6 | — |
| Icefish Oil | — | 3 | — | — | 12 | — | 7 | — |
| Icefish Meat | — | — | — | 6 | — | — | 16 | — |

### Level 50

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Silver Ingot | — | — | — | — | — | 7 | — | 18 |
| Silver Gem | 8 | — | — | 4 | — | — | 14 | — |
| Jungle Wood | 4 | — | — | — | 14 | 8 | — | — |
| Jungle Paper | — | — | 18 | — | — | — | 7 | — |
| Rye String | — | 4 | — | — | 8 | — | 14 | — |
| Rye Grains | 18 | — | — | — | — | — | 7 | — |
| Piranha Oil | — | 4 | — | — | 14 | — | 8 | — |
| Piranha Meat | — | — | — | 7 | — | — | 18 | — |

### Level 60

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Cobalt Ingot | — | — | — | — | — | 8 | — | 20 |
| Cobalt Gem | 9 | — | — | 4 | — | — | 15 | — |
| Dark Wood | 4 | — | — | — | 15 | 9 | — | — |
| Dark Paper | — | — | 20 | — | — | — | 8 | — |
| Millet String | — | 4 | — | — | 9 | — | 15 | — |
| Millet Grains | 20 | — | — | — | — | — | 8 | — |
| Koi Oil | — | 4 | — | — | 15 | — | 9 | — |
| Koi Meat | — | — | — | 8 | — | — | 20 | — |

### Level 70

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Kanderstone Ingot | — | — | — | — | — | 8 | — | 22 |
| Kanderstone Gem | 10 | — | — | 4 | — | — | 17 | — |
| Light Wood | 4 | — | — | — | 17 | 10 | — | — |
| Light Paper | — | — | 22 | — | — | — | 8 | — |
| Decay String | — | 4 | — | — | 10 | — | 17 | — |
| Decay Grains | 22 | — | — | — | — | — | 8 | — |
| Gylia Oil | — | 4 | — | — | 17 | — | 10 | — |
| Gylia Meat | — | — | — | 8 | — | — | 22 | — |

### Level 80

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Diamond Ingot | — | — | — | — | — | 9 | — | 24 |
| Diamond Gem | 10 | — | — | 4 | — | — | 18 | — |
| Pine Wood | 4 | — | — | — | 18 | 10 | — | — |
| Pine Paper | — | — | 24 | — | — | — | 9 | — |
| Rice String | — | 4 | — | — | 10 | — | 18 | — |
| Rice Grains | 24 | — | — | — | — | — | 9 | — |
| Bass Oil | — | 4 | — | — | 18 | — | 10 | — |
| Bass Meat | — | — | — | 9 | — | — | 24 | — |

### Level 90

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Molten Ingot | — | — | — | — | — | 9 | — | 26 |
| Molten Gem | 11 | — | — | 5 | — | — | 20 | — |
| Avo Wood | 5 | — | — | — | 20 | 11 | — | — |
| Avo Paper | — | — | 26 | — | — | — | 9 | — |
| Sorghum String | — | 5 | — | — | 11 | — | 20 | — |
| Sorghum Grains | 26 | — | — | — | — | — | 9 | — |
| Molten Oil | — | 5 | — | — | 20 | — | 11 | — |
| Molten Meat | — | — | — | 9 | — | — | 26 | — |

### Level 100

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Voidstone Ingot | — | — | — | — | — | 10 | — | 28 |
| Voidstone Gem | 12 | — | — | 5 | — | — | 21 | — |
| Sky Wood | 5 | — | — | — | 21 | 12 | — | — |
| Sky Paper | — | — | 28 | — | — | — | 10 | — |
| Hemp String | — | 5 | — | — | 12 | — | 21 | — |
| Hemp Grains | 28 | — | — | — | — | — | 10 | — |
| Starfish Oil | — | 5 | — | — | 21 | — | 12 | — |
| Starfish Meat | — | — | — | 10 | — | — | 28 | — |

### Level 105

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Dernic Ingot | — | — | — | — | — | 10 | — | 29 |
| Dernic Gem | 12 | — | — | 5 | — | — | 22 | — |
| Dernic Wood | 5 | — | — | — | 22 | 12 | — | — |
| Dernic Paper | — | — | 29 | — | — | — | 10 | — |
| Dernic String | — | 5 | — | — | 12 | — | 22 | — |
| Dernic Grains | 29 | — | — | — | — | — | 10 | — |
| Dernic Oil | — | 5 | — | — | 22 | — | 12 | — |
| Dernic Meat | — | — | — | 10 | — | — | 29 | — |

### Level 110

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Titanium Ingot | — | — | — | — | — | 11 | — | 30 |
| Titanium Gem | 13 | — | — | 5 | — | — | 23 | — |
| Maple Wood | 5 | — | — | — | 23 | 13 | — | — |
| Maple Paper | — | — | 30 | — | — | — | 11 | — |
| Jute String | — | 5 | — | — | 13 | — | 23 | — |
| Jute Grains | 30 | — | — | — | — | — | 11 | — |
| Sturgeon Oil | — | 5 | — | — | 23 | — | 13 | — |
| Sturgeon Meat | — | — | — | 11 | — | — | 30 | — |

### Level 115

| Material | Spd | Acc | Alt | Ene | Han | Tou | Bst | Trn |
|---|---|---|---|---|---|---|---|---|
| Cinnabar Ingot | — | — | — | — | — | 11 | — | 31 |
| Cinnabar Gem | 13 | — | — | 5 | — | — | 23 | — |
| Redwood Wood | 5 | — | — | — | 23 | 13 | — | — |
| Redwood Paper | — | — | 31 | — | — | — | 11 | — |
| Heather String | — | 5 | — | — | 13 | — | 23 | — |
| Heather Grains | 31 | — | — | — | — | — | 11 | — |
| Mahseer Oil | — | 5 | — | — | 23 | — | 13 | — |
| Mahseer Meat | — | — | — | 11 | — | — | 31 | — |

> There is currently no known difference in effectiveness between different rarity tiers of these materials.

---

## 2. Derived Column Calculations

These update live as you type:

```
Points Still Needed = max(Max Level − Current Level Limit, 0)
% Complete         = round((Current Level / Max Level) × 100)
Highest Current Level = max of all Current Level values (used for tier unlocking)
Total Potential = sum of all Max Levels (shown in the header as "POTENTIAL 400" by default)
```

---

## 3. Tier Unlocking

Which tier of materials you can use depends on your highest stat level:

```js
const TIER_THRESHOLDS = [1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 105, 110, 115];
maxUsableTier(H) = largest tier ≤ H
```

**Example:** if your highest stat is 25, you can use tier-20 materials (but not tier-30).

---

## 4. Feed Optimizer — Core Algorithm

The optimizer answers: *"What's the minimum number of feeds to reach your target stats?"*

It runs in three phases and picks the cheapest plan.

### Phase A: Stay at current tier

Solve entirely using materials at your current tier.

### Phase B: Train first to unlock a higher tier

If training a stat to the next tier threshold reduces total feeds, recommend that.

### Phase C: Multi-tier path (start from tier 1)

Sometimes lower-tier materials are cheaper for initial coverage — this catches those cases.

---

### The Solver: LP Relaxation + Branch & Bound

For a given set of materials and needed stat points, it solves:

```
Minimize total feeds purchased
Subject to: each stat's need is fully covered
```

#### Step 1 — LP Relaxation (Big-M Simplex)

Sets up a linear programming problem treating feed counts as continuous (fractional) variables:

```
minimize: Σ x_j          (total feeds)
subject to:
  Σ x_j × material[j][stat_s] ≥ needed[stat_s]   for each stat s
  x_j ≥ 0
```

Uses a standard two-phase simplex with artificial variables and a Big-M penalty (`1e9`) to drive artificials to zero. The LP gives a fractional lower bound — each `x_j` is then ceiling'd to get an integer starting point.

#### Step 2 — Greedy Reduction

Starting from the LP ceiling solution, it tries to reduce each feed count by 1. If removing a feed still satisfies all constraints, keep the reduction. Repeat until no more reductions are possible.

#### Step 3 — Branch and Bound (DFS + Memoization)

From the greedy solution, runs a depth-first search over all materials, trying different integer counts.

**Key pruning:**

```
Lower bound = max over all stats of: ceil(remaining[s] / maxCoeff[s])
```

If `currentFeeds + lowerBound ≥ bestSoFar`, prune that branch. Memoizes by `(depth | remaining[])` to avoid re-exploring states. Hard timeout of 2 seconds per tier solve.

---

### Multi-Phase Optimizer

When a single tier can't reach all targets, the optimizer loops:

```
while remaining needs exist:
  1. Try solving at current tier (baseline cost)
  2. Try every future tier × every stat as an "unlock target"
     - Cost = feeds to raise stat to unlock new tier + feeds at new tier
  3. Pick whichever is cheaper
  4. Update curLevels and remaining needs
  5. Repeat
```

This greedily picks the cheapest unlock path across all possible tier jumps — not just the next adjacent tier.

---

## 5. "Ignore Training" Toggle

When enabled (`noUnlock = true`), the optimizer skips all tier-unlock attempts. You're locked to your current tier's materials, and the roadmap uses your actual current levels instead of assuming you'll train up.

---

## 6. Multi-Generation Breeding Roadmap

A dynamic programming system that figures out the optimal breeding strategy to reach a target potential.

### Key Concepts

- **Potential** = Max Level × 8 (all 8 stats combined)
- Each stat has a **limit** (how high you can train it before breeding)
- Breeding two horses produces offspring whose new limit ≈ `parent_limit + 30`
- The offspring's starting base limit ≈ `parent_limit − 20` (born lower, improved by breeding bonus)

### Feed cost calculation

The best points-per-feed scales by your horse's level:

```js
function getBestPoints(level) {
  // lookup table: [minLevel, ptsPerFeed]
  [[115,42],[110,41],[105,39],[100,38],[90,36],[80,33],
   [70,31],[60,28],[50,26],[40,22],[30,20],[20,18],[10,15],[1,12]]
}
```

**Feeds needed to raise a stat limit:**

```
feeds = ceil((targetLimit − startLimit) × 8 / bestPoints(level))
time  = feeds × 6 hours  (each feed takes 6h)
```

**For Gen 0 base parents** (starting limit = 10, hardcoded):

```
feeds to reach limit L = ceil((L×8 − 80) / 20)   // 20pts/feed at tier 30+
time = feeds × 6h + 6h (for breed itself)
```

### DP Transition

```
dp[gen][M_curr] = minimum total hours to produce a horse with limit M_curr at generation gen
```

**Base case (Gen 1):**

```
Two Gen-0 parents, each fed to limit L0 (range 20–30)
M1 = L0 + 30
cost = 2 × feedCostGen0(L0) + 6
```

**Transition (Gen N):**

```
For each (M_prev in dp[gen-1]):
  base = M_prev − 20       (offspring's starting limit)
  For each L from base to M_prev:
    M_curr = L + 30
    cost = 2 × (parentCost + feedCostGenN(base, L, level)) + 6
```

The `× 2` accounts for needing two parents at that quality, and `+ 6` is the breed time.

### Finding the Optimal Path

```
findBestDP(dp, targetPot):
  need = ceil(targetPot / 8)   // per-stat target
  Search all gen/M combos where M_curr ≥ need
  Return the one with lowest total cost hours
```

Then it back-traces through the DP table to reconstruct the step-by-step breeding plan.

### Parallel Breeding (Account Tier)

The roadmap shows real elapsed time assuming you breed multiple lines in parallel:

```
parallelPairs = 3 (Normal) | 4 (Hero+) | 5 (Champion)
```

For each generation step (from last to first):

```
real_elapsed += ceil(2^(stepsRemaining − 1) / parallelPairs) × stepHours
```

The `2^(depth)` reflects the exponential number of horses you're raising simultaneously in a full binary tree.

### Base Horses Needed

```
totalHorses = 2 ^ (number of breeding steps)
```

A 4-step path needs `2^4 = 16` base Gen-0 horses.

---

## Summary Table

| Feature | Algorithm |
|---|---|
| Points Still Needed | `max(maxLevel − currentLimit, 0)` |
| % Complete | `(currentLevel / maxLevel) × 100` |
| Tier unlock | Largest threshold ≤ highest current level |
| Feed optimizer | LP relaxation → greedy reduction → B&B DFS |
| Multi-tier path | Greedy phase selection over all tier×stat combos |
| Breeding DP | Bottom-up DP over (generation, limit) |
| Feed time | `ceil(Δlimit × 8 / bestPoints) × 6h` |
| Parallel time | `ceil(2^depth / parallelSlots) × stepHours` |
