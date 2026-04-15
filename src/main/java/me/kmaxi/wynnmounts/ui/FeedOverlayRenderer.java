package me.kmaxi.wynnmounts.ui;

import me.kmaxi.wynnmounts.data.FeedPlan;
import me.kmaxi.wynnmounts.data.FeedResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;

public final class FeedOverlayRenderer {

    private static final int BG_COLOR      = 0xD0101018;
    private static final int BORDER_COLOR  = 0xFF555577;
    private static final int HEADER_COLOR  = 0xFFFFAA00; // gold
    private static final int LABEL_COLOR   = 0xFFAAAAAA; // gray
    private static final int VALUE_COLOR   = 0xFFFFFFFF; // white
    private static final int TRAIN_COLOR   = 0xFFFFFF55; // yellow
    private static final int GOOD_COLOR    = 0xFF55FF55; // green
    private static final int DIVIDER_COLOR = 0xFF444455;
    private static final int STAT_COLOR    = 0xFF888899; // muted blue-gray for stat suffixes

    private static final int PANEL_PAD   = 8;
    private static final int LINE_H      = 10;
    private static final int GAP         = 6; // gap between stacked panels
    private static final int SUFFIX_GAP  = 6; // pixels between material name and stat suffix

    // 3-char abbreviations matching MountStats.STAT_NAMES index order
    private static final String[] STAT_ABBR = {"Spd", "Acc", "Alt", "Ene", "Hnd", "Tgh", "Bst", "Trn"};

    public static void render(GuiGraphics gg, Screen screen, FeedResult result) {
        Font font = Minecraft.getInstance().font;

        int screenH = screen.height;
        int startX = 5; // anchored to left edge — Wynncraft tooltip always opens right-center

        if (!result.hasImprovement()) {
            List<String[]> lines = buildPlanLines(result.currentTierPlan(), "Optimal Path");
            int panelW = panelWidth(font, lines);
            int panelH = panelHeight(lines);
            int startY = (screenH - panelH) / 2;
            drawPanel(gg, font, startX, startY, panelW, panelH, lines);
        } else {
            List<String[]> topLines    = buildPlanLines(result.currentTierPlan(), "Current Tier", true);
            List<String[]> bottomLines = buildPlanLines(result.optimalPlan(), "Training Path");
            int topW    = panelWidth(font, topLines);
            int bottomW = panelWidth(font, bottomLines);
            int topH    = panelHeight(topLines);
            int bottomH = panelHeight(bottomLines);
            int totalH  = topH + GAP + bottomH;
            int startY  = (screenH - totalH) / 2;
            drawPanel(gg, font, startX, startY, topW, topH, topLines);
            drawPanel(gg, font, startX, startY + topH + GAP, bottomW, bottomH, bottomLines);
        }
    }

    // Line format: null = divider, String[2] = {tag, text}, String[3] = {tag, text, stat_suffix}
    private static void drawPanel(GuiGraphics gg, Font font, int x, int y, int w, int h, List<String[]> lines) {
        gg.fill(x, y, x + w, y + h, BG_COLOR);
        gg.fill(x,         y,         x + w,     y + 1,     BORDER_COLOR);
        gg.fill(x,         y + h - 1, x + w,     y + h,     BORDER_COLOR);
        gg.fill(x,         y,         x + 1,     y + h,     BORDER_COLOR);
        gg.fill(x + w - 1, y,         x + w,     y + h,     BORDER_COLOR);

        int cx = x + PANEL_PAD;
        int cy = y + PANEL_PAD;

        for (String[] line : lines) {
            if (line == null) {
                gg.fill(x + PANEL_PAD, cy + 4, x + w - PANEL_PAD, cy + 5, DIVIDER_COLOR);
                cy += LINE_H;
                continue;
            }
            String tag  = line[0];
            String text = line[1];
            int color = switch (tag) {
                case "header"   -> HEADER_COLOR;
                case "train"    -> TRAIN_COLOR;
                case "good"     -> GOOD_COLOR;
                case "label"    -> LABEL_COLOR;
                default         -> VALUE_COLOR;
            };
            gg.drawString(font, text, cx, cy, color, false);
            if (line.length > 2 && line[2] != null) {
                int suffixX = cx + font.width(text) + SUFFIX_GAP;
                gg.drawString(font, line[2], suffixX, cy, STAT_COLOR, false);
            }
            cy += LINE_H;
        }
    }

    private static List<String[]> buildPlanLines(FeedPlan plan, String label) {
        return buildPlanLines(plan, label, false);
    }

    private static List<String[]> buildPlanLines(FeedPlan plan, String label, boolean suppressNoTraining) {
        List<String[]> lines = new ArrayList<>();

        lines.add(new String[]{"header", label + "  (Tier " + plan.tier() + ")"});
        lines.add(null);

        String trainingNote = plan.trainingNote();
        int preFeedCount = plan.preFeedCount();

        if (trainingNote == null && !suppressNoTraining) {
            // Phase A: no training needed — only shown when this is the sole panel
            lines.add(new String[]{"good", "  No training required"});
            lines.add(null);
        }

        if (plan.materials().isEmpty()) {
            lines.add(new String[]{"good", "  Already at max!"});
        } else if (trainingNote != null && preFeedCount > 0) {
            // Phase C: cascading unlock — interleave two train steps with materials
            // trainingNote format: "Train X to N, feed K×Mat, train X to M"
            String[] parts = trainingNote.split(", feed [^,]+, train ", 2);
            String preTrain  = "  " + parts[0];                               // "  Train X to N"
            String postTrain = parts.length > 1 ? "  Train " + parts[1] : ""; // "  Train X to M"

            lines.add(new String[]{"train", preTrain});
            List<FeedPlan.MaterialCount> mats = plan.materials();
            for (int i = 0; i < preFeedCount && i < mats.size(); i++) {
                FeedPlan.MaterialCount mc = mats.get(i);
                String matLine = String.format("  %2dx  %s", mc.count(), mc.material().name());
                lines.add(new String[]{"material", matLine, buildStatSuffix(mc.material().bonuses(), mc.count())});
            }
            if (!postTrain.isEmpty()) {
                lines.add(new String[]{"train", postTrain});
            }
            for (int i = preFeedCount; i < mats.size(); i++) {
                FeedPlan.MaterialCount mc = mats.get(i);
                String matLine = String.format("  %2dx  %s", mc.count(), mc.material().name());
                lines.add(new String[]{"material", matLine, buildStatSuffix(mc.material().bonuses(), mc.count())});
            }
        } else if (trainingNote != null) {
            // Phase B: single training step before all materials
            // trainingNote format: "Train X to N (free in-game training)"
            String trainLine = "  " + trainingNote.replaceFirst("\\s*\\(free in-game training\\)", "");
            lines.add(new String[]{"train", trainLine});
            for (FeedPlan.MaterialCount mc : plan.materials()) {
                String matLine = String.format("  %2dx  %s", mc.count(), mc.material().name());
                lines.add(new String[]{"material", matLine, buildStatSuffix(mc.material().bonuses(), mc.count())});
            }
        } else {
            for (FeedPlan.MaterialCount mc : plan.materials()) {
                String matLine = String.format("  %2dx  %s", mc.count(), mc.material().name());
                String suffix  = buildStatSuffix(mc.material().bonuses(), mc.count());
                lines.add(new String[]{"material", matLine, suffix});
            }
        }

        lines.add(null);
        lines.add(new String[]{"label",
                "Total:  " + plan.totalFeeds() + " feeds"
                + "   (~" + MountTooltipHelper.formatTime(plan.estimatedHours()) + ")"});

        return lines;
    }

    /** Returns e.g. "Spd+8 Acc+4" for the total stat contribution of count feeds of this material. */
    private static String buildStatSuffix(int[] bonuses, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bonuses.length; i++) {
            if (bonuses[i] > 0) {
                if (!sb.isEmpty()) sb.append(' ');
                sb.append(STAT_ABBR[i]).append('+').append(bonuses[i] * count);
            }
        }
        return sb.isEmpty() ? null : sb.toString();
    }

    private static int panelHeight(List<String[]> lines) {
        return PANEL_PAD * 2 + lines.size() * LINE_H;
    }

    private static int panelWidth(Font font, List<String[]> lines) {
        int maxW = 0;
        for (String[] line : lines) {
            if (line == null) continue;
            int w = font.width(line[1]);
            if (line.length > 2 && line[2] != null) w += SUFFIX_GAP + font.width(line[2]);
            maxW = Math.max(maxW, w);
        }
        return maxW + PANEL_PAD * 2;
    }

    private FeedOverlayRenderer() {}
}
