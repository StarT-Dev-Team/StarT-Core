package com.startechnology.start_core.integration.jade.provider;

import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.machine.abyssal_harvester.StarTAbyssalHarvesterMachine;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import com.startechnology.start_core.machine.modular.StarTModularInterfaceHatchPartMachine;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class StarTModularInterfaceHatchPartMachineProvider extends CapabilityBlockProvider<StarTModularInterfaceHatchPartMachine> {
    
    public StarTModularInterfaceHatchPartMachineProvider() {
        super(StarTCore.resourceLocation("modular_interface_hatch_part_machine"));
    }

    @Override
    protected @Nullable StarTModularInterfaceHatchPartMachine getCapability(Level level, BlockPos pos,
            @Nullable Direction side) {
        var capability = StarTCapabilityHelper.getModularInterfaceHatchPartMachine(level, pos, side);

        if (capability != null)
            return capability;

        /* Duplicate node interface display onto the multi controller */
        if (MetaMachine.getMachine(level, pos) instanceof IMultiController controller) {
            for (var part : controller.getParts()) {
                if (part instanceof StarTModularInterfaceHatchPartMachine modularHatch) {
                    if (!modularHatch.isTerminal()) {
                        return modularHatch;
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected void write(CompoundTag data, StarTModularInterfaceHatchPartMachine capability) {
        boolean isTerminal = capability.isTerminal();
        data.putBoolean("is_terminal", isTerminal);
        data.putBoolean("is_linked", capability.isCurrentlyLinked());

        ResourceLocation linkedModule = capability.getLastSupportedModuleName();
        if (isTerminal && linkedModule != null) {
            data.putString("linked_module_namespace", linkedModule.getNamespace());
            data.putString("linked_module_path", linkedModule.getPath());
        }
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
            BlockEntity blockEntity, IPluginConfig config) {
        if (capData.contains("is_terminal"))
        {
            boolean isTerminal = capData.getBoolean("is_terminal");
            boolean isLinked = capData.getBoolean("is_linked");

            if (isLinked) {
                tooltip.add(Component.translatable("modular.start_core.has_link").withStyle(ChatFormatting.GREEN));
                
                if (isTerminal && capData.contains("linked_module_namespace") && capData.contains("linked_module_path")) {
                    tooltip.add(Component.empty());
                    tooltip.add(Component.translatable("modular.start_core.linked_type").withStyle(ChatFormatting.GOLD));
                    tooltip.add(Component.translatable("block." + capData.getString("linked_module_namespace") + "." + capData.getString("linked_module_path")));
                }

            } else {
                tooltip.add(Component.translatable("modular.start_core.no_link").withStyle(ChatFormatting.RED));
            }

        }
    }
    
}
