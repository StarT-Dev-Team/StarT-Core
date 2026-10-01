package com.startechnology.start_core.machine.neutron_star;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.machine.neutron_star.client.NeutronStarRender;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import dev.latvian.mods.kubejs.KubeJS;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.HALF_WIDTH;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.TOP;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.structureAt;

public class StarTNeutronStarMachines {

    public static final int MAX_ENERGY_HATCHES = 4;

    private static final String CASING = "superdense_machine_casing";
    private static final String ACCENT = "stellarium_casing";

    public static final MultiblockMachineDefinition NEUTRON_STAR_FORGE = START_REGISTRATE
            .multiblock("neutron_star_forge", NeutronStarForgeMachine::new)
            .langValue("Neutron Star Forge")
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .allowFlip(false)
            .recipeTypes(StarTRecipeTypes.NEUTRON_STAR_FORGE_RECIPES, StarTRecipeTypes.NEUTRON_STAR_ACCRETION_RECIPES,
                    StarTRecipeTypes.NEUTRON_STAR_CAPTURE_RECIPES)
            .recipeModifiers(NeutronStarForgeMachine::recipeModifier, GTRecipeModifiers.OC_NON_PERFECT_SUBTICK)
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock(CASING))
            .pattern(StarTNeutronStarMachines::createPattern)
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(GTMachineModels.createWorkableCasingMachineModel(
                    KubeJS.id("block/casings/abydos_multis/" + CASING),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(NeutronStarRender::new)))
            .register();

    private static BlockPattern createPattern(MultiblockMachineDefinition definition) {
        int size = 2 * HALF_WIDTH + 1;
        var pattern = FactoryBlockPattern.start();
        for (int aisle = 0; aisle < size; aisle++) {
            var rows = new String[TOP + 1];
            for (int row = 0; row <= TOP; row++) {
                var line = new StringBuilder(size);
                for (int column = 0; column < size; column++) {
                    line.append(structureAt(HALF_WIDTH - column, row, HALF_WIDTH - aisle));
                }
                rows[row] = line.toString();
            }
            pattern.aisle(rows);
        }

        var casing = StarTMachineUtils.getKjsBlock(CASING);
        return pattern
                .where(' ', Predicates.any())
                .where('c', Predicates.blocks(casing))
                .where('S', Predicates.blocks(StarTMachineUtils.getKjsBlock(ACCENT)))
                .where('C', Predicates.blocks(casing)
                        .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setMinGlobalLimited(1)
                                .setPreviewCount(1))
                        .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1)
                                .setPreviewCount(1))
                        .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                        .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                        .or(Predicates.abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER)
                                .setMinGlobalLimited(1).setMaxGlobalLimited(MAX_ENERGY_HATCHES, 1))
                        .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                        .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(1)))
                .where('@', Predicates.controller(Predicates.blocks(definition.get())))
                .build();
    }

    public static void init() {}
}
