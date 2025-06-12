package com.startechnology.start_core.mixin;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.data.recipe.CraftingComponent;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.gregtechceu.gtceu.data.recipe.generated.MaterialRecipeHandler;

import dev.architectury.patchedmixin.staticmixin.spongepowered.asm.mixin.Overwrite;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.get;


@Mixin(value = MaterialRecipeHandler.class, remap = false)
public class MaterialRecipeHandlerMixin {
    
    @Overwrite
    private static void processEBFRecipe(Material material, BlastProperty property, ItemStack output,
                                         Consumer<FinishedRecipe> provider) {
        int blastTemp = property.getBlastTemperature();
        BlastProperty.GasTier gasTier = property.getGasTier();
        int duration = property.getDurationOverride();
        if (duration <= 0) {
            duration = Math.max(1, (int) (material.getMass() * blastTemp / 50L));
        }
        int EUt = property.getEUtOverride();
        if (EUt <= 0) EUt = VA[MV];

        GTRecipeBuilder blastBuilder = BLAST_RECIPES.recipeBuilder("blast_" + material.getName())
                .inputItems(dust, material)
                .outputItems(output)
                .blastFurnaceTemp(blastTemp)
                .EUt(EUt);

        if (gasTier != null) {
            FluidIngredient gas = CraftingComponent.EBF_GASES.get(gasTier).copy();

            blastBuilder.copy("blast_" + material.getName())
                    .circuitMeta(1)
                    .duration(duration)
                    .save(provider);

            blastBuilder.copy("blast_" + material.getName() + "_gas")
                    .circuitMeta(2)
                    .inputFluids(gas)
                    .duration((int) (duration * 0.67))
                    .save(provider);
        } else {
            blastBuilder.duration(duration);
            if (material == Silicon) {
                blastBuilder.circuitMeta(1);
            }
            blastBuilder.save(provider);
        }

        // Add Vacuum Freezer recipe if required.
        if (ingotHot.doGenerateItem(material)) {
            int vacuumEUt = property.getVacuumEUtOverride() != -1 ? property.getVacuumEUtOverride() : VA[MV];
            int vacuumDuration = property.getVacuumDurationOverride() != -1 ? property.getVacuumDurationOverride() :
                    (int) material.getMass() * 3;
            if (blastTemp < 5000) {
                VACUUM_RECIPES.recipeBuilder("cool_hot_" + material.getName() + "_ingot")
                        .inputItems(ingotHot, material)
                        .outputItems(ingot, material)
                        .duration(vacuumDuration)
                        .EUt(vacuumEUt)
                        .save(provider);
            } else if (blastTemp < 15000) {
                VACUUM_RECIPES.recipeBuilder("cool_hot_" + material.getName() + "_ingot")
                        .inputItems(ingotHot, material)
                        .inputFluids(Helium.getFluid(FluidStorageKeys.LIQUID, 500))
                        .outputItems(ingot, material)
                        .outputFluids(Helium.getFluid(250))
                        .duration(vacuumDuration)
                        .EUt(vacuumEUt)
                        .save(provider);
            } else {
                vacuumEUt = property.getVacuumEUtOverride() != -1 ? property.getVacuumEUtOverride() : VA[UV];
                VACUUM_RECIPES.recipeBuilder("cool_hot_" + material.getName() + "_ingot")
                        .inputItems(ingotHot, material)
                        .inputFluids(get("superstate_helium_3").getFluid(FluidStorageKeys.LIQUID, 500))
                        .outputItems(ingot, material)
                        .outputFluids(Helium3.getFluid(250))
                        .duration(vacuumDuration)
                        .EUt(vacuumEUt)
                        .save(provider);
            }
        }
    }
}
