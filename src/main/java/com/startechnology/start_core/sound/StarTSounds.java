package com.startechnology.start_core.sound;

import com.startechnology.start_core.StarTCore;

import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class StarTSounds {

    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT,
            StarTCore.MOD_ID);

    public static final RegistryObject<SoundEvent> DYSON_HUM = register("dyson.hum", 256);
    public static final RegistryObject<SoundEvent> DYSON_IGNITION = register("dyson.ignition", 384);
    public static final RegistryObject<SoundEvent> DYSON_SUPERNOVA = register("dyson.supernova", 512);
    public static final RegistryObject<SoundEvent> DYSON_ASSEMBLY = register("dyson.assembly", 256);
    public static final RegistryObject<SoundEvent> DYSON_DISASSEMBLY = register("dyson.disassembly", 256);

    private StarTSounds() {}

    public static void register(IEventBus modEventBus) {
        SOUNDS.register(modEventBus);
    }

    private static RegistryObject<SoundEvent> register(String name, float range) {
        return SOUNDS.register(name, () -> SoundEvent.createFixedRangeEvent(StarTCore.resourceLocation(name), range));
    }
}
