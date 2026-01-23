package com.startechnology.start_core.mixin;

import java.util.function.Consumer;

import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialEntry;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.Util;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.WireProperties;
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

    private static final Reference2IntMap<TagPrefix> INSULATION_AMOUNT = Util.make(new Reference2IntOpenHashMap<>(),
            map -> {
                map.put(cableGtSingle, 1);
                map.put(cableGtDouble, 1);
                map.put(cableGtQuadruple, 2);
                map.put(cableGtOctal, 3);
                map.put(cableGtHex, 5);
            });

    private static void generateManualRecipe(@NotNull Consumer<FinishedRecipe> provider, @NotNull TagPrefix wirePrefix,
                                             @NotNull TagPrefix cablePrefix, int cableAmount,
                                             @NotNull Material material) {
        int insulationAmount = INSULATION_AMOUNT.getInt(cablePrefix);
        Object[] ingredients = new Object[insulationAmount + 1];
        ingredients[0] = new MaterialEntry(wirePrefix, material);
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
    
    /**
     * @author trulyno
     * @reason more materials
     */
    @Overwrite
    public static void generateCableCovering(@NotNull Consumer<FinishedRecipe> provider,
                                             @NotNull WireProperties property,
                                             @NotNull TagPrefix prefix, @NotNull Material material) {

        if (!material.shouldGenerateRecipesFor(prefix) || property.isSuperconductor()) {
            // Superconductors have no Cables, so exit early
            return;
        }

        int cableAmount = (int) (prefix.getMaterialAmount(material) * 2 / M);
        TagPrefix cablePrefix = TagPrefix.get("cable" + prefix.name().substring(4));
        int voltageTier = GTUtil.getTierByVoltage(property.getVoltage());
        int insulationAmount = INSULATION_AMOUNT.getInt(cablePrefix);

        // Generate hand-crafting recipes for ULV and LV cables
        if (voltageTier <= LV) {
            generateManualRecipe(provider, prefix, cablePrefix, cableAmount, material);
        }

        // Rubber Recipe (ULV-EV cables)
        if (voltageTier <= EV) {
            GTRecipeBuilder builder = ASSEMBLER_RECIPES
                    .recipeBuilder("cover_" + material.getName() + "_" + prefix + "_rubber")
                    .EUt(VA[ULV]).duration(100)
                    .inputItems(prefix, material)
                    .outputItems(cablePrefix, material)
                    .inputFluids(Rubber, L * insulationAmount);

            if (voltageTier == EV) {
                builder.inputItems(foil, PolyvinylChloride, insulationAmount);
            }
            builder.save(provider);
        }

        if (voltageTier <= UV) {
            // Silicone Rubber Recipe (all cables)
            GTRecipeBuilder builder = ASSEMBLER_RECIPES
                .recipeBuilder("cover_" + material.getName() + "_" + prefix + "_silicone")
                .EUt(VA[ULV]).duration(100)
                .inputItems(prefix, material)
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
                .recipeBuilder("cover_" + material.getName() + "_" + prefix + "_styrene_butadiene")
                .EUt(VA[ULV]).duration(100)
                .inputItems(prefix, material)
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
                .recipeBuilder("cover_" + material.getName() + "_" + prefix + "_perfluoroelastomer")
                .EUt(VA[ULV]).duration(100)
                .inputItems(prefix, material)
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
