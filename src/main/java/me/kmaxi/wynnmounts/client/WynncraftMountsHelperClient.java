package me.kmaxi.wynnmounts.client;

import me.kmaxi.wynnmounts.data.Material;
import me.kmaxi.wynnmounts.data.MaterialRegistry;
import me.kmaxi.wynnmounts.data.MountStats;
import me.kmaxi.wynnmounts.ui.MountTooltipHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.component.ItemLore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class WynncraftMountsHelperClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("wynnmounts");
    private static final Set<String> loggedItems = new HashSet<>();

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, tooltipType, lines) -> {
            ItemLore lore = stack.get(DataComponents.LORE);

            boolean isMountItem = lore != null && lore.lines().stream().anyMatch(l -> l.getString().contains("Potential"));
            if (isMountItem) {
                MountStats stats = MountStats.parse(lore.lines());
                if (stats == null) return;
                lines.addAll(MountTooltipHelper.buildSummaryLines(stats));
                return;
            }

            boolean isMaterial = lore != null && lore.lines().stream()
                    .anyMatch(l -> l.getString().contains("Feed to your Mount"));
            LOGGER.info("[wynnmounts] isMaterial={} lore={}", isMaterial, lore != null);
            if (isMaterial) {
                String rawName = stack.getDisplayName().getString();
                LOGGER.info("[wynnmounts] material displayName raw='{}'", rawName);
                Optional<Material> match = MaterialRegistry.ALL.stream()
                        .filter(mat -> rawName.contains(mat.name()))
                        .findFirst();
                LOGGER.info("[wynnmounts] match={}", match.map(Material::name).orElse("NONE"));
                if (match.isPresent()) {
                    Material mat = match.get();
                    for (int i = 0; i < MountStats.STAT_NAMES.length; i++) {
                        if (mat.bonuses()[i] > 0) {
                            lines.add(Component.literal(MountStats.STAT_NAMES[i] + ": +" + mat.bonuses()[i])
                                    .withStyle(ChatFormatting.GREEN));
                        }
                    }
                }
                return;
            }

            // Log non-mount/non-material items for data collection (deduplicated by display name)
            String displayName = stripPrivateUse(stack.getDisplayName().getString());
            if (displayName.isEmpty() || loggedItems.contains(displayName)) return;
            loggedItems.add(displayName);

            StringBuilder sb = new StringBuilder();
            sb.append("## ").append(displayName).append(" [item=").append(stack.getItem()).append("]\n");
            if (lore != null) {
                for (Component line : lore.lines()) {
                    String text = stripPrivateUse(line.getString());
                    if (!text.isEmpty()) sb.append("- ").append(text).append("\n");
                }
            } else {
                sb.append("- (no lore)\n");
            }
            sb.append("\n");

            try {
                Path path = Paths.get("wynnmounts_data.md");
                Files.writeString(path, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                LOGGER.warn("wynnmounts: failed to write item log", e);
            }
        });
    }

    private static String stripPrivateUse(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ) {
            int cp = s.codePointAt(i);
            if (Character.getType(cp) != Character.PRIVATE_USE) sb.appendCodePoint(cp);
            i += Character.charCount(cp);
        }
        return sb.toString().trim();
    }
}
