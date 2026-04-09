package com.startechnology.start_core.mixin.create;

import com.simibubi.create.AllPackets;
import com.simibubi.create.Create;
import com.simibubi.create.content.schematics.client.ClientSchematicLoader;
import com.simibubi.create.content.schematics.client.SchematicAndQuillHandler;
import com.simibubi.create.content.schematics.packet.InstantSchematicPacket;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.CreatePaths;
import com.startechnology.start_core.integration.create.schematic.SchematicClientHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Files;

@Mixin(value = SchematicAndQuillHandler.class, remap = false)
public class SchematicAndQuillHandlerMixin {

    @Shadow
    public BlockPos firstPos;

    @Shadow
    public BlockPos secondPos;

    @Inject(method = "saveSchematic", at = @At("HEAD"), cancellable = true)
    public void saveSchematicFromServer(String string, boolean convertImmediately, CallbackInfo ci) {
        SchematicClientHandler.loadSchematicFromServer(firstPos, secondPos, (data) -> {
            var result = SchematicClientHandler.saveSchematicFile(CreatePaths.SCHEMATICS_DIR, string, false, firstPos, secondPos, data);
            var player = Minecraft.getInstance().player;
            if (result == null) {
                assert player != null;
                CreateLang.translate("schematicAndQuill.failed").style(ChatFormatting.RED).sendStatus(player);
                return;
            }
            var file = result.file();
            CreateLang.translate("schematicAndQuill.saved", file.getFileName()).sendStatus(player);

            firstPos = null;
            secondPos = null;

            if (!convertImmediately) {
                return;
            }
            try {
                if (!ClientSchematicLoader.validateSizeLimitation(Files.size(file)))
                    return;
                AllPackets.getChannel().sendToServer(new InstantSchematicPacket(result.fileName(), result.origin(), result.bounds()));
            } catch (IOException e) {
                Create.LOGGER.error("Error instantly uploading Schematic file: " + result.fileName(), e);
            }
        });

        ci.cancel();
    }
}
