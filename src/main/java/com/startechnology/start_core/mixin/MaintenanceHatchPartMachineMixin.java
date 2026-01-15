package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.MaintenanceHatchPartMachine;
import com.startechnology.start_core.api.dreamlink.IStarTCopyInteractable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = MaintenanceHatchPartMachine.class, remap = false)
public abstract class MaintenanceHatchPartMachineMixin extends TieredPartMachine implements IStarTCopyInteractable {
    @Unique
    private final String start$nbtDuration = "duration";

    @Shadow @Final private boolean isConfigurable;

    @Shadow private float durationMultiplier;

    @Shadow protected abstract void updateMaintenanceSubscription();

    public MaintenanceHatchPartMachineMixin(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
    }

    @Override
    public InteractionResult onCopyUse(Player player, ItemStack dataStick) {
        var tag = dataStick.getTag();
        if (tag == null || !tag.contains(start$nbtDuration)) return InteractionResult.PASS;
        if (!this.isRemote() && this.isConfigurable) {
            this.durationMultiplier = tag.getFloat(start$nbtDuration);
            this.updateMaintenanceSubscription();
        }
        return InteractionResult.sidedSuccess(this.isRemote());
    }

    @Override
    public InteractionResult onCopyShiftUse(Player player, ItemStack dataStick) {
        if (!this.isRemote() && this.isConfigurable) {
            var tag = new CompoundTag();
            tag.putFloat(start$nbtDuration, this.durationMultiplier);
            dataStick.setTag(tag);
        }
        return InteractionResult.SUCCESS;
    }
}
