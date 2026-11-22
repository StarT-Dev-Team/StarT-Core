package com.startechnology.start_core.item;

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
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.item.DataItemBehavior;
import com.gregtechceu.gtceu.common.item.ItemFluidContainer;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.item.claw.StarTClawOfLawItem;
import com.startechnology.start_core.item.components.CopyBehavior;
import com.startechnology.start_core.item.components.StarTCoordinateCrystalBehaviour;
import com.startechnology.start_core.item.components.StarTDreamCopyBehaviour;
import com.startechnology.start_core.item.gcrops.*;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fluids.FluidType;

import static com.gregtechceu.gtceu.common.data.GTItems.cellName;
import static com.gregtechceu.gtceu.common.data.GTItems.materialInfo;
import static com.gregtechceu.gtceu.utils.FormattingUtil.toEnglishName;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTItems {

    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }

    public static final ItemEntry<ComponentItem> TOOL_DATA_DNA_DISK = START_REGISTRATE
            .item("data_dna_disk", ComponentItem::create)
            .lang("§dData DNA Disk")
            .onRegister(attach(new DataItemBehavior(true, 320)))
            .onRegister(attach(new TooltipBehavior(lines -> {
                lines.add(Component.translatable("item.start_core.data_dna_disk.tooltip"));
            })))
            .register();

    public static final ItemEntry<ComponentItem> TOOL_COMPONENT_DATA_CORE = START_REGISTRATE
            .item("component_data_core", ComponentItem::create)
            .lang("§dComponent Data Core")
            .onRegister(attach(new DataItemBehavior(true, 500)))
            .onRegister(attach(new TooltipBehavior(lines -> {
                lines.add(Component.translatable("item.start_core.component_data_core.tooltip"));
            })))
            .register();

    public static final ItemEntry<ComponentItem> TOOL_DREAM_COPY_ITEM = START_REGISTRATE
            .item("lucinducer", ComponentItem::create)
            .lang("Lucinducer")
            .onRegister(attach(new StarTDreamCopyBehaviour()))
            .onRegister(attach(new TooltipBehavior(lines -> {
                lines.add(Component.translatable("item.start_core.lucinducer.tooltip1"));
                lines.add(Component.translatable("item.start_core.lucinducer.tooltip2"));
                lines.add(Component.translatable("item.start_core.lucinducer.tooltip3"));
            })))
            .register();

    public static final ItemEntry<ComponentItem> MECHANICAL_MEMORY_CARD = START_REGISTRATE
            .item("mechanical_memory_card", ComponentItem::create)
            .lang("Mechanical Memory Card")
            .onRegister(attach(new CopyBehavior()))
            .onRegister(attach(new TooltipBehavior(lines -> {
                lines.add(Component.translatable("item.start_core.mechanical_memory_card.tooltip"));
                lines.add(Component.translatable("item.start_core.mechanical_memory_card.tooltip.supported",
                        Component.translatable("gui.start_core.tooltips.fluid_output_hatches")));
                lines.add(Component.translatable("item.start_core.mechanical_memory_card.tooltip.supported",
                        GTMachines.CONFIGURABLE_MAINTENANCE_HATCH.getBlock().getName()));
                lines.add(Component.translatable("item.start_core.mechanical_memory_card.tooltip.supported",
                        Component.translatable("gui.start_core.tooltips.me_pattern_buffers")));
            })))
            .register();

    public static final ItemEntry<StarTCoordinateCrystalItem> COORDINATE_CRYSTAL = START_REGISTRATE
            .item("coordinate_crystal", StarTCoordinateCrystalItem::create)
            .lang("Coordinate Crystal")
            .onRegister(attach(new StarTCoordinateCrystalBehaviour()))
            .model((ctx, prov) -> {
                ResourceLocation prop = StarTCore.resourceLocation("crystal_variant");

                var abydosModel = prov.withExistingParent("item/coordinate_crystals/abydos_coordinate_crystal",
                        prov.mcLoc("item/generated"))
                        .texture("layer0",
                                StarTCore.resourceLocation("item/coordinate_crystals/abydos_coordinate_crystal"));

                var netherModel = prov.withExistingParent("item/coordinate_crystals/nether_coordinate_crystal",
                        prov.mcLoc("item/generated"))
                        .texture("layer0",
                                StarTCore.resourceLocation("item/coordinate_crystals/nether_coordinate_crystal"));

                var endModel = prov.withExistingParent("item/coordinate_crystals/end_coordinate_crystal",
                        prov.mcLoc("item/generated"))
                        .texture("layer0",
                                StarTCore.resourceLocation("item/coordinate_crystals/end_coordinate_crystal"));

                var lanteaModel = prov.withExistingParent("item/coordinate_crystals/lantea_coordinate_crystal",
                        prov.mcLoc("item/generated"))
                        .texture("layer0",
                                StarTCore.resourceLocation("item/coordinate_crystals/lantea_coordinate_crystal"));

                var cavumModel = prov.withExistingParent("item/coordinate_crystals/cavum_coordinate_crystal",
                        prov.mcLoc("item/generated"))
                        .texture("layer0",
                                StarTCore.resourceLocation("item/coordinate_crystals/cavum_coordinate_crystal"));

                var seaModel = prov.withExistingParent("item/coordinate_crystals/sea_coordinate_crystal",
                        prov.mcLoc("item/generated"))
                        .texture("layer0",
                                StarTCore.resourceLocation("item/coordinate_crystals/sea_coordinate_crystal"));

                var voidModel = prov.withExistingParent("item/coordinate_crystals/void_coordinate_crystal",
                        prov.mcLoc("item/generated"))
                        .texture("layer0",
                                StarTCore.resourceLocation("item/coordinate_crystals/void_coordinate_crystal"));

                prov.generated(ctx, StarTCore.resourceLocation("item/coordinate_crystals/blank_coordinate_crystal"))
                        .override().predicate(prop, 1.0f).model(abydosModel).end()
                        .override().predicate(prop, 2.0f).model(netherModel).end()
                        .override().predicate(prop, 3.0f).model(endModel).end()
                        .override().predicate(prop, 4.0f).model(lanteaModel).end()
                        .override().predicate(prop, 5.0f).model(cavumModel).end()
                        .override().predicate(prop, 6.0f).model(seaModel).end()
                        .override().predicate(prop, 7.0f).model(voidModel).end();
            }).register();

    public static final ItemEntry<StarTClawOfLawItem> CLAW_OF_LAW = START_REGISTRATE
            .item("claw_of_law",
                    properties -> new StarTClawOfLawItem(properties, GTValues.IV,
                            GTValues.V[GTValues.IV] * 16, GTValues.V[GTValues.HV], false, GTMaterials.BlueSteel,
                            GTMaterials.SiliconeRubber))
            .lang("Claw of Law")
            .setData(ProviderType.ITEM_MODEL, NonNullBiConsumer.noop())
            .properties(p -> p.stacksTo(1).rarity(Rarity.RARE))
            .register();

    public static final ItemEntry<StarTClawOfLawItem> CLAW_OF_LAW_UHV = START_REGISTRATE
            .item("claw_of_law_uhv", properties -> new StarTClawOfLawItem(properties, GTValues.UHV, Long.MAX_VALUE,
                    GTValues.V[GTValues.ZPM], true, GTMaterials.Neutronium,
                    GTMaterials.Polybenzimidazole))
            .lang("Claw of Law")
            .setData(ProviderType.ITEM_MODEL, NonNullBiConsumer.noop())
            .properties(p -> p.stacksTo(1).rarity(Rarity.EPIC))
            .register();

    public static ItemEntry<ComponentItem> FLUID_CELL_LARGE_ENRICHED_NAQUADAH = createFluidCell(
            GTMaterials.NaquadahEnriched, 768, 12, 16, true, true, false);
    public static ItemEntry<ComponentItem> FLUID_CELL_LARGE_NEUTRONIUM = createFluidCell(GTMaterials.Neutronium, 1024,
            16, 8, true, true, true);

    public static ItemEntry<ComponentItem> createFluidCell(Material mat, int capacity, int matSize, int stackSize,
                                                           boolean acidProof, boolean cryoProof, boolean plasmaProof) {
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
        StarTGCropItems.init();
    }
}
