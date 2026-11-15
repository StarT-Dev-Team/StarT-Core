package com.startechnology.start_core.mixin;

import appeng.server.AECommand;
import appeng.server.Commands;
import com.startechnology.start_core.StarTConfig;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AECommand.class, remap = false)
public class AECommandMixin {

    @Redirect(method = "lambda$add$0",
              at = @At(value = "FIELD", target = "Lappeng/server/Commands;level:I", opcode = Opcodes.GETFIELD))
    private static int start$getCommandPermissionLevel(Commands subCommand) {
        if (StarTConfig.INSTANCE == null) {
            StarTConfig.init();
        }
        if (subCommand == Commands.CHANNEL_MODE && StarTConfig.INSTANCE.ae2.reduceChannelModePermissionLevel) {
            return 2;
        }
        return subCommand.level;
    }
}
