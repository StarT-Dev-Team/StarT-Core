package com.startechnology.start_core.machine.threading;

import java.util.Set;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IWorkableMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class StarTThreadingControllerPartMachine extends MultiblockPartMachine {

    public StarTThreadingControllerPartMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    public void updateControllerStats() {
        if (this.getLevel().isClientSide) {
            return;
        }
        
        var controllers = this.getControllers();
        if (controllers.size() < 1) {
            return;
        }

        IMultiController controller = controllers.first();
        PatternMatchContext matchContext = controller.getMultiblockState().getMatchContext();
        for (var entry : matchContext.entrySet()) {
            if (!entry.getKey().startsWith(StarTThreadingStatsPredicate.THREADING_STATS_HEADER)) {
                continue;
            }

            var stats = (StarTThreadingStatsPredicate.ThreadingStatsBlockTracker)entry.getValue();
        }
    }
}
