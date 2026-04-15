package me.kmaxi.wynnmounts.data;

public enum MountType {
    WYVERN,
    HORSE,
    ADASAUR,
    UNKNOWN;

    /** Detect mount type from stripped lore text. Returns UNKNOWN if not recognised. */
    public static MountType fromLore(String loreText) {
        String lower = loreText.toLowerCase();
        if (lower.contains("wyvern"))  return WYVERN;
        if (lower.contains("horse"))   return HORSE;
        if (lower.contains("adasaur")) return ADASAUR;
        return UNKNOWN;
    }
}
