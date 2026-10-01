package com.startechnology.start_core.machine.hellforge.client;

import com.startechnology.start_core.machine.hellforge.HellFlameProfile;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import org.joml.Matrix3f;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.WeakHashMap;

public final class HellFlameClientState {

    private static final Map<StarTHellForgeMachine, HellFlameClientState> STATES = new WeakHashMap<>();
    private static final float HEAT_TIME = 2;
    private static final float ROAR_ATTACK = 0.4f;
    private static final float ROAR_RELEASE = 1.5f;
    private static final float FLAME_TIME_WRAP = 3600;

    public float heat;
    public float roar;
    public float flameTime;
    public HellFlameAnimation.State state = HellFlameAnimation.State.NONE;
    public final HellFlameAnimation.Frame frame = new HellFlameAnimation.Frame();
    final HellFlameEmbers embers = new HellFlameEmbers();
    private final float seed;
    private long lastNanos;
    private boolean initialized;
    private final Matrix3f basis = new Matrix3f();
    private HellFlameProfile profile;
    private Direction front, upwards, up;
    private Vec3 base, center;
    private AABB bounds;

    private HellFlameClientState(StarTHellForgeMachine machine) {
        seed = (machine.getPos().asLong() * 0x9E3779B97F4A7C15L >>> 40) / (float) (1 << 24);
    }

    public static HellFlameClientState of(StarTHellForgeMachine machine) {
        return STATES.computeIfAbsent(machine, HellFlameClientState::new);
    }

    public void update(StarTHellForgeMachine machine, long gameTime, float partialTick) {
        place(machine);
        long now = System.nanoTime();
        float targetHeat = machine.getDisplayHeat() / 255f;
        float targetRoar = machine.getRecipeLogic().isWorking() ? 1 : 0;
        float kindleAge = HellFlameAnimation.age(gameTime, partialTick, machine.getKindleGameTime());
        float gutterAge = HellFlameAnimation.age(gameTime, partialTick, machine.getGutterGameTime());
        state = HellFlameAnimation.state(machine.isFormed(), kindleAge, gutterAge);
        if (!initialized || state == HellFlameAnimation.State.NONE) {
            initialized = true;
            heat = targetHeat;
            roar = targetRoar;
        } else {
            float dt = Mth.clamp((now - lastNanos) / 1e9f, 0, 0.1f);
            heat = HellFlameAnimation.approach(heat, targetHeat, dt, HEAT_TIME, HEAT_TIME);
            roar = HellFlameAnimation.approach(roar, targetRoar, dt, ROAR_ATTACK, ROAR_RELEASE);
            flameTime = (flameTime + dt * HellFlameAnimation.flameSpeed(heat, roar)) % FLAME_TIME_WRAP;
        }
        lastNanos = now;
        HellFlameAnimation.compute(frame, profile, heat, roar, machine.getHeatBurst(),
                HellFlameAnimation.age(gameTime, partialTick, machine.getHeatBurstGameTime()));
        if (state == HellFlameAnimation.State.KINDLING) HellFlameAnimation.kindle(frame, kindleAge);
        else if (state == HellFlameAnimation.State.GUTTERING) HellFlameAnimation.gutter(frame, gutterAge);
    }

    public AABB bounds(StarTHellForgeMachine machine) {
        place(machine);
        return bounds;
    }

    private void place(StarTHellForgeMachine machine) {
        var front = machine.getFrontFacing();
        var upwards = machine.getUpwardsFacing();
        if (bounds != null && front == this.front && upwards == this.upwards) return;
        this.front = front;
        this.upwards = upwards;
        profile = machine.getProfile();
        up = machine.getFlameUp();
        base = machine.getFlameBase();
        var axis = Vec3.atLowerCornerOf(up.getNormal());
        center = base.add(axis.scale(profile.height / 2));
        HellFlameProfile.basis(up.step(), basis);
        double r = profile.boundRadius;
        bounds = new AABB(base.subtract(axis.scale(0.5)), base.add(axis.scale(profile.boundHeight)))
                .inflate(up.getAxis() == Direction.Axis.X ? 0 : r, up.getAxis() == Direction.Axis.Y ? 0 : r,
                        up.getAxis() == Direction.Axis.Z ? 0 : r);
    }

    HellFlameProfile profile() {
        return profile;
    }

    Direction up() {
        return up;
    }

    Vec3 base() {
        return base;
    }

    Vec3 center() {
        return center;
    }

    Matrix3f basis() {
        return basis;
    }

    AABB bounds() {
        return bounds;
    }

    float seed() {
        return seed;
    }
}
