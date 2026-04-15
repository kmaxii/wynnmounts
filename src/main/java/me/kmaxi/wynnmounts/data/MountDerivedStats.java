package me.kmaxi.wynnmounts.data;

/**
 * Translates raw mount stat values into real-world units.
 * All methods are pure functions with no Minecraft dependencies.
 *
 * Formulas sourced from community testing (Sakurasou/Geo, Wynncraft Discord).
 */
public final class MountDerivedStats {

    /**
     * Estimated maximum altitude in blocks above ground for a given altitude stat.
     *
     * Formula: y = 20.2673 + 2.34895 × ln(altitudeStat)
     * Derived from Wyvern testing. Server hard cap: ~60 blocks.
     */
    public static double altitudeBlocks(int altitudeStat) {
        if (altitudeStat <= 0) return 0.0;
        return Math.min(60.0, 20.2673 + 2.34895 * Math.log(altitudeStat));
    }

    /**
     * Energy gained from a battery item for a given boost stat value.
     *
     * Formula: round(10 × (30 + 7.5 × log₁₀(max(1, boostStat)))) / 10
     * Boost stat may have hidden decimals if Training materials were used.
     */
    public static double batteryEnergy(double boostStat) {
        return Math.round(10.0 * (30.0 + 7.5 * Math.log10(Math.max(1.0, boostStat)))) / 10.0;
    }

    private MountDerivedStats() {}
}
