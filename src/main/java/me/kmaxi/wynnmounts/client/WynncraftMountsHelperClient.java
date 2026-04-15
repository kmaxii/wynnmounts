package me.kmaxi.wynnmounts.client;

import me.kmaxi.wynnmounts.data.MountStats;
import me.kmaxi.wynnmounts.ui.MountTooltipHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemLore;

public class WynncraftMountsHelperClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Append feed summary lines to mount item tooltips
        ItemTooltipCallback.EVENT.register((stack, context, tooltipType, lines) -> {
            ItemLore lore = stack.get(DataComponents.LORE);
            if (lore == null) return;
            if (lore.lines().stream().noneMatch(l -> l.getString().contains("Potential"))) return;

            MountStats stats = MountStats.parse(lore.lines());
            if (stats == null) return;

            lines.addAll(MountTooltipHelper.buildSummaryLines(stats));
        });
    }
}
