package com.startechnology.start_core.api.capability;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.common.machine.owner.FTBOwner;
import com.gregtechceu.gtceu.common.machine.owner.IMachineOwner;
import com.gregtechceu.gtceu.common.machine.owner.PlayerOwner;

import dev.ftb.mods.ftbteams.FTBTeamsAPIImpl;
import dev.ftb.mods.ftbteams.api.Team;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class IStarTGetMachineUUIDSafe {
    
    /**
     * Safely get's the UUID from this machine, or returns the nearest player
     */
    public static final UUID getUUIDSafeMetaMachine(MetaMachine machine) {
        return getUUIDSafe(
            machine.getHolder().getOwner(),
            machine.getPos(),
            machine.getLevel(),
            owner -> machine.getHolder().setOwner(owner)
        );
    }

    public static final UUID getUUIDSafeMetaMachineBlockEntity(MetaMachineBlockEntity machine) {
        return getUUIDSafe(
            machine.getOwner(),
            machine.getBlockPos(),
            machine.getLevel(),
            owner -> machine.setOwner(owner)
        );
    }

    /**
     * Core logic for safely getting UUID from a machine
     */
    private static UUID getUUIDSafe(
            IMachineOwner currentOwner, 
            BlockPos machinePos, 
            Level level,
            OwnerSetter ownerSetter) {
        
        // Safe case: get from the machine's owner since it exists
        try {
            if (!Objects.isNull(currentOwner) && !Objects.isNull(currentOwner.getUUID())) {
                return currentOwner.getUUID();
            }
        } catch (NullPointerException e) {
            if (!e.getMessage().contains("dev.ftb.mods.ftbteams.api.Team.getId()")) {
                throw new RuntimeException("Blame stellaurora or GTM for this one: " + e.getMessage());
            } 
        }
        

        // OOoOOHOOHOHOH Spooky territory: Try to find nearest player

        // if this doesnt exist then what am i supposed to do ...
        if (Objects.isNull(level)) {
            System.out.println("Please replace this Dream-Link, no UUID");
            return UUID.randomUUID();
        }

        Player nearestPlayer = level.getNearestPlayer(
            machinePos.getX(), 
            machinePos.getY(), 
            machinePos.getZ(), 
            10, 
            (_player) -> true
        );
        
        if (Objects.isNull(nearestPlayer)) {
            if (!level.isClientSide()) {
                level.getServer().getPlayerList().broadcastSystemMessage(
                    Component.translatable("start_core.uuid_safe.fail_nearest_player", "x: " + machinePos.getX() + " y: " + machinePos.getY() + " z: "  + machinePos.getZ()), 
                    false
                );
            }
            return UUID.randomUUID();
        }

        // Set the owner based on FTB Teams availability
        IMachineOwner newOwner = createOwnerForPlayer(nearestPlayer);
        ownerSetter.setOwner(newOwner);

        return nearestPlayer.getUUID();
    }

    /**
     * Creates appropriate owner type based on FTB Teams availability
     */
    private static IMachineOwner createOwnerForPlayer(Player player) {
        if (IMachineOwner.MachineOwnerType.FTB.isAvailable()) {
            Optional<Team> team = FTBTeamsAPIImpl.INSTANCE.getManager().getTeamForPlayerID(player.getUUID());
            if (team.isPresent()) {
                return new FTBOwner(team.get(), player.getUUID());
            }
        }
        return new PlayerOwner(player.getUUID());
    }

    /**
     * Functional interface for setting owners on different machine types
     */
    @FunctionalInterface
    private interface OwnerSetter {
        void setOwner(IMachineOwner owner);
    }
}