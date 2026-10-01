package com.startechnology.start_core.machine.black_hole;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.machine.StarTPartAbility;
import com.startechnology.start_core.machine.black_hole.client.BlackHoleRender;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import dev.latvian.mods.kubejs.KubeJS;

import java.util.Locale;

import static com.gregtechceu.gtceu.api.GTValues.UEV;
import static com.gregtechceu.gtceu.api.GTValues.UIV;
import static com.gregtechceu.gtceu.api.GTValues.VN;
import static com.gregtechceu.gtceu.api.GTValues.VNF;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTBlackHoleMachines {

    public static final int STABILIZER_DISTANCE = 20;
    public static final int HOLLOW_RADIUS = 14;
    public static final int CONTROLLER_DROP = 1;
    public static final int STABILIZER_COUNT = 6;
    public static final int STABILIZER_AMPERAGE = 256;
    public static final int MAX_OUTPUT_HATCHES = 4;

    public static final RelativeDirection[] STABILIZER_DIRECTIONS = {
            RelativeDirection.RIGHT, RelativeDirection.LEFT,
            RelativeDirection.UP, RelativeDirection.DOWN,
            RelativeDirection.BACK, RelativeDirection.FRONT
    };

    public static final MachineDefinition UEV_STABILIZER = registerStabilizer(UEV);
    public static final MachineDefinition UIV_STABILIZER = registerStabilizer(UIV);

    public static final MultiblockMachineDefinition BLACK_HOLE_GENERATOR = START_REGISTRATE
            .multiblock("black_hole_generator", BlackHoleGeneratorMachine::new)
            .langValue("Black Hole Generator")
            .rotationState(RotationState.ALL)
            .allowExtendedFacing(true)
            .recipeTypes(StarTRecipeTypes.BLACK_HOLE_IGNITION_RECIPES, StarTRecipeTypes.BLACK_HOLE_FEEDING_RECIPES)
            .appearanceBlock(() -> StarTMachineUtils.getKjsBlock("gravitationally_strained_stabilization_casing"))
            .pattern(StarTBlackHoleMachines::createPattern)
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(GTMachineModels.createWorkableCasingMachineModel(
                    KubeJS.id("block/casings/threading/gravitationally_strained_stabilization_casing"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
                    .andThen(b -> b.addDynamicRenderer(BlackHoleRender::new)))
            .register();

    private static MachineDefinition registerStabilizer(int tier) {
        return START_REGISTRATE
                .machine(VN[tier].toLowerCase(Locale.ROOT) + "_black_hole_stabilizer",
                        holder -> new BlackHoleStabilizerPartMachine(holder, tier, STABILIZER_AMPERAGE))
                .langValue(VNF[tier] + "§r Singularity Stabilizer")
                .rotationState(RotationState.ALL)
                .abilities(StarTPartAbility.BLACK_HOLE_STABILIZER)
                .modelProperty(GTMachineModelProperties.IS_FORMED, false)
                .overlayTieredHullModel(GTCEu.id("block/machine/part/laser_target_hatch"))
                .tier(tier)
                .register();
    }

    private static BlockPattern createPattern(MultiblockMachineDefinition definition) {
        int r = STABILIZER_DISTANCE;
        int size = 2 * r + 1;
        var pattern = FactoryBlockPattern.start();
        for (int aisle = 0; aisle < size; aisle++) {
            var rows = new String[size];
            for (int row = 0; row < size; row++) {
                var line = new StringBuilder(size);
                for (int column = 0; column < size; column++) {
                    line.append(symbolAt(r - column, row - r, r - aisle));
                }
                rows[row] = line.toString();
            }
            pattern.aisle(rows);
        }

        return pattern
                .where(' ', Predicates.any())
                .where('#', Predicates.air())
                .where('S', Predicates.abilities(StarTPartAbility.BLACK_HOLE_STABILIZER))
                .where('C',
                        Predicates
                                .blocks(StarTMachineUtils.getKjsBlock("gravitationally_strained_stabilization_casing"))
                                .or(Predicates.abilities(PartAbility.OUTPUT_LASER).setMinGlobalLimited(1)
                                        .setMaxGlobalLimited(MAX_OUTPUT_HATCHES, 1))
                                .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                                .or(Predicates.abilities(StarTPartAbility.REDSTONE_INTERFACE).setMaxGlobalLimited(1)))
                .where('@', Predicates.controller(Predicates.blocks(definition.get())))
                .build();
    }

    private static char symbolAt(int x, int y, int z) {
        int r = STABILIZER_DISTANCE;
        if (x == 0 && y == -CONTROLLER_DROP && z == -r) return '@';

        boolean onAxis = (x == 0 ? 0 : 1) + (y == 0 ? 0 : 1) + (z == 0 ? 0 : 1) <= 1;
        int axisDistance = Math.abs(x) + Math.abs(y) + Math.abs(z);
        if (onAxis && axisDistance == r) return 'S';
        if (onAxis && axisDistance < r) return '#';
        if (x * x + y * y + z * z <= HOLLOW_RADIUS * HOLLOW_RADIUS) return '#';
        if ((x == 0 && isOnRing(y, z)) || (y == 0 && isOnRing(x, z)) || (z == 0 && isOnRing(x, y))) return 'C';
        return ' ';
    }

    private static boolean isOnRing(int a, int b) {
        return Math.round(Math.sqrt(a * a + b * b)) == STABILIZER_DISTANCE;
    }

    public static void init() {}
}
