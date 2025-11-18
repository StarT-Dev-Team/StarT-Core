package com.startechnology.start_core.machine.threading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifierList;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;
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
    protected TickableSubscription threadTickableSubscription;

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

    private Integer getEffectiveDurationReduction() {
        if (this.assignedSpeed == null) this.assignedSpeed = 0;
        if (this.speed == null) this.speed = 0;
        return this.assignedSpeed + this.speed;
    }

    public MutableComponent getSpeedPrettyFormat() {
        double multiplier = calculateDurationMultiplier();
        double reductionPercent = (1 - multiplier) * 100.0;
        return Component.literal(LocalizationUtils.format(
                "start_core.machine.threading_controller.speed.pretty_format",
                FormattingUtil.formatNumber2Places(reductionPercent)
        ));
    }

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

    private Integer getEffectiveParallels() {
        if (this.assignedParallels == null) this.assignedParallels = 0;
        if (this.parallels == null) this.parallels = 0;
        return Math.floorDiv(this.assignedParallels + this.parallels, 20) + 1;
    }

    public MutableComponent getParallelsPrettyFormat() {
        return Component.literal(LocalizationUtils.format("start_core.machine.threading_controller.parallels.pretty_format",
                FormattingUtil.formatNumbers(getEffectiveParallels())));
    }

    private Integer getEffectiveThreads() {
        if (this.assignedThreading == null) this.assignedThreading = 0;
        if (this.threading == null) this.threading = 0;
        return Math.floorDiv(this.assignedThreading + this.threading, 5);
    }

    public MutableComponent getThreadsPrettyFormat() {
        return Component.literal(LocalizationUtils.format("start_core.machine.threading_controller.threading.pretty_format",
                FormattingUtil.formatNumbers(getEffectiveThreads())));
    }

    public MutableComponent getActualDurationPrettyFormat() {
        double efficiencyMultiplier = calculateDurationMultiplier();
        double parallelMultiplier = Math.sqrt(getEffectiveParallels());
        double actualDurationMultiplier = efficiencyMultiplier * parallelMultiplier * 100.0;
        return Component.literal(LocalizationUtils.format(
                "start_core.machine.threading_controller.duration.pretty_format",
                FormattingUtil.formatNumber2Places(actualDurationMultiplier)));
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

            var stats = (StarTThreadingStatsPredicate.ThreadingStatsBlockTracker) entry.getValue();
            this.stats.put(stats.name.replace(StarTThreadingStatsPredicate.THREADING_STATS_HEADER, ""), stats);
            this.increaseStats(stats);
        }

        this.ensureAssignment();
    }

    private double calculateDurationMultiplier() {
        int spdPointsPerMark = 100; //scaler
        int speedPoints = getEffectiveDurationReduction();
        double amountSpdMark = (double) speedPoints / spdPointsPerMark;
        double timesDurationHalved = (-1 + Math.sqrt(1 + 8 * amountSpdMark)) / 2; //to reach each halving you need sum of that metric, example 6 points for 1/8, 6 = sum(3), 1/8 = 2^-3
        return Math.pow(2, -1 * timesDurationHalved);
    }

    private double calculateEnergyMultiplier() {
        int effPointsPerMark = 30; //1 more thread worth of efficiency per 30 points (linearly scales efficiency to get threads since EUt cost scales linearly)
        int efficiencyPoints = getEffectivePowerReduction();
        return (double) effPointsPerMark / (effPointsPerMark + efficiencyPoints);
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof StarTThreadingCapableMachine controller && controller.isFormed()) {
            int parallels = ParallelLogic.getParallelAmountFast(machine, recipe, controller.getEffectiveParallels());
            double durationMultiplier = controller.calculateDurationMultiplier();
            double energyMultiplier = controller.calculateEnergyMultiplier();

            double finalDurationMultiplier = durationMultiplier * Math.sqrt((int) parallels);

            return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels))
                .durationMultiplier(finalDurationMultiplier)
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

        if (this.threadTickableSubscription != null) {
            this.threadTickableSubscription.unsubscribe();
            this.threadTickableSubscription = null;
        }
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

        if (Objects.isNull(this.threadTickableSubscription)) {
            threadTickableSubscription = subscribeServerTick(this::tickThreads);
        } else {
            threadTickableSubscription.unsubscribe();
            threadTickableSubscription  = subscribeServerTick(this::tickThreads);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (getLevel().isClientSide) {
            return;
        }

        threadTickableSubscription = subscribeServerTick(this::tickThreads);
    }

    @Override
    public void onUnload() {
        super.onUnload();

        if (Objects.nonNull(this.threadTickableSubscription)) {
            this.threadTickableSubscription.unsubscribe();
            this.threadTickableSubscription = null;
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        
        if (isFormed()) {
            textList.add(Component.empty());
            textList.add(Component.translatable("start_core.machine.threading_controller.header"));
            textList.add(getActualDurationPrettyFormat());
            textList.add(getEfficiencyPrettyFormat());
            textList.add(getParallelsPrettyFormat());
            textList.add(getThreadsPrettyFormat());

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

private List<GTRecipe> findThreadedRecipes() {
        int maxThreads = getEffectiveThreads();
        if (maxThreads < 1 || recipeLogic == null) {
            return List.of();
        }

        int availableThreads = maxThreads - (activeThreads != null ? activeThreads.size() : 0);
        if (availableThreads <= 0) {
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

        List<GTRecipe> result = new ArrayList<>(Math.min(availableThreads, allMatches.size()));
        for (GTRecipe candidate : allMatches) {
            if (candidate == null) continue;
            ResourceLocation id = candidate.getId();
            if (id == null || excludedIds.contains(id)) continue;

            if (canConsumeRecipeInputs(candidate)) {
                result.add(candidate);
                if (result.size() >= availableThreads) break;
            }
        }

        return result;
    }

    private boolean canConsumeRecipeInputs(GTRecipe recipe) {
        if (recipe == null) return false;
        return 
            recipe.matchRecipe(this.recipeLogic.machine).isSuccess() && 
            recipe.matchTickRecipe(this.recipeLogic.machine).isSuccess() &&
            recipe.checkConditions(this.recipeLogic).isSuccess();
    }

    private void consumeRecipeInputs(GTRecipe recipe) {
        if (recipe == null) return;
        Map<RecipeCapability<?>, Object2IntMap<?>> chanceCaches = new HashMap<>();
        recipe.handleRecipeIO(IO.IN, this.recipeLogic.machine, chanceCaches);
    }

    private void tickThreads() {
        if (!this.isWorkingEnabled() || this.getLevel().isClientSide || !isFormed()) {
            return;
        }
        
        if (activeThreads != null && !activeThreads.isEmpty()) {
            List<ThreadedRecipeExecution> completedThreads = new ArrayList<>();
            
            for (ThreadedRecipeExecution thread : activeThreads) {
                GTRecipe recipe = thread.recipe;
                if (recipe.matchTickRecipe(this.recipeLogic.machine).isSuccess()) {
                    handleThreadPerTickInputs(thread);
                    handleThreadPerTickOutputs(thread);
                    thread.ticksRemaining--;

                    thread.isWorking = true;

                    if (thread.ticksRemaining <= 0) {
                        completedThreads.add(thread);
                    }
                } else {
                    thread.isWorking = false;
                    thread.ticksRemaining = Math.min(thread.ticksRemaining + 1, thread.totalDuration);
                }
            }

            for (ThreadedRecipeExecution completed : completedThreads) {
                handleThreadCompletion(completed);
                activeThreads.remove(completed);
            }
        }
        
        if (getEffectiveThreads() > 0 && (activeThreads.size() < getEffectiveThreads())) {
            setupThreadedExecution();
        }
    }

    private void setupThreadedExecution() {
        List<GTRecipe> recipes = findThreadedRecipes();
        
        if (recipes.size() < 1) {
            return; 
        }

        for (int i = 0; i < recipes.size(); i++) {
            GTRecipe recipe = recipes.get(i);
            
            if (!canConsumeRecipeInputs(recipe)) {
                continue;
            }
            
            GTRecipe modifiedRecipe = recipe;

            if (getDefinition().getRecipeModifier() != null) {
                if (getDefinition().getRecipeModifier() instanceof RecipeModifierList list) {
                    modifiedRecipe = list.applyModifier(this, modifiedRecipe);
                } else {
                    modifiedRecipe = getDefinition().getRecipeModifier().applyModifier(this, modifiedRecipe);
                }
            }
            
            try {
                consumeRecipeInputs(modifiedRecipe);
                activeThreads.add(new ThreadedRecipeExecution(modifiedRecipe, modifiedRecipe.duration));
            } catch (Exception e) {
                continue;
            }
        }
    }

    private void handleThreadPerTickInputs(ThreadedRecipeExecution thread) {
        if (thread.recipe == null) return;
        Map<RecipeCapability<?>, Object2IntMap<?>> chanceCaches = new HashMap<>();
        thread.recipe.handleTickRecipeIO(IO.IN, this.recipeLogic.machine, chanceCaches);
    }

    private void handleThreadPerTickOutputs(ThreadedRecipeExecution thread) {
        if (thread.recipe == null) return;
        Map<RecipeCapability<?>, Object2IntMap<?>> chanceCaches = new HashMap<>();
        thread.recipe.handleTickRecipeIO(IO.OUT, this.recipeLogic.machine, chanceCaches);
    }

    private void handleThreadCompletion(ThreadedRecipeExecution thread) {
        if (thread.recipe == null) return;
        Map<RecipeCapability<?>, Object2IntMap<?>> chanceCaches = new HashMap<>();
        thread.recipe.handleRecipeIO(IO.OUT, this.recipeLogic.machine, chanceCaches);
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        
        if (this.getLevel().isClientSide) {
            return;
        }
        
        if (this.recipeLogic.getProgress() == 0) {
            if (activeThreads != null) {
                activeThreads.clear();
            }
        }
    }

    public static class ThreadedRecipeExecution {
        public GTRecipe recipe;
        public int ticksRemaining;
        public int totalDuration;
        public boolean isWorking;

        public ThreadedRecipeExecution(GTRecipe recipe, int duration) {
            this.recipe = recipe;
            this.ticksRemaining = duration;
            this.totalDuration = duration;
            this.isWorking = false;
        }

        public ThreadedRecipeExecution(GTRecipe recipe, int duration, int ticksRemaining, boolean isWorking) {
            this.recipe = recipe;
            this.ticksRemaining = ticksRemaining;
            this.totalDuration = duration;
            this.isWorking = isWorking;
        }
    }

    public static class ThreadedRecipeExecutionAccessor extends CustomObjectAccessor<ThreadedRecipeExecution> {

        public ThreadedRecipeExecutionAccessor() {
            super(ThreadedRecipeExecution.class, true);
        }

        @Override
        public ITypedPayload<?> serialize(AccessorOp accessorOp, ThreadedRecipeExecution threadedRecipeExecution) {
            FriendlyByteBuf serializedHolder = new FriendlyByteBuf(Unpooled.buffer());

            serializedHolder.writeInt(threadedRecipeExecution.ticksRemaining);
            serializedHolder.writeInt(threadedRecipeExecution.totalDuration);
            serializedHolder.writeBoolean(threadedRecipeExecution.isWorking);
            serializedHolder.writeUtf(threadedRecipeExecution.recipe.id.toString());
            GTRecipeSerializer.SERIALIZER.toNetwork(serializedHolder, threadedRecipeExecution.recipe);

            return FriendlyBufPayload.of(serializedHolder);
        }

        @Override
        public ThreadedRecipeExecution deserialize(AccessorOp accessorOp, ITypedPayload<?> payload) {
            if (payload instanceof FriendlyBufPayload buffer) {
                int ticksRemaining = buffer.getPayload().readInt();
                int totalDuration = buffer.getPayload().readInt();
                boolean isWorking = buffer.getPayload().readBoolean();

                var id = new ResourceLocation(buffer.getPayload().readUtf());
                GTRecipe innerRecipe = GTRecipeSerializer.SERIALIZER.fromNetwork(id, buffer.getPayload());
                return new ThreadedRecipeExecution(innerRecipe, totalDuration, ticksRemaining, isWorking);
            }
            return null;
        }
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}