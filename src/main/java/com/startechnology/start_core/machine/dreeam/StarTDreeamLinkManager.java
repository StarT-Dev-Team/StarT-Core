package com.startechnology.start_core.machine.dreeam;

import com.startechnology.start_core.StarTCore;

import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = StarTCore.MOD_ID)
public class StarTDreeamLinkManager {

    public enum LinkState {
        UNLINKED,
        WAITING,
        LINKED,
        SAME_ROLE,
        TOO_MANY
    }

    private static final Map<Long, Set<StarTDreeamMachine>> machinesByKey = new HashMap<>();

    private StarTDreeamLinkManager() {}

    public static void register(long key, StarTDreeamMachine machine) {
        machinesByKey.computeIfAbsent(key, k -> new LinkedHashSet<>()).add(machine);
    }

    public static void unregister(long key, StarTDreeamMachine machine) {
        var machines = machinesByKey.get(key);
        if (machines == null) return;

        machines.remove(machine);
        if (machines.isEmpty()) machinesByKey.remove(key);
    }

    public static LinkState stateOf(long key, StarTDreeamMachine machine) {
        var machines = machinesByKey.get(key);
        if (machines == null || !machines.contains(machine)) return LinkState.UNLINKED;
        if (machines.size() == 1) return LinkState.WAITING;
        if (machines.size() > 2) return LinkState.TOO_MANY;

        return otherMachine(machines, machine).getRole() == machine.getRole() ? LinkState.SAME_ROLE :
                LinkState.LINKED;
    }

    public static StarTDreeamMachine partnerOf(long key, StarTDreeamMachine machine) {
        if (stateOf(key, machine) != LinkState.LINKED) return null;

        return otherMachine(machinesByKey.get(key), machine);
    }

    private static StarTDreeamMachine otherMachine(Set<StarTDreeamMachine> machines, StarTDreeamMachine machine) {
        for (var other : machines) {
            if (other != machine) return other;
        }
        return machine;
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        machinesByKey.clear();
    }
}
