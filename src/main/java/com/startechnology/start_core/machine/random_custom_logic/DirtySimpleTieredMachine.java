package com.startechnology.start_core.machine.random_custom_logic;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import it.unimi.dsi.fastutil.ints.Int2IntFunction;

public class DirtySimpleTieredMachine extends SimpleTieredMachine {

    public DirtySimpleTieredMachine(IMachineBlockEntity holder, int tier, Int2IntFunction tankScalingFunction,
                                    Object... args) {
        super(holder, tier, tankScalingFunction, args);
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        getRecipeLogic().markLastRecipeDirty();
    }
}
