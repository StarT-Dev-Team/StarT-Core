package com.startechnology.start_core.recipe.logic;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialStack;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.startechnology.start_core.machine.ore_factory.OreFactoryMachine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class OreFactoryLogic implements ICustomRecipeLogic {

    private static final int BASE_DURATION = 240;
    private static final int RESIDUE_OUTPUT = 1000;

    private static final int PRIMARY_CHANCE = 8000;
    private static final int SECONDARY_CHANCE = 6000;
    private static final int TERTIARY_CHANCE = 3600;
    private static final int QUATERNARY_CHANCE = 2000;
    private static final int QUINARY_CHANCE = 1000;

    private static final int BULK_PRIMARY = 8;
    private static final int BULK_SECONDARY = 6;
    private static final int BULK_TERTIARY = 4;
    private static final int BULK_QUATERNARY = 2;
    private static final int BULK_QUINARY = 1;

    private static final Map<String, OreData> ORES = buildOreData();

    private final int scale;
    private final Supplier<GTRecipeType> recipeType;

    private record OreData(int tier, String secondary, String tertiary, String quaternary, String quinary) {}

    public OreFactoryLogic(int scale, Supplier<GTRecipeType> recipeType) {
        this.scale = scale;
        this.recipeType = recipeType;
    }

    private static void addOre(Map<String, OreData> ores, int tier, String material, String secondary, String tertiary,
                               String quaternary, String quinary) {
        ores.put(material, new OreData(tier, secondary, tertiary, quaternary, quinary));
    }

    private static Map<String, OreData> buildOreData() {
        Map<String, OreData> ores = new HashMap<>();

        addOre(ores, GTValues.ULV, "iron", "nickel", "tin", null, null);
        addOre(ores, GTValues.ULV, "magnetite", "gold", "gold", null, null);
        addOre(ores, GTValues.ULV, "copper", "gold", "nickel", null, null);
        addOre(ores, GTValues.ULV, "tin", "iron", "zinc", null, null);
        addOre(ores, GTValues.ULV, "sphalerite", "gallium", "sulfur", null, null);
        addOre(ores, GTValues.ULV, "galena", "silver", "sulfur", null, null);
        addOre(ores, GTValues.ULV, "stibnite", "antimony", "sulfur", null, null);

        addOre(ores, GTValues.LV, "coal", "carbon", "thorium", "graphite", null);
        addOre(ores, GTValues.LV, "sodalite", "lazurite", "lapis", "kyanite", null);
        addOre(ores, GTValues.LV, "realgar", "sulfur", "antimony", "barite", null);
        addOre(ores, GTValues.LV, "pentlandite", "cobalt", "iron", "sulfur", null);
        addOre(ores, GTValues.LV, "silver", "gold", "lead", "sulfur", null);
        addOre(ores, GTValues.LV, "gold", "silver", "copper", "nickel", null);
        addOre(ores, GTValues.LV, "diamond", "carbon", "graphite", "graphite", null);
        addOre(ores, GTValues.LV, "emerald", "beryllium", "aluminium", "aluminium", null);
        addOre(ores, GTValues.LV, "ruby", "chromium", "red_garnet", "cinnabar", null);
        addOre(ores, GTValues.LV, "green_sapphire", "aluminium", "sapphire", "almandine", null);
        addOre(ores, GTValues.LV, "sapphire", "aluminium", "green_sapphire", "almandine", null);
        addOre(ores, GTValues.LV, "quartzite", "certus_quartz", "opal", "barite", null);
        addOre(ores, GTValues.LV, "certus_quartz", "quartzite", "opal", "barite", null);
        addOre(ores, GTValues.LV, "rock_salt", "salt", "salt", "borax", null);
        addOre(ores, GTValues.LV, "saltpeter", "salt", "potassium", "alunite", null);
        addOre(ores, GTValues.LV, "salt", "rock_salt", "rock_salt", "borax", null);
        addOre(ores, GTValues.LV, "amethyst", "amethyst", "amethyst", "amethyst", null);
        addOre(ores, GTValues.LV, "sulfur", "sulfur", "sulfur", "sulfur", null);
        addOre(ores, GTValues.LV, "lapis", "lazurite", "sodalite", "pyrite", null);
        addOre(ores, GTValues.LV, "nether_quartz", "quartzite", "quartzite", "quartzite", null);

        addOre(ores, GTValues.MV, "blue_topaz", "bastnasite", "topaz", "topaz", null);
        addOre(ores, GTValues.MV, "topaz", "bastnasite", "blue_topaz", "blue_topaz", null);
        addOre(ores, GTValues.MV, "spessartine", "manganese", "red_garnet", "asbestos", null);
        addOre(ores, GTValues.MV, "monazite", "thorium", "thorium", "neodymium", null);
        addOre(ores, GTValues.MV, "apatite", "tricalcium_phosphate", "tricalcium_phosphate", "phosphate", null);
        addOre(ores, GTValues.MV, "lepidolite", "lithium", "lithium", "caesium", null);
        addOre(ores, GTValues.MV, "pyrochlore", "apatite", "apatite", "calcium", null);
        addOre(ores, GTValues.MV, "pyrolusite", "manganese", "manganese", "tantalite", null);
        addOre(ores, GTValues.MV, "magnesite", "magnesium", "magnesium", "cobaltite", null);
        addOre(ores, GTValues.MV, "red_garnet", "spessartine", "pyrope", "almandine", null);
        addOre(ores, GTValues.MV, "yellow_garnet", "andradite", "grossular", "uvarovite", null);
        addOre(ores, GTValues.MV, "garnet_sand", "red_garnet", "yellow_garnet", "yellow_garnet", null);

        addOre(ores, GTValues.HV, "beryllium", "emerald", "emerald", "beryllium", null);
        addOre(ores, GTValues.HV, "chalcopyrite", "gold", "pyrite", "cobalt", null);
        addOre(ores, GTValues.HV, "bornite", "gold", "cadmium", "cobalt", null);
        addOre(ores, GTValues.HV, "barite", "pyrite", "pyrite", "chalcopyrite", null);
        addOre(ores, GTValues.HV, "cassiterite", "tin", "tin", "bismuth", null);
        addOre(ores, GTValues.HV, "tantalite", "manganese", "niobium", "niobium", null);
        addOre(ores, GTValues.HV, "pollucite", "caesium", "aluminium", "aluminium", null);
        addOre(ores, GTValues.HV, "zavaritskite", "topaz", "blue_topaz", "bismuth", null);

        addOre(ores, GTValues.EV, "cooperite", "palladium", "nickel", "nickel", null);
        addOre(ores, GTValues.EV, "bastnasite", "neodymium", "neodymium", "rare_earth", null);
        addOre(ores, GTValues.EV, "tungstate", "silver", "manganese", "lithium", null);
        addOre(ores, GTValues.EV, "bauxite", "gallium", "grossular", "rutile", null);
        addOre(ores, GTValues.EV, "pitchblende", "thorium", "thorium", "uraninite", null);
        addOre(ores, GTValues.EV, "ilmenite", "iron", "iron", "rutile", null);
        addOre(ores, GTValues.EV, "molybdenite", "molybdenum", "sulfur", "molybdenum", null);
        addOre(ores, GTValues.EV, "scheelite", "manganese", "manganese", "molybdenum", null);
        addOre(ores, GTValues.EV, "cobaltite", "cobalt", "sulfur", "cobalt", null);
        addOre(ores, GTValues.EV, "chromite", "iron", "magnesium", "iron", null);

        addOre(ores, GTValues.IV, "naquadah", "barium", "sulfur", "enriched_naquadah", "enriched_naquadah");
        addOre(ores, GTValues.IV, "titanite", "rutile", "xenotime", "calcite", "pyrite");
        addOre(ores, GTValues.IV, "xenotime", "monazite", "rare_earth", "yttrium", "phosphate");
        addOre(ores, GTValues.IV, "zapolite", "lautarite", "crookesite", "kitkaite", "bauxite");
        addOre(ores, GTValues.IV, "lautarite", "zapolite", "kitkaite", "crookesite", "calcium");
        addOre(ores, GTValues.IV, "crookesite", "kitkaite", "zapolite", "lautarite", "bornite");
        addOre(ores, GTValues.IV, "kitkaite", "crookesite", "lautarite", "zapolite", "nickel");
        addOre(ores, GTValues.IV, "strontianite", "celestine", "gypsum", "calcite", "calcium");
        addOre(ores, GTValues.IV, "celestine", "strontianite", "calcite", "gypsum", "calcium");
        addOre(ores, GTValues.IV, "naquadite", "magnesite", "chromite", "naquadah", "magnetite");

        return Map.copyOf(ores);
    }

    private static ItemStack dust(String materialName, int amount) {
        if (materialName == null) return ItemStack.EMPTY;

        Material material = GTMaterials.get(materialName);
        if (material == null) return ItemStack.EMPTY;

        return ChemicalHelper.get(TagPrefix.dust, material, amount);
    }

    @Override
    public void buildRepresentativeRecipes() {
        List<FluidStack> fluids = OreFactoryMachine.FLUID_STATS().entrySet().stream().map(entry -> entry.getKey()
                .getFluid(entry.getValue().amount() * scale)).toList();

        FluidIngredient fluidIngredient = FluidIngredient.of(fluids);
        OreFactoryMachine.FluidStats baseStats = OreFactoryMachine.FLUID_STATS().get(OreFactoryMachine.BASE_FLUID());

        for (Fluid residue : ForgeRegistries.FLUIDS.getValues()) {
            ResourceLocation id = ForgeRegistries.FLUIDS.getKey(residue);

            if (id == null || !id.getNamespace().equals("kubejs") || !id.getPath().endsWith("_residue")) continue;

            String materialName = id.getPath().substring(0, id.getPath().length() - "_residue".length());

            OreData oreData = ORES.get(materialName);
            if (oreData == null) continue;

            Material material = GTMaterials.get(materialName);
            if (material == null) continue;

            ItemStack crushedOre = ChemicalHelper.get(TagPrefix.crushed, material, scale);
            if (crushedOre.isEmpty()) continue;

            GTRecipeBuilder builder = recipeType.get()
                    .recipeBuilder(materialName + "_ore_factory")
                    .inputItems(crushedOre)
                    .inputFluids(fluidIngredient)
                    .outputFluids(new FluidStack(residue, RESIDUE_OUTPUT * scale))
                    .duration((int) Math.round(BASE_DURATION * scale * baseStats.durationMultiplier()))
                    .EUt((long) Math.round(GTValues.V[oreData.tier()] * baseStats.euMultiplier()));

            addItemOutputs(builder, materialName, oreData);

            GTRecipe recipe = builder.buildRawRecipe();
            recipe.setId(recipe.getId().withPrefix("/"));
            recipeType.get().addToMainCategory(recipe);
        }
    }

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        List<?> capabilities = Objects.requireNonNullElseGet(
                holder.getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP),
                Collections::emptyList);

        for (Object capability : capabilities) {
            if (capability instanceof NotifiableFluidTank tank && tank.getTanks() > 0) {
                GTRecipe recipe = createRecipe(holder, tank);
                if (recipe != null) return recipe;
            }
        }

        return null;
    }

    private ItemStack findCrushedOre(IRecipeCapabilityHolder holder) {
        List<?> capabilities = Objects.requireNonNullElseGet(
                holder.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP),
                Collections::emptyList);

        for (Object capability : capabilities) {
            if (!(capability instanceof NotifiableItemStackHandler handler)) continue;

            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);

                if (!stack.isEmpty() && stack.is(OreFactoryMachine.CRUSHED_ORE)) {
                    return stack;
                }
            }
        }

        return null;
    }

    private GTRecipe createRecipe(IRecipeCapabilityHolder holder, NotifiableFluidTank tank) {
        ItemStack ore = findCrushedOre(holder);
        if (ore == null) return null;

        MaterialStack oreMaterial = ChemicalHelper.getMaterialStack(ore);
        if (oreMaterial == null) return null;

        String materialName = oreMaterial.material().getName();

        OreData oreData = ORES.get(materialName);
        if (oreData == null) return null;

        Fluid residue = ForgeRegistries.FLUIDS.getValue(new ResourceLocation("kubejs", materialName + "_residue"));

        if (residue == null) return null;

        for (int i = 0; i < tank.getTanks(); i++) {
            FluidStack input = tank.getFluidInTank(i);

            if (input == null || input.isEmpty()) continue;

            Material fluidMaterial = ChemicalHelper.getMaterial(input.getFluid());
            if (fluidMaterial == null) continue;

            OreFactoryMachine.FluidStats stats = OreFactoryMachine.FLUID_STATS().get(fluidMaterial);

            if (stats == null) continue;

            FluidStack fluidInput = input.copy();
            fluidInput.setAmount(stats.amount() * scale);

            GTRecipeBuilder builder = recipeType.get()
                    .recipeBuilder("ore_factory")
                    .inputItems(ore.copyWithCount(scale))
                    .inputFluids(fluidInput)
                    .outputFluids(new FluidStack(residue, RESIDUE_OUTPUT * scale))
                    .duration((int) Math.round(BASE_DURATION * scale * stats.durationMultiplier()))
                    .EUt((long) Math.round(GTValues.V[oreData.tier()] * stats.euMultiplier()));

            addItemOutputs(builder, materialName, oreData);

            return builder.buildRawRecipe();
        }

        return null;
    }

    private void addItemOutputs(GTRecipeBuilder builder, String primary, OreData oreData) {
        if (scale == 10) {
            addBulkOutputs(builder, primary, oreData);
        } else {
            addNormalOutputs(builder, primary, oreData);
        }
    }

    private static void addNormalOutputs(GTRecipeBuilder builder, String primary, OreData oreData) {
        ItemStack primaryDust = dust(primary, 1);

        if (!primaryDust.isEmpty()) {
            builder.outputItems(primaryDust);
            builder.chancedOutput(primaryDust.copy(), PRIMARY_CHANCE, 0);
        }

        addChancedDust(builder, oreData.secondary(), SECONDARY_CHANCE);
        addChancedDust(builder, oreData.tertiary(), TERTIARY_CHANCE);
        addChancedDust(builder, oreData.quaternary(), QUATERNARY_CHANCE);
        addChancedDust(builder, oreData.quinary(), QUINARY_CHANCE);
    }

    private static void addBulkOutputs(GTRecipeBuilder builder, String primary, OreData oreData) {
        ItemStack primaryDust = dust(primary, 10);

        if (!primaryDust.isEmpty()) {
            builder.outputItems(primaryDust);
        }
        addGuaranteedDust(builder, primary, BULK_PRIMARY);
        addGuaranteedDust(builder, oreData.secondary(), BULK_SECONDARY);
        addGuaranteedDust(builder, oreData.tertiary(), BULK_TERTIARY);
        addGuaranteedDust(builder, oreData.quaternary(), BULK_QUATERNARY);
        addGuaranteedDust(builder, oreData.quinary(), BULK_QUINARY);
    }

    private static void addGuaranteedDust(GTRecipeBuilder builder, String material, int amount) {
        ItemStack output = dust(material, amount);

        if (!output.isEmpty()) {
            builder.outputItems(output);
        }
    }

    private static void addChancedDust(GTRecipeBuilder builder, String material, int chance) {
        ItemStack output = dust(material, 1);

        if (!output.isEmpty()) {
            builder.chancedOutput(output, chance, 0);
        }
    }
}
