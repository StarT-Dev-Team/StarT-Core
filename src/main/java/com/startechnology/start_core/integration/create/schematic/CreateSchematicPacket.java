package com.startechnology.start_core.integration.create.schematic;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.startechnology.start_core.integration.create.CreateCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class CreateSchematicPacket extends SimplePacketBase {

    private final UUID uuid;
    private final BlockPos first;
    private final BlockPos second;

    public CreateSchematicPacket(UUID uuid, BlockPos first, BlockPos second) {
        this.uuid = uuid;
        this.first = first;
        this.second = second;
    }

    public CreateSchematicPacket(FriendlyByteBuf buffer) {
        this(buffer.readUUID(), buffer.readBlockPos(), buffer.readBlockPos());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeBlockPos(first);
        buffer.writeBlockPos(second);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            var player = context.getSender();
            if (player == null) return;

            CreateCompat.saveSchematicServerside(player, uuid, first, second);
        });
        return true;
    }
}
