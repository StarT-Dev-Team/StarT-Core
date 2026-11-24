package com.startechnology.start_core.machine.dimensional_destabiliser.client;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.startechnology.start_core.machine.dimensional_destabiliser.DestabiliserRift;
import com.startechnology.start_core.machine.dimensional_destabiliser.DimensionalDestabiliserMachine;

import org.joml.Matrix3f;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.WeakHashMap;

public final class RiftClientState {

    private static final Map<DimensionalDestabiliserMachine, RiftClientState> STATES = new WeakHashMap<>();
    private static final float RIFT_TIME_WRAP = 3600;

    public float work;
    public float progress;
    public float riftTime;
    public RiftAnimation.State state = RiftAnimation.State.NONE;
    public final RiftAnimation.Frame frame = new RiftAnimation.Frame();
    private final float seed;
    private long lastNanos;
    private boolean initialized;
    private Direction front;
    private Vec3 centre;
    private AABB bounds;
    private final Matrix3f basis = new Matrix3f();

    private RiftClientState(DimensionalDestabiliserMachine machine) {
        seed = (machine.getPos().asLong() * 0x9E3779B97F4A7C15L >>> 40) / (float) (1 << 24);
        riftTime = seed * RIFT_TIME_WRAP;
    }

    public static RiftClientState of(DimensionalDestabiliserMachine machine) {
        return STATES.computeIfAbsent(machine, RiftClientState::new);
    }

    public void update(DimensionalDestabiliserMachine machine, long gameTime, float partialTick) {
        place(machine);
        long now = System.nanoTime();
        boolean formed = machine.isFormed();
        boolean working = formed && machine.getRecipeLogic().getStatus() == RecipeLogic.Status.WORKING;
        float targetWork = working ? 1 : 0;
        float targetProgress = machine.getDisplayProgress() / (float) DestabiliserRift.PROGRESS_STEPS;
        state = RiftAnimation.state(formed, working,
                RiftAnimation.age(gameTime, partialTick, machine.getOpenGameTime()),
                RiftAnimation.age(gameTime, partialTick, machine.getCloseGameTime()));
        if (!initialized || state == RiftAnimation.State.NONE) {
            initialized = true;
            work = targetWork;
            progress = targetProgress;
        } else {
            float dt = Mth.clamp((now - lastNanos) / 1e9f, 0, 0.1f);
            work = RiftAnimation.approach(work, targetWork, dt, RiftAnimation.WORK_ATTACK, RiftAnimation.WORK_RELEASE);
            progress = RiftAnimation.approach(progress, targetProgress, dt, RiftAnimation.PROGRESS_TIME,
                    RiftAnimation.PROGRESS_TIME);
            riftTime = (riftTime + dt * RiftAnimation.timeSpeed(work)) % RIFT_TIME_WRAP;
        }
        lastNanos = now;
        RiftAnimation.compute(frame, work, progress,
                RiftAnimation.age(gameTime, partialTick, machine.getPulseGameTime()));
        if (state == RiftAnimation.State.OPENING) {
            RiftAnimation.opening(frame, RiftAnimation.age(gameTime, partialTick, machine.getOpenGameTime()));
        } else if (state == RiftAnimation.State.CLOSING) {
            RiftAnimation.closing(frame, RiftAnimation.age(gameTime, partialTick, machine.getCloseGameTime()));
        }
    }

    Matrix3f basis(Vec3 camera) {
        double dx = camera.x - centre.x, dz = camera.z - centre.z;
        double length = Math.sqrt(dx * dx + dz * dz);
        float zx = length < 1e-6 ? 0 : (float) (dx / length);
        float zz = length < 1e-6 ? 1 : (float) (dz / length);
        return basis.set(zz, 0, zx, 0, 1, 0, -zx, 0, zz);
    }

    AABB bounds() {
        return bounds;
    }

    public AABB bounds(DimensionalDestabiliserMachine machine) {
        place(machine);
        return bounds;
    }

    private void place(DimensionalDestabiliserMachine machine) {
        var front = machine.getFrontFacing();
        if (bounds != null && front == this.front) return;
        this.front = front;
        centre = machine.getRiftCentre();
        bounds = new AABB(centre, centre).inflate(DestabiliserRift.BOUND_RADIUS, DestabiliserRift.BOUND_HALF_HEIGHT,
                DestabiliserRift.BOUND_RADIUS);
    }

    Vec3 centre() {
        return centre;
    }

    float seed() {
        return seed;
    }
}
