package com.startechnology.start_core.item.claw;

import com.startechnology.start_core.StarTCore;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class StarTClawSelectorNetwork {

    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            StarTCore.resourceLocation("claw_selector"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void init() {
        CHANNEL.registerMessage(0, SelectMessage.class, SelectMessage::encode, SelectMessage::decode,
                SelectMessage::handle);
    }

    public record SelectMessage(int mode) {

        public static void encode(SelectMessage message, FriendlyByteBuf buf) {
            buf.writeVarInt(message.mode);
        }

        public static SelectMessage decode(FriendlyByteBuf buf) {
            return new SelectMessage(buf.readVarInt());
        }

        public static void handle(SelectMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context context = contextSupplier.get();

            context.enqueueWork(() -> {
                ServerPlayer player = context.getSender();
                StarTClawMode[] modes = StarTClawMode.values();

                if (player == null || message.mode < 0 || message.mode >= modes.length) return;

                ItemStack stack = StarTClawOfLawItem.getHeldClaw(player);

                if (stack.isEmpty()) return;

                StarTClawMode current = StarTClawOfLawItem.getMode(stack);

                if (current == modes[message.mode]) return;

                StarTClawOfLawItem.setMode(stack, modes[message.mode]);
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ARMOR_EQUIP_CHAIN, SoundSource.PLAYERS, 1.0F, 1.0F);
            });

            context.setPacketHandled(true);
        }
    }
}
