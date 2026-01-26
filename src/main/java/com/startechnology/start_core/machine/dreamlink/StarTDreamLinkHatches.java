package com.startechnology.start_core.machine.dreamlink;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.StarTMachineUtils;

import net.minecraft.network.chat.Component;

public class StarTDreamLinkHatches {
    
    public static MachineDefinition[] registerDreamLinkHatch(int amperage, PartAbility ability, int... tiers) {
        return StarTMachineUtils.registerTieredMachines(amperage + "a_dream_link_energy_hatch",
                (holder, tier) -> new StarTDreamLinkHatchPartMachine(holder, tier, amperage), (tier, builder) -> builder
                        .langValue(GTValues.VNF[tier] + "§r " + FormattingUtil.formatNumbers(amperage) + "§eA§r Dream-Link Energy Hatch")
                        .rotationState(RotationState.ALL)
                        .tooltips(
                                Component.translatable("block.start_core.dream_link_energy_hatch_tooltip"),
                                Component.translatable("gtceu.universal.tooltip.voltage_in",
                                        FormattingUtil.formatNumbers(GTValues.V[tier]), GTValues.VNF[tier]),
                                Component.translatable("gtceu.universal.tooltip.amperage_in", amperage),
                                Component.translatable("gtceu.universal.tooltip.energy_storage_capacity",
                                        FormattingUtil
                                                .formatNumbers(
                                                        EnergyHatchPartMachine.getHatchEnergyCapacity(tier, amperage))))
                        .abilities(ability)
                        .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                        .workableTieredHullModel(StarTCore.resourceLocation("block/dreamlink/" + GTValues.VN[tier].toLowerCase() + "_" + amperage + "a_energy_hatch"))
                        .register(),
                tiers);
    }

    public static final MachineDefinition[] DREAM_LINK_HATCH = registerDreamLinkHatch(2, PartAbility.INPUT_ENERGY, 
        GTValues.tiersBetween(GTValues.UV, GTValues.MAX)
    );

    public static final MachineDefinition[] DREAM_LINK_HATCH_4 = registerDreamLinkHatch(4, PartAbility.INPUT_ENERGY, 
        GTValues.tiersBetween(GTValues.UV, GTValues.MAX)
    );

    public static final MachineDefinition[] DREAM_LINK_HATCH_16 = registerDreamLinkHatch(16, PartAbility.INPUT_ENERGY, 
        GTValues.tiersBetween(GTValues.UV, GTValues.MAX)
    );

    public static final MachineDefinition[] DREAM_LINK_HATCH_64 = registerDreamLinkHatch(64, PartAbility.INPUT_ENERGY, 
        GTValues.tiersBetween(GTValues.UV, GTValues.MAX)
    );

    public static final MachineDefinition[] DREAM_LINK_HATCH_256 = registerDreamLinkHatch(256, PartAbility.INPUT_ENERGY, 
        GTValues.tiersBetween(GTValues.UV, GTValues.MAX)
    );

    public static final MachineDefinition[] DREAM_LINK_HATCH_1024 = registerDreamLinkHatch(1024, PartAbility.INPUT_ENERGY, 
        GTValues.tiersBetween(GTValues.UV, GTValues.MAX)
    );

    public static final MachineDefinition[] DREAM_LINK_HATCH_4096 = registerDreamLinkHatch(4096, PartAbility.INPUT_ENERGY, 
        GTValues.tiersBetween(GTValues.UV, GTValues.MAX)
    );

    public static final MachineDefinition[] DREAM_LINK_HATCH_16384 = registerDreamLinkHatch(16384, PartAbility.INPUT_ENERGY, 
        GTValues.tiersBetween(GTValues.UV, GTValues.MAX)
    );


    public static void init() {}
}
