package com.startechnology.start_core.mixin;

import java.util.Map;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.google.common.collect.ImmutableMap;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.WireProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.UnificationEntry;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.gregtechceu.gtceu.data.recipe.generated.WireRecipeHandler;
import com.gregtechceu.gtceu.utils.GTUtil;

import static com.gregtechceu.gtceu.api.GTValues.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.*;
import static com.gregtechceu.gtceu.api.data.tag.TagPrefix.*;
import static com.gregtechceu.gtceu.common.data.GTRecipeTypes.*;
import static com.gregtechceu.gtceu.common.data.GTMaterials.get;

import net.minecraft.data.recipes.FinishedRecipe;

@Mixin(value = WireRecipeHandler.class, remap = false)
public class WireRecipeHandlerMixin {

    private static final Map<TagPrefix, Integer> INSULATION_AMOUNT = ImmutableMap.of(
            cableGtSingle, 1,
            cableGtDouble, 1,
            cableGtQuadruple, 2,
            cableGtOctal, 3,
            cableGtHex, 5);
    
    private static void generateManualRecipe(TagPrefix wirePrefix, Material material, TagPrefix cablePrefix,
                                             int cableAmount, Consumer<FinishedRecipe> provider) {
        int insulationAmount = INSULATION_AMOUNT.get(cablePrefix);
        Object[] ingredients = new Object[insulationAmount + 1];
        ingredients[0] = new UnificationEntry(wirePrefix, material);
        for (int i = 1; i <= insulationAmount; i++) {
            ingredients[i] = ChemicalHelper.get(plate, Rubber);
        }
        VanillaRecipeHelper.addShapelessRecipe(provider, String.format("%s_cable_%d", material.getName(), cableAmount),
                ChemicalHelper.get(cablePrefix, material),
                ingredients);

        PACKER_RECIPES.recipeBuilder("cover_" + material.getName() + "_" + wirePrefix)
                .inputItems(wirePrefix, material)
                .inputItems(plate, Rubber, insulationAmount)
                .outputItems(cablePrefix, material)
                .duration(100).EUt(VA[ULV])
                .save(provider);
    }
    
    @Overwrite
    public static void generateCableCovering(TagPrefix wirePrefix, Material material, WireProperties property,
                                             Consumer<FinishedRecipe> provider) {
        // Superconductors have no Cables, so exit early
        if (property.isSuperconductor()) return;

        int cableAmount = (int) (wirePrefix.getMaterialAmount(material) * 2 / M);
        TagPrefix cablePrefix = TagPrefix.get("cable" + wirePrefix.name().substring(4));
        int voltageTier = GTUtil.getTierByVoltage(property.getVoltage());
        int insulationAmount = INSULATION_AMOUNT.get(cablePrefix);

        // Generate hand-crafting recipes for ULV and LV cables
        if (voltageTier <= LV) {
            generateManualRecipe(wirePrefix, material, cablePrefix, cableAmount, provider);
        }

        // Rubber Recipe (ULV-EV cables)
        if (voltageTier <= EV) {
            GTRecipeBuilder builder = ASSEMBLER_RECIPES
                    .recipeBuilder("cover_" + material.getName() + "_" + wirePrefix + "_rubber")
                    .EUt(VA[ULV]).duration(100)
                    .inputItems(wirePrefix, material)
                    .outputItems(cablePrefix, material)
                    .inputFluids(Rubber.getFluid(L * insulationAmount));

            if (voltageTier == EV) {
                builder.inputItems(foil, PolyvinylChloride, insulationAmount);
            }
            builder.save(provider);
        }

        if (voltageTier <= UV) {
            // Silicone Rubber Recipe (all cables)
            GTRecipeBuilder builder = ASSEMBLER_RECIPES
                .recipeBuilder("cover_" + material.getName() + "_" + wirePrefix + "_silicone")
                .EUt(VA[ULV]).duration(100)
                .inputItems(wirePrefix, material)
                .outputItems(cablePrefix, material);

            // Insulation
            // Apply a PVC Foil if EV or above.
            if (voltageTier >= EV) {
                builder.inputItems(foil, PolyvinylChloride, insulationAmount);
            }

            // Apply a Polyphenylene Sulfate Foil if LuV or above.
            if (voltageTier >= LuV) {
                builder.inputItems(foil, PolyphenyleneSulfide, insulationAmount);
            }
            
            builder.inputFluids(SiliconeRubber.getFluid(L * insulationAmount / 2))
                    .save(provider);
        }
        
        if (voltageTier <= UHV) {
            // Styrene Butadiene Rubber Recipe (all cables)
            GTRecipeBuilder builder = ASSEMBLER_RECIPES
                .recipeBuilder("cover_" + material.getName() + "_" + wirePrefix + "_styrene_butadiene")
                .EUt(VA[ULV]).duration(100)
                .inputItems(wirePrefix, material)
                .outputItems(cablePrefix, material);

            // Insulation
            // Apply a PVC Foil if EV or above.
            if (voltageTier >= EV) {
                builder.inputItems(foil, PolyvinylChloride, insulationAmount);
            }

            // Apply a Polyphenylene Sulfate Foil if LuV or above.
            if (voltageTier >= LuV) {
                builder.inputItems(foil, PolyphenyleneSulfide, insulationAmount);
            }

            // Apply Polyamide Foil if UHV or above.
            if (voltageTier >= UHV) {
                builder.inputItems(foil, get("polyimide"), insulationAmount);
            }

            builder.inputFluids(StyreneButadieneRubber.getFluid(L * insulationAmount / 4))
                    .save(provider);
        }
        
        // Perfluoroelastomer Rubber Recipe (all cables)
        GTRecipeBuilder builder = ASSEMBLER_RECIPES
                .recipeBuilder("cover_" + material.getName() + "_" + wirePrefix + "_perfluoroelastomer")
                .EUt(VA[ULV]).duration(100)
                .inputItems(wirePrefix, material)
                .outputItems(cablePrefix, material);

        // Insulation
        // Apply a PVC Foil if EV or above.
        if (voltageTier >= EV) {
            builder.inputItems(foil, PolyvinylChloride, insulationAmount);
        }

        // Apply a Polyphenylene Sulfate Foil if LuV or above.
        if (voltageTier >= LuV) {
            builder.inputItems(foil, PolyphenyleneSulfide, insulationAmount);
        }

        // Apply Polyamide Foil if UHV or above.
        if (voltageTier >= UHV) {
            builder.inputItems(foil, get("polyimide"), insulationAmount);
        }

        builder.inputFluids(get("perfluoroelastomer_rubber").getFluid(L * insulationAmount / 8))
                .save(provider);
    }
}
