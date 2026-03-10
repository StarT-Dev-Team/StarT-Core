package com.startechnology.start_core.machine.boosting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.startechnology.start_core.machine.modular.StarTModularInterfaceHatchPartMachine;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.machine.multiblock.generator.LargeCombustionEngineMachine;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

public class ModularCombustionBoosting extends LargeCombustionEngineMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            ModularCombustionBoosting.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);
    public static final int T1_COMBUSTION_MODULE = GTValues.IV;
    public static final int T2_COMBUSTION_MODULE = GTValues.LuV;
    public static final int T1_ROCKET_MODULE = GTValues.ZPM;
    public static final int T2_ROCKET_MODULE = GTValues.UV;

    private int tier;
    private boolean isActiveBoosting;
    private int runningTimer = 0;

    private final List<ResourceLocation> acceptedFrameIds;

    private Material LUBRICANT = GTMaterials.get("lubricant");
    private Material OXIDIZER =  GTMaterials.get("water");

    public ModularCombustionBoosting(IMachineBlockEntity holder, int tier, ResourceLocation... acceptedFrameIds) {
        super(holder, tier);
        this.tier = tier;
        this.acceptedFrameIds = List.copyOf(Arrays.asList(acceptedFrameIds));
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        addFrameIds(acceptedFrameIds);
    }

    @Override
    public void onStructureInvalid() {
        addFrameIds(Collections.emptyList());
        super.onStructureInvalid();
    }

    private void addFrameIds(List<ResourceLocation> ids) {
        for (IMultiPart part : getParts()) {
            if (part instanceof StarTModularInterfaceHatchPartMachine hatch && hatch.isTerminal()) {
                hatch.setSupportedModules(ids);
            }
        }
    }
    private Integer getParallelBonus() {
        switch(this.tier) {
            case T1_COMBUSTION_MODULE:
                return 3;
            case T2_COMBUSTION_MODULE:
                return 6;
            case T1_ROCKET_MODULE:
                return 9;
            case T2_ROCKET_MODULE:
                return 12;
            default:
                return 1;
        }
    }

    private double getBoostingBonus() {
        switch(this.tier) {
            case T1_COMBUSTION_MODULE:
                return isActiveBoosting ? 2 : 1.25;
            case T2_COMBUSTION_MODULE:
                return isActiveBoosting ? 3 : 1.5;
            case T1_ROCKET_MODULE:
                return isActiveBoosting ? 4 : 1.75;
            case T2_ROCKET_MODULE:
                return isActiveBoosting ? 5 : 2;
            default:
                return 1;
        }
    }

    private long getBaseEUGeneration(){
        switch(this.tier){
            case T1_COMBUSTION_MODULE:
                return GTValues.V[GTValues.IV];
            case T2_COMBUSTION_MODULE:
                return GTValues.V[GTValues.LuV];
            case T1_ROCKET_MODULE:
                return GTValues.V[GTValues.ZPM];
            case T2_ROCKET_MODULE:
                return GTValues.V[GTValues.UV];
            default:
                return GTValues.V[GTValues.IV];


        }
    }

    @Override
    protected @NotNull GTRecipe getLubricantRecipe() {
        return GTRecipeBuilder.ofRaw().inputFluids(LUBRICANT.getFluid(1)).buildRawRecipe();
    }
    private GTRecipe getActiveBoostingRecipe() {
        switch(this.tier) {
            case T1_COMBUSTION_MODULE:
                return GTRecipeBuilder.ofRaw().inputFluids(OXIDIZER.getFluid(1500)).buildRawRecipe();
            case T2_COMBUSTION_MODULE:
                return GTRecipeBuilder.ofRaw().inputFluids(OXIDIZER.getFluid(2000)).buildRawRecipe();
            case T1_ROCKET_MODULE:
                return GTRecipeBuilder.ofRaw().inputFluids(OXIDIZER.getFluid(2500)).buildRawRecipe();
            case T2_ROCKET_MODULE:
                return GTRecipeBuilder.ofRaw().inputFluids(OXIDIZER.getFluid(3000)).buildRawRecipe();
            default:
                return GTRecipeBuilder.ofRaw().buildRawRecipe();
        }
    }
    private double getBonus() {
        if (this.isActiveBoosting) {
            return this.getBoostingBonus();
        } else {
            return 1;
        }
    }

    //one could say Crazyman
    public ModifierFunction getModifierFunction(long EUt) {
        int baseParallels = (int)(getBaseEUGeneration() / EUt);
        int parallels = (int)(baseParallels * getParallelBonus() * getBonus());
        double finalMultiplier = (double) getBaseEUGeneration() / EUt * getParallelBonus() * getBonus();

        return ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(parallels))
                .outputModifier(ContentModifier.multiplier(parallels))
                .eutMultiplier(finalMultiplier)
                .parallels(parallels)
                .build();
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof ModularCombustionBoosting engineMachine)) {
            return RecipeModifier.nullWrongType(ModularCombustionBoosting.class, machine);
        }

        ModifierFunction parentModifier = LargeCombustionEngineMachine.recipeModifier(machine, recipe);
        if (parentModifier == ModifierFunction.NULL) {
            return ModifierFunction.NULL;
        }

        long EUt = recipe.getOutputEUt().getTotalEU();

        if (EUt > 0) {
            return engineMachine.getModifierFunction(EUt);
        }

        return ModifierFunction.NULL;
    }


    @Override
    public boolean onWorking() {
        boolean value = super.onWorking();

        // check every 3.6s 1000 times = 1hr
        if (runningTimer % 72 == 0) {
            // passive boosting recipe.
            GTRecipe passiveBoosterRecipe = getLubricantRecipe();
            boolean isPassiveBoosting = RecipeHelper.matchRecipe(this, passiveBoosterRecipe).isSuccess() &&
                    RecipeHelper.handleRecipeIO(this, passiveBoosterRecipe, IO.IN, this.recipeLogic.getChanceCaches()).isSuccess();

            // active boosting recipe, only if passive is running
            if (isPassiveBoosting) {
                GTRecipe activeBoosterRecipe  = getActiveBoostingRecipe();
                this.isActiveBoosting = RecipeHelper.matchRecipe(this, activeBoosterRecipe).isSuccess() &&
                        RecipeHelper.handleRecipeIO(this, activeBoosterRecipe, IO.IN, this.recipeLogic.getChanceCaches()).isSuccess();
            }
        }

        runningTimer++;
        if (runningTimer > 72000) runningTimer %= 72000; // reset once every hour of running

        return value;
    }
    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}