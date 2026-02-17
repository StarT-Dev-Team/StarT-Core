package com.startechnology.start_core.api.vacuumpump;

import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.machine.vcr.VacuumChemicalReactorMachine;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface IVacuumPumpType {

    @NotNull String getName();

    int getRate();

    int getCap();

    int getTier();

    ResourceLocation getTexture();

    default Component formatCap() {
        return VacuumChemicalReactorMachine.formatVacuumPumpCap(getCap());
    }

    default Component formatRate() {
        return VacuumChemicalReactorMachine.formatVacuumPumpRate(getRate());
    }

}
