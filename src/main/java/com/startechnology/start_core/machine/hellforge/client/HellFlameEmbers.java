package com.startechnology.start_core.machine.hellforge.client;

import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.client.megastructure.RenderTier;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import com.startechnology.start_core.particle.StarTParticles;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public final class HellFlameEmbers {

    private static final double RANGE = 96;
    private static final int MAX_EMBERS = 40;
    private static final int MAX_SMOKE = 15;
    private static final int SMOKE_TICKS = 60;
    private static final int PUFF_TICKS = 40;
    private static final int BURST_EMBERS = 16;
    private static final int KINDLE_EMBERS = 8;
    private static final int GUTTER_PUFFS = 6;
    private static final Vector3fc WHITE = new Vector3f(1);
    private static final Vector3fc SMOKE_RISE = new Vector3f(0, 0.04f, 0);

    private final long[] emberDeaths = new long[MAX_EMBERS];
    private final long[] smokeDeaths = new long[MAX_SMOKE];
    private float embersDue, smokeDue;
    private long burstSeen = Long.MIN_VALUE / 2, kindleSeen = Long.MIN_VALUE / 2, gutterSeen = Long.MIN_VALUE / 2;

    HellFlameEmbers() {}

    public static void tick(StarTHellForgeMachine machine) {
        var minecraft = Minecraft.getInstance();
        var level = machine.getLevel();
        if (level == null || level != minecraft.level) return;
        if (!StarTConfig.INSTANCE.client.hellFlameParticles || HellFlameRenderManager.tier() == RenderTier.OFF) return;
        float share = switch (minecraft.options.particles().get()) {
            case ALL -> 1;
            case DECREASED -> 2 / 3f;
            case MINIMAL -> 0;
        };
        if (share == 0) return;
        var state = HellFlameClientState.of(machine);
        long gameTime = level.getGameTime();
        state.update(machine, gameTime, 0);
        if (state.state == HellFlameAnimation.State.NONE) return;
        if (!state.center().closerThan(minecraft.gameRenderer.getMainCamera().getPosition(), RANGE)) return;
        state.embers.emit(machine, state, gameTime, share, minecraft.particleEngine, level.getRandom());
    }

    private void emit(StarTHellForgeMachine machine, HellFlameClientState state, long gameTime, float share,
                      ParticleEngine engine, RandomSource random) {
        var hot = new Vector3f(state.profile().theme).lerp(WHITE, 0.4f);
        int burstEmbers = 0, kindleEmbers = 0, puffs = 0;
        if (machine.getHeatBurstGameTime() != burstSeen) {
            burstSeen = machine.getHeatBurstGameTime();
            burstEmbers = onStart(gameTime - burstSeen, HellFlameAnimation.BURST_TICKS, BURST_EMBERS, share);
        }
        if (machine.getKindleGameTime() != kindleSeen) {
            kindleSeen = machine.getKindleGameTime();
            kindleEmbers = onStart(gameTime - kindleSeen, HellFlameAnimation.KINDLE_TICKS, KINDLE_EMBERS, share);
        }
        if (machine.getGutterGameTime() != gutterSeen) {
            gutterSeen = machine.getGutterGameTime();
            puffs = onStart(gameTime - gutterSeen, HellFlameAnimation.GUTTER_TICKS, GUTTER_PUFFS, share);
        }

        embersDue += share * HellFlameAnimation.emberRate(state.heat, state.roar) / 20;
        for (; embersDue >= 1; embersDue--) ember(state, gameTime, 0.5f, hot, engine, random);
        for (; burstEmbers > 0; burstEmbers--) ember(state, gameTime, 0.5f, hot, engine, random);
        for (; kindleEmbers > 0; kindleEmbers--) ember(state, gameTime, 0.1f, hot, engine, random);
        var puffType = state.profile().smoke > 0 ? ParticleTypes.LARGE_SMOKE : ParticleTypes.SOUL;
        for (; puffs > 0; puffs--) puff(state, gameTime, puffType, engine, random);
        if (state.profile().smoke > 0 && state.state != HellFlameAnimation.State.GUTTERING) {
            smokeDue += share * Mth.lerp(state.roar, 0.1f, 0.25f);
            for (; smokeDue >= 1; smokeDue--) smoke(state, gameTime, engine, random);
        }
    }

    private void ember(HellFlameClientState state, long gameTime, float maxHeight, Vector3fc hot,
                       ParticleEngine engine, RandomSource random) {
        int slot = free(emberDeaths, gameTime);
        if (slot < 0) return;
        var frame = state.frame;
        float h = random.nextFloat() * maxHeight;
        float r = state.profile().radiusAt(h) * frame.width * 0.8f * (float) Math.sqrt(random.nextFloat());
        float angle = random.nextFloat() * Mth.TWO_PI;
        var local = new Vector3f(r * Mth.cos(angle), frame.lift + h * frame.height, r * Mth.sin(angle));
        var velocity = state.basis().transformTranspose(
                new Vector3f(0.02f * local.x, 0.08f + 0.07f * random.nextFloat(), 0.02f * local.z));
        var particle = spawn(engine, StarTParticles.FORGE_EMBER.get(), state, local, velocity);
        if (!(particle instanceof ForgeEmberParticle ember)) return;
        ember.setColors(hot, state.profile().fringe);
        emberDeaths[slot] = gameTime + ember.getLifetime();
    }

    private void smoke(HellFlameClientState state, long gameTime, ParticleEngine engine, RandomSource random) {
        int slot = free(smokeDeaths, gameTime);
        if (slot < 0) return;
        var frame = state.frame;
        float spread = 0.3f * state.profile().radius * frame.width;
        var local = new Vector3f((random.nextFloat() - 0.5f) * spread, frame.lift + 0.95f * frame.height,
                (random.nextFloat() - 0.5f) * spread);
        var particle = spawn(engine, ParticleTypes.CAMPFIRE_COSY_SMOKE, state, local, SMOKE_RISE);
        if (particle == null) return;
        particle.setLifetime(SMOKE_TICKS);
        smokeDeaths[slot] = gameTime + SMOKE_TICKS;
    }

    private void puff(HellFlameClientState state, long gameTime, ParticleOptions type, ParticleEngine engine,
                      RandomSource random) {
        int slot = free(smokeDeaths, gameTime);
        if (slot < 0) return;
        var frame = state.frame;
        float h = 0.1f + 0.3f * random.nextFloat();
        float r = state.profile().radiusAt(h) * frame.width * 0.6f;
        float angle = random.nextFloat() * Mth.TWO_PI;
        var local = new Vector3f(r * Mth.cos(angle), frame.lift + h * frame.height, r * Mth.sin(angle));
        var velocity = state.basis().transformTranspose(new Vector3f(0.01f * local.x, 0.03f, 0.01f * local.z));
        var particle = spawn(engine, type, state, local, velocity);
        if (particle == null) return;
        particle.setLifetime(PUFF_TICKS);
        smokeDeaths[slot] = gameTime + PUFF_TICKS;
    }

    private static @Nullable Particle spawn(ParticleEngine engine, ParticleOptions type, HellFlameClientState state,
                                            Vector3f local, Vector3fc velocity) {
        var offset = state.basis().transformTranspose(local);
        var base = state.base();
        return engine.createParticle(type, base.x + offset.x, base.y + offset.y, base.z + offset.z, velocity.x(),
                velocity.y(), velocity.z());
    }

    private static int onStart(long age, int ticks, int count, float share) {
        return age < ticks ? Math.round(count * share) : 0;
    }

    private static int free(long[] deaths, long gameTime) {
        for (int i = 0; i < deaths.length; i++) {
            if (deaths[i] <= gameTime) return i;
        }
        return -1;
    }
}
