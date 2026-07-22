package com.startechnology.start_core.machine.ore_factory;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class OreFactoryMachine extends WorkableElectricMultiblockMachine {

    private static final Material WATER = GTMaterials.get("water");
    private static final Material DISTILLED_WATER = GTMaterials.get("distilled_water");
    private static final Material MERCURY = GTMaterials.get("mercury");
    private static final Material SODIUM_PERSULFATE = GTMaterials.get("sodium_persulfate");

    public static final class FluidStats {
        public final int amount;
        public final double durationMultiplier;
        public final double euMultiplier;

        FluidStats(int amount, double durationMultiplier, double euMultiplier) {
            this.amount = amount;
            this.durationMultiplier = durationMultiplier;
            this.euMultiplier = euMultiplier;
        }
    }

    public static final Map<Material, FluidStats> FLUID_STATS = Map.of(
            WATER, new FluidStats(100, 1.0, 1.0),
            DISTILLED_WATER, new FluidStats(80, 0.9, 1.1),
            MERCURY, new FluidStats(50, 1.2, 0.8),
            SODIUM_PERSULFATE, new FluidStats(25, 0.75, 1.25)
    );

    private Material activeFluid = null;

    public OreFactoryMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    private boolean hasFluid(Material material, int amount) {
        return RecipeHelper.matchRecipe(this,
                GTRecipeBuilder.ofRaw().inputFluids(material.getFluid(amount)).buildRawRecipe()
        ).isSuccess();
    }

    private boolean selectActiveFluid() {
        for (Map.Entry<Material, FluidStats> entry : FLUID_STATS.entrySet()) {
            if (hasFluid(entry.getKey(), entry.getValue().amount)) {
                activeFluid = entry.getKey();
                return false;
            }
        }

        activeFluid = null;
        return true;
    }

    private FluidStats getActiveFluidStats() {
        return FLUID_STATS.get(activeFluid);
    }

    private boolean consumeFluid() {
        if (selectActiveFluid()) {
            return false;
        }

        FluidStats stats = getActiveFluidStats();

        GTRecipe consumeRecipe = GTRecipeBuilder.ofRaw()
                .inputFluids(activeFluid.getFluid(stats.amount))
                .buildRawRecipe();

        return RecipeHelper.handleRecipeIO(this, consumeRecipe, IO.IN, this.recipeLogic.getChanceCaches()).isSuccess();
    }

    @Override
    public boolean onWorking() {
        boolean value = super.onWorking();

        if (getLevel().isClientSide) return value;
        if (!value) return false;

        if (selectActiveFluid()) {
            return false;
        }

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
        if (!(machine instanceof OreFactoryMachine oreFactoryMachine)) {
            return RecipeModifier.nullWrongType(OreFactoryMachine.class, machine);
        }

        if (oreFactoryMachine.selectActiveFluid()) {
            return ModifierFunction.NULL;
        }

        FluidStats stats = oreFactoryMachine.getActiveFluidStats();

        return ModifierFunction.builder()
                .durationMultiplier(stats.durationMultiplier)
                .eutMultiplier(stats.euMultiplier)
                .build();
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        if (isFormed()) {
            if (activeFluid == MERCURY) {
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.mercury")); // 50mb, 1.2x Duration, 0.8x eu/t
            } else if (activeFluid == SODIUM_PERSULFATE) {
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.sodium_persulfate")); // 25mb, 0.75x Duration , 1.25x eu/t
            } else if (activeFluid == WATER) {
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.water")); //100mb 1x duration , 1x eu/t
            } else if (activeFluid == DISTILLED_WATER) {
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.distilled_water")); // 80mb, 0.9 Duration, 1.1x eu/t
            } else {
                textList.add(Component.translatable("ui.start_core.orefactory.ore_factory.no_fluid"));
            }
        }
    }
}