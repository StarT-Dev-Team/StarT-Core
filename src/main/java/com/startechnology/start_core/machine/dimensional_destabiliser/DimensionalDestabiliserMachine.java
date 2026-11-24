package com.startechnology.start_core.machine.dimensional_destabiliser;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.machine.dimensional_destabiliser.client.RiftRender;
import lombok.Getter;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;

import static com.startechnology.start_core.machine.dimensional_destabiliser.DestabiliserRift.*;

public class DimensionalDestabiliserMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            DimensionalDestabiliserMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @DescSynced
    @Getter
    private int displayProgress;
    @DescSynced
    @Getter
    private long pulseGameTime = Long.MIN_VALUE / 2;
    @DescSynced
    @Getter
    private long openGameTime = Long.MIN_VALUE / 2;
    @DescSynced
    @Getter
    private long closeGameTime = Long.MIN_VALUE / 2;
    @Persisted
    private boolean riftLit;

    public DimensionalDestabiliserMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    public static MachineBuilder.ModelInitializer model(String casing, String overlay) {
        return GTMachineModels.createWorkableCasingMachineModel(new ResourceLocation(casing),
                new ResourceLocation(overlay)).andThen(b -> b.addDynamicRenderer(RiftRender::new));
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        if (getLevel() instanceof ServerLevel level && !riftLit) {
            riftLit = true;
            openGameTime = level.getGameTime();
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (!(getLevel() instanceof ServerLevel level) || !isStructureLoaded()) return;
        if (riftLit) {
            riftLit = false;
            closeGameTime = level.getGameTime();
        }
    }

    @Override
    public boolean onWorking() {
        if (!super.onWorking()) return false;
        var logic = getRecipeLogic();
        int progress = DestabiliserRift.displayProgress(logic.getProgress(), logic.getDuration());
        if (progress != displayProgress) displayProgress = progress;
        return true;
    }

    @Override
    public void afterWorking() {
        var logic = getRecipeLogic();
        boolean completed = DestabiliserRift.completed(logic.getProgress(), logic.getDuration());
        super.afterWorking();
        if (completed && getLevel() instanceof ServerLevel level &&
                DestabiliserRift.pulseAllowed(level.getGameTime(), pulseGameTime)) {
            pulseGameTime = level.getGameTime();
        }
    }

    public Vec3 getRiftCentre() {
        var pos = getPos();
        var front = getFrontFacing();
        return new Vec3(centreX(pos.getX(), front.getStepX()), centreY(pos.getY()),
                centreZ(pos.getZ(), front.getStepZ()));
    }

    private boolean isStructureLoaded() {
        var pos = getPos();
        var box = BoundingBox.fromCorners(
                pos.relative(relative(RelativeDirection.FRONT), EXTENT_FRONT)
                        .relative(relative(RelativeDirection.DOWN), EXTENT_DOWN)
                        .relative(relative(RelativeDirection.LEFT), EXTENT_HALF_WIDTH),
                pos.relative(relative(RelativeDirection.BACK), EXTENT_BACK)
                        .relative(relative(RelativeDirection.UP), EXTENT_UP)
                        .relative(relative(RelativeDirection.RIGHT), EXTENT_HALF_WIDTH));
        return getLevel().hasChunksAt(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
    }

    private Direction relative(RelativeDirection direction) {
        return direction.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }
}
