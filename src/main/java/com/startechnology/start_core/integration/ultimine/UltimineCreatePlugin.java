package com.startechnology.start_core.integration.ultimine;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import dev.ftb.mods.ftbultimine.FTBUltiminePlayerData;
import dev.ftb.mods.ftbultimine.api.rightclick.RegisterRightClickHandlerEvent;
import dev.ftb.mods.ftbultimine.integration.FTBUltiminePlugin;
import dev.ftb.mods.ftbultimine.shape.ShapeContext;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.Collection;

public class UltimineCreatePlugin {

    public static void init() {
        FTBUltiminePlugin.register(new FTBUltiminePlugin() {

            @Override
            public boolean canUltimine(Player player) {
                return UltimineCreatePlugin.canUltimine(player);
            }
        });

        RegisterRightClickHandlerEvent.REGISTER
                .register(dispatcher -> dispatcher.registerHandler(UltimineCreatePlugin::handleRightClickBlock));
    }

    private static final ReferenceSet<Player> disabledUltimineWhenWrenching = new ReferenceOpenHashSet<>();

    private static boolean canUltimine(Player player) {
        return !disabledUltimineWhenWrenching.contains(player);
    }

    private static int handleRightClickBlock(ShapeContext shapeContext, InteractionHand hand,
                                             Collection<BlockPos> positions) {
        var player = shapeContext.player();
        var blockState = shapeContext.block(shapeContext.pos());
        var heldItem = player.getItemInHand(hand);

        if (isManualApplication(player, blockState, heldItem)) {
            return handleManualApplicationRightClick(player, hand, positions);
        }

        if (isWrenchRightClick(heldItem)) {
            return handleWrenchRightClick(player, hand, heldItem, positions);
        }

        return 0;
    }

    private static boolean isManualApplication(ServerPlayer player, BlockState blockState, ItemStack heldItem) {
        return player.server.getRecipeManager()
                .getAllRecipesFor(AllRecipeTypes.ITEM_APPLICATION.<RecipeType<ManualApplicationRecipe>>getType())
                .stream()
                .anyMatch(r -> r.testBlock(blockState) && r.getIngredients().get(1).test(heldItem));
    }

    private static int handleManualApplicationRightClick(ServerPlayer player, InteractionHand hand,
                                                         Collection<BlockPos> positions) {
        var hitResult = FTBUltiminePlayerData.rayTrace(player);
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return 0;

        return (int) positions.stream().filter(pos -> {
            var event = new PlayerInteractEvent.RightClickBlock(player, hand, pos, blockHitResult.withPosition(pos));
            ManualApplicationRecipe.manualApplicationRecipesApplyInWorld(event);
            return event.isCanceled() && event.getCancellationResult() == InteractionResult.SUCCESS;
        }).count();
    }

    private static boolean isWrenchRightClick(ItemStack heldItem) {
        return AllItems.WRENCH.isIn(heldItem) || AllTags.AllItemTags.WRENCH.matches(heldItem.getItem());
    }

    private static boolean wrenchBlock(BlockState state, ServerPlayer player, BlockHitResult hitVec,
                                       InteractionHand hand) {
        var block = state.getBlock();
        if (!(block instanceof IWrenchable actor))
            return false;
        var context = new UseOnContext(player, hand, hitVec);
        var result = player.isShiftKeyDown() ? actor.onSneakWrenched(state, context) : actor.onWrenched(state, context);
        return result == InteractionResult.SUCCESS;
    }

    private static int handleWrenchRightClick(ServerPlayer player, InteractionHand hand, ItemStack heldItem,
                                              Collection<BlockPos> positions) {
        var hitResult = FTBUltiminePlayerData.rayTrace(player);
        if (!(hitResult instanceof BlockHitResult blockHitResult)) return 0;

        @SuppressWarnings("resource")
        var level = player.serverLevel();

        var shift = player.isShiftKeyDown();
        if (shift) disabledUltimineWhenWrenching.add(player);

        try {
            return (int) positions.stream().filter(pos -> {
                var state = level.getBlockState(pos);
                return wrenchBlock(state, player, blockHitResult.withPosition(pos), hand);
            }).count();
        } finally {
            if (shift) disabledUltimineWhenWrenching.remove(player);
        }
    }
}
