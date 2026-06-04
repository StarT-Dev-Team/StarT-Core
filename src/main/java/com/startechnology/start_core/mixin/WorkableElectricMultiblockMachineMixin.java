package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.startechnology.start_core.machine.parallel.IStarTMinimumParallelHatch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

@Mixin(value = WorkableElectricMultiblockMachine.class, remap = false)
public class WorkableElectricMultiblockMachineMixin extends WorkableMultiblockMachine {

    public WorkableElectricMultiblockMachineMixin(IMachineBlockEntity holder, Object[] args) {
        super(holder, args);
    }

    /* this is scary */
    @WrapOperation(method = "addDisplayText", at = @At(value = "INVOKE", target = "Lcom/gregtechceu/gtceu/api/machine/multiblock/MultiblockDisplayText$Builder;addParallelsLine(IZ)Lcom/gregtechceu/gtceu/api/machine/multiblock/MultiblockDisplayText$Builder;"))
    private MultiblockDisplayText.Builder wrapAddParallelsLine(
            MultiblockDisplayText.Builder builder,
            int numParallels,
            boolean exact,
            Operation<MultiblockDisplayText.Builder> original,
            @Local(argsOnly = true) List<Component> textList) {
                
        this.getParallelHatch().ifPresent(parallelHatch -> {
            if (parallelHatch instanceof IStarTMinimumParallelHatch minimumParallelHatch) {

                int minParallels = minimumParallelHatch.starT_Core$getMinimumParallels();

                if (minParallels > 1) {
                    Component minParallelComponent = Component.literal(
                            FormattingUtil.formatNumbers(minParallels))
                            .withStyle(ChatFormatting.DARK_PURPLE);

                    textList.add(Component.translatable(
                            "start_core.parallel_hatch.jade_min_parallel",
                            minParallelComponent).withStyle(ChatFormatting.GRAY));
                }
            }
        });

        return original.call(builder, numParallels, exact);
    }
}
