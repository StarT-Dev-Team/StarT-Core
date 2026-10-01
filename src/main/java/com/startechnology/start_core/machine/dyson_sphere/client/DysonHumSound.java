package com.startechnology.start_core.machine.dyson_sphere.client;

import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.machine.dyson_sphere.DysonSphereMachine;
import com.startechnology.start_core.sound.StarTSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

final class DysonHumSound extends AbstractTickableSoundInstance {

    private static final float FADE_RATE = 0.05f;
    private static final float FULL_DISTANCE = 80;
    private static final float SILENT_DISTANCE = 240;

    private final DysonSphereMachine machine;
    private final DysonClientState state;

    DysonHumSound(DysonSphereMachine machine, DysonClientState state) {
        super(StarTSounds.DYSON_HUM.get(), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
        this.machine = machine;
        this.state = state;
        var center = Vec3.atCenterOf(machine.getCenter());
        x = center.x;
        y = center.y;
        z = center.z;
        looping = true;
        delay = 0;
        attenuation = Attenuation.NONE;
        volume = 0.001f;
        pitch = target()[1];
    }

    boolean silent() {
        return target()[0] <= 0.001f;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (machine.isInValid() || machine.getLevel() != Minecraft.getInstance().level) {
            stop();
            return;
        }
        var target = target();
        volume = Mth.approach(volume, target[0], FADE_RATE);
        pitch = Mth.approach(pitch, target[1], FADE_RATE * 0.5f);
        if (target[0] <= 0 && volume <= 0.001f) stop();
    }

    private float[] target() {
        var level = machine.getLevel();
        float progress = 0;
        if (level != null) {
            float ticks = level.getGameTime() - machine.getPhaseStartGameTime();
            float duration = machine.getPhase().duration;
            progress = duration > 0 ? Mth.clamp(ticks / duration, 0, 1) : 0;
        }
        float activity = state.activity();
        float starving = 1 - state.supply;
        float gain = StarTConfig.INSTANCE.client.dysonHumVolume / 100f;
        float[] target = switch (machine.getPhase()) {
            case IDLE, SUPERNOVA -> new float[] { 0, 1 };
            case PROTOSTAR -> new float[] { 0.5f, 0.7f };
            case IGNITING -> new float[] { Mth.lerp(progress, 0.5f, 1), Mth.lerp(progress, 0.7f, 1) };
            case MAIN_SEQUENCE -> new float[] { 0.8f + 0.2f * activity, 0.9f + 0.2f * activity };
            case RED_GIANT_TRANSITION -> new float[] { 1, Mth.lerp(progress, 1, 0.78f) };
            case RED_GIANT -> new float[] { 1, 0.78f };
            case NEBULA -> new float[] { 1 - progress, Mth.lerp(progress, 0.78f, 0.6f) };
            case FIZZLE -> new float[] { 0.9f * (1 - progress), Mth.lerp(progress, 0.9f, 0.5f) };
            case DISPERSING -> new float[] { 0.8f * (1 - progress), 0.8f };
        };
        if (machine.isMuffled()) target[0] = 0;
        target[0] *= gain * (1 - 0.5f * starving) * falloff();
        target[1] *= 1 - 0.15f * starving;
        return target;
    }

    private float falloff() {
        var camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        float distance = (float) camera.distanceTo(new Vec3(x, y, z));
        return 1 - Mth.clamp((distance - FULL_DISTANCE) / (SILENT_DISTANCE - FULL_DISTANCE), 0, 1);
    }
}
