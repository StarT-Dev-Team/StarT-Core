package com.startechnology.start_core.integration.jade.provider;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.integration.jade.MachineJadeProvider;
import com.startechnology.start_core.machine.modular.StarTModularConduitAutoScalingHatchPartMachine;
import com.startechnology.start_core.machine.modular.StarTModularInterfaceHatchPartMachine;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class StarTModularInterfaceHatchPartMachineProvider extends
                                                           MachineJadeProvider<StarTModularInterfaceHatchPartMachine> {

    public StarTModularInterfaceHatchPartMachineProvider() {
        super(StarTCore.resourceLocation("modular_interface_hatch_part_machine"),
                StarTModularInterfaceHatchPartMachine.class);
    }

    @Override
    protected @Nullable StarTModularInterfaceHatchPartMachine getMachine(Level level, BlockPos pos) {
        MetaMachine machine = MetaMachine.getMachine(level, pos);
        if (machine instanceof StarTModularInterfaceHatchPartMachine modularHatch) {
            return modularHatch;
        }

        /* Duplicate node interface display onto the multi controller */
        if (machine instanceof IMultiController controller) {
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
    protected void write(CompoundTag data, StarTModularInterfaceHatchPartMachine machine) {
        boolean isTerminal = machine.isTerminal();
        data.putBoolean("is_terminal", isTerminal);
        data.putBoolean("is_linked", machine.isCurrentlyLinked());

        ResourceLocation linkedModule = machine.getLastSupportedModuleName();
        if (isTerminal && linkedModule != null) {
            data.putString("linked_module_namespace", linkedModule.getNamespace());
            data.putString("linked_module_path", linkedModule.getPath());
        }

        if (machine instanceof StarTModularConduitAutoScalingHatchPartMachine autoScalingHatch) {
            data.putLong("scaled_voltage", autoScalingHatch.getScaledVoltage());
            data.putLong("scaled_amperage", autoScalingHatch.getScaledAmperage());
        }
    }

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (capData.contains("is_terminal")) {
            boolean isTerminal = capData.getBoolean("is_terminal");
            boolean isLinked = capData.getBoolean("is_linked");

            if (capData.contains("scaled_voltage") && capData.contains("scaled_amperage")) {
                long scaledVoltage = capData.getLong("scaled_voltage");
                long scaledAmperage = capData.getLong("scaled_amperage");

                StarTModularConduitAutoScalingHatchPartMachine
                        .addDisplayTextToList(tooltip::add, scaledVoltage, scaledAmperage);
            }

            if (isLinked) {
                tooltip.add(Component.translatable("modular.start_core.has_link").withStyle(ChatFormatting.GREEN));

                if (isTerminal && capData.contains("linked_module_namespace") &&
                        capData.contains("linked_module_path")) {
                    tooltip.add(Component.empty());
                    tooltip.add(
                            Component.translatable("modular.start_core.linked_type").withStyle(ChatFormatting.GOLD));
                    tooltip.add(Component.translatable("block." + capData.getString("linked_module_namespace") + "." +
                            capData.getString("linked_module_path")));
                }

            } else {
                tooltip.add(Component.translatable("modular.start_core.no_link").withStyle(ChatFormatting.RED));
            }
        }
    }
}
