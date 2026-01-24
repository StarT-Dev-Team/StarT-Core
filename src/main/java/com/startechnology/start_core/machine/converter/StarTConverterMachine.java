package com.startechnology.start_core.machine.converter;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import com.gregtechceu.gtceu.common.machine.electric.ConverterMachine;
import com.startechnology.start_core.machine.StarTMachineUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class StarTConverterMachine {
    
    public static MachineDefinition[] registerConverter(int amperage, int startTier, int endTier) {
        MachineDefinition[] converters = StarTMachineUtils.registerTieredMachines(amperage + "a_energy_converter",
                (holder, tier) -> new ConverterMachine(holder, tier, amperage),
                (tier, builder) -> builder
                        .rotationState(RotationState.ALL)
                        .langValue("%s %s§eA§r Energy Converter".formatted(GTValues.VCF[tier] + GTValues.VN[tier] + ChatFormatting.RESET,
                                amperage))
                        .modelProperty(GTMachineModelProperties.IS_FE_TO_EU, false)
                        .model(GTMachineModels.createConverterModel(amperage))
                        .tooltips(Component.translatable("gtceu.machine.energy_converter.description"),
                                Component.translatable("gtceu.machine.energy_converter.tooltip_tool_usage"),
                                Component.translatable("gtceu.machine.energy_converter.tooltip_conversion_native",
                                        FeCompat.toFeLong(GTValues.V[tier] * amperage,
                                                FeCompat.ratio(true)),
                                        amperage, GTValues.V[tier], GTValues.VNF[tier]),
                                Component.translatable("gtceu.machine.energy_converter.tooltip_conversion_eu", amperage,
                                GTValues.V[tier], GTValues.VNF[tier],
                                        FeCompat.toFeLong(GTValues.V[tier] * amperage,
                                                FeCompat.ratio(false))))
                        .register(),
                        GTValues.tiersBetween(startTier, endTier));
        return converters;
    }

    public static final MachineDefinition[] ENERGY_CONVERTER_64A = registerConverter(64, GTValues.EV, GTValues.MAX);

    public static void init() {
    }   
}
