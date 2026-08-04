package com.startechnology.start_core.machine.gcrop;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import com.startechnology.start_core.machine.random_custom_logic.*;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import static com.gregtechceu.gtceu.common.data.machines.GTMachineUtils.ELECTRIC_TIERS;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class GCropMachines {

    public static final MachineDefinition[] GCROP_BREEDER = StarTMachineUtils.registerTieredMachines("gcrop_breeder",
            (holder, tier) -> new DirtySimpleTieredMachine(holder, tier, GTMachineUtils.defaultTankSizeFunction),
            (tier, builder) -> builder
                    .langValue("%s gCrop Breeder %s".formatted(GTValues.VLVH[tier], GTValues.VLVT[tier]))
                    .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(GTCEu.id("gcrop_breeder"),
                            StarTRecipeTypes.GCROP_BREEDER_RECIPES))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .recipeType(StarTRecipeTypes.GCROP_MUTATOR_RECIPES)
                    .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
                    .workableTieredHullModel(GTCEu.id("block/machines/extruder"))
                    .register(),
            ELECTRIC_TIERS);

    public static final MultiblockMachineDefinition GCROP_BREEDING_PLANT = START_REGISTRATE
            .multiblock("gcrop_breeder", DirtyWorkableElectricMultiblockMachine::new)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .langValue("gCrop Fertilization Array [gCFA]")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT, GTRecipeModifiers.BATCH_MODE)
            .recipeTypes(StarTRecipeTypes.GCROP_BREEDER_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("CCC", "CCC", "CCC")
                    .aisle("CCC", "C C", "CCC")
                    .aisle("CCC", "C@C", "CCC")
                    // spotless:on
                    .where(" ", Predicates.any())
                    .where("C", Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()).setMinGlobalLimited(4)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/machines/extruder"))
            .register();

    public static final MachineDefinition[] GCROP_MUTATOR = StarTMachineUtils.registerTieredMachines("gcrop_mutator",
            (holder, tier) -> new DirtySimpleTieredMachine(holder, tier, GTMachineUtils.defaultTankSizeFunction),
            (tier, builder) -> builder
                    .langValue("%s gCrop Mutator %s".formatted(GTValues.VLVH[tier], GTValues.VLVT[tier]))
                    .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(GTCEu.id("gcrop_mutator"),
                            StarTRecipeTypes.GCROP_MUTATOR_RECIPES))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .recipeType(StarTRecipeTypes.GCROP_MUTATOR_RECIPES)
                    .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
                    .workableTieredHullModel(GTCEu.id("block/machines/cutter"))
                    .register(),
            ELECTRIC_TIERS);

    public static final MultiblockMachineDefinition GCROP_MUTATION_STATION = START_REGISTRATE
            .multiblock("gcrop_mutator", DirtyWorkableElectricMultiblockMachine::new)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .langValue("Advanced Mutation Station [AMS]")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT, GTRecipeModifiers.BATCH_MODE)
            .recipeTypes(StarTRecipeTypes.GCROP_MUTATOR_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("CCC", "CCC", "CCC")
                    .aisle("CCC", "C C", "CCC")
                    .aisle("CCC", "C@C", "CCC")
                    // spotless:on
                    .where(" ", Predicates.any())
                    .where("C", Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()).setMinGlobalLimited(4)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/machines/cutter"))
            .register();

    public static final MachineDefinition[] GCROP_HARVESTER = StarTMachineUtils.registerTieredMachines(
            "gcrop_harvester",
            (holder, tier) -> new DirtySimpleTieredMachine(holder, tier, GTMachineUtils.defaultTankSizeFunction),
            (tier, builder) -> builder
                    .langValue("%s gCrop Harvester %s".formatted(GTValues.VLVH[tier], GTValues.VLVT[tier]))
                    .editableUI(SimpleTieredMachine.EDITABLE_UI_CREATOR.apply(GTCEu.id("gcrop_harvester"),
                            StarTRecipeTypes.GCROP_HARVESTER_RECIPES))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .recipeType(StarTRecipeTypes.GCROP_MUTATOR_RECIPES)
                    .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
                    .workableTieredHullModel(GTCEu.id("block/machines/bender"))
                    .register(),
            ELECTRIC_TIERS);

    public static final MultiblockMachineDefinition GCROP_HARVESTING_PLANT = START_REGISTRATE
            .multiblock("gcrop_harvester", DirtyWorkableElectricMultiblockMachine::new)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .langValue("Enlarged gCrop Harvesting Plant [EgCHP]")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT, GTRecipeModifiers.BATCH_MODE)
            .recipeTypes(StarTRecipeTypes.GCROP_HARVESTER_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("CCC", "CCC", "CCC")
                    .aisle("CCC", "C C", "CCC")
                    .aisle("CCC", "C@C", "CCC")
                    // spotless:on
                    .where(" ", Predicates.any())
                    .where("C", Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get()).setMinGlobalLimited(4)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/machines/bender"))
            .register();

    public static void init() {}
}
