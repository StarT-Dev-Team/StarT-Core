package com.startechnology.start_core.machine.ore_factory;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class OreFactoryMachine extends WorkableElectricMultiblockMachine {

    public static final TagKey<Item> CRUSHED_ORE = TagPrefix.crushed.getItemParentTags()[0];

    public static Material WATER() {
        return GTMaterials.Water;
    }

    public static Material DISTILLED_WATER() {
        return GTMaterials.DistilledWater;
    }

    public static Material MERCURY() {
        return GTMaterials.Mercury;
    }

    public static Material SODIUM_PERSULFATE() {
        return GTMaterials.SodiumPersulfate;
    }

    public static Material BASE_FLUID() {
        return WATER();
    }

    public static Map<Material, FluidStats> FLUID_STATS() {
        return Map.of(
                WATER(), new FluidStats(100, 1.0, 1.0),
                DISTILLED_WATER(), new FluidStats(80, 0.9, 1.1),
                MERCURY(), new FluidStats(50, 1.2, 0.8),
                SODIUM_PERSULFATE(), new FluidStats(25, 0.75, 1.25));
    }

    public record FluidStats(int amount, double durationMultiplier, double euMultiplier) {}

    private Material activeFluid;

    public OreFactoryMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    private boolean hasFluid(Material material, int amount) {
        GTRecipe recipe = GTRecipeBuilder.ofRaw()
                .inputFluids(material.getFluid(amount))
                .buildRawRecipe();

        return RecipeHelper.matchRecipe(this, recipe).isSuccess();
    }

    private boolean hasCrushedOre() {
        GTRecipe recipe = GTRecipeBuilder.ofRaw()
                .inputItems(CRUSHED_ORE, 1)
                .buildRawRecipe();

        return RecipeHelper.matchRecipe(this, recipe).isSuccess();
    }

    private boolean selectFluid() {
        for (var entry : FLUID_STATS().entrySet()) {
            if (hasFluid(entry.getKey(), entry.getValue().amount())) {
                activeFluid = entry.getKey();
                return true;
            }
        }

        activeFluid = null;
        return false;
    }

    private boolean consumeFluid() {
        if (!selectFluid()) return false;

        FluidStats stats = FLUID_STATS().get(activeFluid);

        GTRecipe recipe = GTRecipeBuilder.ofRaw()
                .inputFluids(activeFluid.getFluid(stats.amount()))
                .buildRawRecipe();

        return RecipeHelper.handleRecipeIO(
                this,
                recipe,
                IO.IN,
                recipeLogic.getChanceCaches()).isSuccess();
    }

    @Override
    public boolean onWorking() {
        boolean working = super.onWorking();

        if (getLevel().isClientSide || !working) return working;
        if (!hasCrushedOre() || !selectFluid()) return false;

        // 100 ticks = 5 seconds.
        if (getOffsetTimer() % 100L == 0L) {
            return consumeFluid();
        }

        return true;
    }

    @Override
    public void onStructureInvalid() {
        activeFluid = null;
        super.onStructureInvalid();
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof OreFactoryMachine oreFactory)) {
            return RecipeModifier.nullWrongType(OreFactoryMachine.class, machine);
        }

        if (!oreFactory.hasCrushedOre() || !oreFactory.selectFluid()) {
            return ModifierFunction.NULL;
        }

        FluidStats stats = FLUID_STATS().get(oreFactory.activeFluid);

        return ModifierFunction.builder()
                .durationMultiplier(stats.durationMultiplier)
                .eutMultiplier(stats.euMultiplier)
                .build();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        if (isFormed()) {
            if (activeFluid == MERCURY()) {
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.mercury"));
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.mercury_boost"));
            } else if (activeFluid == SODIUM_PERSULFATE()) {
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.sodium_persulfate"));
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.sodium_persulfate_boost"));
            } else if (activeFluid == WATER()) {
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.water"));
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.water_boost"));
            } else if (activeFluid == DISTILLED_WATER()) {
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.distilled_water"));
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.distilled_water_boost"));
            } else {
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.no_fluid"));
            }
        }
    }
}
