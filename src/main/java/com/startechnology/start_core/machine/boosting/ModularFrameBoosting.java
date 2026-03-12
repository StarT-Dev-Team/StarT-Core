package com.startechnology.start_core.machine.boosting;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.machine.modular.StarTModularControllerMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ModularFrameBoosting extends StarTModularControllerMachine {

    public static final double FRAME_BOOST = 1.5;

    private long netInLastSec = 0L;
    private long netOutLastSec = 0L;
    private long inputPerSec = 0L;
    private long outputPerSec = 0L;

    public ModularFrameBoosting(IMachineBlockEntity holder, ResourceLocation... supportedMultiblockIds) {
        super(holder, supportedMultiblockIds);
    }

    @Override
    public void onStructureInvalid() {
        this.netInLastSec = 0L;
        this.netOutLastSec = 0L;
        this.inputPerSec = 0L;
        this.outputPerSec = 0L;
        super.onStructureInvalid();
    }

    @Override
    protected void transferEnergy() {
        super.transferEnergy();
        if (getLevel().isClientSide) return;

        if (getOffsetTimer() % 60L == 0L) {
            this.inputPerSec = this.netInLastSec;
            this.outputPerSec = this.netOutLastSec;
        }
    }

    @Override
    protected boolean transferModuleInterfacesTick() {
        if (getLevel().isClientSide || !this.readyToUpdate || !isWorkingEnabled()) return false;

        long energyStored = inputHatches.getEnergyStored();
        if (energyStored <= 0) return false;

        this.netInLastSec = energyStored;

        long boostedEnergy = (long)(energyStored * FRAME_BOOST);
        long totalEnergyTransferred = outputConduits.changeEnergy(boostedEnergy);

        if (totalEnergyTransferred > 0) {
            inputHatches.removeEnergy(totalEnergyTransferred);
            this.netOutLastSec = totalEnergyTransferred;
            return true;
        }
        return false;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        if (isFormed()) {
            Style STYLE_GREEN = Style.EMPTY.withColor(ChatFormatting.GREEN);
            Style STYLE_YELLOW = Style.EMPTY.withColor(ChatFormatting.YELLOW);
            Style STYLE_RED = Style.EMPTY.withColor(ChatFormatting.RED);

            MutableComponent avgInComponent = Component.literal(FormattingUtil.formatNumbers(this.inputPerSec / 60L));
            textList.add(Component.literal("Avg EU In: ")
                    .withStyle(STYLE_YELLOW)
                    .append(avgInComponent.withStyle(STYLE_GREEN))
                    .append(Component.literal(" EU/t")));

            textList.add(Component.literal("Frame Boost: +" + (int)((FRAME_BOOST - 1.0) * 100) + "%")
                    .withStyle(STYLE_GREEN));

            MutableComponent avgOutComponent = Component.literal(FormattingUtil.formatNumbers(this.outputPerSec / 60L));
            textList.add(Component.literal("Boosted EU Out: ")
                    .withStyle(STYLE_YELLOW)
                    .append(avgOutComponent.withStyle(STYLE_RED))
                    .append(Component.literal(" EU/t")));
        }
    }
}