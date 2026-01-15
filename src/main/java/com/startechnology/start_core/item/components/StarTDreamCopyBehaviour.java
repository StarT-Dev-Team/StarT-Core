package com.startechnology.start_core.item.components;

// StarTDreamCopyBehaviour
// 
// Behaviour that allows an item to be a copying tool for
// dream-link network information
//
// Adds a tooltip description & Allows for interaction for copying.


import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.machine.MachineCoverContainer;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.common.machine.owner.IMachineOwner;
import com.startechnology.start_core.api.dreamlink.IStarTDreamCopyInteractable;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class StarTDreamCopyBehaviour implements IInteractionItem  {

    @Override
    public InteractionResult onItemUseFirst(ItemStack itemStack, UseOnContext context) {
        /* Ensure we only operate on meta machines */
        if (context.getLevel().getBlockEntity(context.getClickedPos()) instanceof MetaMachineBlockEntity blockEntity) {
            if (!IMachineOwner.canOpenOwnerMachine(context.getPlayer(), blockEntity)) {
                return InteractionResult.FAIL;
            }
            MetaMachine machine = blockEntity.getMetaMachine();

            /* Handle case where machine implements IStarTDreamCopyInteractable */
            if (machine instanceof IStarTDreamCopyInteractable interactable) {
                if (context.isSecondaryUseActive()) {
                    return interactable.onDreamCopyShiftUse(context.getPlayer(), itemStack);
                } else {
                    return interactable.onDreamCopyUse(context.getPlayer(), itemStack);
                }
            }

            /* Try hit covers on the machine on this side */
            MachineCoverContainer coverContainer = machine.getCoverContainer();
            CoverBehavior cover = coverContainer.getCoverAtSide(context.getClickedFace());

            /* Handle case where cover implements IStarTDreamCopyInteractable */
            if (cover instanceof IStarTDreamCopyInteractable interactable) {
                if (context.isSecondaryUseActive()) {
                    return interactable.onDreamCopyShiftUse(context.getPlayer(), itemStack);
                } else {
                    return interactable.onDreamCopyUse(context.getPlayer(), itemStack);
                }
            }
            
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

}
