package com.startechnology.start_core.item.claw.client;

import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.item.claw.StarTClawKeyMappings;
import com.startechnology.start_core.item.claw.StarTClawMode;
import com.startechnology.start_core.item.claw.StarTClawOfLawItem;
import com.startechnology.start_core.item.claw.StarTClawSelectorNetwork;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class StarTClawSelectorHandler {

    private static final int TAP_TICKS = 6;

    private static boolean armed;
    private static boolean scrolled;
    private static int ticksHeld;
    @Getter
    private static int selected;
    @Nullable
    private static StarTClawMode suggested;

    public static boolean isShown() {
        return armed && (scrolled || ticksHeld >= TAP_TICKS);
    }

    private static void press(Minecraft mc, LocalPlayer player) {
        armed = true;
        scrolled = false;
        ticksHeld = 0;
        selected = StarTClawOfLawItem.getMode(StarTClawOfLawItem.getHeldClaw(player)).ordinal();
        suggested = StarTClawSuggestions.suggest(mc.hitResult, mc.level);
    }

    private static void release(LocalPlayer player) {
        StarTClawMode current = StarTClawOfLawItem.getMode(StarTClawOfLawItem.getHeldClaw(player));
        StarTClawMode target;

        if (scrolled || ticksHeld >= TAP_TICKS) {
            target = StarTClawMode.values()[selected];
        } else if (Screen.hasShiftDown()) {
            target = current.prev();
        } else {
            target = suggested != null && suggested != current ? suggested : current.next();
        }

        if (target != current) {
            StarTClawSelectorNetwork.CHANNEL.sendToServer(new StarTClawSelectorNetwork.SelectMessage(target.ordinal()));
        }
    }

    private static void reset() {
        armed = false;
        scrolled = false;
        ticksHeld = 0;
        suggested = null;
    }

    @Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModEvents {

        @SubscribeEvent
        public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("claw_selector", StarTClawSelectorOverlay::render);
        }
    }

    @Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, value = Dist.CLIENT)
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase != TickEvent.Phase.END) return;

            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;

            boolean valid = player != null && mc.screen == null && !StarTClawOfLawItem.getHeldClaw(player).isEmpty();
            boolean down = StarTClawKeyMappings.CYCLE.isKeyDown();

            if (valid && down) {
                if (!armed) {
                    press(mc, player);
                } else {
                    ticksHeld++;
                    suggested = StarTClawSuggestions.suggest(mc.hitResult, mc.level);
                }
            } else if (armed) {
                if (valid) release(player);

                reset();
            }
        }

        @SubscribeEvent
        public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
            if (!armed || event.getScrollDelta() == 0) return;

            selected = Math.floorMod(selected + (event.getScrollDelta() > 0 ? -1 : 1), StarTClawMode.values().length);
            scrolled = true;

            event.setCanceled(true);
        }
    }
}
