package com.startechnology.start_core.machine.abyssal_harvester;

import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.machine.redstone.IStarTRedstoneIndicatorMachine;
import com.startechnology.start_core.machine.redstone.StarTRedstoneIndicatorRecord;
// import com.startechnology.start_core.materials.StarTAbyssalHarvesterVoidFluids;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class StarTAbyssalHarvesterMachine extends WorkableElectricMultiblockMachine implements IStarTRedstoneIndicatorMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTAbyssalHarvesterMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    protected Integer saturation;

    protected TickableSubscription tryTickSub;
    private boolean startSaturationGain;

    private boolean isWorking;


    public StarTAbyssalHarvesterMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.saturation = 0;
        this.startSaturationGain = false;
        this.isWorking = false;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof StarTAbyssalHarvesterMachine abyssalHarvesterMachine)) {
            return RecipeModifier.nullWrongType(StarTAbyssalHarvesterMachine.class, machine);
        }
        
        if (!recipe.data.contains("min_saturation") || !recipe.data.contains("max_saturation")) {
            return ModifierFunction.IDENTITY;
        }
        int machineSaturation = abyssalHarvesterMachine.getSaturation();
        int minRecipeSaturation = recipe.data.getInt("min_saturation");
        int maxRecipeSaturation = recipe.data.getInt("max_saturation");
        if (minRecipeSaturation > machineSaturation || maxRecipeSaturation < machineSaturation) {
            return ModifierFunction.NULL;
        }
       
        if ((1750 <= machineSaturation && machineSaturation <= 2750) ||
         (4750 <= machineSaturation && machineSaturation <= 5750) ||
         (7750 <= machineSaturation && machineSaturation <= 8750)) {
        int maxPossibleParallels = ParallelLogic.getParallelAmountWithoutEU(machine, recipe, 2);
        return ModifierFunction.builder()
            .modifyAllContents(ContentModifier.multiplier(maxPossibleParallels))
            .parallels(maxPossibleParallels)
            .build();
        }

        return ModifierFunction.IDENTITY;
    }
 
    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        textList.add(Component.translatable("ui.start_core.abyssal_harvester", 
        String.format("%.2f", this.getSaturation() / 100.0)));
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
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

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        this.isWorking = false;
        this.startSaturationGain = true;
        this.saturationChanged();
    }

    private static final List<Integer> redstoneSaturationMarkers = List.of(
        12000, // 120.00%
        9000,  // 90.00%
        6000,  // 60.00%
        3000   // 30.00%
    );

    private void saturationChanged() {
        redstoneSaturationMarkers.forEach(marker -> {
            BigDecimal label = BigDecimal.valueOf(marker).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            this.setIndicatorValue("variadic.start_core.indicator.abyssal_harvester." + label.toString(),
                    (int) Math.floor(calculatePercentageSaturation(marker)));
        });
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.isWorking = false;
        this.startSaturationGain = false;
    }
    
    @Override
    public void onLoad() {
        super.onLoad();

        if (getLevel().isClientSide)
            return;

        tryTickSub = subscribeServerTick(tryTickSub, this::tryGainSaturation);
    }
    
    public Integer getSaturation() {
        return this.saturation;
    }

    protected void tryGainSaturation() {
        if (getOffsetTimer() % 100 == 0 && this.startSaturationGain) {

            this.saturation = Math.min(this.saturation + 55, 12000);
            this.saturationChanged();

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

    public double calculatePercentageSaturation(double marker) {
        return Math.min((this.saturation / marker) * 15.0, 15.0);
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        this.isWorking = false;
        tryAbsorbSaturation();
    }

    private void tryAbsorbSaturation() {
        this.saturation = Math.max(this.saturation - 500, 0);
        this.saturationChanged();
    }

    @Override
    public List<StarTRedstoneIndicatorRecord> getInitialIndicators() {
        return redstoneSaturationMarkers.stream().map(
            marker -> {
                BigDecimal label = BigDecimal.valueOf(marker).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                return new StarTRedstoneIndicatorRecord(
                    "variadic.start_core.indicator.abyssal_harvester." + label.toString(), 
                    Component.translatable("variadic.start_core.indicator.abyssal_harvester", Component.literal(label.toString() + "%").withStyle(ChatFormatting.DARK_PURPLE)), 
                    Component.translatable("variadic.start_core.description.abyssal_harvester", label.toString()).withStyle(ChatFormatting.GRAY), 
                    (int) Math.floor(calculatePercentageSaturation(marker)), 
                    marker
                );
            }
        ).toList();
    }
}
