package com.startechnology.start_core.machine.boosting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.startechnology.start_core.machine.modular.StarTModularInterfaceHatchPartMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
    public static final int T1_COMBUSTION_MODULE = GTValues.LuV;
    public static final int T2_COMBUSTION_MODULE = GTValues.ZPM;
    public static final int T1_ROCKET_MODULE = GTValues.UV;
    public static final int T2_ROCKET_MODULE = GTValues.UHV;

    private int tier;
    private boolean isActiveBoosting;
    private int runningTimer = 0;

    private final List<ResourceLocation> acceptedFrameIds;

    private Material LUBRICANT = GTMaterials.get("lubricant");
    private Material WS2_FLUID = GTMaterials.get("tungsten_disulfide");//t2 Lube

    private Material T1COXIDIZER = GTMaterials.get("water"); //placeholder Oxidizer
    private Material T2COXIDIZER = GTMaterials.get("neon");  //placeholder Oxidizer
    private Material T1ROXIDIZER = GTMaterials.get("helium");  //placeholder Oxidizer
    private Material T2ROXIDIZER = GTMaterials.get("hydrogen"); //placeholder Oxidizer

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
                return 3;
            case T2_ROCKET_MODULE:
                return 3;
            default:
                return 1;
        }
    }

    private long getBaseEUGeneration(){
        switch(this.tier){
            case T1_COMBUSTION_MODULE:
                return GTValues.V[T1_COMBUSTION_MODULE];
            case T2_COMBUSTION_MODULE:
                return GTValues.V[T2_COMBUSTION_MODULE];
            case T1_ROCKET_MODULE:
                return GTValues.V[T1_ROCKET_MODULE];
            case T2_ROCKET_MODULE:
                return GTValues.V[T2_ROCKET_MODULE];
            default:
                return GTValues.V[GTValues.IV];


        }
    }

    @Override
    protected @NotNull GTRecipe getLubricantRecipe() {
        return getAvailableLubricant();
    }

    private GTRecipe getActiveBoostingRecipe() {
        switch(this.tier) {
            case T1_COMBUSTION_MODULE:
                return GTRecipeBuilder.ofRaw().inputFluids(T1COXIDIZER.getFluid(1500)).buildRawRecipe();
            case T2_COMBUSTION_MODULE:
                return GTRecipeBuilder.ofRaw().inputFluids(T2COXIDIZER.getFluid(1500)).buildRawRecipe();
            case T1_ROCKET_MODULE:
                return GTRecipeBuilder.ofRaw().inputFluids(T1ROXIDIZER.getFluid(1500)).buildRawRecipe();
            case T2_ROCKET_MODULE:
                return GTRecipeBuilder.ofRaw().inputFluids(T2ROXIDIZER.getFluid(1500)).buildRawRecipe();
            default:
                return GTRecipeBuilder.ofRaw().buildRawRecipe();
        }
    }

    private GTRecipe getAvailableLubricant() {
        switch(this.tier) {
            case T1_COMBUSTION_MODULE, T2_COMBUSTION_MODULE:
                return GTRecipeBuilder.ofRaw().inputFluids(LUBRICANT.getFluid(1)).buildRawRecipe();
            case T1_ROCKET_MODULE, T2_ROCKET_MODULE:
                return GTRecipeBuilder.ofRaw().inputFluids(WS2_FLUID.getFluid(1)).buildRawRecipe();
            default:
                return GTRecipeBuilder.ofRaw().buildRawRecipe();
        }
    }

    private double getBonus() {
        if (this.isActiveBoosting) {
            return 4;
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

    //evil >:(
    @Override
    public void addDisplayText(List<Component> textList) {
        MultiblockDisplayText.Builder builder = MultiblockDisplayText.builder(textList, this.isFormed()).setWorkingStatus(this.recipeLogic.isWorkingEnabled(), this.recipeLogic.isActive());
        long lastEUt = this.recipeLogic.getLastRecipe() != null ? this.recipeLogic.getLastRecipe().getOutputEUt().getTotalEU() : 0L;
        if (getParallelBonus()==3) //t1 Rocket and Combustion modules parallel bonus
            builder.addEnergyProductionLine(GTValues.V[this.tier + 1]*3, lastEUt);//t1 Rocket and Combustion modules * Parallel bonus
        else
            builder.addEnergyProductionLine(GTValues.V[this.tier + 1]*6, lastEUt);//t2 Rocket and Combustion modules * Parallel bonus

        if (this.isActive() && this.isWorkingEnabled()) {
            builder.addCurrentEnergyProductionLine(lastEUt);
        }

//        if (!this.recipeLogic.isWaiting()) {
//            builder.addFuelNeededLine(this.getRecipeFluidInputInfo(), this.recipeLogic.getDuration());
//        }

        if (isFormed()) {
            boolean oxidizerBoosted = RecipeHelper.matchRecipe(this, getActiveBoostingRecipe()).isSuccess();
            if (oxidizerBoosted) {
                textList.add(Component.translatable("start_core.multiblock.boosted_combustion_oxidizer").withStyle(ChatFormatting.DARK_AQUA));
            }
        }
        builder.addWorkingStatusLine();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}