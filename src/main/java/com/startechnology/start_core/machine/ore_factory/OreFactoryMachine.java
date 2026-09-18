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
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class OreFactoryMachine extends WorkableElectricMultiblockMachine {

    public static final TagKey<Item> CRUSHED_ORE = TagPrefix.crushed.getItemParentTags()[0];

    public static Map<Material, FluidStats> FLUID_STATS = Map.of(
            GTMaterials.Water, new FluidStats(ChatFormatting.BLUE, 100, 1.0, 1.0),
            GTMaterials.DistilledWater, new FluidStats(ChatFormatting.DARK_AQUA, 80, 0.9, 1.1),
            GTMaterials.Mercury, new FluidStats(ChatFormatting.DARK_BLUE, 50, 1.2, 0.8),
            GTMaterials.SodiumPersulfate, new FluidStats(ChatFormatting.BLUE, 25, 0.75, 1.25));

    public record FluidStats(ChatFormatting color, int amount, double durationMultiplier, double euMultiplier) {}

    private Material activeFluid;

    @Persisted
    private int runningTimer = 0;

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
        for (var entry : FLUID_STATS.entrySet()) {
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

        FluidStats stats = FLUID_STATS.get(activeFluid);

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
        boolean value = super.onWorking();

        runningTimer++;

        // 100 ticks = 5 seconds.
        if (runningTimer > 100) {
            runningTimer %= 100;
            return consumeFluid();
        }

        return value;
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

        if (!oreFactory.hasCrushedOre()) {
            return ModifierFunction.NULL;
        }

        if (oreFactory.hasCrushedOre() && !oreFactory.selectFluid()) {
            return ModifierFunction.cancel(Component.translatable("ui.start_core.orefactory.no_fluid"));
        }

        FluidStats stats = FLUID_STATS.get(oreFactory.activeFluid);

        return ModifierFunction.builder()
                .durationMultiplier(stats.durationMultiplier)
                .eutMultiplier(stats.euMultiplier)
                .build();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        if (isFormed()) {

            if (this.activeFluid == null) {
                textList.add(Component.translatable("ui.start_core.orefactory.no_fluid"));
                return;
            }

            FluidStats stats = FLUID_STATS.get(this.activeFluid);
            textList.add(Component.translatable("ui.start_core.orefactory.active_fluid",
                    stats.color() + this.activeFluid.getName()));
            textList.add(Component.translatable("ui.start_core.orefactory.consumption", stats.amount()));
            textList.add(Component.translatable("ui.start_core.orefactory.duration", stats.durationMultiplier));
            textList.add(Component.translatable("ui.start_core.orefactory.power_discount", stats.euMultiplier));
        }
    }
}
