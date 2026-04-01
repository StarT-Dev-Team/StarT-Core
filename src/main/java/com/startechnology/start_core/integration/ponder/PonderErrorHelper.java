package com.startechnology.start_core.integration.ponder;

import com.startechnology.start_core.StarTCore;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class PonderErrorHelper {

    public static void reportJsPonderError(Exception e) {
        ConsoleJS.CLIENT.error(e);
        StarTCore.LOGGER.error(e.getMessage(), e);
        var clientPlayer = KubeJS.PROXY.getClientPlayer();
        if (clientPlayer != null) {
            var first = Component.literal("[JS Ponder ERROR] ").withStyle(ChatFormatting.DARK_RED);
            var second = Component.literal(e.getMessage()).withStyle(ChatFormatting.RED);
            clientPlayer.sendSystemMessage(first.append(second));
        }
    }
}
