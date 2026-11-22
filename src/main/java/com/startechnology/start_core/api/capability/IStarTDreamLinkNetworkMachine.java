package com.startechnology.start_core.api.capability;

import java.util.function.Consumer;

import com.startechnology.start_core.api.dreamlink.IStarTDreamCopyInteractable;
import com.startechnology.start_core.api.machine.IStarTJadeDisplayable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public interface IStarTDreamLinkNetworkMachine extends IStarTDreamCopyInteractable, IStarTJadeDisplayable {

    String DEFAULT_NETWORK = "Untitled Network";

    /* Set the dream link network of this machine */
    public void setNetwork(String network);

    /* Get the network of this machine */
    public String getNetwork();

    /* Get if the machine is currently in the Dreaming (active) state */
    public boolean isDreaming();

    @Override
    default void writeJadeData(CompoundTag data) {
        if (getNetwork() != null) {
            data.putString("network", getNetwork());
        }
        data.putBoolean("dreaming", isDreaming());
    }

    @Override
    default void appendJadeTooltip(CompoundTag data, Consumer<Component> tooltip) {
        if (data.contains("network") && data.contains("dreaming")) {
            String network = data.getString("network");
            boolean dreaming = data.getBoolean("dreaming");

            if (dreaming) {
                tooltip.accept(Component.translatable("start_core.machine.dream_link.active_network", network));
            } else {
                tooltip.accept(Component.translatable("start_core.machine.dream_link.inactive_network", network));
            }
        }
    }
}
