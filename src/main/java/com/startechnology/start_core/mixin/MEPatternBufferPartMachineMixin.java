package com.startechnology.start_core.mixin;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.core.definitions.AEItems;
import appeng.core.localization.PlayerMessages;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.util.inv.PlayerInternalInventory;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.MEPatternBufferPartMachine;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.copy.ICopyInteractable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = MEPatternBufferPartMachine.class, remap = false)
public class MEPatternBufferPartMachineMixin extends MEBusPartMachine implements ICopyInteractable {
    @Shadow @Final private CustomItemStackHandler patternInventory;
    @Shadow @Final private InternalInventory internalPatternInventory;
    @Shadow @Final protected static int MAX_PATTERN_COUNT;

    @Unique
    private final String start$nbtPatterns = "patterns";

    public MEPatternBufferPartMachineMixin(IMachineBlockEntity holder, IO io, Object... args) {
        super(holder, io, args);
    }

    /**
     * Logic for this is derived from {@link appeng.helpers.patternprovider.PatternProviderLogic#importSettings}
     */
    @Override
    public InteractionResult onUse(Player player, ItemStack card) {
        var tag = card.getTag();
        if (tag == null || !tag.contains(start$nbtPatterns)) return InteractionResult.PASS;
        if (!this.isRemote()) {
            this.start$clearPatternInventory(player);
            var desiredPatterns = new CustomItemStackHandler(MAX_PATTERN_COUNT);
            desiredPatterns.deserializeNBT(tag.getCompound(start$nbtPatterns));
            var playerInv = player.getInventory();
            var blankPatternsAvailable = player.getAbilities().instabuild ? Integer.MAX_VALUE : playerInv.countItem(AEItems.BLANK_PATTERN.asItem());
            var blankPatternsUsed = 0;

            for (int slot = 0; slot < desiredPatterns.getSlots(); ++slot) {
                var pattern = PatternDetailsHelper.decodePattern(desiredPatterns.getStackInSlot(slot), this.getLevel(), true);
                if (pattern != null) {
                    ++blankPatternsUsed;
                    if (blankPatternsAvailable >= blankPatternsUsed) {
                        this.internalPatternInventory.setItemDirect(slot, pattern.getDefinition().toStack());
                    }
                }
            }

            if (blankPatternsUsed > 0 && !player.getAbilities().instabuild) {
                new PlayerInternalInventory(playerInv).removeItems(blankPatternsUsed, AEItems.BLANK_PATTERN.stack(), null);
            }

            if (blankPatternsUsed > blankPatternsAvailable) {
                player.sendSystemMessage(PlayerMessages.MissingBlankPatterns.text(blankPatternsUsed - blankPatternsAvailable));
            }

            player.sendSystemMessage(pasteSettings);
        }
        return InteractionResult.sidedSuccess(this.isRemote());
    }

    @Unique
    private void start$clearPatternInventory(Player player) {
        if (player.getAbilities().instabuild) {
            for (int i = 0; i < this.internalPatternInventory.size(); ++i) {
                this.internalPatternInventory.setItemDirect(i, ItemStack.EMPTY);
            }
        } else {
            var playerInv = player.getInventory();
            int blankPatternCount = 0;

            for (int i = 0; i < this.internalPatternInventory.size(); ++i) {
                var pattern = this.internalPatternInventory.getStackInSlot(i);
                if (!pattern.is(AEItems.PROCESSING_PATTERN.asItem())) {
                    playerInv.placeItemBackInInventory(pattern);
                } else {
                    blankPatternCount += pattern.getCount();
                }

                this.internalPatternInventory.setItemDirect(i, ItemStack.EMPTY);
            }
            if (blankPatternCount > 0) {
                playerInv.placeItemBackInInventory(AEItems.BLANK_PATTERN.stack(blankPatternCount), false);
            }
        }
    }

    @Override
    public InteractionResult onShiftUse(Player player, ItemStack card) {
        if (!this.isRemote()) {
            var tag = new CompoundTag();
            tag.put(start$nbtPatterns, this.patternInventory.serializeNBT());
            card.setTag(tag);
            card.setHoverName(card.getHoverName().copy().append(" - ").append(holder.getDefinition().getBlock().getName()));
            player.sendSystemMessage(copySettings);
        }
        return InteractionResult.SUCCESS;
    }
}
