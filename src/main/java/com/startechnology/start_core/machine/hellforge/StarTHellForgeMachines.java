package com.startechnology.start_core.machine.hellforge;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.startechnology.start_core.machine.bacteria.BacterialRunicMutator;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;

public class StarTHellForgeMachines {
    
    public static final MultiblockMachineDefinition HELL_FORGE = START_REGISTRATE
        .multiblock("hell_forge", StarTHellForgeMachine::new)
        .langValue("Hell Forge")
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT)
        .rotationState(RotationState.NON_Y_AXIS)
        .recipeTypes(StarTRecipeTypes.HELL_FORGE_RECIPES)
        .pattern(definition -> FactoryBlockPattern.start()
            .aisle("@AAAAAAAACB")
            .where("@", Predicates.controller(Predicates.blocks(definition.get())))
            .where("A", Predicates.abilities(PartAbility.IMPORT_FLUIDS))
            .where("C", Predicates.abilities(PartAbility.EXPORT_FLUIDS))
            .where("B", Predicates.abilities(PartAbility.INPUT_ENERGY))
        .build()
    )
    .workableCasingRenderer(KubeJS.id("block/casings/machine_casing_peek"),
        GTCEu.id("block/multiblock/implosion_compressor"), false)
    .register();

    public static void init() {}
}
