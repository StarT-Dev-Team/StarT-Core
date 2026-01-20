package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;
import com.startechnology.start_core.api.copy.ICopyInteractable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = FluidHatchPartMachine.class, remap = false)
public abstract class FluidHatchPartMachineMixin  extends TieredIOPartMachine implements ICopyInteractable {
    @Unique
    private final String start$nbtFilterFluid = "filterFluid";

    @Shadow @Final public NotifiableFluidTank tank;

    @Shadow protected abstract void updateTankSubscription();

    public FluidHatchPartMachineMixin(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
    }

    @Override
    public InteractionResult onUse(Player player, ItemStack card) {
        var tag = card.getTag();
        if (tag == null || !tag.contains(start$nbtFilterFluid)) return InteractionResult.PASS;
        if (!this.isRemote() && this.io == IO.OUT) {
            this.tank.getLockedFluid().deserializeNBT(tag.getCompound(start$nbtFilterFluid));
            this.tank.setLocked(true);
            this.updateTankSubscription();
            player.sendSystemMessage(pasteSettings);
        }
        return InteractionResult.sidedSuccess(this.isRemote());
    }

    @Override
    public InteractionResult onShiftUse(Player player, ItemStack card) {
        if (!this.isRemote() && this.io == IO.OUT) {
            var tag = new CompoundTag();
            tag.put(start$nbtFilterFluid, this.tank.getLockedFluid().serializeNBT());
            card.setTag(tag);
            card.setHoverName(card.getHoverName().copy().append(" - ").append(holder.getDefinition().getBlock().getName()));
            player.sendSystemMessage(copySettings);
        }
        return InteractionResult.SUCCESS;
    }
}
