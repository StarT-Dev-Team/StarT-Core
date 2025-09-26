package com.startechnology.start_core.machine.boosting;

import java.math.BigDecimal;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.machine.multiblock.generator.LargeCombustionEngineMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.generator.LargeTurbineMachine;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import dev.architectury.platform.Mod;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class BoostedPlasmaTurbine extends LargeTurbineMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            BoostedPlasmaTurbine.class, LargeTurbineMachine.MANAGED_FIELD_HOLDER);

    private static final int SUPREME_TURBINE_TIER = GTValues.UHV;
    private static final int NYINSANE_TURBINE_TIER = GTValues.UIV;

    private Integer tier;
    private boolean isActiveBoosting;
    private boolean isPassiveBoosting;
    private int runningTimer = 0;

    public BoostedPlasmaTurbine(IMachineBlockEntity holder, int tier) {
        super(holder, GTValues.IV);
        this.tier = tier;
    }
    

    private Material WS2_FLUID = GTMaterials.get("tungsten_disulfide");
    private Material SS_HE3_FLUID = GTMaterials.get("superstate_helium_3");
    private Material BEC_OG_FLUID = GTMaterials.get("bec_og");

    private Integer getParallelBonus() {
        switch(this.tier) {
            case SUPREME_TURBINE_TIER:
                return 6;
            case NYINSANE_TURBINE_TIER:
                return 12;
            default: 
                return 1;
        }
    }

    /* Boosting bonus while active/passive boosting. */
    private double getBoostingBonus() {
        switch(this.tier) {
            case SUPREME_TURBINE_TIER:
                return isActiveBoosting ? 2 : 1.25;
            case NYINSANE_TURBINE_TIER:
                return isActiveBoosting ? 3 : 1.5;
            default: 
                return 1;
        }
    }

    /* Loss when not passive boosting */
    private double getNonBoostingBonus() {
        switch(this.tier) {
            case SUPREME_TURBINE_TIER:
                return 0.9;
            case NYINSANE_TURBINE_TIER:
                return 0.8;
            default: 
                return 1;
        }
    }

    /* Passive boosting recipe */
    private GTRecipe getPassiveBoostingRecipe() {
        switch(this.tier) {
            case SUPREME_TURBINE_TIER:
                return GTRecipeBuilder.ofRaw().inputFluids(WS2_FLUID.getFluid(2000)).buildRawRecipe();
            case NYINSANE_TURBINE_TIER:
                return GTRecipeBuilder.ofRaw().inputFluids(WS2_FLUID.getFluid(5000)).buildRawRecipe();
            default: 
                return GTRecipeBuilder.ofRaw().buildRawRecipe();
        }
    }

    /* Active boosting recipe */
    private GTRecipe getActiveBoostingRecipe() {
        switch(this.tier) {
            case SUPREME_TURBINE_TIER:
                return GTRecipeBuilder.ofRaw().inputFluids(SS_HE3_FLUID.getFluid(2500)).buildRawRecipe();
            case NYINSANE_TURBINE_TIER:
                return GTRecipeBuilder.ofRaw().inputFluids(BEC_OG_FLUID.getFluid(800)).buildRawRecipe();
            default: 
                return GTRecipeBuilder.ofRaw().buildRawRecipe();
        }
    }

    private double getBonus() {
        // Bonus of this turbine
        if (this.isPassiveBoosting) {
            return this.getBoostingBonus();
        } else {
            return this.getNonBoostingBonus();
        }
    }

    @Override
    public long getOverclockVoltage() {
        long largeTurbineOCVoltage = super.getOverclockVoltage();

        // wow i love losing precision !! content modifier does double cast anyway so we would lose precision there
        return (long)(((double)largeTurbineOCVoltage) * this.getParallelBonus());
    }

    public ModifierFunction getModifierFunction() {
        return ModifierFunction.builder()
            .eutMultiplier(this.getBonus())
            .build();
    }

    /* return component for description of active boosting being ran */
    private MutableComponent getActiveBoostingComponent() {
        switch(this.tier) {
            case SUPREME_TURBINE_TIER:
                return Component.translatable("start_core.multiblock.supreme_turbine.ss_h32_boosting").withStyle(ChatFormatting.YELLOW);
            case NYINSANE_TURBINE_TIER:
                return Component.translatable("start_core.multiblock.nyinsane_turbine.bec_og_boosting").withStyle(ChatFormatting.LIGHT_PURPLE);
            default: 
                return Component.empty();
        }
    }


    /* return component for description of passive boosting being ran */
    private MutableComponent getPassiveBoostingComponent() {
        return Component.translatable("start_core.multiblock.boosted_plasma_turbine.ws2_boosting").withStyle(ChatFormatting.GREEN);
    }

    /* return component for description of noot passive boosting being ran */
    private MutableComponent getNotPassiveBoostingComponent() {
        return Component.translatable("start_core.multiblock.boosted_plasma_turbine.no_ws2_boosting").withStyle(ChatFormatting.RED);
    }

    /**
     * Recipe modifier for the Boosted Plasma Turbines
     * 
     * @param machine a {@link BoostedPlasmaTurbine}
     * @param recipe  recipe
     * @return A {@link ModifierFunction} for the given Boosted Plasma Turbine
     */
    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof BoostedPlasmaTurbine engineMachine)) {
            return RecipeModifier.nullWrongType(BoostedPlasmaTurbine.class, machine);
        }

        // Output of this plasma turbine
        long EUt = RecipeHelper.getOutputEUt(recipe);
        
        if (EUt > 0) {
            return engineMachine.getModifierFunction();
        }

        return ModifierFunction.NULL;
    }

    @Override
    public boolean onWorking() {
        boolean value = super.onWorking();

        // check every 3.6s 1000 times = 1hr
        if (runningTimer % 72 == 0) {
            // passive boosting recipe.
            GTRecipe passiveBoosterRecipe = getPassiveBoostingRecipe();
            this.isPassiveBoosting = passiveBoosterRecipe.matchRecipe(this).isSuccess() &&
                    passiveBoosterRecipe.handleRecipeIO(IO.IN, this, this.recipeLogic.getChanceCaches());

            // active boosting recipe, only if passive is running
            if (this.isPassiveBoosting) {
                GTRecipe activeBoosterRecipe  = getActiveBoostingRecipe();
                this.isActiveBoosting = activeBoosterRecipe.matchRecipe(this).isSuccess() &&
                        activeBoosterRecipe.handleRecipeIO(IO.IN, this, this.recipeLogic.getChanceCaches());
            }
        }

        runningTimer++;
        if (runningTimer > 72000) runningTimer %= 72000; // reset once every hour of running

        return value;
    }

    /// gui stuff
    
    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        if (isFormed()) {

            if (isActive()) {
                long maxProduction = (long)((double)getOverclockVoltage() * this.getBonus());
                long currentProduction = isActive() && recipeLogic.getLastRecipe() != null ? RecipeHelper.getOutputEUt(recipeLogic.getLastRecipe()) : 0;

                // glglglblglblgblgblglblgblglblgblgb
                textList.remove(3);
                textList.add(3, Component.translatable("gtceu.multiblock.turbine.energy_per_tick", FormattingUtil.formatNumbers(currentProduction), FormattingUtil.formatNumbers(maxProduction)));
            }


            if (isPassiveBoosting) {
                textList.add(getPassiveBoostingComponent());
            } else {
                textList.add(getNotPassiveBoostingComponent());
            }

            if (isActiveBoosting) {
                textList.add(getActiveBoostingComponent());
            }
        }
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
