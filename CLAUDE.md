# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Game mechanics reference

See [WYNNCRAFT_MOUNTS.md](WYNNCRAFT_MOUNTS.md) for an explanation of the mount feeding, training, and breeding system that this mod is built around. Read it before modifying the optimizer or stat model.

## What this mod does

**Wynncraft Mounts Helper** is a Fabric client-side mod for Minecraft 1.21.11. It intercepts Wynncraft mount item tooltips, parses the 8 mount stats from their lore text, runs a feed optimizer, and renders a two-panel overlay (current-tier plan vs. optimal plan) over the vanilla tooltip.

## Build & run commands

```bash
# Build the mod jar
./gradlew build

# Run tests (JUnit 5, no Minecraft runtime needed)
./gradlew test

# Run a specific test class
./gradlew test --tests "me.kmaxi.wynnmounts.optimizer.FeedOptimizerTest"

# Launch the Minecraft client in dev (requires DevAuth for Mojang account)
./gradlew runClient
```

The built jar lands in `build/libs/`. DevAuth (`me.djtheredstoner:DevAuth-fabric:1.2.2`) is a runtime-only dependency that handles authentication in dev runs.

## Architecture

### Data flow
1. **`HandledScreenMixin`** — injects into `AbstractContainerScreen.renderTooltip` (runs on every hovered slot). When a slot contains an item whose lore includes `"Potential"`, it parses stats and caches the result. The cache invalidates only when the hovered slot index changes, so the optimizer doesn't re-run every frame.
2. **`MountStats.parse()`** — strips Wynncraft's custom-font private-use characters (keeping only ASCII 32–126), then regex-matches lines of the form `StatName current/limit (max)`. Returns `null` if fewer than 8 stats are found.
3. **`FeedOptimizer.solve()`** — two-phase greedy optimizer:
   - **Phase A**: greedy pick using only materials at the current usable tier (determined by `highestCurrent()` across all stats).
   - **Phase B**: for each higher tier threshold, checks whether any stat can be trained there for free (its `limit` already meets the threshold — training is a free in-game action). If so, runs a greedy pass with that tier's materials and keeps whichever total feeds is lowest.
4. **`FeedOverlayRenderer`** — draws two side-by-side panels anchored to the left edge of the screen (Wynncraft's tooltip always opens center-right), rendered after the vanilla tooltip via a second `@Inject(at = @At("TAIL"))`.
5. **`WynncraftMountsHelperClient`** also registers an `ItemTooltipCallback` that appends summary lines directly to the item tooltip (simpler path, no overlay needed).

### Key data types
- **`StatEntry(current, limit, max)`** — `needed()` returns `max(max - limit, 0)` (the gap to fill via feeding).
- **`Material(tier, name, int[8] bonuses)`** — bonus index order: Speed, Acceleration, Altitude, Energy, Handling, Toughness, Boost, Training (same order as `MountStats.STAT_NAMES`).
- **`MaterialRegistry`** — static registry of all 112 materials across 14 tiers (thresholds: 1, 10, 20 … 110, 115). All tier lookups go through `maxUsableTier()` and `materialsAtTier()`.
- **`FeedPlan`** — optimizer output: list of `MaterialCount`, total feeds, estimated hours (feeds × 6), optional `trainingNote`, and the tier used.
- **`FeedResult(currentTierPlan, optimalPlan)`** — `hasImprovement()` is true when `optimalPlan.totalFeeds() < currentTierPlan.totalFeeds()`.

### Mixin
`wynnmounts.mixins.json` registers `HandledScreenMixin` on `AbstractContainerScreen`. The mixin also writes raw lore to `wynnmounts_data.md` in the run directory (data-collection behaviour for debugging).

### Tests
`FeedOptimizerTest` and `MaterialRegistryTest` in `src/test/` run without any Minecraft classes — the optimizer and registry are pure Java. Tests document expected feed counts verified manually against known optimal plans.
