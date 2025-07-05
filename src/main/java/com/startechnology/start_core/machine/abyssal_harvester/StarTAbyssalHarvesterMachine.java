package com.startechnology.start_core.machine.abyssal_harvester;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
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
import com.startechnology.start_core.materials.StarTAbyssalHarvesterVoidFluids;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Recipe;

public class StarTAbyssalHarvesterMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTAbyssalHarvesterMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    protected Integer entropy;

    protected TickableSubscription tryTickSub;
    private boolean startEntropyLoss;

    private boolean isWorking;
    public ArrayList<StarTRedstoneInterfacePartMachine> redstoneOutputHatches;


    public StarTAbyssalHarvesterMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.entropy = 10000;
        this.startEntropyLoss = false;
        this.isWorking = false;
        this.redstoneOutputHatches = new ArrayList<>();
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof StarTAbyssalHarvesterMachine abyssalHarvesterMachine)) {
            return RecipeModifier.nullWrongType(StarTAbyssalHarvesterMachine.class, machine);
        }
        
        if (!recipe.data.contains("min_entropy") || !recipe.data.contains("max_entropy")) {
            return ModifierFunction.IDENTITY;
        }
        int machineEntropy = abyssalHarvesterMachine.getEntropy();
        int recipeEntropy = recipe.data.getInt("min_entropy");
        int maxRecipeEntropy = recipe.data.getInt("max_entropy");
        if (recipeEntropy > machineEntropy || maxRecipeEntropy < machineEntropy) {
            return ModifierFunction.NULL;
        }
        // if (recipeEntropy <= machineEntropy) {
        //     return ModifierFunction.IDENTITY;
        // }
        return ModifierFunction.IDENTITY;
    }
 
    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        textList.add(Component.translatable("ui.start_core.abyssal_harvester", this.getEntropy()));
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
        this.startEntropyLoss = true;

        // Find output redstone if it exists
        this.getParts()
            .stream()
            .filter(StarTRedstoneInterfacePartMachine.class::isInstance)
            .forEach(part -> {
                this.redstoneOutputHatches.add((StarTRedstoneInterfacePartMachine)part);
            });


        this.entropyChanged();
    }

    private static final List<Integer> redstoneEntropyMarkers = List.of(
        150000,
        300000,
        500000
    );

    private void entropyChanged() {
        if (this.redstoneOutputHatches.isEmpty()) return;

        redstoneEntropyMarkers.stream().forEach(
            entry -> {
                final double percentageOfEntropy = (this.entropy / ((double)entry)) * 15.0;

                this.redstoneOutputHatches.forEach(hatch -> {
                    hatch.setIndicatorSignal(
                    "Percentage to " +  entry.toString() + " §5Entropy", 
                    (int)Math.floor(percentageOfEntropy)
                    );
                });
            }
        );
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.isWorking = false;
    }
    
    @Override
    public void onLoad() {
        super.onLoad();

        if (getLevel().isClientSide)
            return;

        tryTickSub = subscribeServerTick(tryTickSub, this::tryRemoveEntropy);
    }
    
    public Integer getEntropy() {
        return this.entropy;
    }

    protected void tryRemoveEntropy() {
        if (getOffsetTimer() % 20 == 0 && this.startEntropyLoss) {

            if (!this.isWorking)
                this.entropy = Math.max(this.entropy - 50, 0);
                this.entropyChanged();

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
        tryIncreaseEntropy();
    }

    private void tryIncreaseEntropy() {
        this.entropy = Math.min(this.entropy + 1000, 500000);
        this.entropyChanged();
    }
}
