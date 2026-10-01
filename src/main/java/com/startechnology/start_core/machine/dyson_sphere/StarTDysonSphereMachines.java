package com.startechnology.start_core.machine.dyson_sphere;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.machine.dyson_sphere.client.DysonSphereRender;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import dev.latvian.mods.kubejs.KubeJS;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.CONTROLLER_Y;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.PEDESTAL_RADIUS;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.PEDESTAL_TOP;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.PYLON_OFFSET;

public class StarTDysonSphereMachines {

    public static final int MAX_OUTPUT_HATCHES = 4;
    public static final int MAX_ENERGY_HATCHES = 4;

    private static final String CASING = "superdense_machine_casing";
    private static final String ACCENT = "stellarium_casing";

    public static final MultiblockMachineDefinition DYSON_SPHERE = START_REGISTRATE
            .multiblock("dyson_sphere", DysonSphereMachine::new)
            .langValue("Dyson Sphere")
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .allowFlip(false)
            .recipeTypes(StarTRecipeTypes.DYSON_SPHERE_FUEL_RECIPES, StarTRecipeTypes.DYSON_SPHERE_REMNANT_RECIPES)
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock(CASING))
            .pattern(StarTDysonSphereMachines::createPattern)
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(GTMachineModels.createWorkableCasingMachineModel(
                    KubeJS.id("block/casings/abydos_multis/" + CASING),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(DysonSphereRender::new)))
            .register();

    private static BlockPattern createPattern(MultiblockMachineDefinition definition) {
        int r = PEDESTAL_RADIUS;
        int size = 2 * r + 1;
        var pattern = FactoryBlockPattern.start();
        for (int aisle = 0; aisle < size; aisle++) {
            var rows = new String[PEDESTAL_TOP + 1];
            for (int row = 0; row <= PEDESTAL_TOP; row++) {
                var line = new StringBuilder(size);
                for (int column = 0; column < size; column++) {
                    line.append(symbolAt(r - column, row, r - aisle));
                }
                rows[row] = line.toString();
            }
            pattern.aisle(rows);
        }

        return pattern
                .where(' ', Predicates.any())
                .where('S', Predicates.blocks(StarTMachineUtils.getKjsBlock(ACCENT)))
                .where('C', Predicates.blocks(StarTMachineUtils.getKjsBlock(CASING))
                        .or(Predicates.abilities(PartAbility.OUTPUT_LASER).setMinGlobalLimited(1)
                                .setMaxGlobalLimited(MAX_OUTPUT_HATCHES, 1))
                        .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                        .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setMinGlobalLimited(1)
                                .setPreviewCount(1))
                        .or(Predicates.abilities(PartAbility.INPUT_ENERGY, PartAbility.INPUT_LASER)
                                .setMaxGlobalLimited(MAX_ENERGY_HATCHES, 1))
                        .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                        .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(1)))
                .where('@', Predicates.controller(Predicates.blocks(definition.get())))
                .build();
    }

    private static char symbolAt(int x, int y, int z) {
        if (x == 0 && y == CONTROLLER_Y && z == -PEDESTAL_RADIUS) return '@';

        int ax = Math.abs(x);
        int az = Math.abs(z);
        if (ax == PYLON_OFFSET && az == PYLON_OFFSET && y >= 2) return 'S';
        return switch (y) {
            case 0, 1 -> inOctagon(ax, az, PEDESTAL_RADIUS, 17) ? 'C' : ' ';
            case 2 -> inOctagon(ax, az, 9, 14) ? 'C' : ' ';
            case 3 -> inOctagon(ax, az, 7, 11) ? 'C' : ' ';
            case PEDESTAL_TOP -> ax == 0 && az == 0 ? 'S' : ' ';
            default -> ax <= 1 && az <= 1 ? 'S' : ' ';
        };
    }

    private static boolean inOctagon(int ax, int az, int radius, int cut) {
        return ax <= radius && az <= radius && ax + az <= cut;
    }

    public static void init() {}
}
