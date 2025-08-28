package com.startechnology.start_core.machine.abyssal_harvester;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.lowdragmc.lowdraglib.syncdata.managed.ManagedField;
import com.startechnology.start_core.machine.redstone.StarTRedstoneInterfacePartMachine;
// import com.startechnology.start_core.materials.StarTAbyssalHarvesterVoidFluids;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

public class StarTAbyssalHarvesterMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTAbyssalHarvesterMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    protected Integer saturation;

    protected TickableSubscription tryTickSub;
    private boolean startSaturationGain;

    private boolean isWorking;
    public ArrayList<StarTRedstoneInterfacePartMachine> redstoneOutputHatches;


    public StarTAbyssalHarvesterMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.saturation = 0;
        this.startSaturationGain = false;
        this.isWorking = false;
        this.redstoneOutputHatches = new ArrayList<>();
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
         (5250 <= machineSaturation && machineSaturation <= 6250) ||
         (8750 <= machineSaturation && machineSaturation <= 9750)) {
        int maxPossibleParallels = ParallelLogic.getParallelAmountFast(machine, recipe, 2);
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

        // Find output redstone if it exists
        this.getParts()
            .stream()
            .filter(StarTRedstoneInterfacePartMachine.class::isInstance)
            .forEach(part -> {
                this.redstoneOutputHatches.add((StarTRedstoneInterfacePartMachine)part);
            });


        this.saturationChanged();
    }

    private static final List<Integer> redstoneSaturationMarkers = List.of(
        12000, // 120.00%
        9000,  // 90.00%
        6000,  // 60.00%
        3000   // 30.00%
    );

    private void saturationChanged() {
        if (this.redstoneOutputHatches.isEmpty()) return;

        redstoneSaturationMarkers.stream().forEach(marker -> {
            double strength = (this.saturation / (double) marker) * 15.0;

            this.redstoneOutputHatches.forEach(hatch -> {
                hatch.setIndicatorSignal(
                    Component.translatable("ui.start_core.abyssal_harvester.redstone", 
                    String.format("%.2f", (double) marker / 100)).getString(),
                    (int) Math.min(15, Math.max(0, Math.floor(strength)))
                );
            });
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
}
