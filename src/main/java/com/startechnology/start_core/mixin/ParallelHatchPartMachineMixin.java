package com.startechnology.start_core.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
import com.lowdragmc.lowdraglib.utils.Position;
import com.startechnology.start_core.machine.parallel.IStarTMinimumParallelHatch;

import lombok.Getter;
import net.minecraft.util.Mth;

@Mixin(value = ParallelHatchPartMachine.class, remap = false)
public class ParallelHatchPartMachineMixin extends TieredPartMachine implements IFancyUIMachine, IStarTMinimumParallelHatch {

    public ParallelHatchPartMachineMixin(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
    }

    private static final int MIN_PARALLEL = 1;

    @Mutable
    @Final
    @Shadow
    private int maxParallel;

    @Mutable
    @Shadow
    private int currentParallel;

    private int minimumRunParallel;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void modifyMaxParallel(IMachineBlockEntity holder, int tier, CallbackInfo ci) {
        // Change the maxParallel calculation from Math.pow(4, tier - GTValues.EV)
        // to a custom expression: 2 * (int) Math.pow(4, tier - GTValues.EV)
        // This doubles the parallel capacity compared to the original
        this.maxParallel = (tier <= GTValues.UHV) ? (int) Math.pow(4, tier - GTValues.EV) : 
                           (int) Math.pow(2, tier + 1);
        this.currentParallel = maxParallel;
        this.minimumRunParallel = MIN_PARALLEL;
    }

    private void setMinimumRunParallel(int parallelAmount) {
        this.minimumRunParallel = Mth.clamp(parallelAmount, MIN_PARALLEL, this.maxParallel);
        for (IMultiController controller : this.getControllers()) {
            if (controller instanceof IRecipeLogicMachine rlm) {
                rlm.getRecipeLogic().markLastRecipeDirty();
            }
        }
    }

    @Override
    public Widget createUIWidget() {
        ParallelHatchPartMachine thisParallel = ((ParallelHatchPartMachine)(Object)this);

        WidgetGroup parallelAmountGroup = new WidgetGroup(0, 0, 100, 80);
        parallelAmountGroup.addWidget(new LabelWidget(-14, 4, "start_core.parallel_hatch.max_parallel"));
        parallelAmountGroup.addWidget(new IntInputWidget(new Position(0, 18), thisParallel::getCurrentParallel, thisParallel::setCurrentParallel)
                .setMin(MIN_PARALLEL)
                .setMax(maxParallel));

        parallelAmountGroup.addWidget(new LabelWidget(-10, 50, "start_core.parallel_hatch.min_parallel"));
        parallelAmountGroup.addWidget(new IntInputWidget(new Position(0, 64), this::getMinimumParallels, this::setMinimumRunParallel)
                .setMin(MIN_PARALLEL)
                .setMax(maxParallel));

        return parallelAmountGroup;
    }

    @Override
    public Integer getMinimumParallels() {
        return this.minimumRunParallel;
    }
}
