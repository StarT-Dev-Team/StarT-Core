package com.startechnology.start_core.machine.modular;

import java.util.function.Function;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import com.gregtechceu.gtceu.common.machine.multiblock.part.EnergyHatchPartMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkHatchPartMachine;
import com.startechnology.start_core.machine.redstone.StarTRedstoneInterfacePartMachine;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Start;

public class StarTModularConnectionHatches {
    public static String getModularConnectionHatchIOName(IO io) {
        return io == IO.IN ? "node" : "terminal";
    }


    private static class StarTModularEnergyOverlay {
        @Getter
        private final ResourceLocation tintedPart;
        @Getter
        private final ResourceLocation inPart;
        @Getter
        private final ResourceLocation inPartEmissive;
        @Getter
        private final ResourceLocation outPart;
        @Getter
        private final ResourceLocation outPartEmissive;

        public StarTModularEnergyOverlay(Integer amperage) {
            this.tintedPart = StarTCore.resourceLocation("block/overlay/modular/overlay_energy_" + amperage + "a_tinted");

            /* This can be the same since the emmissive will cover it up ^^ */
            this.inPart = StarTCore.resourceLocation("block/overlay/modular/overlay_energy_" + amperage + "a_out");
            this.inPartEmissive = StarTCore.resourceLocation("block/overlay/modular/overlay_energy_" + amperage + "a_in_emissive");
            this.outPart = StarTCore.resourceLocation("block/overlay/modular/overlay_energy_" + amperage + "a_out");
            this.outPartEmissive = StarTCore.resourceLocation("block/overlay/modular/overlay_energy_" + amperage + "a_out_emissive"); 
        }
    }

    public static MachineBuilder.ModelInitializer createNonPoweredModularHatchModel(IO io) {
        return (ctx, prov, builder) -> {
            ResourceLocation tintTexture = StarTCore.resourceLocation("block/overlay/modular/overlay_energy_2a_tinted");
            ResourceLocation overlayOutTexture = StarTCore.resourceLocation("block/overlay/modular/overlay_interface_out");
            ResourceLocation overlayOutEmissive = StarTCore.resourceLocation("block/overlay/modular/overlay_interface_out_emissive");
            ResourceLocation overlayInEmissive = StarTCore.resourceLocation("block/overlay/modular/overlay_interface_in_emissive");

            builder.forAllStatesModels(states -> {
                // genuinely why does GT manually write out each energy model for output and input
                BlockModelBuilder model;


                if (io == IO.OUT) {
                    model = prov.models().nested()
                            .parent(prov.models().getExistingFile(GTCEu.id("block/machine/part/energy_input_hatch")))
                            .texture("overlay_tint", tintTexture)
                            .texture("overlay_in", overlayOutTexture)
                            .texture("overlay_in_emissive", overlayOutEmissive);
                } else {
                    model = prov.models().nested()
                            .parent(prov.models().getExistingFile(GTCEu.id("block/machine/part/energy_input_hatch")))
                            .texture("overlay_tint", tintTexture)
                            .texture("overlay_in", overlayOutTexture)
                            .texture("overlay_in_emissive", overlayInEmissive);
                }

                GTMachineModels.tieredHullTextures(model, builder.getOwner().getTier());
                return model;
            });

            builder.addReplaceableTextures("bottom", "top", "side");
        };
    }

    public static MachineBuilder.ModelInitializer createPowerModularHatchModel(int amperage, IO io) {
        return (ctx, prov, builder) -> {
            final StarTModularEnergyOverlay energyOverlay = new StarTModularEnergyOverlay(amperage);

            builder.forAllStatesModels(states -> {
                // genuinely why does GT manually write out each energy model for output and input
                BlockModelBuilder model;

                if (io == IO.OUT) {
                    model = prov.models().nested()
                            .parent(prov.models().getExistingFile(GTCEu.id("block/machine/part/energy_input_hatch")))
                            .texture("overlay_tint", energyOverlay.getTintedPart())
                            .texture("overlay_in", energyOverlay.getOutPart())
                            .texture("overlay_in_emissive", energyOverlay.getOutPartEmissive());
                } else {
                    model = prov.models().nested()
                            .parent(prov.models().getExistingFile(GTCEu.id("block/machine/part/energy_input_hatch")))
                            .texture("overlay_tint", energyOverlay.getTintedPart())
                            .texture("overlay_in", energyOverlay.getInPart())
                            .texture("overlay_in_emissive", energyOverlay.getInPartEmissive());
                }

                GTMachineModels.tieredHullTextures(model, builder.getOwner().getTier());
                return model;
            });

            builder.addReplaceableTextures("bottom", "top", "side");
        };
    }

        public static MachineDefinition[] registerPowerModularConnectionHatch(int amperage, IO io, PartAbility ability, PartAbility conduitAbility,
                        int... tiers) {
        String name = getModularConnectionHatchIOName(io);

        return StarTMachineUtils.registerTieredMachines(amperage + "a_modular_conduit_" + name,
                (holder, tier) -> new StarTModularConduitHatchPartMachine(holder, io, tier, amperage),
                (tier, builder) -> builder
                        .langValue(GTValues.VNF[tier] + "§r " + FormattingUtil.formatNumbers(amperage)
                                + "§eA§r Modular Conduit " + FormattingUtil.toEnglishName(name))
                        .rotationState(RotationState.ALL)
                        .tooltips(
                                Component.translatable(
                                        "gtceu.universal.tooltip.voltage_" + (io == IO.IN ? "in" : "out"),
                                        FormattingUtil.formatNumbers(GTValues.V[tier]), GTValues.VNF[tier]),
                                Component.translatable("gtceu.universal.tooltip.amperage_in", amperage),
                                Component.translatable("gtceu.universal.tooltip.energy_storage_capacity",
                                        FormattingUtil
                                                .formatNumbers(
                                                        EnergyHatchPartMachine.getHatchEnergyCapacity(tier, amperage))),
                                Component.translatable("gtceu.part_sharing.disabled"))
                        .abilities(ability, conduitAbility)
                        .model(createPowerModularHatchModel(amperage, io))
                        .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                        .register(),
                tiers);
    }

    public static final Integer MODULAR_CONDUIT_BASE_TIER = GTValues.UV;

    public static Function<IMachineBlockEntity, MetaMachine> getHolder(IO io) {
        return holder -> new StarTModularInterfaceHatchPartMachine(holder, io, MODULAR_CONDUIT_BASE_TIER);
    }

        public static MachineDefinition registerNonPoweredModularConnectionHatch(IO io, PartAbility ability,
                        PartAbility interfaceAbility) {
        String name = getModularConnectionHatchIOName(io);

        return StarTCore.START_REGISTRATE.machine(
                "modular_interface_" + name, getHolder(io))
                .langValue("Modular Interface " + FormattingUtil.toEnglishName(name))
                .tooltips(
                        Component.translatable("gtceu.part_sharing.disabled"))
                .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                .rotationState(RotationState.ALL)
                .abilities(ability, interfaceAbility)
                .model(createNonPoweredModularHatchModel(io))
                .tier(MODULAR_CONDUIT_BASE_TIER)
                .register();
    }

    public static final MachineDefinition[] MODULAR_TERMINAL_2 = registerPowerModularConnectionHatch(2, IO.OUT,
        StarTPartAbility.MODULAR_TERMINAL, StarTPartAbility.MODULAR_TERMINAL_CONDUIT_2A,
        GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition[] MODULAR_TERMINAL_4 = registerPowerModularConnectionHatch(4, IO.OUT,
            StarTPartAbility.MODULAR_TERMINAL, StarTPartAbility.MODULAR_TERMINAL_CONDUIT_4A,
            GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition[] MODULAR_TERMINAL_16 = registerPowerModularConnectionHatch(16, IO.OUT,
            StarTPartAbility.MODULAR_TERMINAL, StarTPartAbility.MODULAR_TERMINAL_CONDUIT_16A,
            GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition[] MODULAR_TERMINAL_64 = registerPowerModularConnectionHatch(64, IO.OUT,
            StarTPartAbility.MODULAR_TERMINAL, StarTPartAbility.MODULAR_TERMINAL_CONDUIT_64A,
            GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition[] MODULAR_TERMINAL_256 = registerPowerModularConnectionHatch(256, IO.OUT,
            StarTPartAbility.MODULAR_TERMINAL, StarTPartAbility.MODULAR_TERMINAL_CONDUIT_256A,
            GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition[] MODULAR_TERMINAL_1024 = registerPowerModularConnectionHatch(1024, IO.OUT,
            StarTPartAbility.MODULAR_TERMINAL, StarTPartAbility.MODULAR_TERMINAL_CONDUIT_1024A,
            GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition[] MODULAR_TERMINAL_4096 = registerPowerModularConnectionHatch(4096, IO.OUT,
            StarTPartAbility.MODULAR_TERMINAL, StarTPartAbility.MODULAR_TERMINAL_CONDUIT_4096A,
            GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition[] MODULAR_NODE_2 = registerPowerModularConnectionHatch(2, IO.IN,
        StarTPartAbility.MODULAR_NODE, StarTPartAbility.MODULAR_NODE_CONDUIT_2A,
        GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition[] MODULAR_NODE_4 = registerPowerModularConnectionHatch(4, IO.IN,
            StarTPartAbility.MODULAR_NODE, StarTPartAbility.MODULAR_NODE_CONDUIT_4A,
            GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition[] MODULAR_NODE_16 = registerPowerModularConnectionHatch(16, IO.IN,
            StarTPartAbility.MODULAR_NODE, StarTPartAbility.MODULAR_NODE_CONDUIT_16A,
            GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition[] MODULAR_NODE_64 = registerPowerModularConnectionHatch(64, IO.IN,
            StarTPartAbility.MODULAR_NODE, StarTPartAbility.MODULAR_NODE_CONDUIT_64A,
            GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition[] MODULAR_NODE_256 = registerPowerModularConnectionHatch(256, IO.IN,
            StarTPartAbility.MODULAR_NODE, StarTPartAbility.MODULAR_NODE_CONDUIT_256A,
            GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition[] MODULAR_NODE_1024 = registerPowerModularConnectionHatch(1024, IO.IN,
            StarTPartAbility.MODULAR_NODE, StarTPartAbility.MODULAR_NODE_CONDUIT_1024A,
            GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition[] MODULAR_NODE_4096 = registerPowerModularConnectionHatch(4096, IO.IN,
            StarTPartAbility.MODULAR_NODE, StarTPartAbility.MODULAR_NODE_CONDUIT_4096A,
            GTValues.tiersBetween(MODULAR_CONDUIT_BASE_TIER, GTValues.MAX)
    );

    public static final MachineDefinition MODULAR_NODE = registerNonPoweredModularConnectionHatch(IO.IN,
            StarTPartAbility.MODULAR_NODE, StarTPartAbility.MODULAR_NODE_INTERFACE);

    public static final MachineDefinition MODULAR_TERMINAL = registerNonPoweredModularConnectionHatch(IO.OUT,
            StarTPartAbility.MODULAR_TERMINAL, StarTPartAbility.MODULAR_TERMINAL_INTERFACE);

    public static void init() {}
}