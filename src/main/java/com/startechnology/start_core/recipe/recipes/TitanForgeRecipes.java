// package com.startechnology.start_core.recipe.recipes;

// import com.gregtechceu.gtceu.api.GTValues;
// import com.gregtechceu.gtceu.api.data.chemical.material.Material;
// import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
// import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;

// import net.minecraft.data.recipes.FinishedRecipe;
// import net.minecraft.resources.ResourceLocation;

// import org.jetbrains.annotations.NotNull;

// import java.util.function.Consumer;

// import static com.startechnology.start_core.recipe.StarTRecipeTypes.TITAN_FORGE;
// import static com.startechnology.start_core.materials.StarTMaterialFlags.*;
// import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;

// public class TitanForgeRecipes {
    
//     public static void init(@NotNull Consumer<FinishedRecipe> provider, @NotNull Material material) {
//         // createWireSpool(provider, material);
//         createHyperdensePlate(provider, material);
//         // createReinforcedBeam(provider, material);
//         // createBallBearing(provider, material);
//         // createRivet(provider, material);
//         // createMesh(provider, material);
//         // createFoilReam(provider, material);
//         // createGearShift(provider, material);
//     }

//     private static void createHyperdensePlate(@NotNull Consumer<FinishedRecipe> provider, @NotNull Material material) {
//         if (!material.hasFlag(MaterialFlags.GENERATE_DENSE) || !material.hasProperty(PropertyKey.INGOT)) return;

//         TITAN_FORGE.recipeBuilder("forged_hyperdense_" + material.getName() + "_plate")
//             .inputItems(plateDense, material, 4)
//             .outputItems(hyperdense_plate, material, 1)
//             .duration((int) material.getMass() * 8)
//             .EUt(GTValues.VA[GTValues.ZPM])
//             .circuitMeta(4)
//             .save(provider);

//     }

// }
