package com.startechnology.start_core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.FluidPipeProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.startechnology.start_core.api.StarTCreativeTab;
import com.startechnology.start_core.machine.StarTMachines;
import com.startechnology.start_core.machine.fusion.StarTFusionMachines;
import com.startechnology.start_core.materials.StarTMaterials;
import com.startechnology.start_core.recipe.StarTRecipeCategories;
import com.startechnology.start_core.recipe.StarTRecipeTypes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@SuppressWarnings("unused")
@Mod(StarTCore.MOD_ID)
public class StarTCore {
    public static final String MOD_ID = "start_core";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final GTRegistrate START_REGISTRATE = GTRegistrate.create(StarTCore.MOD_ID);
    public static final RandomSource RNG = RandomSource.createThreadSafe();

    public static ResourceLocation resourceLocation(String path) {
        return new ResourceLocation(StarTCore.MOD_ID, path);
    }

    public StarTCore() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        StarTCreativeTab.init();
        START_REGISTRATE.creativeModeTab(() -> StarTCreativeTab.START_CORE);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::addMaterialRegistries);
        modEventBus.addListener(this::addMaterials);
        modEventBus.addListener(this::modifyMaterials);
        modEventBus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        modEventBus.addGenericListener(MachineDefinition.class, this::registerMachines);
        modEventBus.addGenericListener(GTRecipeCategory.class, this::registerRecipeCategories);
        START_REGISTRATE.registerRegistrate();

        // Most other events are fired on Forge's bus.
        // If we want to use annotations to register event listeners,
        // we need to register our object like this!
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void clientSetup(final FMLClientSetupEvent event) {
    }

    // You MUST have this for custom materials.
    // Remember to register them not to GT's namespace, but your own.
    private void addMaterialRegistries(MaterialRegistryEvent event) {
        GTCEuAPI.materialManager.createRegistry(StarTCore.MOD_ID);
    }

    // As well as this.
    private void addMaterials(MaterialEvent event) {
        StarTMaterials.register();
    }

    // This is optional, though.
    private void modifyMaterials(PostMaterialEvent event) {

        // Prevent crash from KubeJS
        if (!GTMaterials.NaquadahEnriched.hasProperty(PropertyKey.FLUID_PIPE)) {
            GTMaterials.NaquadahEnriched.setProperty(PropertyKey.FLUID_PIPE, new FluidPipeProperties(8000, 500, true, true, true, false));
        }
    }


    private void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        // Modify Electric blast furnace to have two outputs
        GTRecipeTypes.BLAST_RECIPES.setMaxIOSize(3, 3, 3, 3);
        StarTRecipeTypes.init();
    }

    private void registerRecipeCategories(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeCategory> event) {
        StarTRecipeCategories.init();
    }

    private void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        StarTMachines.init();
    }
}
