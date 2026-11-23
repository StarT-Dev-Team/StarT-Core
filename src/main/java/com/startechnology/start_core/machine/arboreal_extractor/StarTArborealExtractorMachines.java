package com.startechnology.start_core.machine.arboreal_extractor;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.WidgetUtils;
import com.gregtechceu.gtceu.api.gui.editor.EditableMachineUI;
import com.gregtechceu.gtceu.api.gui.texture.UVSpriteTexture;
import com.gregtechceu.gtceu.api.gui.widget.ProgressTextureBar;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.MultiblockShapeInfo;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.machine.multiblock.primitive.PrimitiveWorkableMachine;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.SupplierDataSource;
import com.lowdragmc.lowdraglib2.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.data.FillDirection;
import com.lowdragmc.lowdraglib2.gui.ui.elements.FluidSlot;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ItemSlot;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.block.arboreal_extractor.ArborealBlocks;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;

import dev.vfyjxf.taffy.style.TaffyPosition;

import java.util.ArrayList;

public class StarTArborealExtractorMachines {

    public static final MultiblockMachineDefinition LARGE_ARBOREAL_EXTRACTOR = StarTCore.START_REGISTRATE
            .multiblock("large_arboreal_extractor", ArborealExtractorMachine::new)
            .langValue("Large Arboreal Extractor [LAE]")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(StarTRecipeTypes.ARBOREAL_EXTRACTION_RECIPES)
            .appearanceBlock(GTBlocks.CASING_PRIMITIVE_BRICKS)
            .pattern((definition) -> FactoryBlockPattern.start()
                    .aisle("BOB", "SSS", "   ", "   ", "   ", " L ", "   ")
                    .aisle("BDB", "BGB", " G ", " G ", " G ", "LGL", " L ")
                    .aisle("BIB", "BCB", "SSS", "   ", "   ", " L ", "   ")
                    .where('C', Predicates.controller(Predicates.blocks(definition.get())))
                    .where('B', Predicates.blocks(Blocks.BRICKS))
                    .where('O', Predicates.ability(PartAbility.EXPORT_FLUIDS).or(Predicates.blocks(Blocks.BRICKS)))
                    .where('I', Predicates.ability(PartAbility.IMPORT_ITEMS).or(Predicates.blocks(Blocks.BRICKS)))
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
                        .where('I', GTMachines.ITEM_IMPORT_BUS[GTValues.ULV], Direction.NORTH)
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
                    () -> {
                        var template = new UIElement();
                        template.layout(layout -> layout.width(166).height(100));
                        template.style(style -> style.background(GuiTextures.PRIMITIVE_BACKGROUND));

                        var progress = ProgressTextureBar.of(GuiTextures.PROGRESS_BAR_ARROW,
                                FillDirection.LEFT_TO_RIGHT, 20, 18);
                        progress.setId("progress");
                        progress.layout(layout -> layout.positionType(TaffyPosition.ABSOLUTE).left(82).top(38));

                        var slot0 = new ItemSlot();
                        slot0.setId("slot_0");
                        slot0.layout(layout -> layout.positionType(TaffyPosition.ABSOLUTE).left(34).top(38));
                        slot0.style(style -> style.backgroundTexture(GuiTextures.PRIMITIVE_SLOT));

                        var slot1 = new ItemSlot();
                        slot1.setId("slot_1");
                        slot1.layout(layout -> layout.positionType(TaffyPosition.ABSOLUTE).left(52).top(38));
                        slot1.style(style -> style.backgroundTexture(GuiTextures.PRIMITIVE_SLOT));

                        var tank = new FluidSlot();
                        tank.setId("tank");
                        tank.setAllowClickDrained(false);
                        tank.slotStyle(style -> style.fillDirection(FillDirection.ALWAYS_FULL));
                        tank.layout(layout -> layout.positionType(TaffyPosition.ABSOLUTE).left(114).top(38));
                        tank.style(style -> style.backgroundTexture(new GuiTextureGroup(GuiTextures.PRIMITIVE_SLOT,
                                UVSpriteTexture.of(GuiTextures.PRIMITIVE_LARGE_FLUID_TANK_OVERLAY, 0, 0.04f, 1,
                                        0.22f))));

                        template.addChildren(progress, slot0, slot1, tank);
                        return template;
                    }, (template, metaMachine) -> {
                        var machine = (PrimitiveWorkableMachine) metaMachine;

                        WidgetUtils.widgetByIdForEach(template, "^progress$", ProgressTextureBar.class,
                                progress -> progress.bindDataSource(SupplierDataSource
                                        .of(() -> (float) machine.getRecipeLogic().getProgressPercent())));
                        WidgetUtils.widgetByIdForEach(template, "^slot_[0-9]+$", ItemSlot.class, slot -> {
                            var index = WidgetUtils.widgetIdIndex(slot);
                            if (index >= 0 && index < machine.importItems.storage.getSlots()) {
                                slot.bind(machine.importItems.storage, index);
                            }
                        });
                        WidgetUtils.widgetByIdForEach(template, "^tank$", FluidSlot.class,
                                tank -> tank.bind(machine.exportFluids.getStorages()[0], 0));
                    }))
            .register();

    public static void init() {}
}
