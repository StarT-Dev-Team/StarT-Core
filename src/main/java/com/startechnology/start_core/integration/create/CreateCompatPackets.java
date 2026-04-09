package com.startechnology.start_core.integration.create;

import com.simibubi.create.foundation.networking.SimplePacketBase;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.integration.create.schematic.CreateSchematicPacket;
import com.startechnology.start_core.integration.create.schematic.CreateSchematicResultPacket;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public enum CreateCompatPackets {

    CREATE_SCHEMATIC(CreateSchematicPacket.class, CreateSchematicPacket::new, NetworkDirection.PLAY_TO_SERVER),
    CREATE_SCHEMATIC_RESULT(CreateSchematicResultPacket.class, CreateSchematicResultPacket::new, NetworkDirection.PLAY_TO_CLIENT);

    @Getter
    private static SimpleChannel channel;

    @Getter
    private final PacketType<?> packetType;

    public static final ResourceLocation CHANNEL_NAME = StarTCore.resourceLocation("createcompat");
    public static final int NETWORK_VERSION = 1;
    public static final String NETWORK_VERSION_STR = String.valueOf(NETWORK_VERSION);

    <T extends SimplePacketBase> CreateCompatPackets(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
        packetType = new PacketType<>(type, factory, direction);
    }

    public static void registerPackets() {
        channel = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .serverAcceptedVersions(NETWORK_VERSION_STR::equals)
                .clientAcceptedVersions(NETWORK_VERSION_STR::equals)
                .networkProtocolVersion(() -> NETWORK_VERSION_STR)
                .simpleChannel();

        Arrays.stream(values()).map(CreateCompatPackets::getPacketType).forEach(PacketType::register);
    }


    private static class PacketType<T extends SimplePacketBase> {
        private static int index = 0;

        private final BiConsumer<T, FriendlyByteBuf> encoder;
        private final Function<FriendlyByteBuf, T> decoder;
        private final BiConsumer<T, Supplier<NetworkEvent.Context>> handler;
        private final Class<T> type;
        private final NetworkDirection direction;

        private PacketType(Class<T> type, Function<FriendlyByteBuf, T> factory, NetworkDirection direction) {
            encoder = T::write;
            decoder = factory;
            handler = (packet, contextSupplier) -> {
                var context = contextSupplier.get();
                if (packet.handle(context)) {
                    context.setPacketHandled(true);
                }
            };
            this.type = type;
            this.direction = direction;
        }

        private void register() {
            getChannel().messageBuilder(type, index++, direction)
                    .encoder(encoder)
                    .decoder(decoder)
                    .consumerNetworkThread(handler)
                    .add();
        }
    }
}
