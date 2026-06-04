package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IParallelHatch;
import com.gregtechceu.gtceu.api.gui.widget.IntInputWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ParallelHatchPartMachine;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.utils.Position;
import com.startechnology.start_core.machine.parallel.IStarTMinimumParallelHatch;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.Mth;

@Mixin(value = ParallelHatchPartMachine.class, remap = false)
public abstract class ParallelHatchPartMachineMixin extends TieredPartMachine implements IFancyUIMachine, IStarTMinimumParallelHatch {

    public ParallelHatchPartMachineMixin(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
    }

    @Unique
    private static final int MIN_PARALLEL = 1;

    @Mutable
    @Final
    @Shadow
    private int maxParallel;

    @Mutable
    @Shadow
    private int currentParallel;

    @Shadow
    public abstract void setCurrentParallel(int parallelAmount);

    @Shadow
    public abstract int getCurrentParallel();

    @Unique
    @Persisted(key = "minimumRunParallel")
    private int start_core$minimumRunParallel;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void modifyMaxParallel(IMachineBlockEntity holder, int tier, CallbackInfo ci) {
        // Change the maxParallel calculation from Math.pow(4, tier - GTValues.EV)
        // to a custom expression: 2 * (int) Math.pow(4, tier - GTValues.EV)
        // This doubles the parallel capacity compared to the original
        this.maxParallel = (tier <= GTValues.UHV) ? (int) Math.pow(4, tier - GTValues.EV) : 
                           (int) Math.pow(2, tier + 1);
        this.currentParallel = maxParallel;
        this.starT_Core$minimumRunParallel = MIN_PARALLEL;
    }

    @Unique
    private void starT_Core$setMinimumRunParallel(int parallelAmount) {
        this.starT_Core$minimumRunParallel = Mth.clamp(parallelAmount, MIN_PARALLEL, this.maxParallel);
        for (IMultiController controller : this.getControllers()) {
            if (controller instanceof IRecipeLogicMachine rlm) {
                rlm.getRecipeLogic().markLastRecipeDirty();
            }
        }
    }

    @Unique
    private void start_core$setUIMaxParallel(int newMax) {
        var prevMin = start_core$getMinimumParallels();
        setCurrentParallel(Math.max(newMax, 1));
        start_core$setMinimumRunParallel(Mth.clamp(prevMin, 1, newMax));
    }

    @Unique
    private void start_core$setUIMinParallel(int newMin) {
        var prevMax = getCurrentParallel();
        start_core$setMinimumRunParallel(Mth.clamp(newMin, 1, prevMax));
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup parallelAmountGroup = new WidgetGroup(0, 0, 100, 80);
        parallelAmountGroup.addWidget(new LabelWidget(-14, 4, "start_core.parallel_hatch.max_parallel"));
        parallelAmountGroup.addWidget(new IntInputWidget(new Position(0, 18), this::getCurrentParallel, this::start_core$setUIMaxParallel)
                .setMin(MIN_PARALLEL)
                .setMax(maxParallel));

        parallelAmountGroup.addWidget(new LabelWidget(-10, 50, "start_core.parallel_hatch.min_parallel"));
        parallelAmountGroup.addWidget(new IntInputWidget(new Position(0, 64), this::start_core$getMinimumParallels, this::start_core$setUIMinParallel)
                .setMin(MIN_PARALLEL)
                .setMax(maxParallel));

        return parallelAmountGroup;
    }

    @Override
    public int start_core$getMinimumParallels() {
        return this.start_core$minimumRunParallel;
    }
}
