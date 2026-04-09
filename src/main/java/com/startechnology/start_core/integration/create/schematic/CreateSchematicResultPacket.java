package com.startechnology.start_core.integration.create.schematic;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;

public class CreateSchematicResultPacket extends SimplePacketBase {

    private final UUID uuid;
    private final CompoundTag structure;

    public CreateSchematicResultPacket(UUID uuid, CompoundTag structure) {
        this.uuid = uuid;
        this.structure = structure;
    }

    public CreateSchematicResultPacket(FriendlyByteBuf buffer) {
        this(buffer.readUUID(), buffer.readNbt());
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(uuid);
        buffer.writeNbt(structure);
    }

    @Override
    public boolean handle(NetworkEvent.Context context) {
        context.enqueueWork(() -> {
            SchematicClientHandler.handleResult(uuid, structure);
        });
        return true;
    }
}
