package me.kmaxi.wynnmounts.mixin;

import me.kmaxi.wynnmounts.data.FeedResult;
import me.kmaxi.wynnmounts.data.MountStats;
import me.kmaxi.wynnmounts.optimizer.FeedOptimizer;
import me.kmaxi.wynnmounts.ui.FeedOverlayRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Mixin(AbstractContainerScreen.class)
public class HandledScreenMixin {

    @Shadow
    protected Slot hoveredSlot;

    private int wynnmounts$lastLoggedSlot = -1;
    private String wynnmounts$lastScreenTitle = null;

    // Cached optimizer results — recomputed only when hovered slot changes
    private MountStats wynnmounts$cachedStats = null;
    private FeedResult wynnmounts$cachedResult = null;

    @Inject(method = "renderTooltip", at = @At("HEAD"))
    private void wynnmounts$detectMountItem(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        if (hoveredSlot == null || !hoveredSlot.hasItem()) {
            wynnmounts$lastLoggedSlot = -1;
            wynnmounts$cachedStats = null;
            wynnmounts$cachedResult = null;
            return;
        }

        int slotId = hoveredSlot.index;
        if (slotId == wynnmounts$lastLoggedSlot) return; // already cached

        wynnmounts$lastLoggedSlot = slotId;
        wynnmounts$cachedStats = null;
        wynnmounts$cachedResult = null;

        AbstractContainerScreen<?> self = (AbstractContainerScreen<?>) (Object) this;
        String screenTitle = self.getTitle().getString();

        ItemStack stack = hoveredSlot.getItem();
        ItemLore lore = stack.get(DataComponents.LORE);

        // Only process mount items
        if (lore == null) return;
        boolean isMountItem = lore.lines().stream().anyMatch(l -> l.getString().contains("Potential"));
        if (!isMountItem) return;

        // Parse stats and compute feed plans
        wynnmounts$cachedStats = MountStats.parse(lore.lines());
        if (wynnmounts$cachedStats != null) {
            wynnmounts$cachedResult = FeedOptimizer.solve(wynnmounts$cachedStats);
        }

        // Save to markdown (existing behaviour)
        String name = stripPrivateUse(stack.getDisplayName().getString());
        StringBuilder sb = new StringBuilder();

        if (!screenTitle.equals(wynnmounts$lastScreenTitle)) {
            wynnmounts$lastScreenTitle = screenTitle;
            String titleDisplay = stripPrivateUse(screenTitle);
            if (titleDisplay.isEmpty()) titleDisplay = "(Wynncraft custom font title)";
            sb.append("# Container: ").append(titleDisplay).append("\n\n");
        }

        sb.append("## ").append(name).append("\n");
        for (Component line : lore.lines()) {
            String text = stripPrivateUse(line.getString());
            if (!text.isEmpty()) sb.append("- ").append(text).append("\n");
        }
        sb.append("\n");

        try {
            Path path = Paths.get("wynnmounts_data.md");
            Files.writeString(path, sb.toString(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // ignore write failures silently
        }
    }

    /** Runs AFTER the vanilla tooltip is drawn so our panels appear on top. */
    @Inject(method = "renderTooltip", at = @At("TAIL"))
    private void wynnmounts$renderFeedOverlay(GuiGraphics guiGraphics, int x, int y, CallbackInfo ci) {
        if (wynnmounts$cachedResult == null) return;
        AbstractContainerScreen<?> self = (AbstractContainerScreen<?>) (Object) this;
        FeedOverlayRenderer.render(guiGraphics, (Screen) self, wynnmounts$cachedResult);
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
