package com.startechnology.start_core.particle;

import com.startechnology.start_core.StarTCore;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class StarTParticles {

    private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister
            .create(Registries.PARTICLE_TYPE, StarTCore.MOD_ID);

    public static final RegistryObject<SimpleParticleType> FORGE_EMBER = PARTICLES.register("forge_ember",
            () -> new SimpleParticleType(true));

    private StarTParticles() {}

    public static void register(IEventBus modEventBus) {
        PARTICLES.register(modEventBus);
    }
}
