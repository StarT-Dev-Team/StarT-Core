package com.startechnology.start_core.machine.maintenance;

import com.gregtechceu.gtceu.api.capability.ICleanroomReceiver;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.ICleanroomProvider;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.api.machine.multiblock.DummyCleanroom;
import com.gregtechceu.gtceu.common.machine.multiblock.part.AutoMaintenanceHatchPartMachine;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.gregtechceu.gtceu.api.GTValues.UHV;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StarTCleaningMaintenanceHatchPartMachine extends AutoMaintenanceHatchPartMachine {
    private final ICleanroomProvider DUMMY_CLEANROOM;

    public StarTCleaningMaintenanceHatchPartMachine(IMachineBlockEntity holder, CleanroomType... cleanroomTypes) {
        super(holder);

        DUMMY_CLEANROOM = DummyCleanroom.createForTypes(List.of(cleanroomTypes));
    }

    @Override
    public void addedToController(IMultiController controller) {
        super.addedToController(controller);
        if (controller instanceof ICleanroomReceiver receiver) {
            receiver.setCleanroom(DUMMY_CLEANROOM);
        }
    }

    @Override
    public void removedFromController(IMultiController controller) {
        super.removedFromController(controller);
        if (controller instanceof ICleanroomReceiver receiver && receiver.getCleanroom() == DUMMY_CLEANROOM) {
            receiver.setCleanroom(null);
        }
    }

    @Override
    public int getTier() {
        return UHV;
    }
}
