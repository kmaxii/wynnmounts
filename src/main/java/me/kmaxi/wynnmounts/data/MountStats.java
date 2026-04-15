package me.kmaxi.wynnmounts.data;

import net.minecraft.network.chat.Component;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record MountStats(
        MountType mountType,
        StatEntry speed,
        StatEntry acceleration,
        StatEntry altitude,
        StatEntry energy,
        StatEntry handling,
        StatEntry toughness,
        StatEntry boost,
        StatEntry training
) {
    // Stat indices matching Material.bonuses[] order
    public static final int IDX_SPEED = 0;
    public static final int IDX_ACCELERATION = 1;
    public static final int IDX_ALTITUDE = 2;
    public static final int IDX_ENERGY = 3;
    public static final int IDX_HANDLING = 4;
    public static final int IDX_TOUGHNESS = 5;
    public static final int IDX_BOOST = 6;
    public static final int IDX_TRAINING = 7;

    public static final String[] STAT_NAMES = {
            "Speed", "Acceleration", "Altitude", "Energy",
            "Handling", "Toughness", "Boost", "Training"
    };

    private static final Pattern STAT_PATTERN =
            Pattern.compile("^(Speed|Acceleration|Altitude|Jump Height|Energy|Handling|Toughness|Boost|Training)\\s*(\\d+)/(\\d+)\\s*\\((\\d+)\\)");

    /** Parse mount stats from raw lore components. Returns null if any stat is missing. */
    public static MountStats parse(List<Component> lore) {
        Map<String, StatEntry> found = new HashMap<>();
        MountType mountType = MountType.UNKNOWN;
        for (Component line : lore) {
            String text = stripNonAscii(line.getString());
            if (mountType == MountType.UNKNOWN) {
                MountType detected = MountType.fromLore(text);
                if (detected != MountType.UNKNOWN) mountType = detected;
            }
            Matcher m = STAT_PATTERN.matcher(text);
            if (m.find()) {
                LoggerFactory.getLogger("wynnmounts").info("Matched stat line: '{}'", text);
                String name = m.group(1).equals("Jump Height") ? "Altitude" : m.group(1);
                int current = Integer.parseInt(m.group(2));
                int limit   = Integer.parseInt(m.group(3));
                int max     = Integer.parseInt(m.group(4));
                found.put(name, new StatEntry(current, limit, max));
            }
        }
        if (found.size() < 8) return null;
        return new MountStats(
                mountType,
                found.get("Speed"),
                found.get("Acceleration"),
                found.get("Altitude"),
                found.get("Energy"),
                found.get("Handling"),
                found.get("Toughness"),
                found.get("Boost"),
                found.get("Training")
        );
    }

    /** Returns the 8 StatEntry values in canonical index order. */
    public StatEntry[] toArray() {
        return new StatEntry[]{speed, acceleration, altitude, energy, handling, toughness, boost, training};
    }

    /** Highest current level across all stats — determines usable tier. */
    public int highestCurrent() {
        int h = 0;
        for (StatEntry s : toArray()) h = Math.max(h, s.current());
        return h;
    }

    /** Keep only printable ASCII (32–126) — removes all Wynncraft custom font chars. */
    private static String stripNonAscii(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 32 && c <= 126) sb.append(c);
        }
        return sb.toString().trim();
    }
}
