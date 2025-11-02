package com.startechnology.start_core.machine.threading;

import java.util.HashMap;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.startechnology.start_core.machine.parallel.IStarTAbsoluteParallelHatch;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class StarTThreadingCapableMachine extends WorkableElectricMultiblockMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StarTThreadingCapableMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    /* Map of each type to it's stats */
    @Getter
    private HashMap<String, StarTThreadingStatsPredicate.ThreadingStatsBlockTracker> stats;

    private Integer generalTotal;

    @Persisted
    private Integer assignedSpeed;
    private Integer speed;

    @Persisted
    private Integer assignedEfficiency;
    private Integer efficiency;

    @Persisted
    private Integer assignedParallels;
    private Integer parallels;

    @Persisted
    private Integer assignedThreading;
    private Integer threading;

    public StarTThreadingCapableMachine(IMachineBlockEntity holder) {
        super(holder);
        this.stats = new HashMap<>();
        this.assignedEfficiency = 0;
        this.assignedParallels = 0;
        this.assignedSpeed = 0;
        this.assignedThreading = 0;
    }

    public void resetStats() {
        this.generalTotal = 0;
        this.speed = 0;
        this.efficiency = 0;
        this.parallels = 0;
        this.threading = 0;
    }

    public void ensureAssignment() {
        // Check if we need to reduce assigned stats due to reduced capacity
        Integer remaining = getRemainingAssignable();
        if (remaining < 0) {
            Integer deficit = Math.abs(remaining);
            
            if (this.assignedSpeed > 0) {
                Integer toSubtract = Math.min(deficit, this.assignedSpeed);
                this.assignedSpeed -= toSubtract;
                deficit -= toSubtract;
            }
            
            if (deficit > 0 && this.assignedEfficiency > 0) {
                Integer toSubtract = Math.min(deficit, this.assignedEfficiency);
                this.assignedEfficiency -= toSubtract;
                deficit -= toSubtract;
            }
            
            if (deficit > 0 && this.assignedParallels > 0) {
                Integer toSubtract = Math.min(deficit, this.assignedParallels);
                this.assignedParallels -= toSubtract;
                deficit -= toSubtract;
            }
            
            if (deficit > 0 && this.assignedThreading > 0) {
                Integer toSubtract = Math.min(deficit, this.assignedThreading);
                this.assignedThreading -= toSubtract;
                deficit -= toSubtract;
            }
        }
    }

    public Integer getRemainingAssignable() {
        return this.generalTotal
                - (this.assignedEfficiency + this.assignedParallels + this.assignedSpeed + this.assignedThreading);
    }

    /* effective duration reduction in % */
    private Integer getEffectiveDurationReduction() {
        return this.assignedSpeed + this.speed;
    }

    public MutableComponent getSpeedPrettyFormat() {
        return Component.literal(LocalizationUtils.format("start_core.machine.threading_controller.speed.pretty_format",
                FormattingUtil.formatNumbers(getEffectiveDurationReduction())));
    }

    /* effective power reduction in % */
    private Integer getEffectivePowerReduction() {
        return this.assignedEfficiency + this.efficiency;
    }

    public MutableComponent getEfficiencyPrettyFormat() {
        return Component.literal(LocalizationUtils.format("start_core.machine.threading_controller.efficiency.pretty_format",
                FormattingUtil.formatNumbers(getEffectivePowerReduction())));
    }

    /* effective parallels */
    private Integer getEffectiveParallels() {
        return Math.floorDiv(this.assignedParallels + this.parallels, 5) + 1;
    }

    public MutableComponent getParallelsPrettyFormat() {
        return Component.literal(LocalizationUtils.format("start_core.machine.threading_controller.parallels.pretty_format",
                FormattingUtil.formatNumbers(getEffectiveParallels())));
    }

    /* effective threads */
    private Integer getEffectiveThreads() {
        return Math.floorDiv(this.assignedThreading + this.threading, 5) + 1;
    }

    public MutableComponent getThreadsPrettyFormat() {
        return Component.literal(LocalizationUtils.format("start_core.machine.threading_controller.threading.pretty_format",
                FormattingUtil.formatNumbers(getEffectiveThreads())));
    }

    public Integer getStatAssigned(String stat) {
        switch (stat) {
            case "speed":
                return this.assignedSpeed;
            case "efficiency":
                return this.assignedEfficiency;
            case "parallels":
                return this.assignedParallels;
            case "threading":
                return this.assignedThreading;
        }

        return -1;
    }

    public Integer getStatTotal(String stat) {
        switch (stat) {
            case "speed":
                return this.assignedSpeed + this.speed;
            case "efficiency":
                return this.assignedEfficiency + this.efficiency;
            case "parallels":
                return this.assignedParallels + this.parallels;
            case "threading":
                return this.assignedThreading + this.threading;
        }

        return -1;
    }

    public MutableComponent getPrettyFormat(String stat) {
        switch (stat) {
            case "speed":
                return getSpeedPrettyFormat();
            case "efficiency":
                return getEfficiencyPrettyFormat();
            case "parallels":
                return getParallelsPrettyFormat();
            case "threading":
                return getThreadsPrettyFormat();
        }

        return Component.literal("Invalid");
    }

    public void assignStat(String stat) {
        if ((this.getRemainingAssignable()) <= 0) {
            return;
        }

        switch (stat) {
            case "speed":
                this.assignedSpeed += 1;
                break;
            case "efficiency":
                this.assignedEfficiency += 1;
                break;
            case "parallels":
                this.assignedParallels += 1;
                break;
            case "threading":
                this.assignedThreading += 1;
                break;
        }
    }

    public void unassignStat(String stat) {
        switch (stat) {
            case "speed":
                if (this.assignedSpeed > 0) {
                    this.assignedSpeed -= 1;
                }
                break;
            case "efficiency":
                if (this.assignedEfficiency > 0) {
                    this.assignedEfficiency -= 1;
                }
                break;
            case "parallels":
                if (this.assignedParallels > 0) {
                    this.assignedParallels -= 1;
                }
                break;
            case "threading":
                if (this.assignedThreading > 0) {
                    this.assignedThreading -= 1;
                }
                break;
        }
    }

    /* return all types of stats in their string form */
    public String[] getStatTypes() {
        return new String[] { "speed", "efficiency", "parallels", "threading" };
    }

    public void increaseStats(StarTThreadingStatsPredicate.ThreadingStatsBlockTracker stats) {
        this.generalTotal += stats.general * stats.amount;
        this.speed += stats.speed * stats.amount;
        this.efficiency += stats.efficiency * stats.amount;
        this.parallels += stats.parallels * stats.amount;
        this.threading += stats.threading * stats.amount;
    }

    public void updateStats() {
        if (this.getLevel().isClientSide) {
            return;
        }

        this.resetStats();

        PatternMatchContext matchContext = this.getMultiblockState().getMatchContext();
        for (var entry : matchContext.entrySet()) {
            if (!entry.getKey().startsWith(StarTThreadingStatsPredicate.THREADING_STATS_HEADER)) {
                continue;
            }

            /* Track it in the hashmap */
            var stats = (StarTThreadingStatsPredicate.ThreadingStatsBlockTracker) entry.getValue();
            this.stats.put(stats.name.replace(StarTThreadingStatsPredicate.THREADING_STATS_HEADER, ""), stats);
            this.increaseStats(stats);
        }

        this.ensureAssignment();
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof StarTThreadingCapableMachine controller && controller.isFormed()) {
            int parallels = ParallelLogic.getParallelAmount(machine, recipe, controller.getEffectiveParallels());
            int durationModifier = Math.min(controller.getEffectiveDurationReduction(), 100);
            int energyModifier = Math.min(controller.getEffectivePowerReduction(), 100);

            //threading later

            return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels))
                .durationMultiplier((100.0 - (double)durationModifier)/100.0)
                .eutMultiplier((100.0 - (double)energyModifier)/100.0)
                .parallels(parallels)
                .build();
        }
        return ModifierFunction.IDENTITY;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.updateStats();

        this.getParts().forEach(part -> {
            if (part instanceof StarTThreadingControllerPartMachine threadingController) {
                threadingController.setAssociatedController(this);
            }
        });
    }
}
