package com.startechnology.start_core.machine.hellforge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IRedstoneSignalMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkTransmissionMachine;
import com.startechnology.start_core.materials.StarTHellForgeHeatingLiquids;

import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class StarTHellForgeMachine extends WorkableElectricMultiblockMachine {
    /*
     * persist/save data onto the world using NBT with the @Persisted field annotation
     */
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTHellForgeMachine.class,
        WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    protected Integer temperature;

    protected TickableSubscription tryTickSub;
    private boolean startHeatLoss;

    private boolean isWorking;

    public StarTHellForgeMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.temperature = 0;
        this.startHeatLoss = false;
        this.isWorking = false;
    }

    /* A map of fluids to their maximum heat cap for the Hell Forge */
    public static Map<Material, Integer> fluidsMap = Map.of(
        StarTHellForgeHeatingLiquids.FlamewakeSolvent, 900,
        StarTHellForgeHeatingLiquids.EmberheartNectar, 1800,
        StarTHellForgeHeatingLiquids.IgniferousElixir, 2700,
        StarTHellForgeHeatingLiquids.BlazingPhlogiston, 3600
    );

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.isWorking = false;
        this.startHeatLoss = true;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.isWorking = false;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        textList.add(
            Component.translatable("ui.start_core.hellforge_crucible", this.temperature)
        );
    }

    /**
     * Retrieves the appropriate heating fluid for the Hellforge based on the required temperature.
     * The method selects the fluid with the highest temperature cap that is still equal to or greater than the input temperature.
     *
     * @param temperature The required temperature of the Hellforge.
     * @return The Material representing the heating fluid, or null if no fluid can meet the required temperature.
     */
    public static Material getHellforgeHeatingLiquid(Integer temperature) {
        Material selectedFluid = null;
        int smallestCapAboveTemperature = Integer.MAX_VALUE;

        // Iterate over all fluid entries
        for (Map.Entry<Material, Integer> entry : fluidsMap.entrySet()) {
            int fluidCap = entry.getValue();

            // Select the fluid if its cap is >= required temperature AND is the smallest such cap found so far
            if (fluidCap >= temperature && fluidCap < smallestCapAboveTemperature) {
                smallestCapAboveTemperature = fluidCap;
                selectedFluid = entry.getKey();
            }
        }

        return selectedFluid; // May be null if no suitable fluid is found
    }
    
    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (getLevel().isClientSide)
            return;

        tryTickSub = subscribeServerTick(tryTickSub, this::tryRemoveHeat);
    }

    @Override
    public void onUnload() {
        super.onUnload();
    
        if (getLevel().isClientSide)
            return;

        if (tryTickSub != null) {
            tryTickSub.unsubscribe();
            tryTickSub = null;
        }
    }

    protected void tryRemoveHeat() {
        if (getOffsetTimer() % 20 == 0 && this.startHeatLoss) {
            // Remove 10 MK every second

            if (!this.isWorking)
                this.temperature = Math.max(this.temperature - 10, 0);

            this.temperature = Math.max(this.temperature - 1, 0);
        }
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        boolean isWorking = super.beforeWorking(recipe);

        if (isWorking) {
            this.isWorking = true;
        }

        return isWorking;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        this.isWorking = false;
        GTRecipe lastRecipe = getRecipeLogic().getLastRecipe();

        List<Content> content = lastRecipe.getInputContents(GTRecipeCapabilities.FLUID);

        if (content.size() < 1)
            return;

        if (content.get(0).getContent() instanceof FluidIngredient ingredient) {
            FluidStack ingredientFluid = ingredient.getStacks()[0];
            Material material = ChemicalHelper.getMaterial(ingredientFluid.getFluid());

            if (fluidsMap.containsKey(material)) {
                Integer maxHeat = fluidsMap.get(material);
                Integer addTemperature = ingredientFluid.getFluid().getFluidType().getTemperature() / 1_000_000;

                Integer amountToAdd = (int) Math.floor(ingredientFluid.getAmount() / 1000);
                this.temperature = Math.min(temperature + addTemperature * amountToAdd, maxHeat);
            }
        }
    }

    public Integer getCrucibleTemperature() {
        return this.temperature;
    }
}
