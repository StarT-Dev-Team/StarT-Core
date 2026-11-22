package com.startechnology.start_core.api.machine;

import java.util.function.Consumer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

/**
 * Interface implemented by MetaMachines that supply Jade tooltips.
 */
public interface IStarTJadeDisplayable {

    /**
     * Server-side: Use to write packets to transfer data.
     */
    void writeJadeData(CompoundTag data);

    /**
     * Client-side: Use the server packets to render a Jade tooltip.
     */
    void appendJadeTooltip(CompoundTag data, Consumer<Component> tooltip);
}
