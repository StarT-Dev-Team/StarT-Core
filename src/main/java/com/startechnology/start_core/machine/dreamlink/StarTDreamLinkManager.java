package com.startechnology.start_core.machine.dreamlink;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkRecieveEnergy;

import net.minecraft.core.BlockPos;
import rx.Observable;

public class StarTDreamLinkManager {
    private HashMap<UUID, RTree<IStarTDreamLinkNetworkRecieveEnergy, Geometry>> DREAM_LINK_TREE = new HashMap<>();
    private HashSet<IStarTDreamLinkNetworkRecieveEnergy> INSERTED_SET = new HashSet<>();

    // Singleton for management
    private static final StarTDreamLinkManager MANAGER = new StarTDreamLinkManager();
    
    private StarTDreamLinkManager() {}

    public static void addDevice(IStarTDreamLinkNetworkRecieveEnergy machine, UUID machineOwner) {
        /* Translate position to RTree position */
        BlockPos position = machine.devicePos();

        int x = position.getX();
        int z = position.getZ();

        // Dont insert if this machine is already in there
        if (MANAGER.INSERTED_SET.contains(machine))
            return;

        MANAGER.DREAM_LINK_TREE.putIfAbsent(machineOwner, RTree.create());

        MANAGER.INSERTED_SET.add(machine);
        MANAGER.DREAM_LINK_TREE.compute(machineOwner, (owner, tree) -> {
            return tree.add(machine, Geometries.point(x, z));
        });
    }

    public static void removeDevice(IStarTDreamLinkNetworkRecieveEnergy machine, UUID machineOwner) {
        /* Translate position to RTree position */
        BlockPos position = machine.devicePos();

        int x = position.getX();
        int z = position.getZ();

        MANAGER.DREAM_LINK_TREE.putIfAbsent(machineOwner, RTree.create());

        // Delete from the set and tree.
        MANAGER.INSERTED_SET.remove(machine);
        MANAGER.DREAM_LINK_TREE.compute(machineOwner, (owner, tree) -> {
            return tree.delete(machine, Geometries.point(x, z));
        });
    }

    public static Observable<Entry<IStarTDreamLinkNetworkRecieveEnergy, Geometry>> getDevices(int tx, int tz, int bx, int bz, UUID machineOwner) {
        MANAGER.DREAM_LINK_TREE.putIfAbsent(machineOwner, RTree.create());
        var tree = MANAGER.DREAM_LINK_TREE.get(machineOwner);
        return tree.search(Geometries.rectangle(bx, bz, tx, tz));
    }

    public static Observable<Entry<IStarTDreamLinkNetworkRecieveEnergy, Geometry>> getAllDevices(UUID machineOwner) {
        MANAGER.DREAM_LINK_TREE.putIfAbsent(machineOwner, RTree.create());
        var tree = MANAGER.DREAM_LINK_TREE.get(machineOwner);
        return tree.entries();
    }
}
