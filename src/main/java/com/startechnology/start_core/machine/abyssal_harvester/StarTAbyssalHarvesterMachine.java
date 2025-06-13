package com.startechnology.start_core.machine.abyssal_harvester;

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

    public StarTAbyssalHarvesterMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.entropy = 10000;
        this.startEntropyLoss = false;
        this.isWorking = false;
    }

    public static ModifierFunction recipeModifier(@NotNull MetaMachine machine, @NotNull GTRecipe recipe) {
        if (!(machine instanceof StarTAbyssalHarvesterMachine abyssalHarvesterMachine)) {
            return RecipeModifier.nullWrongType(StarTAbyssalHarvesterMachine.class, machine);
        }
        if (!recipe.data.contains("entropy") || recipe.data.getDouble("entropy") > abyssalHarvesterMachine.getEntropy()) {
            return ModifierFunction.NULL;
        }
        if (recipe.data.getDouble("entropy") < abyssalHarvesterMachine.getEntropy()) {
            abyssalHarvesterMachine.isWorking = true;
            return ModifierFunction.IDENTITY;
        }
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
        this.entropy = 10000; // Reset entropy when structure is formed
        this.isWorking = false;
        this.startEntropyLoss = true;
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
                this.entropy = Math.max(this.entropy - 10, 0);

        }
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        this.isWorking = false;
        this.entropy += 1000;
    }

}
