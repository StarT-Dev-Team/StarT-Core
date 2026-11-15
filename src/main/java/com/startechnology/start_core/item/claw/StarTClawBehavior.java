package com.startechnology.start_core.item.claw;

import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.behavior.IToolBehavior;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolAction;

import java.util.List;

public class StarTClawBehavior implements IToolBehavior {

    public static final StarTClawBehavior INSTANCE = new StarTClawBehavior();

    private StarTClawBehavior() {}

    private static List<IToolBehavior> behaviors(ItemStack stack) {
        if (!StarTClawOfLawItem.hasCharge(stack)) return List.of();

        GTToolType toolType = StarTClawOfLawItem.getMode(stack).getToolType();

        return toolType == null ? List.of() : toolType.toolDefinition.getBehaviors();
    }

    @Override
    public void hitEntity(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        behaviors(stack).forEach(behavior -> behavior.hitEntity(stack, target, attacker));
    }

    @Override
    public void onBlockStartBreak(@NotNull ItemStack stack, @NotNull BlockPos pos, @NotNull Player player) {
        behaviors(stack).forEach(behavior -> behavior.onBlockStartBreak(stack, pos, player));
    }

    @Override
    public void onBlockDestroyed(@NotNull ItemStack stack, @NotNull Level world, @NotNull BlockState state,
                                 @NotNull BlockPos pos, @NotNull LivingEntity entityLiving) {
        behaviors(stack).forEach(behavior -> behavior.onBlockDestroyed(stack, world, state, pos, entityLiving));
    }

    @Override
    public void onEntitySwing(@NotNull LivingEntity entityLiving, @NotNull ItemStack stack) {
        behaviors(stack).forEach(behavior -> behavior.onEntitySwing(entityLiving, stack));
    }

    @Override
    public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return behaviors(stack).stream()
                .anyMatch(behavior -> behavior.canDisableShield(stack, shield, entity, attacker));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        for (IToolBehavior behavior : behaviors(stack)) {
            InteractionResult result = behavior.onItemUseFirst(stack, context);

            if (result.consumesAction()) return result;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction action) {
        return behaviors(stack).stream().anyMatch(behavior -> behavior.canPerformAction(stack, action));
    }

    @Override
    public @NotNull InteractionResult onItemUse(UseOnContext context) {
        for (IToolBehavior behavior : behaviors(context.getItemInHand())) {
            InteractionResult result = behavior.onItemUse(context);

            if (result != InteractionResult.PASS) return result;
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> onItemRightClick(@NotNull Level world, @NotNull Player player,
                                                                        @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        for (IToolBehavior behavior : behaviors(stack)) {
            InteractionResultHolder<ItemStack> result = behavior.onItemRightClick(world, player, hand);

            if (result.getResult() == InteractionResult.SUCCESS) return result;
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean shouldOpenUIAfterUse(UseOnContext context) {
        return behaviors(context.getItemInHand()).stream().allMatch(behavior -> behavior.shouldOpenUIAfterUse(context));
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip,
                               @NotNull TooltipFlag flag) {
        behaviors(stack).forEach(behavior -> behavior.addInformation(stack, world, tooltip, flag));
    }

    @Override
    public void addBehaviorNBT(@NotNull ItemStack stack, @NotNull CompoundTag tag) {
        for (StarTClawMode mode : StarTClawMode.values()) {
            if (mode.getToolType() == null) continue;

            mode.getToolType().toolDefinition.getBehaviors().forEach(behavior -> behavior.addBehaviorNBT(stack, tag));
        }
    }
}
