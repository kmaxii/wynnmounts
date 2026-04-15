package me.kmaxi.wynnmounts.data;

import java.util.Arrays;
import java.util.List;

/**
 * All 112 feeding materials across 14 tiers.
 * Bonus index order: Speed, Acceleration, Altitude, Energy, Handling, Toughness, Boost, Training
 */
public final class MaterialRegistry {

    public static final int[] TIER_THRESHOLDS = {1, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 105, 110, 115};

    private static Material m(int tier, String name, int spd, int acc, int alt, int ene, int han, int tou, int bst, int trn) {
        return new Material(tier, name, new int[]{spd, acc, alt, ene, han, tou, bst, trn});
    }

    public static final List<Material> ALL = List.of(
            // Tier 1
            m(1,   "Copper Ingot",      0, 0, 0, 4, 0, 8, 0, 0),
            m(1,   "Copper Gem",        4, 0, 0, 2, 0, 0, 0, 6),
            m(1,   "Oak Plank",         2, 6, 0, 0, 0, 4, 0, 0),
            m(1,   "Oak Paper",         0, 0, 8, 0, 0, 0, 4, 0),
            m(1,   "Wheat String",      0, 2, 0, 0, 4, 0, 6, 0),
            m(1,   "Wheat Grains",      8, 0, 4, 0, 0, 0, 0, 0),
            m(1,   "Gudgeon Oil",       0, 0, 2, 0, 6, 0, 0, 4),
            m(1,   "Gudgeon Meat",      0, 4, 0, 8, 0, 0, 0, 0),
            // Tier 10
            m(10,  "Granite Ingot",     0, 0, 0, 5, 0,10, 0, 0),
            m(10,  "Granite Gem",       5, 0, 0, 2, 0, 0, 0, 8),
            m(10,  "Birch Plank",       2, 8, 0, 0, 0, 5, 0, 0),
            m(10,  "Birch Paper",       0, 0,10, 0, 0, 0, 5, 0),
            m(10,  "Barley String",     0, 2, 0, 0, 5, 0, 8, 0),
            m(10,  "Barley Grains",    10, 0, 5, 0, 0, 0, 0, 0),
            m(10,  "Trout Oil",         0, 0, 2, 0, 8, 0, 0, 5),
            m(10,  "Trout Meat",        0, 5, 0,10, 0, 0, 0, 0),
            // Tier 20
            m(20,  "Gold Ingot",        0, 0, 0, 5, 0,12, 0, 0),
            m(20,  "Gold Gem",          6, 0, 0, 3, 0, 0, 0, 9),
            m(20,  "Willow Plank",      3, 9, 0, 0, 0, 6, 0, 0),
            m(20,  "Willow Paper",      0, 0,12, 0, 0, 0, 5, 0),
            m(20,  "Oat String",        0, 3, 0, 0, 6, 0, 9, 0),
            m(20,  "Oat Grains",       12, 0, 5, 0, 0, 0, 0, 0),
            m(20,  "Salmon Oil",        0, 0, 3, 0, 9, 0, 0, 6),
            m(20,  "Salmon Meat",       0, 5, 0,12, 0, 0, 0, 0),
            // Tier 30
            m(30,  "Sandstone Ingot",   0, 0, 0, 6, 0,14, 0, 0),
            m(30,  "Sandstone Gem",     6, 0, 0, 3, 0, 0, 0,11),
            m(30,  "Acacia Plank",      3,11, 0, 0, 0, 6, 0, 0),
            m(30,  "Acacia Paper",      0, 0,14, 0, 0, 0, 6, 0),
            m(30,  "Malt String",       0, 3, 0, 0, 6, 0,11, 0),
            m(30,  "Malt Grains",      14, 0, 6, 0, 0, 0, 0, 0),
            m(30,  "Carp Oil",          0, 0, 3, 0,11, 0, 0, 6),
            m(30,  "Carp Meat",         0, 6, 0,14, 0, 0, 0, 0),
            // Tier 40
            m(40,  "Iron Ingot",        0, 0, 0, 6, 0,16, 0, 0),
            m(40,  "Iron Gem",          7, 0, 0, 3, 0, 0, 0,12),
            m(40,  "Spruce Plank",      3,12, 0, 0, 0, 7, 0, 0),
            m(40,  "Spruce Paper",      0, 0,16, 0, 0, 0, 6, 0),
            m(40,  "Hops String",       0, 3, 0, 0, 7, 0,12, 0),
            m(40,  "Hops Grains",      16, 0, 6, 0, 0, 0, 0, 0),
            m(40,  "Icefish Oil",       0, 0, 3, 0,12, 0, 0, 7),
            m(40,  "Icefish Meat",      0, 6, 0,16, 0, 0, 0, 0),
            // Tier 50
            m(50,  "Silver Ingot",      0, 0, 0, 7, 0,18, 0, 0),
            m(50,  "Silver Gem",        8, 0, 0, 4, 0, 0, 0,14),
            m(50,  "Jungle Plank",      4,14, 0, 0, 0, 8, 0, 0),
            m(50,  "Jungle Paper",      0, 0,18, 0, 0, 0, 7, 0),
            m(50,  "Rye String",        0, 4, 0, 0, 8, 0,14, 0),
            m(50,  "Rye Grains",       18, 0, 7, 0, 0, 0, 0, 0),
            m(50,  "Piranha Oil",       0, 0, 4, 0,14, 0, 0, 8),
            m(50,  "Piranha Meat",      0, 7, 0,18, 0, 0, 0, 0),
            // Tier 60
            m(60,  "Cobalt Ingot",      0, 0, 0, 8, 0,20, 0, 0),
            m(60,  "Cobalt Gem",        9, 0, 0, 4, 0, 0, 0,15),
            m(60,  "Dark Plank",        4,15, 0, 0, 0, 9, 0, 0),
            m(60,  "Dark Paper",        0, 0,20, 0, 0, 0, 8, 0),
            m(60,  "Millet String",     0, 4, 0, 0, 9, 0,15, 0),
            m(60,  "Millet Grains",    20, 0, 8, 0, 0, 0, 0, 0),
            m(60,  "Koi Oil",           0, 0, 4, 0,15, 0, 0, 9),
            m(60,  "Koi Meat",          0, 8, 0,20, 0, 0, 0, 0),
            // Tier 70
            m(70,  "Kanderstone Ingot", 0, 0, 0, 8, 0,22, 0, 0),
            m(70,  "Kanderstone Gem",  10, 0, 0, 4, 0, 0, 0,17),
            m(70,  "Light Plank",       4,17, 0, 0, 0,10, 0, 0),
            m(70,  "Light Paper",       0, 0,22, 0, 0, 0, 8, 0),
            m(70,  "Decay String",      0, 4, 0, 0,10, 0,17, 0),
            m(70,  "Decay Grains",     22, 0, 8, 0, 0, 0, 0, 0),
            m(70,  "Gylia Oil",         0, 0, 4, 0,17, 0, 0,10),
            m(70,  "Gylia Meat",        0, 8, 0,22, 0, 0, 0, 0),
            // Tier 80
            m(80,  "Diamond Ingot",     0, 0, 0, 9, 0,24, 0, 0),
            m(80,  "Diamond Gem",      10, 0, 0, 4, 0, 0, 0,18),
            m(80,  "Pine Plank",        4,18, 0, 0, 0,10, 0, 0),
            m(80,  "Pine Paper",        0, 0,24, 0, 0, 0, 9, 0),
            m(80,  "Rice String",       0, 4, 0, 0,10, 0,18, 0),
            m(80,  "Rice Grains",      24, 0, 9, 0, 0, 0, 0, 0),
            m(80,  "Bass Oil",          0, 0, 4, 0,18, 0, 0,10),
            m(80,  "Bass Meat",         0, 9, 0,24, 0, 0, 0, 0),
            // Tier 90
            m(90,  "Molten Ingot",      0, 0, 0, 9, 0,26, 0, 0),
            m(90,  "Molten Gem",       11, 0, 0, 5, 0, 0, 0,20),
            m(90,  "Avo Plank",         5,20, 0, 0, 0,11, 0, 0),
            m(90,  "Avo Paper",         0, 0,26, 0, 0, 0, 9, 0),
            m(90,  "Sorghum String",    0, 5, 0, 0,11, 0,20, 0),
            m(90,  "Sorghum Grains",   26, 0, 9, 0, 0, 0, 0, 0),
            m(90,  "Molten Oil",        0, 0, 5, 0,20, 0, 0,11),
            m(90,  "Molten Meat",       0, 9, 0,26, 0, 0, 0, 0),
            // Tier 100
            m(100, "Voidstone Ingot",   0, 0, 0,10, 0,28, 0, 0),
            m(100, "Voidstone Gem",    12, 0, 0, 5, 0, 0, 0,21),
            m(100, "Sky Plank",         5,21, 0, 0, 0,12, 0, 0),
            m(100, "Sky Paper",         0, 0,28, 0, 0, 0,10, 0),
            m(100, "Hemp String",       0, 5, 0, 0,12, 0,21, 0),
            m(100, "Hemp Grains",      28, 0,10, 0, 0, 0, 0, 0),
            m(100, "Starfish Oil",      0, 0, 5, 0,21, 0, 0,12),
            m(100, "Starfish Meat",     0,10, 0,28, 0, 0, 0, 0),
            // Tier 105
            m(105, "Dernic Ingot",      0, 0, 0,10, 0,29, 0, 0),
            m(105, "Dernic Gem",       12, 0, 0, 5, 0, 0, 0,22),
            m(105, "Dernic Plank",      5,22, 0, 0, 0,12, 0, 0),
            m(105, "Dernic Paper",      0, 0,29, 0, 0, 0,10, 0),
            m(105, "Dernic String",     0, 5, 0, 0,12, 0,22, 0),
            m(105, "Dernic Grains",    29, 0,10, 0, 0, 0, 0, 0),
            m(105, "Dernic Oil",        0, 0, 5, 0,22, 0, 0,12),
            m(105, "Dernic Meat",       0,10, 0,29, 0, 0, 0, 0),
            // Tier 110
            m(110, "Titanium Ingot",    0, 0, 0,11, 0,30, 0, 0),
            m(110, "Titanium Gem",     13, 0, 0, 5, 0, 0, 0,23),
            m(110, "Maple Plank",       5,23, 0, 0, 0,13, 0, 0),
            m(110, "Maple Paper",       0, 0,30, 0, 0, 0,11, 0),
            m(110, "Jute String",       0, 5, 0, 0,13, 0,23, 0),
            m(110, "Jute Grains",      30, 0,11, 0, 0, 0, 0, 0),
            m(110, "Sturgeon Oil",      0, 0, 5, 0,23, 0, 0,13),
            m(110, "Sturgeon Meat",     0,11, 0,30, 0, 0, 0, 0),
            // Tier 115
            m(115, "Cinnabar Ingot",    0, 0, 0,11, 0,31, 0, 0),
            m(115, "Cinnabar Gem",     13, 0, 0, 5, 0, 0, 0,23),
            m(115, "Redwood Plank",     5,23, 0, 0, 0,13, 0, 0),
            m(115, "Redwood Paper",     0, 0,31, 0, 0, 0,11, 0),
            m(115, "Heather String",    0, 5, 0, 0,13, 0,23, 0),
            m(115, "Heather Grains",   31, 0,11, 0, 0, 0, 0, 0),
            m(115, "Mahseer Oil",       0, 0, 5, 0,23, 0, 0,13),
            m(115, "Mahseer Meat",      0,11, 0,31, 0, 0, 0, 0)
    );

    /** Returns all materials at exactly the given tier. */
    public static List<Material> materialsAtTier(int tier) {
        return ALL.stream().filter(mat -> mat.tier() == tier).toList();
    }

    /** Returns the highest usable tier threshold ≤ highestLevel. */
    public static int maxUsableTier(int highestLevel) {
        int best = TIER_THRESHOLDS[0];
        for (int t : TIER_THRESHOLDS) {
            if (t <= highestLevel) best = t;
            else break;
        }
        return best;
    }

    private MaterialRegistry() {}
}
