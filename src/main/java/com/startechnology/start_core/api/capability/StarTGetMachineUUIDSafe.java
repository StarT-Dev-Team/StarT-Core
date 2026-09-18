package com.startechnology.start_core.api.capability;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.common.machine.owner.MachineOwner;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class StarTGetMachineUUIDSafe {

    /**
     * Safely get's the UUID from this machine, or returns the nearest player
     */
    public static UUID getUUIDSafeMetaMachine(MetaMachine machine) {
        return getUUIDSafe(
                machine.getOwnerUUID(),
                machine.getPos(),
                machine.getLevel(),
                machine::setOwnerUUID);
    }

    public static UUID getUUIDSafeMetaMachineBlockEntity(MetaMachineBlockEntity machine) {
        var metaMachine = machine.getMetaMachine();
        return getUUIDSafe(
                metaMachine.getOwnerUUID(),
                machine.getBlockPos(),
                machine.getLevel(),
                metaMachine::setOwnerUUID);
    }

    public static UUID resolveDreamLinkOwner(UUID playerUUID) {
        if (!GTCEu.Mods.isFTBTeamsLoaded()) {
            return playerUUID;
        }

        var api = FTBTeamsAPI.api();
        if (!api.isManagerLoaded()) {
            return playerUUID;
        }

        return resolveDreamLinkOwner(playerUUID,
                uuid -> api.getManager().getTeamForPlayerID(uuid).map(Team::getId));
    }

    static UUID resolveDreamLinkOwner(UUID playerUUID,
                                      Function<UUID, Optional<UUID>> effectiveTeamLookup) {
        return effectiveTeamLookup.apply(playerUUID).orElse(playerUUID);
    }

    private static UUID getUUIDSafe(UUID currentOwnerUUID, BlockPos machinePos, Level level,
                                    Consumer<UUID> ownerUUIDSetter) {
        if (currentOwnerUUID != null && !MachineOwner.EMPTY.equals(currentOwnerUUID)) {
            return resolveDreamLinkOwner(currentOwnerUUID);
        }

        if (level == null) {
            System.out.println("Please replace this Dream-Link, no UUID");
            return UUID.randomUUID();
        }

        Player nearestPlayer = level.getNearestPlayer(
                machinePos.getX(),
                machinePos.getY(),
                machinePos.getZ(),
                10,
                (_player) -> true);

        if (nearestPlayer == null) {
            if (!level.isClientSide()) {
                level.getServer().getPlayerList().broadcastSystemMessage(
                        Component.translatable("start_core.uuid_safe.fail_nearest_player",
                                "x: " + machinePos.getX() + " y: " + machinePos.getY() + " z: " + machinePos.getZ()),
                        false);
            }
            return UUID.randomUUID();
        }

        var playerUUID = nearestPlayer.getUUID();
        ownerUUIDSetter.accept(playerUUID);
        return resolveDreamLinkOwner(playerUUID);
    }
}
