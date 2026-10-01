package com.startechnology.start_core.recipe;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeSerializer;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.startechnology.start_core.block.solar.StarTSolarCellBlocks;
import com.startechnology.start_core.machine.black_hole.BlackHoleSeeds;
import com.startechnology.start_core.machine.dyson_sphere.StellarBalance;
import com.startechnology.start_core.machine.fusion.ReflectorFusionReactorMachine;
import com.startechnology.start_core.machine.hellforge.StarTHellForgeMachine;
import com.startechnology.start_core.machine.neutron_star.NeutronStarBalance;
import com.startechnology.start_core.recipe.logic.ArborealExtractionRecipeLogic;
import com.startechnology.start_core.recipe.logic.HellForgeHeatingLogic;
import com.startechnology.start_core.recipe.logic.SolarPanelReplacementLogic;
import com.startechnology.start_core.recipe.logic.bacteria.BacteriaVatLogic;
import com.startechnology.start_core.recipe.logic.bacteria.BacterialDormantAwakeningLogic;
import com.startechnology.start_core.recipe.logic.bacteria.BacterialHydrocarbonHarvesterLogic;
import com.startechnology.start_core.recipe.logic.bacteria.BacterialRunicMutatorLogic;
import com.startechnology.start_core.recipe.logic.gcrops.GCropBreederLogic;
import com.startechnology.start_core.recipe.logic.gcrops.GCropHarvesterLogic;
import com.startechnology.start_core.recipe.logic.gcrops.GCropMutatorLogic;
import com.startechnology.start_core.recipe.logic.gcrops.GCropSeedDiscoveryLogic;
import com.startechnology.start_core.recipe.logic.gcrops.GenomeDuplicationLogic;
import com.startechnology.start_core.recipe.logic.gcrops.GenomeHarvestingLogic;
import com.startechnology.start_core.recipe.logic.gcrops.GenomeInsertionLogic;
import com.startechnology.start_core.recipe.logic.gcrops.GenomeMixingLogic;
import com.startechnology.start_core.recipe.logic.gcrops.GenomeSeparatingLogic;
import com.startechnology.start_core.recipe.recipes.DysonSphereRecipes;
import com.startechnology.start_core.recipe.recipes.NeutronStarRecipes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeType;

public class StarTRecipeTypes {

    public static final GTRecipeType FUSION_RECIPES = GTRecipeTypes
            .register("reflector_fusion_reactor", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(0, 0, 2, 1)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .setSound(GTSoundEntries.ARC)
            .setOffsetVoltageText(true)
            .setVoltageTextOffset(19)
            .setMaxTooltips(4)
            .addDataInfo(data -> {
                var reflectorTier = data.getInt("reflector_tier");
                return LocalizationUtils.format("start_core.recipe.min_reflector_tier", reflectorTier);
            })
            .setUiBuilder(ReflectorFusionReactorMachine::addEUToStartLabel);

    public static final GTRecipeType BACTERIAL_BREEDING_VAT_RECIPES = GTRecipeTypes
            .register("bacterial_breeding_vat", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(1, 2, 2, 0)
            .setEUIO(IO.IN)
            .addCustomRecipeLogic(new BacteriaVatLogic())
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType ABYSSAL_CONTAINMENT_RECIPE_TYPE = GTRecipeTypes
            .register("abyssal_containment", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(0, 0, 2, 0)
            .setEUIO(IO.IN);

    public static final GTRecipeType BACTERIAL_RUNIC_MUTATOR_RECIPES = GTRecipeTypes
            .register("bacterial_runic_mutator", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(2, 1, 2, 0)
            .setEUIO(IO.IN)
            .addCustomRecipeLogic(new BacterialRunicMutatorLogic())
            .addCustomRecipeLogic(new BacterialDormantAwakeningLogic())
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType BACTERIAL_HYDROCARBON_HARVESTER_RECIPES = GTRecipeTypes
            .register("bacterial_hydrocarbon_harvester", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(2, 0, 2, 5)
            .setEUIO(IO.IN)
            .addCustomRecipeLogic(new BacterialHydrocarbonHarvesterLogic())
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType GCROP_MUTATOR_RECIPES = GTRecipeTypes
            .register("gcrop_mutator", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3, 1, 3, 0)
            .setEUIO(IO.IN)
            .addCustomRecipeLogic(new GCropMutatorLogic())
            .addCustomRecipeLogic(new GCropSeedDiscoveryLogic())
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType GCROP_BREEDER_RECIPES = GTRecipeTypes
            .register("gcrop_breeder", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3, 1, 2, 0)
            .setEUIO(IO.IN)
            .addCustomRecipeLogic(new GCropBreederLogic())
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType GCROP_HARVESTER_RECIPES = GTRecipeTypes
            .register("gcrop_harvester", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3, 1, 2, 0)
            .setEUIO(IO.IN)
            .addCustomRecipeLogic(new GCropHarvesterLogic())
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType VOID_MESH = GTRecipeTypes
            .register("void_mesh", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(1, 1, 0, 0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType VOID_GAS_COLLECTOR = GTRecipeTypes
            .register("void_gas_collector", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(1, 0, 0, 1)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType GENOME_GATHERING = GTRecipeTypes
            .register("genome_gathering", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3, 2, 3, 0)
            .setEUIO(IO.IN)
            .addCustomRecipeLogic(new GenomeDuplicationLogic())
            .addCustomRecipeLogic(new GenomeHarvestingLogic())
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType GENOME_MIXING = GTRecipeTypes
            .register("genome_mixing", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3, 2, 3, 0)
            .setEUIO(IO.IN)
            .addCustomRecipeLogic(new GenomeMixingLogic())
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType GENOME_SEPARATING = GTRecipeTypes
            .register("genome_separating", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3, 2, 3, 0)
            .setEUIO(IO.IN)
            .addCustomRecipeLogic(new GenomeSeparatingLogic())
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType GENOME_INSERTION = GTRecipeTypes
            .register("genome_insertion", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3, 2, 3, 0)
            .setEUIO(IO.IN)
            .addCustomRecipeLogic(new GenomeInsertionLogic())
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType VACUUM_CHEMICAL_REACTION_CHAMBER_RECIPES = GTRecipeTypes
            .register("vacuum_chemical_reaction_chamber", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3, 3, 3, 3)
            .setEUIO(IO.IN)
            .setOffsetVoltageText(true)
            .setVoltageTextOffset(19)
            .setProgressBar(GuiTextures.PROGRESS_BAR_BATH, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .addDataInfo(data -> {
                var vacuumLevel = data.getInt("vacuum_level");
                if (vacuumLevel >= 100)
                    return LocalizationUtils.format("start_core.recipe.min_vacuum_amount_full").replace("%", "%%");
                return LocalizationUtils
                        .format("start_core.recipe.min_vacuum_amount",
                                FormattingUtil.DECIMAL_FORMAT_0F.format(vacuumLevel))
                        .replace("%", "%%");
            });

    public static GTRecipeType registerStarTPrioritiseCustomLogic(String name, String group,
                                                                  RecipeType<?>... proxyRecipes) {
        var recipeType = new StarTPrioritiseCustomLogicRecipeType(GTCEu.id(name), group, proxyRecipes);
        GTRegistries.register(BuiltInRegistries.RECIPE_TYPE, recipeType.registryName, recipeType);
        GTRegistries.register(BuiltInRegistries.RECIPE_SERIALIZER, recipeType.registryName, new GTRecipeSerializer());
        GTRegistries.RECIPE_TYPES.register(recipeType.registryName, recipeType);
        return recipeType;
    }

    public static final GTRecipeType HELL_FORGE_RECIPES = registerStarTPrioritiseCustomLogic("hellforge",
            GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(2, 1, 7, 1)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .addCustomRecipeLogic(new HellForgeHeatingLogic())
            .addDataInfo(data -> {
                int temp = data.getInt("ebf_temp");

                if (temp > 0) {
                    return LocalizationUtils.format("start_core.recipe.temperature",
                            FormattingUtil.formatNumbers(temp));
                }

                return "";
            })
            .addDataInfo(data -> {
                int temp = data.getInt("ebf_temp");
                Material requiredFluid = StarTHellForgeMachine.getHellforgeHeatingLiquid(temp);

                if (temp > 0) {
                    return Component.translatable("start_core.recipe.heating_fluid",
                            requiredFluid.getLocalizedName().getString()).getString();
                }

                return "";
            }).setUiBuilder((recipe, widgetGroup) -> {})
            .setSound(GTSoundEntries.FURNACE);

    public static final GTRecipeType ABYSSAL_HARVESTER_RECIPES = GTRecipeTypes
            .register("abyssal_harvester", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(1, 0, 1, 4)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .addDataInfo(data -> {
                int minSaturation = data.getInt("min_saturation");
                if (minSaturation > 0) {
                    return LocalizationUtils.format("start_core.recipe.min_saturation",
                            FormattingUtil.formatPercent(minSaturation / 100.0));
                }
                return LocalizationUtils.format("start_core.recipe.min_saturation.0");
            })
            .addDataInfo(data -> {
                int maxSaturation = data.getInt("max_saturation");
                if (maxSaturation > 0) {
                    return LocalizationUtils.format("start_core.recipe.max_saturation",
                            FormattingUtil.formatPercent(maxSaturation / 100.0));
                }
                return LocalizationUtils.format("start_core.recipe.max_saturation.0");
            })
            .setSound(GTSoundEntries.CENTRIFUGE);

    public static final GTRecipeType SOLAR_ENERGY = GTRecipeTypes.register("solar_energy", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(0, 0, 0, 0)
            .setEUIO(IO.OUT);

    public static final GTRecipeType SOLAR_PANEL_REPLACEMENT = GTRecipeTypes
            .register("solar_panel_replacement", GTRecipeTypes.MULTIBLOCK)
            .addCustomRecipeLogic(new SolarPanelReplacementLogic())
            .setIconSupplier(() -> StarTSolarCellBlocks.EV_SOLAR_CELL.asStack())
            .setMaxIOSize(1, 1, 0, 0);

    public static final GTRecipeType TITAN_FORGE_RECIPES = GTRecipeTypes
            .register("titan_forge", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(4, 1, 0, 0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW_MULTIPLE, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static final GTRecipeType MODULAR_ROCKET_MODULE_RECIPES = GTRecipeTypes
            .register("modular_rocket_module", GTRecipeTypes.ELECTRIC)
            .setMaxIOSize(0, 0, 1, 0)
            .setEUIO(IO.OUT);

    public static final GTRecipeType COMBUSTION_FRAME_RECIPE_TYPE = GTRecipeTypes
            .register("modular_combustion_frame", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(0, 0, 1, 0)
            .setEUIO(IO.OUT);

    public static final GTRecipeType ARBOREAL_EXTRACTION_RECIPES = GTRecipeTypes
            .register("arboreal_extraction", "primitive")
            .setMaxIOSize(3, 0, 0, 1)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .addDataInfoFull(ArborealExtractionRecipeLogic::getDataInfo)
            .addCustomRecipeLogic(new ArborealExtractionRecipeLogic())
            .setUiBuilder(ArborealExtractionRecipeLogic::uiBuilder);

    public static final GTRecipeType BLACK_HOLE_IGNITION_RECIPES = GTRecipeTypes
            .register("black_hole_ignition", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(1, 0, 0, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .addDataInfo(data -> {
                var profile = BlackHoleSeeds.get(data.getString(BlackHoleSeeds.RECIPE_DATA_SEED));
                return profile == null ? "" :
                        LocalizationUtils.format("start_core.recipe.black_hole_seed", profile.displayName());
            });

    public static final GTRecipeType BLACK_HOLE_FEEDING_RECIPES = GTRecipeTypes
            .register("black_hole_feeding", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(1, 0, 1, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .addDataInfo(data -> LocalizationUtils.format("start_core.recipe.black_hole_mass",
                    FormattingUtil.formatNumbers(data.getFloat(BlackHoleSeeds.RECIPE_DATA_MASS))));

    public static final GTRecipeType DYSON_SPHERE_FUEL_RECIPES = GTRecipeTypes
            .register("dyson_sphere_fuel", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(0, 0, 1, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .addDataInfo(data -> LocalizationUtils.format(
                    "start_core.recipe.dyson_fuel." + data.getString(DysonSphereRecipes.RECIPE_DATA_STAGE),
                    FormattingUtil.formatNumbers(data.getFloat(DysonSphereRecipes.RECIPE_DATA_MASS))));

    public static final GTRecipeType DYSON_SPHERE_REMNANT_RECIPES = GTRecipeTypes
            .register("dyson_sphere_remnant", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(0, 1, 0, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .addDataInfo(data -> LocalizationUtils.format(
                    "start_core.recipe.dyson_remnant.mass." + data.getString(DysonSphereRecipes.RECIPE_DATA_OUTCOME),
                    StellarBalance.M_SUPERNOVA, StellarBalance.M_SEED))
            .addDataInfo(data -> LocalizationUtils.format(
                    "start_core.recipe.dyson_remnant.outcome." +
                            data.getString(DysonSphereRecipes.RECIPE_DATA_OUTCOME)));

    public static final GTRecipeType NEUTRON_STAR_FORGE_RECIPES = GTRecipeTypes
            .register("neutron_star_forge", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(6, 6, 3, 3)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .addDataInfo(data -> LocalizationUtils.format("start_core.recipe.neutron_spin_draw",
                    FormattingUtil.formatNumber2Places(data.getFloat(NeutronStarRecipes.RECIPE_DATA_SPIN_DRAW))))
            .setSound(GTSoundEntries.ARC);

    public static final GTRecipeType NEUTRON_STAR_ACCRETION_RECIPES = GTRecipeTypes
            .register("neutron_star_accretion", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(0, 0, 1, 0)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .addDataInfo(data -> LocalizationUtils.format("start_core.recipe.neutron_accretion.spin",
                    FormattingUtil
                            .formatNumber2Places(1000 * data.getFloat(NeutronStarRecipes.RECIPE_DATA_SPIN_PER_MB)),
                    NeutronStarBalance.M_INITIAL))
            .addDataInfo(data -> {
                float massPerMb = data.getFloat(NeutronStarRecipes.RECIPE_DATA_MASS_PER_MB);
                if (massPerMb <= 0) return "";
                return LocalizationUtils.format("start_core.recipe.neutron_accretion.mass", FormattingUtil
                        .formatNumbers(Math.round((NeutronStarBalance.M_TOV - NeutronStarBalance.M_INITIAL) /
                                massPerMb / 1000)));
            });

    public static final GTRecipeType NEUTRON_STAR_CAPTURE_RECIPES = GTRecipeTypes
            .register("neutron_star_capture", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(1, 0, 0, 0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_FUSION, ProgressTexture.FillDirection.LEFT_TO_RIGHT)
            .addDataInfo(data -> LocalizationUtils.format("start_core.recipe.neutron_capture",
                    FormattingUtil.formatNumber2Places(data.getFloat(NeutronStarRecipes.RECIPE_DATA_MASS)),
                    FormattingUtil.formatNumbers(Math.round(data.getFloat(NeutronStarRecipes.RECIPE_DATA_SPIN)))));

    public static void init() {}
}
