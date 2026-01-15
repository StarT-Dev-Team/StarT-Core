package com.startechnology.start_core.item;

import static com.gregtechceu.gtceu.common.data.GTItems.cellName;
import static com.gregtechceu.gtceu.common.data.GTItems.materialInfo;
import static com.gregtechceu.gtceu.utils.FormattingUtil.toEnglishName;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import com.google.common.base.Preconditions;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.ItemMaterialInfo;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialStack;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.api.item.component.ThermalFluidStats;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.item.DataItemBehavior;
import com.gregtechceu.gtceu.common.item.ItemFluidContainer;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;
import com.startechnology.start_core.item.components.CopyBehavior;
import com.startechnology.start_core.item.components.StarTDreamCopyBehaviour;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidType;

public class StarTItems {
    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }

    public static final ItemEntry<ComponentItem> TOOL_DATA_DNA_DISK = START_REGISTRATE.item("data_dna_disk", ComponentItem::create)
        .lang("Data DNA Disk")
        .onRegister(attach(new DataItemBehavior(true)))
        .onRegister(attach(new TooltipBehavior(lines -> {
            lines.add(Component.translatable("item.start_core.data_dna_disk.tooltip"));
        })))
        .register();

    public static final ItemEntry<ComponentItem> TOOL_COMPONENT_DATA_CORE = START_REGISTRATE.item("component_data_core", ComponentItem::create)
        .lang("Component Data Core")
        .onRegister(attach(new DataItemBehavior(true)))
        .onRegister(attach(new TooltipBehavior(lines -> {
            lines.add(Component.translatable("item.start_core.component_data_core.tooltip"));
        })))
        .register();

    public static final ItemEntry<ComponentItem> TOOL_DREAM_COPY_ITEM = START_REGISTRATE.item("lucinducer", ComponentItem::create)
        .lang("Lucinducer")
        .onRegister(attach(new StarTDreamCopyBehaviour()))
        .onRegister(attach(new TooltipBehavior(lines -> lines.add(Component.translatable("item.start_core.lucinducer.tooltip")))))
        .register();

    public static final ItemEntry<ComponentItem> MECHANICAL_MEMORY_CARD = START_REGISTRATE.item("mechanical_memory_card", ComponentItem::create)
            .lang("Mechanical Memory Card")
            .onRegister(attach(new CopyBehavior()))
            .onRegister(attach(new TooltipBehavior(lines -> lines.add(Component.translatable("item.start_core.mechanical_memory_card.tooltip")))))
            .register();

    public static ItemEntry<ComponentItem> FLUID_CELL_LARGE_ENRICHED_NAQUADAH = createFluidCell(GTMaterials.NaquadahEnriched, 768, 12, 16, true, true, false);
    public static ItemEntry<ComponentItem> FLUID_CELL_LARGE_NEUTRONIUM = createFluidCell(GTMaterials.Neutronium, 1024, 16, 8, true, true, true);

    public static ItemEntry<ComponentItem> createFluidCell(Material mat, int capacity, int matSize, int stackSize, boolean acidProof, boolean cryoProof, boolean plasmaProof) {
        var prop = mat.getProperty(PropertyKey.FLUID_PIPE);
        Preconditions.checkArgument(prop != null,
                "Material { %s } does not have Fluid Pipe properties, but is being used to create a Fluid Cell",
                mat.getName());
        return START_REGISTRATE
                .item("%s_fluid_cell".formatted(mat.getName()), ComponentItem::create)
                .lang("%s " + toEnglishName(mat.getName()) + " Cell")
                .color(() -> GTItems::cellColor)
                .setData(ProviderType.ITEM_MODEL, NonNullBiConsumer.noop())
                .properties(p -> p.stacksTo(stackSize))
                .onRegister(GTItems.attach(cellName(),
                        ThermalFluidStats.create(FluidType.BUCKET_VOLUME * capacity,
                                prop.getMaxFluidTemperature(), true, acidProof, cryoProof, plasmaProof, true),
                        new ItemFluidContainer()))
                .onRegister(
                        materialInfo(new ItemMaterialInfo(new MaterialStack(mat, GTValues.M * matSize))))
                .register();
    }

    public static void init() {
        StarTBacteriaItems.init();
    }
}
