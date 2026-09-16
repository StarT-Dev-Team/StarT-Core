package com.startechnology.start_core.machine.dreamlink;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkReceiveEnergy;
import rx.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class StarTDreamLinkManager {

    private final Map<UUID, RTree<IStarTDreamLinkNetworkReceiveEnergy, Geometry>> dreamLinkTrees = new HashMap<>();
    private final Map<IStarTDreamLinkNetworkReceiveEnergy, Registration> registrations = new HashMap<>();

    // Singleton for management
    private static final StarTDreamLinkManager MANAGER = new StarTDreamLinkManager();

    private StarTDreamLinkManager() {}

    public static void addDevice(IStarTDreamLinkNetworkReceiveEnergy machine, UUID machineOwner) {
        MANAGER.registerDevice(machine, machineOwner);
    }

    public static void removeDevice(IStarTDreamLinkNetworkReceiveEnergy machine) {
        MANAGER.unregisterDevice(machine);
    }

    public static Observable<Entry<IStarTDreamLinkNetworkReceiveEnergy, Geometry>> getDevices(int tx, int tz, int bx,
                                                                                              int bz,
                                                                                              UUID machineOwner) {
        return MANAGER.getDevicesForOwner(tx, tz, bx, bz, machineOwner);
    }

    public static Observable<Entry<IStarTDreamLinkNetworkReceiveEnergy, Geometry>> getAllDevices(UUID machineOwner) {
        return MANAGER.getAllDevicesForOwner(machineOwner);
    }

    void registerDevice(IStarTDreamLinkNetworkReceiveEnergy machine, UUID machineOwner) {
        Objects.requireNonNull(machine, "machine");
        Objects.requireNonNull(machineOwner, "machineOwner");

        var position = machine.devicePos();
        var location = Geometries.point(position.getX(), position.getZ());
        var currentRegistration = registrations.get(machine);
        if (currentRegistration != null &&
                currentRegistration.owner().equals(machineOwner) &&
                currentRegistration.location().equals(location)) {
            return;
        }

        if (currentRegistration != null) {
            removeRegistration(machine, currentRegistration);
        }

        var tree = dreamLinkTrees.getOrDefault(machineOwner, RTree.create());
        dreamLinkTrees.put(machineOwner, tree.add(machine, location));
        registrations.put(machine, new Registration(machineOwner, location));
    }

    void unregisterDevice(IStarTDreamLinkNetworkReceiveEnergy machine) {
        var registration = registrations.remove(machine);
        if (registration != null) {
            removeFromTree(machine, registration);
        }
    }

    Observable<Entry<IStarTDreamLinkNetworkReceiveEnergy, Geometry>> getDevicesForOwner(int tx, int tz, int bx, int bz,
                                                                                        UUID machineOwner) {
        return treeFor(machineOwner).search(Geometries.rectangle(bx, bz, tx, tz));
    }

    Observable<Entry<IStarTDreamLinkNetworkReceiveEnergy, Geometry>> getAllDevicesForOwner(UUID machineOwner) {
        return treeFor(machineOwner).entries();
    }

    private void removeRegistration(IStarTDreamLinkNetworkReceiveEnergy machine, Registration registration) {
        registrations.remove(machine);
        removeFromTree(machine, registration);
    }

    private void removeFromTree(IStarTDreamLinkNetworkReceiveEnergy machine, Registration registration) {
        var tree = dreamLinkTrees.get(registration.owner());
        if (tree == null) {
            return;
        }

        var updatedTree = tree.delete(machine, registration.location());
        if (updatedTree.isEmpty()) {
            dreamLinkTrees.remove(registration.owner());
        } else {
            dreamLinkTrees.put(registration.owner(), updatedTree);
        }
    }

    private RTree<IStarTDreamLinkNetworkReceiveEnergy, Geometry> treeFor(UUID machineOwner) {
        return dreamLinkTrees.getOrDefault(machineOwner, RTree.create());
    }

    private record Registration(UUID owner, Geometry location) {}
}
