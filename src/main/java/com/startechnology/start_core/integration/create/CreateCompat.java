package com.startechnology.start_core.integration.create;

import com.simibubi.create.content.schematics.SchematicAndQuillItem;
import com.startechnology.start_core.integration.create.schematic.SerializeAEHandler;
import com.startechnology.start_core.integration.create.schematic.SerializeCBMHandler;
import com.startechnology.start_core.integration.create.schematic.CreateSchematicResultPacket;
import com.startechnology.start_core.integration.create.schematic.SerializeGTMHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

public class CreateCompat {

    public static void init() {
        SerializeAEHandler.init();
        SerializeCBMHandler.init();
        SerializeGTMHandler.init();

        CreateCompatPackets.registerPackets();
    }

    public static void saveSchematicServerside(ServerPlayer player, UUID uuid, BlockPos first, BlockPos second) {
        var level = player.level();

        var bb = BoundingBox.fromCorners(first, second);
        var origin = new BlockPos(bb.minX(), bb.minY(), bb.minZ());
        var bounds = new BlockPos(bb.getXSpan(), bb.getYSpan(), bb.getZSpan());

        var structure = new StructureTemplate();
        structure.fillFromWorld(level, origin, bounds, true, Blocks.AIR);
        var data = structure.save(new CompoundTag());
        SchematicAndQuillItem.replaceStructureVoidWithAir(data);
        SchematicAndQuillItem.clampGlueBoxes(level, new AABB(origin, origin.offset(bounds)), data);

        CreateCompatPackets.getChannel().send(PacketDistributor.PLAYER.with(() -> player), new CreateSchematicResultPacket(uuid, data));
    }
}
