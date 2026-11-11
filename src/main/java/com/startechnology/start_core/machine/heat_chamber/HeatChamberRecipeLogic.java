package com.startechnology.start_core.machine.heat_chamber;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.ActionResult;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class HeatChamberRecipeLogic extends RecipeLogic {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            HeatChamberRecipeLogic.class,
            RecipeLogic.MANAGED_FIELD_HOLDER);

    private final HeatChamberMachine heatChamberMachine;

    public HeatChamberRecipeLogic(HeatChamberMachine machine) {
        super(machine);
        this.heatChamberMachine = machine;
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public static boolean isHeatingRecipe(GTRecipe recipe) {
        return recipe != null && recipe.data.getBoolean("is_heating_recipe");
    }

    public static int getMinTemp(GTRecipe recipe) {
        return (recipe == null || !recipe.data.contains("min_temp")) ? 0 : recipe.data.getInt("min_temp");
    }

    public static GTRecipe createHeatingRecipe(GTRecipe modifiedTargetRecipe, int heatingRate, int targetTemp) {
        GTRecipe heatingRecipe = modifiedTargetRecipe.getEUCWUOnlyTickRecipe().copy();

        heatingRecipe.inputs.clear();
        heatingRecipe.outputs.clear();
        heatingRecipe.duration = 100;
        heatingRecipe.data = heatingRecipe.data.copy();
        heatingRecipe.data.remove(GTRecipe.tickPeriodKey);
        heatingRecipe.data.putBoolean("is_heating_recipe", true);
        heatingRecipe.data.putInt("heating_rate", heatingRate);
        heatingRecipe.data.putInt("target_temp", targetTemp);

        return heatingRecipe;
    }

    @Override
    public void findAndHandleRecipe() {
        this.lastFailedMatches = null;

        if (!this.recipeDirty && this.lastRecipe != null && !isHeatingRecipe(this.lastRecipe)) {
            int minTemp = getMinTemp(this.lastRecipe);
            if (minTemp <= this.heatChamberMachine.getTemperature() && checkRecipe(this.lastRecipe).isSuccess()) {
                GTRecipe recipe = this.lastRecipe;
                this.lastRecipe = null;
                this.lastOriginRecipe = null;
                this.setupRecipe(recipe);

                if (this.lastRecipe != null && getStatus() == Status.WORKING) {
                    this.heatChamberMachine.onStartWorkingRecipe(minTemp);
                }

                this.recipeDirty = false;

                return;
            }
        }

        this.failureReasonMap.clear();
        this.lastRecipe = null;
        this.lastOriginRecipe = null;
        this.handleSearchingRecipes(this.searchRecipe());
        this.recipeDirty = false;
    }

    @Override
    protected void handleSearchingRecipes(Iterator<GTRecipe> iterator) {
        GTRecipe bestHeatingRaw = null;
        GTRecipe bestHeatingModified = null;
        int bestHeatingMinTemp = Integer.MAX_VALUE;

        while (iterator.hasNext()) {
            GTRecipe rawRecipe = iterator.next();
            if (rawRecipe == null) continue;

            ActionResult conditionResult = RecipeHelper.checkConditions(rawRecipe, this);
            if (!conditionResult.isSuccess()) {
                if (conditionResult.reason() != null) {
                    putFailureReason(this, rawRecipe, conditionResult.reason());
                }
                continue;
            }

            GTRecipe modifiedRecipe = this.machine.fullModifyRecipe(rawRecipe.copy());
            if (modifiedRecipe == null) {
                continue;
            }

            if (this.heatChamberMachine.getAchievableParallels(rawRecipe, modifiedRecipe) <= 0) {
                continue;
            }

            ActionResult checkResult = checkRecipe(modifiedRecipe);
            if (!checkResult.isSuccess()) {
                if (checkResult.reason() != null) {
                    putFailureReason(this, rawRecipe, checkResult.reason());
                }
                continue;
            }

            int minTemp = Math.max(getMinTemp(rawRecipe), getMinTemp(modifiedRecipe));
            if (minTemp <= this.heatChamberMachine.getTemperature()) {
                this.setupRecipe(modifiedRecipe);
                if (this.lastRecipe != null && getStatus() == Status.WORKING) {
                    this.lastOriginRecipe = rawRecipe;
                    this.lastFailedMatches = null;
                    this.heatChamberMachine.onStartWorkingRecipe(minTemp);
                    return;
                }
            } else if (minTemp < bestHeatingMinTemp &&
                    this.heatChamberMachine.calculateHeatingRate(rawRecipe, modifiedRecipe) > 0) {
                        bestHeatingMinTemp = minTemp;
                        bestHeatingRaw = rawRecipe;
                        bestHeatingModified = modifiedRecipe;
                    }
        }

        if (bestHeatingModified != null && bestHeatingRaw != null) {
            int rate = this.heatChamberMachine.calculateHeatingRate(bestHeatingRaw, bestHeatingModified);
            if (rate > 0) {
                GTRecipe heatingRecipe = createHeatingRecipe(bestHeatingModified, rate, bestHeatingMinTemp);
                ActionResult checkHeating = this.checkRecipe(heatingRecipe);

                if (checkHeating.isSuccess()) {
                    this.setupRecipe(heatingRecipe);

                    if (this.lastRecipe != null && getStatus() == Status.WORKING) {
                        this.lastOriginRecipe = null;
                        this.lastFailedMatches = null;
                        this.heatChamberMachine.onStartHeatingRecipe(bestHeatingMinTemp, rate);
                        return;
                    }
                }
            }
        }

        this.heatChamberMachine.updateCoolingSubscription();
    }

    @Override
    public void onRecipeFinish() {
        if (this.lastRecipe != null && isHeatingRecipe(this.lastRecipe)) {
            this.machine.afterWorking();
            this.runAttempt = 0;
            this.runDelay = 0;

            if (this.suspendAfterFinish) {
                this.setStatus(Status.SUSPEND);
                this.consecutiveRecipes = 0;
                this.progress = 0;
                this.duration = 0;
                this.isActive = false;
                this.lastRecipe = null;
                this.lastOriginRecipe = null;
                this.heatChamberMachine.updateCoolingSubscription();
                return;
            }

            this.lastRecipe = null;
            this.lastOriginRecipe = null;
            this.markLastRecipeDirty();
            this.findAndHandleRecipe();

            if (this.lastRecipe == null) {
                this.setStatus(Status.IDLE);
                this.consecutiveRecipes = 0;
                this.progress = 0;
                this.duration = 0;
                this.isActive = false;
                this.heatChamberMachine.updateCoolingSubscription();
            }
            return;
        }

        super.onRecipeFinish();

        if (this.getStatus() == Status.IDLE && !this.suspendAfterFinish) {
            this.markLastRecipeDirty();
            this.findAndHandleRecipe();
        }
    }
}
