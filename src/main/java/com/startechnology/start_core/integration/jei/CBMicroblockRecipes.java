package com.startechnology.start_core.integration.jei;

import codechicken.microblock.init.CBMicroblockTags;
import codechicken.microblock.item.ItemMicroBlock;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.integration.CBMicroblockUtils;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class CBMicroblockRecipes {

    public static void registerRecipes(IRecipeRegistration registration) {
        var saw = Ingredient.of(CBMicroblockTags.Items.TOOL_SAW);
        var cobblestone = new ResourceLocation("minecraft:cobblestone");

        registration.addRecipes(RecipeTypes.CRAFTING, List.of(
                // thinning full
                makeMicroblockRecipe(
                        "cb_microblock_recipe_face8_to_face4",
                        ItemMicroBlock.createStack(2, 0, 4, cobblestone),
                        recipeThinning(saw, Ingredient.of(Items.COBBLESTONE))),
                // thinnig face
                makeMicroblockRecipe(
                        "cb_microblock_recipe_face4_to_face2",
                        ItemMicroBlock.createStack(2, 0, 2, cobblestone),
                        recipeThinning(saw, ItemMicroBlock.create(0, 4, cobblestone))),
                makeMicroblockRecipe(
                        "cb_microblock_recipe_face2_to_face1",
                        ItemMicroBlock.createStack(2, 0, 1, cobblestone),
                        recipeThinning(saw, ItemMicroBlock.create(0, 2, cobblestone))),
                // splitting face
                makeMicroblockRecipe(
                        "cb_microblock_recipe_face4_to_edge4",
                        ItemMicroBlock.createStack(2, 3, 4, cobblestone),
                        recipeSplitting(saw, ItemMicroBlock.create(0, 4, cobblestone))),
                makeMicroblockRecipe(
                        "cb_microblock_recipe_face2_to_edge2",
                        ItemMicroBlock.createStack(2, 3, 2, cobblestone),
                        recipeSplitting(saw, ItemMicroBlock.create(0, 2, cobblestone))),
                makeMicroblockRecipe(
                        "cb_microblock_recipe_face1_to_edge1",
                        ItemMicroBlock.createStack(2, 3, 1, cobblestone),
                        recipeSplitting(saw, ItemMicroBlock.create(0, 1, cobblestone))),
                // thinning edge
                makeMicroblockRecipe(
                        "cb_microblock_recipe_edge4_to_edge2",
                        ItemMicroBlock.createStack(2, 3, 2, cobblestone),
                        recipeThinning(saw, ItemMicroBlock.create(3, 4, cobblestone))),
                makeMicroblockRecipe(
                        "cb_microblock_recipe_edge2_to_edge1",
                        ItemMicroBlock.createStack(2, 3, 1, cobblestone),
                        recipeThinning(saw, ItemMicroBlock.create(3, 2, cobblestone))),
                // splitting edge
                makeMicroblockRecipe(
                        "cb_microblock_recipe_edge4_to_corner4",
                        ItemMicroBlock.createStack(2, 2, 4, cobblestone),
                        recipeSplitting(saw, ItemMicroBlock.create(3, 4, cobblestone))),
                makeMicroblockRecipe(
                        "cb_microblock_recipe_edge2_to_corner2",
                        ItemMicroBlock.createStack(2, 2, 2, cobblestone),
                        recipeSplitting(saw, ItemMicroBlock.create(3, 2, cobblestone))),
                makeMicroblockRecipe(
                        "cb_microblock_recipe_edge1_to_corner1",
                        ItemMicroBlock.createStack(2, 2, 1, cobblestone),
                        recipeSplitting(saw, ItemMicroBlock.create(3, 1, cobblestone))),
                // thinning corner
                makeMicroblockRecipe(
                        "cb_microblock_recipe_corner4_to_corner2",
                        ItemMicroBlock.createStack(2, 2, 2, cobblestone),
                        recipeThinning(saw, ItemMicroBlock.create(2, 4, cobblestone))),
                makeMicroblockRecipe(
                        "cb_microblock_recipe_corner2_to_corner1",
                        ItemMicroBlock.createStack(2, 2, 1, cobblestone),
                        recipeThinning(saw, ItemMicroBlock.create(2, 2, cobblestone))),
                // make hollow
                makeMicroblockRecipe(
                        "cb_microblock_recipe_face4_to_hollow4",
                        ItemMicroBlock.createStack(8, 1, 4, cobblestone),
                        recipeHollow(ItemMicroBlock.create(0, 4, cobblestone))),
                makeMicroblockRecipe(
                        "cb_microblock_recipe_face2_to_hollow2",
                        ItemMicroBlock.createStack(8, 1, 2, cobblestone),
                        recipeHollow(ItemMicroBlock.create(0, 2, cobblestone))),
                makeMicroblockRecipe(
                        "cb_microblock_recipe_face1_to_hollow1",
                        ItemMicroBlock.createStack(8, 1, 1, cobblestone),
                        recipeHollow(ItemMicroBlock.create(0, 1, cobblestone))),
                // fill hollow
                makeMicroblockRecipe(
                        "cb_microblock_recipe_hollow4_to_face4",
                        ItemMicroBlock.createStack(1, 0, 4, cobblestone),
                        recipeSingle(ItemMicroBlock.create(1, 4, cobblestone))),
                makeMicroblockRecipe(
                        "cb_microblock_recipe_hollow2_to_face2",
                        ItemMicroBlock.createStack(1, 0, 2, cobblestone),
                        recipeSingle(ItemMicroBlock.create(1, 2, cobblestone))),
                makeMicroblockRecipe(
                        "cb_microblock_recipe_hollow1_to_face1",
                        ItemMicroBlock.createStack(1, 0, 1, cobblestone),
                        recipeSingle(ItemMicroBlock.create(1, 1, cobblestone)))
                // combine?
        ));
    }

    private static Ingredient[] recipeThinning(Ingredient saw, ItemStack block) {
        return recipeThinning(saw, Ingredient.of(block));
    }

    private static Ingredient[] recipeThinning(Ingredient saw, Ingredient block) {
        var empty = Ingredient.EMPTY;
        return new Ingredient[]{
                empty, empty, empty,
                empty, saw, empty,
                empty, block, empty
        };
    }

    private static Ingredient[] recipeSplitting(Ingredient saw, ItemStack block) {
        return recipeSplitting(saw, Ingredient.of(block));
    }

    private static Ingredient[] recipeSplitting(Ingredient saw, Ingredient block) {
        var empty = Ingredient.EMPTY;
        return new Ingredient[]{
                empty, empty, empty,
                empty, saw, block,
                empty, empty, empty
        };
    }

    private static Ingredient[] recipeHollow(ItemStack block) {
        return recipeHollow(Ingredient.of(block));
    }

    private static Ingredient[] recipeHollow(Ingredient block) {
        var empty = Ingredient.EMPTY;
        return new Ingredient[]{
                block, block, block,
                block, empty, block,
                block, block, block
        };
    }

    private static Ingredient[] recipeSingle(ItemStack block) {
        return recipeSingle(Ingredient.of(block));
    }

    private static Ingredient[] recipeSingle(Ingredient block) {
        var empty = Ingredient.EMPTY;
        return new Ingredient[]{
                empty, empty, empty,
                empty, block, empty,
                empty, empty, empty
        };
    }

    public static void registerCategoryExtension(IVanillaCategoryExtensionRegistration registration) {
        var materials = CBMicroblockUtils.getMicroMaterials();

        var craftingCategory = registration.getCraftingCategory();
        craftingCategory.addCategoryExtension(
                ShapedRecipe.class,
                r -> r.getGroup().equals("cb_microblock.recipes"),
                recipe -> new ICraftingCategoryExtension() {
                    @Override
                    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull ICraftingGridHelper helper, @NotNull IFocusGroup focus) {
                        var resultItem = getResultItem(recipe);
                        var replacementResult = CBMicroblockUtils.convertToMaterials(materials, resultItem).orElseGet(() -> List.of(resultItem));

                        helper.createAndSetOutputs(builder, replacementResult);
                        helper.createAndSetInputs(builder, recipe.getIngredients().stream().map(ingredient -> {
                            var items = ingredient.getItems();
                            return Optional.ofNullable(items.length == 1 ? items[0] : null)
                                    .flatMap(i -> CBMicroblockUtils.convertToMaterials(materials, i))
                                    .orElseGet(() -> Arrays.asList(items));
                        }).toList(), getWidth(), getHeight());
                    }

                    @Override
                    public ResourceLocation getRegistryName() {
                        return recipe.getId();
                    }

                    @Override
                    public int getWidth() {
                        return recipe.getRecipeWidth();
                    }

                    @Override
                    public int getHeight() {
                        return recipe.getRecipeHeight();
                    }
                });
    }

    private static ItemStack getResultItem(Recipe<?> recipe) {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null) {
            throw new NullPointerException("level must not be null.");
        } else {
            RegistryAccess registryAccess = level.registryAccess();
            return recipe.getResultItem(registryAccess);
        }
    }

    private static ShapedRecipe makeMicroblockRecipe(String id, ItemStack result, Ingredient... ingredients) {
        return new ShapedRecipe(
                StarTCore.resourceLocation(id).withPrefix("/"),
                "cb_microblock.recipes",
                CraftingBookCategory.MISC,
                3,
                3,
                NonNullList.of(Ingredient.EMPTY, ingredients),
                result
        );
    }


}
