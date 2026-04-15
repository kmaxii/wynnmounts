# Wynncraft Mount System

## Stat display format

Each of the 8 stats is shown in the mount tooltip as:

```
StatName  current/limit  (max)
```

| Field | Meaning |
|---|---|
| `current` | Active level. Determines which tier of feeding materials can be used. |
| `limit` | How high the stat can be trained. Raised by feeding. **This is what breeding uses — not `current` or `max`.** |
| `max` | Absolute ceiling for this stat. Fixed per mount. Cannot be exceeded. |

**Potential** (shown in the mount header) = sum of all `max` values. It is a misleading metric for breeding — `limit` is what matters.

---

## Training (free)

Raises `current` up to `limit` at no cost. Only reason to train `current` is to unlock higher material tiers for feeding. You do **not** need `current == limit` before breeding.

---

## Feeding

- Adds a material's bonuses to the stat's `limit` (not `current`)
- Each feed has a ~6 real-time hour cooldown
- A material requires at least one stat's `current` to meet the tier threshold

`needed(stat)` = `max(max − limit, 0)` — remaining gap to fill via feeding.

---

## Tier system

| Tier | Threshold | | Tier | Threshold |
|---|---|---|---|---|
| 1 | 1 | | 8 | 70 |
| 2 | 10 | | 9 | 80 |
| 3 | 20 | | 10 | 90 |
| 4 | 30 | | 11 | 100 |
| 5 | 40 | | 12 | 105 |
| 6 | 50 | | 13 | 110 |
| 7 | 60 | | 14 | 115 |

Higher-tier materials give larger bonuses per feed. To unlock a tier, train any one stat's `current` to that threshold (free action if `limit` already meets it).

---

## Breeding

**Core rule:** offspring's per-stat limit is derived from the parent's **limit**, not `current` or `max`.

```
offspring starting limit ≈ parent_limit − 20   (born lower)
offspring max limit      ≈ parent_limit + 30   (ceiling it can be fed to)
```

Two parents are needed. Both should be fed to the same target limit.

### Optimal breeding strategy

1. **Train one stat to 10** (unlocks tier-10 materials, which are significantly more efficient than tier-1)
2. **Feed all stats until limit = max** for stats you care about in the final build
3. For stats you don't prioritize, you may stop early to save time (fewer 6h feeds)
4. **Breed** — offspring inherits `parent_limit + 30` as its new feedable ceiling
5. Repeat each generation, feeding the offspring to its own max before breeding again

Example gen-0 breed-prep plan (speed/altitude focused, all max=30):
- Speed, Acceleration, Altitude → feed to limit 30 (fully fed)
- Energy, Handling, Toughness, Boost, Training → may stop at limit 10–20 (lower priority)
- Only train Speed to 10 (to unlock tier-10 materials); everything else stays at current=1

### Feed efficiency by stat level

Best points-per-feed at each tier (used for time estimates):

| Min level | pts/feed |
|---|---|
| 115 | 42 |
| 110 | 41 |
| 105 | 39 |
| 100 | 38 |
| 90 | 36 |
| 80 | 33 |
| 70 | 31 |
| 60 | 28 |
| 50 | 26 |
| 40 | 22 |
| 30 | 20 |
| 20 | 18 |
| 10 | 15 |
| 1 | 12 |

Feed time estimate: `ceil((targetLimit − startLimit) × 8 / ptsPerFeed) × 6h`

### Parallel breeding (account tier)

| Account tier | Parallel breed slots |
|---|---|
| Normal | 3 |
| Hero+ | 4 |
| Champion | 5 |

Real elapsed time accounts for running multiple lines simultaneously. Total base horses needed = `2^(breeding generations)`.

---

## Stat → real-world formulas

### Speed → blocks per second

Empirically derived from player testing:
- Speed ~215 ≈ 18 blocks/sec
- Speed ~402 ≈ 19 blocks/sec

### Altitude → actual height (Wyvern, blocks above ground)

Logarithmic relationship derived from testing:

```
height = 20.2673 + 2.34895 × ln(altitudeStat)
```

Hard server-enforced cap: **~60 blocks** above ground regardless of stat value. The formula hits diminishing returns well before that cap at practical stat ranges.

### Energy battery → energy gained (based on Boost stat)

```
batteryEnergy = round(10 × (30 + 7.5 × log₁₀(max(1, boostStat)))) / 10
```

Base value is 30 energy. At Boost=10 you gain ~7.5 more. The stat can have hidden decimal values (e.g. 17.3) if Training materials were used, which slightly affects the formula result.

---

## Optimizer logic (current mod)

**Phase A:** greedy picks from materials at current tier (`highestCurrent()` across all 8 stats).

**Phase B:** for each higher tier threshold, checks if any stat's `limit` already meets it (free train). If so, runs greedy at that tier and keeps the cheaper total.

The optimizer always targets `limit == max` for all stats. It does not yet support partial targets (e.g. "only optimize Speed and Altitude to max, ignore the rest") which would be useful for intermediate breeding generations.
