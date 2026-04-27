package com.startechnology.start_core.machine.boosting;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
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
    public static final int T3_COMBUSTION_MODULE = GTValues.UV;
    public static final int T4_COMBUSTION_MODULE = GTValues.UEV;

    private int tier;
    private boolean isActiveBoosting;
    @Persisted
    private int runningTimer = 0;

    private final List<ResourceLocation> acceptedFrameIds;

    private Material LUBRICANT = GTMaterials.get("lubricant");
    private Material WS2_FLUID = GTMaterials.get("tungsten_disulfide");//t2 Lube

    private Material T1COXIDIZER = GTMaterials.get("water"); //placeholder for WFNA
    private Material T2COXIDIZER = GTMaterials.get("neon");  //placeholder for RFNA
    private Material T1ROXIDIZER = GTMaterials.get("helium");  //placeholder for O2F2
    private Material T2ROXIDIZER = GTMaterials.get("hydrogen"); //placeholder for FSB

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
    private int getOxidizerConsumption() {
        return (int)(Math.pow(4, tier - 1) * 4);
    }

    private double getBoostingBonus() {
        return switch (this.tier){
            case T1_COMBUSTION_MODULE -> isActiveBoosting ? 5.0 : 1; // 5A Luv || 1A Luv
            case T2_COMBUSTION_MODULE -> isActiveBoosting ? 6.0 : 1; // 6A ZPM || 1A ZPM
            case T3_COMBUSTION_MODULE -> isActiveBoosting ? 8.0 : 2; // 8A UV || 2A UV
            case T4_COMBUSTION_MODULE -> isActiveBoosting ? 12.0 : 2; // 12A UEV || 2A EUV
            default -> 1;
        };
    }
    @Override
    protected @NotNull GTRecipe getLubricantRecipe() {
        return getAvailableLubricant();
    }

    private GTRecipe getActiveBoostingRecipe() {
        return switch (this.tier) {
            case T1_COMBUSTION_MODULE -> GTRecipeBuilder.ofRaw().inputFluids(T1COXIDIZER.getFluid(getOxidizerConsumption())).buildRawRecipe();
            case T2_COMBUSTION_MODULE -> GTRecipeBuilder.ofRaw().inputFluids(T2COXIDIZER.getFluid(getOxidizerConsumption())).buildRawRecipe();
            case T3_COMBUSTION_MODULE -> GTRecipeBuilder.ofRaw().inputFluids(T1ROXIDIZER.getFluid(getOxidizerConsumption())).buildRawRecipe();
            case T4_COMBUSTION_MODULE -> GTRecipeBuilder.ofRaw().inputFluids(T2ROXIDIZER.getFluid(getOxidizerConsumption())).buildRawRecipe();
            default -> GTRecipeBuilder.ofRaw().buildRawRecipe();
        };
    }

    private GTRecipe getAvailableLubricant() {
        return switch (this.tier) {
            case T1_COMBUSTION_MODULE -> GTRecipeBuilder.ofRaw().inputFluids(LUBRICANT.getFluid(100)).buildRawRecipe();
            case T2_COMBUSTION_MODULE -> GTRecipeBuilder.ofRaw().inputFluids(LUBRICANT.getFluid(200)).buildRawRecipe();
            case T3_COMBUSTION_MODULE -> GTRecipeBuilder.ofRaw().inputFluids(WS2_FLUID.getFluid(200)).buildRawRecipe();
            case T4_COMBUSTION_MODULE -> GTRecipeBuilder.ofRaw().inputFluids(WS2_FLUID.getFluid(400)).buildRawRecipe();
            default -> GTRecipeBuilder.ofRaw().buildRawRecipe();
        };
    }

    private int boostingParallels() {
        return switch (this.tier){
            case T1_COMBUSTION_MODULE, T2_COMBUSTION_MODULE, T3_COMBUSTION_MODULE, T4_COMBUSTION_MODULE -> isActiveBoosting ? 2 : 1;
            default -> 1;
        };
    }
    //one could say Crazyman
    public ModifierFunction getModifierFunction(long recipeEUt) {
        int parallels = (int)(GTValues.V[tier] / recipeEUt);
        return ModifierFunction.builder()
                .inputModifier(ContentModifier.multiplier(parallels))
                .outputModifier(ContentModifier.multiplier(parallels))
                .eutMultiplier(parallels * getBoostingBonus())
                .parallels(parallels * boostingParallels())
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
            GTRecipe lubeRecipe = getLubricantRecipe();
            //o_o
            boolean lubed = RecipeHelper.matchRecipe(this, lubeRecipe).isSuccess(); // active boosting recipe, only if passive is running

            if (lubed) {
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

        if (this.isActive() && this.isWorkingEnabled()) {
            builder.addCurrentEnergyProductionLine(lastEUt);
        }

        if (!this.recipeLogic.isWaiting()) {
            builder.addFuelNeededLine(this.getRecipeFluidInputInfo(), this.recipeLogic.getDuration());
        }

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