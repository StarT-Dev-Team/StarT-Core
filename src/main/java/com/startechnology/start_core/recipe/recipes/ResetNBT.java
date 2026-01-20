package com.startechnology.start_core.recipe.recipes;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;

import net.minecraft.data.recipes.FinishedRecipe;

import static com.startechnology.start_core.item.StarTItems.*;

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
        VanillaRecipeHelper.addShapelessNBTClearingRecipe(provider, "mechanical_memory_card", MECHANICAL_MEMORY_CARD.asStack(),
                MECHANICAL_MEMORY_CARD.asStack()
        );
    }

}
