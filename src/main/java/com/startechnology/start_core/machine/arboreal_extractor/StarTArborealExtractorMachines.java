package com.startechnology.start_core.machine.arboreal_extractor;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.editor.EditableMachineUI;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.gui.widget.TankWidget;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.machine.multiblock.primitive.PrimitiveWorkableMachine;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.widget.ProgressWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.block.arboreal_extractor.ArborealBlocks;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;

public class StarTArborealExtractorMachines {

    public static final MultiblockMachineDefinition ARBOREAL_EXTRACTOR = StarTCore.START_REGISTRATE
            .multiblock("large_arboreal_extractor", ArborealExtractorMachine::new)
            .langValue("Arboreal Extractor [AEx]")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(StarTRecipeTypes.ARBOREAL_EXTRACTOR_RECIPES)
            .appearanceBlock(GTBlocks.CASING_PRIMITIVE_BRICKS)
            .pattern((definition) -> FactoryBlockPattern.start()
                    .aisle("BOB", "SSS", "   ", "   ", "   ", " L ", "   ")
                    .aisle("BDB", "BGB", " G ", " G ", " G ", "LGL", " L ")
                    .aisle("BIB", "BCB", "SSS", "   ", "   ", " L ", "   ")
                    .where('C', Predicates.controller(Predicates.blocks(definition.get())))
                    .where('B', Predicates.blocks(Blocks.BRICKS))
                    .where('O', Predicates.ability(PartAbility.EXPORT_FLUIDS).or(Predicates.blocks(Blocks.BRICKS)))
                    .where('I', Predicates.ability(PartAbility.IMPORT_FLUIDS).or(Predicates.blocks(Blocks.BRICKS)))
                    .where('S', Predicates.blocks(Blocks.BRICK_SLAB))
                    .where('L', StarTArborealPredicates.leaves())
                    .where('G', StarTArborealPredicates.logs())
                    .where('D', Predicates.blocks(Blocks.DIRT))
                    .where(' ', Predicates.any())
                    .build())
            .shapeInfos(definition -> {
                var shapes = new ArrayList<MultiblockShapeInfo>();
                var pattern = MultiblockShapeInfo.builder()
                        .aisle("BIB", "BCB", "SSS", "   ", "   ", " L ", "   ")
                        .aisle("BDB", "BGB", " G ", " G ", " G ", "LGL", " L ")
                        .aisle("BOB", "SSS", "   ", "   ", "   ", " L ", "   ")
                        .where('C', definition, Direction.NORTH)
                        .where('B', Blocks.BRICKS.defaultBlockState())
                        .where('I', GTMachines.FLUID_IMPORT_HATCH[GTValues.ULV], Direction.NORTH)
                        .where('O', GTMachines.FLUID_EXPORT_HATCH[GTValues.ULV], Direction.SOUTH)
                        .where('S', Blocks.BRICK_SLAB.defaultBlockState())
                        .where('D', Blocks.DIRT.defaultBlockState())
                        .where(' ', Blocks.AIR.defaultBlockState());

                for (var tree : ArborealBlocks.TREES) {
                    shapes.add(pattern.shallowCopy()
                            .where('L', tree.getLeaves().get())
                            .where('G', tree.getLog().get())
                            .build());
                }
                return shapes;
            })
            .workableCasingModel(new ResourceLocation("minecraft:block/bricks"),
                    new ResourceLocation("gtceu:block/machines/extractor"))
            .editableUI(new EditableMachineUI("primitive", new ResourceLocation("gtceu:arboreal_extractor"),
                    WidgetGroup::new, (template, metaMachine) -> {
                        var machine = (PrimitiveWorkableMachine) metaMachine;

                        template.setSize(166, 100);
                        template.setBackground(GuiTextures.PRIMITIVE_BACKGROUND);

                        template.addWidget(new ProgressWidget(
                                () -> machine.getRecipeLogic().getProgressPercent(),
                                82, 38, 20, 18));

                        template.addWidget(new SlotWidget(machine.importItems.storage, 0, 34, 38, true, true)
                                .setBackground(GuiTextures.PRIMITIVE_SLOT));
                        template.addWidget(new SlotWidget(machine.importItems.storage, 1, 52, 38, true, true)
                                .setBackground(GuiTextures.PRIMITIVE_SLOT));

                        template.addWidget(new TankWidget(machine.exportFluids.getStorages()[0], 114, 38, true, false)
                                .setBackground(new GuiTextureGroup(GuiTextures.PRIMITIVE_SLOT,
                                        GuiTextures.PRIMITIVE_LARGE_FLUID_TANK_OVERLAY.getSubTexture(0, 0.04, 1,
                                                0.22))));
                    }))
            .register();

    public static void init() {}
}
