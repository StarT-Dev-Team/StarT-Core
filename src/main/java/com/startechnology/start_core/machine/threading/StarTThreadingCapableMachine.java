package com.startechnology.start_core.machine.threading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifierList;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.AccessorOp;
import com.lowdragmc.lowdraglib.syncdata.accessor.CustomObjectAccessor;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.syncdata.payload.FriendlyBufPayload;
import com.lowdragmc.lowdraglib.syncdata.payload.ITypedPayload;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;

import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class StarTThreadingCapableMachine extends WorkableElectricMultiblockMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StarTThreadingCapableMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    /* Map of each type to it's stats */
    @Getter
    private HashMap<String, StarTThreadingStatsPredicate.ThreadingStatsBlockTracker> stats;

    @Persisted
    @DescSynced
    private Integer generalTotal;

    @Persisted
    @DescSynced
    private Integer assignedSpeed;

    @Persisted
    @DescSynced
    private Integer speed;

    @Persisted
    @DescSynced
    private Integer assignedEfficiency;

    @Persisted
    @DescSynced
    private Integer efficiency;

    @Persisted
    @DescSynced
    private Integer assignedParallels;

    @Persisted
    @DescSynced
    private Integer parallels;

    @Persisted
    @DescSynced
    private Integer assignedThreading;

    @Persisted
    @DescSynced
    private Integer threading;

    @Persisted
    @DescSynced
    @Getter
    private List<ThreadedRecipeExecution> activeThreads;


    public StarTThreadingCapableMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.stats = new HashMap<>();
        this.assignedEfficiency = 0;
        this.assignedParallels = 0;
        this.assignedSpeed = 0;
        this.assignedThreading = 0;
        this.speed = 0;
        this.efficiency = 0;
        this.parallels = 0;
        this.threading = 0;
        this.generalTotal = 0;
        this.activeThreads = new ArrayList<>();
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

        this.activeThreads = new ArrayList<>(this.activeThreads.subList(0, Math.min(getEffectiveThreads(), this.activeThreads.size())));
    }

    public Integer getRemainingAssignable() {
        return this.generalTotal
                - (this.assignedEfficiency + this.assignedParallels + this.assignedSpeed + this.assignedThreading);
    }

    /* effective duration reduction in % */
    private Integer getEffectiveDurationReduction() {
        if (this.assignedSpeed == null) this.assignedSpeed = 0;
        if (this.speed == null) this.speed = 0;
        return this.assignedSpeed + this.speed;
    }

    public MutableComponent getSpeedPrettyFormat() {
        double multiplier = calculateDurationMultiplier();
        double reductionPercent = (1 - multiplier) * 100.0; // e.g., 0.9 → 10%
        return Component.literal(LocalizationUtils.format(
                "start_core.machine.threading_controller.speed.pretty_format",
                FormattingUtil.formatNumber2Places(reductionPercent)
        ));
    }

    /* effective power reduction in % */
    private Integer getEffectivePowerReduction() {
        if (this.assignedEfficiency == null) this.assignedEfficiency = 0;
        if (this.efficiency == null) this.efficiency = 0;
        return this.assignedEfficiency + this.efficiency;
    }

    public MutableComponent getEfficiencyPrettyFormat() {
        double multiplier = calculateEnergyMultiplier();
        double reductionPercent = (1 - multiplier) * 100.0;
        return Component.literal(LocalizationUtils.format(
                "start_core.machine.threading_controller.efficiency.pretty_format",
                FormattingUtil.formatNumber2Places(reductionPercent)
        ));
    }

    /* effective parallels */
    private Integer getEffectiveParallels() {
        if (this.assignedParallels == null) this.assignedParallels = 0;
        if (this.parallels == null) this.parallels = 0;
        return Math.floorDiv(this.assignedParallels + this.parallels, 5) + 1;
    }

    public MutableComponent getParallelsPrettyFormat() {
        return Component.literal(LocalizationUtils.format("start_core.machine.threading_controller.parallels.pretty_format",
                FormattingUtil.formatNumbers(getEffectiveParallels())));
    }

    /* effective threads */
    private Integer getEffectiveThreads() {
        if (this.assignedThreading == null) this.assignedThreading = 0;
        if (this.threading == null) this.threading = 0;
        return Math.floorDiv(this.assignedThreading + this.threading, 5);
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

    public void assignStat(String stat, int amount) {
        amount = Math.min(this.getRemainingAssignable(), amount);
        if ((this.getRemainingAssignable() - amount) < 0) {
            return;
        }

        switch (stat) {
            case "speed":
                this.assignedSpeed += amount;
                break;
            case "efficiency":
                this.assignedEfficiency += amount;
                break;
            case "parallels":
                this.assignedParallels += amount;
                break;
            case "threading":
                this.assignedThreading += amount;
                break;
        }

        this.ensureAssignment();
    }

    public void unassignStat(String stat, int amount) {
        switch (stat) {
            case "speed":
                amount = Math.min(this.assignedSpeed, amount);
                if ((this.assignedSpeed - amount) >= 0) {
                    this.assignedSpeed -= amount;
                }
                break;
            case "efficiency":
                amount = Math.min(this.assignedEfficiency, amount);
                if ((this.assignedEfficiency - amount) >= 0) {
                    this.assignedEfficiency -= amount;
                }
                break;
            case "parallels":
                amount = Math.min(this.assignedParallels, amount);
                if ((this.assignedParallels - amount) >= 0) {
                    this.assignedParallels -= amount;
                }
                break;
            case "threading":
                amount = Math.min(this.assignedThreading, amount);
                if ((this.assignedThreading - amount) >= 0) {
                    this.assignedThreading -= amount;
                }
                break;
        }

        this.ensureAssignment();
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

    /**
     * Calculate duration modifier using exponential formula
     * DurationNew = (0.9975^s) * DurationCurrent
     */
    private double calculateDurationMultiplier() {
        int speedPoints = getEffectiveDurationReduction();
        return Math.pow(0.9975, speedPoints);
    }

    /**
     * Calculate energy modifier using exponential formula
     * EnergyCostNew = (0.9995^e) * EnergyCostCurrent
     */
    private double calculateEnergyMultiplier() {
        int efficiencyPoints = getEffectivePowerReduction();
        return Math.pow(0.9995, efficiencyPoints);
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof StarTThreadingCapableMachine controller && controller.isFormed()) {
            int parallels = ParallelLogic.getParallelAmount(machine, recipe, controller.getEffectiveParallels());
            double durationMultiplier = controller.calculateDurationMultiplier();
            double energyMultiplier = controller.calculateEnergyMultiplier();

            return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels))
                .durationMultiplier(durationMultiplier)
                .eutMultiplier(energyMultiplier)
                .parallels(parallels)
                .build();
        }
        return ModifierFunction.IDENTITY;
    }

    @Override
    public void onStructureInvalid() {
        this.getParts().forEach(part -> {
            if (part instanceof StarTThreadingControllerPartMachine threadingController) {
                threadingController.clearController();
            }
        });

        this.activeThreads.clear();
        super.onStructureInvalid();
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

    /**
     * Override addDisplayText to show threading information
     */
    @Override
    public void addDisplayText(List<Component> textList) {
        // Call parent to add standard display text
        super.addDisplayText(textList);
        
        // Add threading stats display
        if (isFormed()) {
            textList.add(Component.empty());
            textList.add(Component.translatable("start_core.machine.threading_controller.header"));
            textList.add(getSpeedPrettyFormat());
            textList.add(getEfficiencyPrettyFormat());
            textList.add(getParallelsPrettyFormat());
            textList.add(getThreadsPrettyFormat());

            // Display active threads using synced data
            if (activeThreads.size() > 0) {
                textList.add(Component.empty());
                textList.add(Component.translatable("start_core.machine.threading_controller.active_threads"));

                int threadNum = 1;
                for (ThreadedRecipeExecution thread : this.activeThreads) {
                    textList.add(Component.empty());
                    textList.add(Component.literal(LocalizationUtils.format("start_core.machine.threading_controller.thread_header", FormattingUtil.formatNumbers(threadNum))));
                    
                    MultiblockDisplayText.builder(textList, true)
                            .setWorkingStatus(true, true)
                            .addProgressLine(thread.totalDuration - thread.ticksRemaining, thread.totalDuration,
                                    (double)(thread.totalDuration - thread.ticksRemaining) / ((double)thread.totalDuration))
                            .addOutputLines(thread.recipe);
                    
                    threadNum++;
                }
            } 
            
            textList.add(Component.empty());
            textList.add(Component.literal(LocalizationUtils.format("start_core.machine.threading_controller.threads_available",
                    FormattingUtil.formatNumbers(getEffectiveThreads() - activeThreads.size()))));
        }
    }

    /**
     * Find multiple distinct recipes that can be run simultaneously
     * Each thread processes a different recipe type
     */
    private List<GTRecipe> findThreadedRecipes() {
        int maxThreads = getEffectiveThreads();
        if (maxThreads < 1 || recipeLogic == null) {
            return List.of();
        }

        var recipeType = recipeLogic.machine.getRecipeType();
        if (recipeType == null) {
            return List.of();
        }

        Set<ResourceLocation> excludedIds = new HashSet<>(maxThreads + 4);

        GTRecipe lastRecipe = recipeLogic.getLastRecipe();
        if (lastRecipe != null && lastRecipe.getId() != null) {
            excludedIds.add(lastRecipe.getId());
        }

        // Exclude currently active threaded recipes
        if (activeThreads != null && !activeThreads.isEmpty()) {
            for (ThreadedRecipeExecution thread : activeThreads) {
                GTRecipe r = thread.recipe;
                if (r != null && r.getId() != null) {
                    excludedIds.add(r.getId());
                }
            }
        }

        Set<GTRecipe> allMatches = recipeType.getLookup().findRecipeCollisions(recipeLogic.machine);
        if (allMatches == null || allMatches.isEmpty()) {
            return List.of();
        }

        // early stop
        List<GTRecipe> result = new ArrayList<>(Math.min(maxThreads, allMatches.size()));
        for (GTRecipe candidate : allMatches) {
            if (candidate == null) continue;
            ResourceLocation id = candidate.getId();
            if (id == null || excludedIds.contains(id)) continue;

            // Only add recipes that can be consumed
            if (canConsumeRecipeInputs(candidate)) {
                result.add(candidate);
                if (result.size() >= maxThreads) break;
            }
        }

        return result;
    }


    /**
     * Check if the machine has enough resources to consume for a recipe
     */
    private boolean canConsumeRecipeInputs(GTRecipe recipe) {
        if (recipe == null) return false;
        
        // Check if recipe matches with current inputs
        // The recipe system will handle the actual checking
        return recipe.matchRecipe(this.recipeLogic.machine).isSuccess();
    }

    /**
     * Consume inputs for a threaded recipe
     */
    private void consumeRecipeInputs(GTRecipe recipe) {
        if (recipe == null) return;
        
        // Use empty chance map for deterministic consumption
        Map<RecipeCapability<?>, Object2IntMap<?>> chanceCaches = new HashMap<>();
        recipe.handleRecipeIO(IO.IN, this.recipeLogic.machine, chanceCaches);
    }

    /**
     * Override recipe logic to handle multi-threading
     * This is called when the machine needs to find a recipe to process
     */
    @Override
    public boolean onWorking() {
        boolean result = super.onWorking();
        
        // Only run on server side
        if (this.getLevel().isClientSide) {
            return result;
        }
        
        // Tick all additional threads
        if (activeThreads != null && !activeThreads.isEmpty()) {
            List<ThreadedRecipeExecution> completedThreads = new ArrayList<>();
            
            for (ThreadedRecipeExecution thread : activeThreads) {
                thread.ticksRemaining--;
                
                if (thread.ticksRemaining <= 0) {
                    completedThreads.add(thread);
                }
            }

            // Handle completed threads
            for (ThreadedRecipeExecution completed : completedThreads) {
                handleThreadCompletion(completed);
                activeThreads.remove(completed);
            }
        }
        
        // If we're working and threading is enabled, try to start additional threads
        if (result && getEffectiveThreads() > 0 && (activeThreads.size() < getEffectiveThreads())) {
            setupThreadedExecution();
        }

        return result;
    }

    private void setupThreadedExecution() {
        List<GTRecipe> recipes = findThreadedRecipes();
        
        // no recipes found
        if (recipes.size() < 1) {
            return; 
        }

        for (int i = 0; i < recipes.size(); i++) {
            GTRecipe recipe = recipes.get(i);
            
            // Check if we can actually run this recipe
            if (!canConsumeRecipeInputs(recipe)) {
                continue;
            }
            
            // Apply ALL recipe modifiers including machine's modifiers
            GTRecipe modifiedRecipe = recipe;
            
            // Apply machine definition's recipe modifier if it exists
            if (getDefinition().getRecipeModifier() != null) {
                if (getDefinition().getRecipeModifier() instanceof RecipeModifierList list) {
                    // Apply each modifier in the list
                    modifiedRecipe = list.applyModifier(this, modifiedRecipe);
                } else {
                    // Apply single modifier
                    modifiedRecipe = getDefinition().getRecipeModifier().applyModifier(this, modifiedRecipe);
                }
            }
            
            // Try to consume inputs
            try {
                consumeRecipeInputs(modifiedRecipe);
                // Start the thread only if consumption succeeded
                activeThreads.add(new ThreadedRecipeExecution(modifiedRecipe, modifiedRecipe.duration));
            } catch (Exception e) {
                // Failed to consume inputs, skip this recipe
                continue;
            }
        }
    }

    /**
     * Handle completion of a threaded recipe
     */
    private void handleThreadCompletion(ThreadedRecipeExecution thread) {
        if (thread.recipe == null) return;
        
        Map<RecipeCapability<?>, Object2IntMap<?>> chanceCaches = new HashMap<>();
        thread.recipe.handleRecipeIO(IO.OUT, this.recipeLogic.machine, chanceCaches);
    }

    /**
     * Called after recipe logic, clear threading if main recipe ends
     */
    @Override
    public void afterWorking() {
        super.afterWorking();
        
        // Only run on server side
        if (this.getLevel().isClientSide) {
            return;
        }
        
        // Check if main recipe is complete
        if (this.recipeLogic.getProgress() == 0) {
            // Clear threading state when main recipe finishes
            if (activeThreads != null) {
                activeThreads.clear();
            }
        }
    }

    /**
     * Inner class to track threaded recipe execution
     * Must be static and have proper serialization for @Persisted to work
     */
    public static class ThreadedRecipeExecution {
        public GTRecipe recipe;
        public int ticksRemaining;
        public int totalDuration;

        public ThreadedRecipeExecution(GTRecipe recipe, int duration) {
            this.recipe = recipe;
            this.ticksRemaining = duration;
            this.totalDuration = duration;
        }

        public ThreadedRecipeExecution(GTRecipe recipe, int duration, int ticksRemaining) {
            this.recipe = recipe;
            this.ticksRemaining = ticksRemaining;
            this.totalDuration = duration;
        }
    }

    public static class ThreadedRecipeExecutionAccessor extends CustomObjectAccessor<ThreadedRecipeExecution> {

        public ThreadedRecipeExecutionAccessor() {
            super(ThreadedRecipeExecution.class, true); // field class, whether this accessor is available for its children class
        }

        @Override
        public ITypedPayload<?> serialize(AccessorOp accessorOp, ThreadedRecipeExecution threadedRecipeExecution) {
            FriendlyByteBuf serializedHolder = new FriendlyByteBuf(Unpooled.buffer());

            /* Serialize different components over netowrk */
            serializedHolder.writeInt(threadedRecipeExecution.ticksRemaining);
            serializedHolder.writeInt(threadedRecipeExecution.totalDuration);
            serializedHolder.writeUtf(threadedRecipeExecution.recipe.id.toString());
            GTRecipeSerializer.SERIALIZER.toNetwork(serializedHolder, threadedRecipeExecution.recipe);

            return FriendlyBufPayload.of(serializedHolder);
        }

        @Override
        public ThreadedRecipeExecution deserialize(AccessorOp accessorOp, ITypedPayload<?> payload) {
            if (payload instanceof FriendlyBufPayload buffer) {
                /* Deserialize from network, should be in same order as serialisation */
                int ticksRemaining = buffer.getPayload().readInt();
                int totalDuration = buffer.getPayload().readInt();

                var id = new ResourceLocation(buffer.getPayload().readUtf());
                GTRecipe innerRecipe = GTRecipeSerializer.SERIALIZER.fromNetwork(id, buffer.getPayload());
                return new ThreadedRecipeExecution(innerRecipe, totalDuration, ticksRemaining);
            }
            return null;
        }
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}