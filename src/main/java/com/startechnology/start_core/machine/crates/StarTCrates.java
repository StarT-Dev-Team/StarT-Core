package com.startechnology.start_core.machine.crates;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import com.gregtechceu.gtceu.common.machine.storage.CrateMachine;
import net.minecraft.network.chat.Component;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTCrates {

    public static final MachineDefinition ENRICHED_NAQUADAH_CRATE = registerCrate(GTMaterials.NaquadahEnriched, 9 * 18, "Enriched Naquadah Crate");
    public static final MachineDefinition NEUTRONIUM_CRATE = registerCrate(GTMaterials.Neutronium, 10 * 18, "Neutronium Crate");

    /**
     * Originally from {@link com.gregtechceu.gtceu.common.data.machines.GTMachineUtils#registerCrate}
     */
    public static MachineDefinition registerCrate(Material material, int capacity, String lang) {
        return START_REGISTRATE.machine(material.getName() + "_crate", holder -> new CrateMachine(holder, material, capacity))
                .langValue(lang)
                .rotationState(RotationState.NONE)
                .tooltips(Component.translatable("gtceu.universal.tooltip.item_storage_capacity", capacity))
                .model(GTMachineModels.createCrateModel(false))
                .paintingColor(material.getMaterialRGB())
                .itemColor((s, t) -> material.getMaterialRGB())
                .register();
    }

    public static void init() {}
}
