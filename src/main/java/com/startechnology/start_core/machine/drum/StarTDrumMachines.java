package com.startechnology.start_core.machine.drum;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidPipeProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.item.DrumMachineItem;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.machine.storage.DrumMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidType;
import com.gregtechceu.gtceu.api.block.MetaMachineBlock;

import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.DRUM_CAPACITY;
import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.TANK_TOOLTIPS;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTDrumMachines {

    public static MachineDefinition ENRICHED_NAQUADAH_DRUM = registerDrum(GTMaterials.NaquadahEnriched, (2048 * FluidType.BUCKET_VOLUME), "Enriched Naquadah Drum");
    public static MachineDefinition NEUTRONIUM_DRUM = registerDrum(GTMaterials.Neutronium, (4096 * FluidType.BUCKET_VOLUME), "Neutronium Drum");

    public static MachineDefinition registerDrum(Material material, int capacity, String lang) {
        var definition = START_REGISTRATE
                .machine(material.getName() + "_drum", MachineDefinition::new,
                        holder -> new DrumMachine(holder, material, capacity), MetaMachineBlock::new,
                        (holder, prop) -> DrumMachineItem.create(holder, prop, material),
                        MetaMachineBlockEntity::new)
                .langValue(lang)
                .rotationState(RotationState.NONE)
                .simpleModel(
                        GTCEu.id("block/machine/template/drum/metal_drum"))
                .tooltipBuilder((stack, list) -> {
                    TANK_TOOLTIPS.accept(stack, list);
                    if (material.hasProperty(PropertyKey.FLUID_PIPE)) {
                        FluidPipeProperties pipeprops = material.getProperty(PropertyKey.FLUID_PIPE);
                        pipeprops.appendTooltips(list, false, true);
                    }
                })
                .tooltips(Component.translatable("gtceu.machine.quantum_tank.tooltip"),
                        Component.translatable("gtceu.universal.tooltip.fluid_storage_capacity",
                                FormattingUtil.formatNumbers(capacity)))
                .paintingColor(material.getMaterialRGB())
                .itemColor((s, i) -> material.getMaterialRGB())
                .register();
        DRUM_CAPACITY.put(definition, capacity);
        return definition;
    }

    public static void init() {
    }
}
