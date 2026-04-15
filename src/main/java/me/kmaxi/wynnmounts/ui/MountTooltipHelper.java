package me.kmaxi.wynnmounts.ui;

import me.kmaxi.wynnmounts.data.MountDerivedStats;
import me.kmaxi.wynnmounts.data.MountStats;
import me.kmaxi.wynnmounts.data.MountType;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public final class MountTooltipHelper {

    public static List<Component> buildSummaryLines(MountStats stats) {
        List<Component> lines = new ArrayList<>();

        // Derived real-world stat values
        double batNow = MountDerivedStats.batteryEnergy(stats.boost().current());
        double batMax = MountDerivedStats.batteryEnergy(stats.boost().max());

        lines.add(Component.empty());

        // Altitude: Wyvern-only (formula derived from Wyvern testing)
        if (stats.mountType() == MountType.WYVERN) {
            double altNow = MountDerivedStats.altitudeBlocks(stats.altitude().current());
            double altMax = MountDerivedStats.altitudeBlocks(stats.altitude().max());
            lines.add(Component.literal("  Alt: ")
                    .withStyle(ChatFormatting.GRAY)
                    .append(Component.literal(String.format("~%.0f blk", altNow))
                            .withStyle(ChatFormatting.WHITE))
                    .append(Component.literal(String.format("  (max ~%.0f blk)", altMax))
                            .withStyle(ChatFormatting.DARK_GRAY)));
        }

        lines.add(Component.literal("  Battery: ")
                .withStyle(ChatFormatting.GRAY)
                .append(Component.literal(String.format("~%.1f energy", batNow))
                        .withStyle(ChatFormatting.WHITE))
                .append(Component.literal(String.format("  (max ~%.1f)", batMax))
                        .withStyle(ChatFormatting.DARK_GRAY)));

        return lines;
    }

    /** Converts hours to a human-readable string like "3d 4h" or "18h". */
    public static String formatTime(int hours) {
        if (hours <= 0) return "0h";
        if (hours < 24) return hours + "h";
        int days = hours / 24;
        int rem = hours % 24;
        return rem == 0 ? days + "d" : days + "d " + rem + "h";
    }

    private MountTooltipHelper() {}
}
