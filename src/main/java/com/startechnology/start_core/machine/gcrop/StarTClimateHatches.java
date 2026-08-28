package com.startechnology.start_core.machine.gcrop;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.gcrop.StarTClimateType;
import com.startechnology.start_core.machine.StarTPartAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createMaintenanceModel;

public class StarTClimateHatches {

    public static final MachineDefinition FROSTY_CLIMATE_HATCH = registerClimateHatch(
            "frosty_climate_hatch",
            ChatFormatting.BLUE + "Frosty Climate Hatch",
            GTValues.IV,
            StarTClimateType.FROSTY);

    public static final MachineDefinition SCORCHING_CLIMATE_HATCH = registerClimateHatch(
            "scorching_climate_hatch",
            ChatFormatting.RED + "Scorching Climate Hatch",
            GTValues.IV,
            StarTClimateType.SCORCHING);

    public static final MachineDefinition TROPICAL_CLIMATE_HATCH = registerClimateHatch(
            "tropical_climate_hatch",
            ChatFormatting.AQUA + "Tropical Climate Hatch",
            GTValues.IV,
            StarTClimateType.TROPICAL);

    public static final MachineDefinition DESERTIC_CLIMATE_HATCH = registerClimateHatch(
            "desertic_climate_hatch",
            ChatFormatting.YELLOW + "Desertic Climate Hatch",
            GTValues.IV,
            StarTClimateType.DESERTIC);

    public static final MachineDefinition DAMP_CLIMATE_HATCH = registerClimateHatch(
            "damp_climate_hatch",
            ChatFormatting.DARK_BLUE + "Damp Climate Hatch",
            GTValues.IV,
            StarTClimateType.DAMP);

    private static MachineDefinition registerClimateHatch(String id, String langName, int tier,
                                                          StarTClimateType... climates) {
        return StarTCore.START_REGISTRATE
                .machine(id, holder -> new StarTClimateHatchPartMachine(holder, tier, climates))
                .langValue(langName + ChatFormatting.RESET)
                .rotationState(RotationState.ALL)
                .abilities(StarTPartAbility.CLIMATE_HATCH)
                .tooltips(Component.translatable("gtceu.part_sharing.disabled"),
                        Component.translatable("start_core.machine.climate_hatch.tooltip"))
                .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                .modelProperty(GTMachineModelProperties.IS_TAPED, false)
                // TODO: add overlay textures
                .model(createMaintenanceModel(StarTCore.resourceLocation("block/maintenance_sterile")))
                .tier(tier)
                .register();
    }

    public static void init() {}
}
