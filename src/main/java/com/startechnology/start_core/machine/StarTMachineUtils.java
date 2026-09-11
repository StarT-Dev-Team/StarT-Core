package com.startechnology.start_core.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import com.gregtechceu.gtceu.api.registry.registrate.MultiblockMachineBuilder;
import dev.latvian.mods.kubejs.KubeJS;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTMachineUtils {

    public static MultiblockMachineDefinition[] registerTieredMultis(String name,
                                                                     BiFunction<IMachineBlockEntity, Integer, MultiblockControllerMachine> factory,
                                                                     BiFunction<Integer, MultiblockMachineBuilder, MultiblockMachineDefinition> builder,
                                                                     int... tiers) {
        MultiblockMachineDefinition[] definitions = new MultiblockMachineDefinition[GTValues.TIER_COUNT];
        for (int tier : tiers) {
            var register = START_REGISTRATE
                    .multiblock(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_" + name,
                            holder -> factory.apply(holder, tier))
                    .tier(tier);
            definitions[tier] = builder.apply(tier, register);
        }
        return definitions;
    }

    public static MachineDefinition[] registerTieredMachines(String name,
                                                             BiFunction<IMachineBlockEntity, Integer, MetaMachine> factory,
                                                             BiFunction<Integer, MachineBuilder<MachineDefinition>, MachineDefinition> builder,
                                                             int... tiers) {
        MachineDefinition[] definitions = new MachineDefinition[GTValues.TIER_COUNT];
        for (int tier : tiers) {
            var register = START_REGISTRATE
                    .machine(GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_" + name,
                            holder -> factory.apply(holder, tier))
                    .tier(tier);
            definitions[tier] = builder.apply(tier, register);
        }
        return definitions;
    }

    public static Block getKjsBlock(String block) {
        return ForgeRegistries.BLOCKS.getValue(KubeJS.id(block));
    }

    public static Block getBlock(String block) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(block));
    }

    public static Block getGTCEuBlock(String block) {
        return ForgeRegistries.BLOCKS.getValue(GTCEu.id(block));
    }

    public static Item item(ResourceLocation itemId) {
        return ForgeRegistries.ITEMS.getValue(itemId);
    }

    public static Item item(String itemId) {
        return item(new ResourceLocation(itemId));
    }

    public static Supplier<Item> lazyItem(ResourceLocation itemId) {
        return Lazy.of(() -> item(itemId));
    }

    public static Supplier<Item> lazyItem(String itemId) {
        return lazyItem(new ResourceLocation(itemId));
    }

    public static Block block(ResourceLocation blockId) {
        return ForgeRegistries.BLOCKS.getValue(blockId);
    }

    public static Block block(String blockId) {
        return block(new ResourceLocation(blockId));
    }

    public static Supplier<Block> lazyBlock(ResourceLocation blockId) {
        return Lazy.of(() -> block(blockId));
    }

    public static Supplier<Block> lazyBlock(String blockId) {
        return lazyBlock(new ResourceLocation(blockId));
    }

    public static Supplier<Fluid> lazyFluid(ResourceLocation fluidId) {
        return Lazy.of(() -> ForgeRegistries.FLUIDS.getValue(fluidId));
    }

    public static Supplier<Fluid> lazyFluid(String fluidId) {
        return lazyFluid(new ResourceLocation(fluidId));
    }

    public static ResourceLocation getItemId(Item item) {
        return ForgeRegistries.ITEMS.getKey(item);
    }

    public static ResourceLocation getBlockId(Block item) {
        return ForgeRegistries.BLOCKS.getKey(item);
    }
}
