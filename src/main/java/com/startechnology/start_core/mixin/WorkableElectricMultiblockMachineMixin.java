package com.startechnology.start_core.mixin;

import java.util.List;

import org.checkerframework.checker.units.qual.min;
import org.spongepowered.asm.mixin.Mixin;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.IOverclockMachine;
import com.gregtechceu.gtceu.api.machine.feature.ITieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.startechnology.start_core.machine.parallel.IStarTMinimumParallelHatch;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

@Mixin(value = WorkableElectricMultiblockMachine.class, remap = false)
public class WorkableElectricMultiblockMachineMixin extends WorkableMultiblockMachine {

    public WorkableElectricMultiblockMachineMixin(IMachineBlockEntity holder, Object[] args) {
                                               super(holder, args);
    }

    @WrapMethod(method = "addDisplayText")
    private void addDisplayText(List<Component> textList, Operation<Void> original) {
        original.call(textList);

        /* hopefully this place never is too weird to put this parallel line */
        this.getParallelHatch().ifPresent(parallelHatch -> {
            if (parallelHatch instanceof IStarTMinimumParallelHatch minimumParallelHatch) {
                int minParallels = minimumParallelHatch.getMinimumParallels();

                if (minParallels > 1) {
                    Component minParallelComponent = Component.literal(FormattingUtil.formatNumbers(minParallels))
                            .withStyle(ChatFormatting.DARK_PURPLE);
                    textList.add(3, Component.translatable("start_core.parallel_hatch.jade_min_parallel", minParallelComponent).withStyle(ChatFormatting.GRAY));
                }
            }
        });
        
    }
}
