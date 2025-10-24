package com.startechnology.start_core.recipe.recipes;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import static com.startechnology.start_core.item.StarTItems.TOOL_DATA_DNA_DISK;
import static com.startechnology.start_core.item.StarTItems.TOOL_COMPONENT_DATA_CORE;
import static com.startechnology.start_core.item.StarTItems.TOOL_DREAM_COPY_ITEM;

import net.minecraft.data.recipes.FinishedRecipe;

public class ResetNBT {

    public static void init(Consumer<FinishedRecipe> provider) {
        VanillaRecipeHelper.addShapelessNBTClearingRecipe(provider, "data_dna_disk_nbt", TOOL_DATA_DNA_DISK.asStack(), 
            TOOL_DATA_DNA_DISK.asStack()
        );
        VanillaRecipeHelper.addShapelessNBTClearingRecipe(provider, "component_data_core", TOOL_COMPONENT_DATA_CORE.asStack(), 
            TOOL_COMPONENT_DATA_CORE.asStack()
        );
        VanillaRecipeHelper.addShapelessNBTClearingRecipe(provider, "lucinducer", TOOL_DREAM_COPY_ITEM.asStack(), 
            TOOL_DREAM_COPY_ITEM.asStack()
        );
    }

}
