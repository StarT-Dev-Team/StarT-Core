package com.startechnology.start_core.machine.gcrop;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import com.startechnology.start_core.machine.random_custom_logic.*;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import net.minecraft.world.level.block.Blocks;

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
                    .recipeType(StarTRecipeTypes.GCROP_BREEDER_RECIPES)
                    .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
                    .workableTieredHullModel(GTCEu.id("block/machines/extruder"))
                    .register(),
            ELECTRIC_TIERS);

    public static final MultiblockMachineDefinition GCROP_BREEDING_PLANT = START_REGISTRATE
            .multiblock("gcrop_breeder", DirtyWorkableElectricMultiblockMachine::new)
            .appearanceBlock(GCYMBlocks.CASING_CORROSION_PROOF)
            .langValue("gCrop Fertilization Array [gCFA]")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT)
            .recipeTypes(StarTRecipeTypes.GCROP_BREEDER_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("  A   A  ", "  A   A  ", "  A   A  ", "  A   A  ", "  AAAAA  ")
                    .aisle("   BBB   ", "   BBB   ", "   BCB   ", "   BDB   ", " AABBBAA ")
                    .aisle("A BBBBB A", "A BEEEB A", "A C   C A", "A B F B A", "AABBBBBAA")
                    .aisle(" BBBBBBB ", " BEGEGEB ", " B     B ", " B FFF B ", "ABBBBBBBA")
                    .aisle(" BBBBBBB ", " BEEHEEB ", " C     C ", " DFF FFD ", "ABBBBBBBA")
                    .aisle(" BBBBBBB ", " BEGEGEB ", " B     B ", " B FFF B ", "ABBBBBBBA")
                    .aisle("A BBBBB A", "A BEEEB A", "A C   C A", "A B F B A", "AABBBBBAA")
                    .aisle("   BBB   ", "   B@B   ", "   BCB   ", "   BDB   ", " AABBBAA ")
                    .aisle("  A   A  ", "  A   A  ", "  A   A  ", "  A   A  ", "  AAAAA  ")
                    // spotless:on
                    .where("A", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.Tungsten)))
                    .where("B", Predicates.blocks(GCYMBlocks.CASING_CORROSION_PROOF.get()).setMinGlobalLimited(30)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                    .where("C", Predicates.blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                    .where("D", Predicates.blocks(GTBlocks.CASING_ENGINE_INTAKE.get()))
                    .where("E", Predicates.blocks(StarTMachineUtils.getKjsBlock("soul_infused_casing")))
                    .where("F", Predicates.blocks(GTBlocks.CASING_TITANIUM_PIPE.get()))
                    .where("G", Predicates.blocks(Blocks.FARMLAND))
                    .where("H", Predicates.blocks(Blocks.WATER))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where(" ", Predicates.any())
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/gcym/corrosion_proof_casing"),
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
            .appearanceBlock(GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING)
            .langValue("Advanced Mutation Station [AMS]")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT)
            .recipeTypes(StarTRecipeTypes.GCROP_MUTATOR_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("AAAAA", "AAAAA", "ABBBA", "ABCBA", "DBBBD", " DDD ")
                    .aisle("AAAAA", "ABBBA", "B   B", "B   B", "B   B", "DBBBD")
                    .aisle("AAAAA", "ABBBA", "B   B", "C E C", "B   B", "DBBBD")
                    .aisle("AAAAA", "ABBBA", "B   B", "B   B", "B   B", "DBBBD")
                    .aisle("AA@AA", "AAAAA", "ABBBA", "ABCBA", "DBBBD", " DDD ")
                    // spotless:on
                    .where("A",
                            Predicates.blocks(GCYMBlocks.CASING_HIGH_TEMPERATURE_SMELTING.get()).setMinGlobalLimited(15)
                                    .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                                    .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                    .where("B", Predicates.blocks(GCYMBlocks.CASING_LASER_SAFE_ENGRAVING.get()))
                    .where("C", Predicates.blocks(StarTMachineUtils.getBlock("thermal_extra:shellite_glass")))
                    .where("D",
                            Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.TungstenCarbide)))
                    .where("E", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.block, GTMaterials.NetherStar)))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where(" ", Predicates.any())
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/gcym/high_temperature_smelting_casing"),
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
                    .recipeType(StarTRecipeTypes.GCROP_HARVESTER_RECIPES)
                    .recipeModifier(GTRecipeModifiers.OC_NON_PERFECT)
                    .workableTieredHullModel(GTCEu.id("block/machines/bender"))
                    .register(),
            ELECTRIC_TIERS);

    public static final MultiblockMachineDefinition GCROP_HARVESTING_PLANT = START_REGISTRATE
            .multiblock("gcrop_harvester", DirtyWorkableElectricMultiblockMachine::new)
            .appearanceBlock(GCYMBlocks.CASING_WATERTIGHT)
            .langValue("Enlarged gCrop Harvesting Plant [EgCHP]")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT)
            .recipeTypes(StarTRecipeTypes.GCROP_HARVESTER_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("ABBBBBA", "ABBBBBA", "ACCCCCA", "ACCCCCA", " AAAAA ")
                    .aisle("BDDDDDB", "BEEEEEB", "C     C", "C     C", "ACCCCCA")
                    .aisle("BBBBBBB", "BFFFFFB", "C     C", "C     C", "ACCCCCA")
                    .aisle("BDDDDDB", "BEEEEEB", "C     C", "C     C", "ACCCCCA")
                    .aisle("ABBBBBA", "ABB@BBA", "ACCCCCA", "ACCCCCA", " AAAAA ")
                    // spotless:on
                    .where("A", Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.BlueSteel)))
                    .where("B", Predicates.blocks(GCYMBlocks.CASING_WATERTIGHT.get()).setMinGlobalLimited(30)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                    .where("C", Predicates.blocks(GTBlocks.CASING_LAMINATED_GLASS.get()))
                    .where("D", Predicates.blocks(StarTMachineUtils.getKjsBlock("soul_infused_casing")))
                    .where("E", Predicates.blocks(Blocks.FARMLAND))
                    .where("F", Predicates.blocks(Blocks.WATER))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where(" ", Predicates.any())
                    .build())
            .workableCasingModel(GTCEu.id("block/casings/gcym/watertight_casing"),
                    GTCEu.id("block/machines/bender"))
            .register();

    public static void init() {}
}
