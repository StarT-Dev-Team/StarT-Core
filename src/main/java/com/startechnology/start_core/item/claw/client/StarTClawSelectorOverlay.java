package com.startechnology.start_core.item.claw.client;

import com.startechnology.start_core.item.claw.StarTClawMode;
import com.startechnology.start_core.item.claw.StarTClawOfLawItem;
import com.startechnology.start_core.item.claw.StarTClawToolRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class StarTClawSelectorOverlay {

    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
    private static final int VISIBLE = 9;
    private static final int SLOT = 20;

    public static void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int width, int height) {
        if (!StarTClawSelectorHandler.isShown()) return;

        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null) return;

        ItemStack claw = StarTClawOfLawItem.getHeldClaw(player);

        if (claw.isEmpty()) return;

        StarTClawMode[] modes = StarTClawMode.values();

        int selected = StarTClawSelectorHandler.getSelected();
        int centerSlot = VISIBLE / 2;
        int barLeft = width / 2 - VISIBLE * SLOT / 2 - 1;
        int barY = height - 88;

        graphics.blit(WIDGETS, barLeft, barY, 0, 0, 182, 22);
        graphics.blit(WIDGETS, barLeft - 1 + centerSlot * SLOT, barY - 1, 0, 22, 24, 24);

        for (int slot = 0; slot < VISIBLE; slot++) {
            StarTClawMode mode = modes[Math.floorMod(selected + slot - centerSlot, modes.length)];

            if (mode == StarTClawMode.NONE) continue;

            ItemStack icon = StarTClawToolRenderer.getTool(claw, mode);

            if (icon.isEmpty()) {
                icon = claw.copyWithCount(1);

                StarTClawOfLawItem.setMode(icon, mode);
            }

            graphics.renderItem(icon, barLeft + 3 + slot * SLOT, barY + 3);
        }
    }
}
