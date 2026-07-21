package com.startechnology.start_core.integration.emi;

import codechicken.microblock.api.MicroMaterial;
import codechicken.microblock.init.CBMicroblockTags;
import codechicken.microblock.item.ItemMicroBlock;
import com.google.common.collect.Lists;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.integration.CBMicroblockUtils;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.render.EmiRender;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.ListEmiIngredient;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.screen.tooltip.EmiTextTooltipWrapper;
import dev.emi.emi.screen.tooltip.IngredientTooltipComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class CBMicroblockRecipes {

    public static void register(EmiRegistry registry) {
        var materials = CBMicroblockUtils.getMicroMaterials();
        var saw = EmiIngredient.of(CBMicroblockTags.Items.TOOL_SAW);
        var cobblestone = new ResourceLocation("minecraft:cobblestone");

        // thinning full
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeThinning(saw, EmiStack.of(Items.COBBLESTONE)),
                EmiStack.of(ItemMicroBlock.createStack(2, 0, 4, cobblestone)),
                "cb_microblock_recipe_face8_to_face4"));
        // thinnig face
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeThinning(saw, ItemMicroBlock.create(0, 4, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(2, 0, 2, cobblestone)),
                "cb_microblock_recipe_face4_to_face2"));
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeThinning(saw, ItemMicroBlock.create(0, 2, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(2, 0, 1, cobblestone)),
                "cb_microblock_recipe_face2_to_face1"));
        // splitting face
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeSplitting(saw, ItemMicroBlock.create(0, 4, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(2, 3, 4, cobblestone)),
                "cb_microblock_recipe_face4_to_edge4"));
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeSplitting(saw, ItemMicroBlock.create(0, 2, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(2, 3, 2, cobblestone)),
                "cb_microblock_recipe_face2_to_edge2"));
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeSplitting(saw, ItemMicroBlock.create(0, 1, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(2, 3, 1, cobblestone)),
                "cb_microblock_recipe_face1_to_edge1"));
        // thinning edge
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeThinning(saw, ItemMicroBlock.create(3, 4, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(2, 3, 2, cobblestone)),
                "cb_microblock_recipe_edge4_to_edge2"));
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeThinning(saw, ItemMicroBlock.create(3, 2, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(2, 3, 1, cobblestone)),
                "cb_microblock_recipe_edge2_to_edge1"));
        // splitting edge
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeSplitting(saw, ItemMicroBlock.create(3, 4, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(2, 2, 4, cobblestone)),
                "cb_microblock_recipe_edge4_to_corner4"));
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeSplitting(saw, ItemMicroBlock.create(3, 2, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(2, 2, 2, cobblestone)),
                "cb_microblock_recipe_edge2_to_corner2"));
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeSplitting(saw, ItemMicroBlock.create(3, 1, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(2, 2, 1, cobblestone)),
                "cb_microblock_recipe_edge1_to_corner1"));
        // thinning corner
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeThinning(saw, ItemMicroBlock.create(2, 4, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(2, 2, 2, cobblestone)),
                "cb_microblock_recipe_corner4_to_corner2"));
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeThinning(saw, ItemMicroBlock.create(2, 2, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(2, 2, 1, cobblestone)),
                "cb_microblock_recipe_corner2_to_corner1"));
        // make hollow
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeHollow(ItemMicroBlock.create(0, 4, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(8, 1, 4, cobblestone)),
                "cb_microblock_recipe_face4_to_hollow4"));
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeHollow(ItemMicroBlock.create(0, 2, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(8, 1, 2, cobblestone)),
                "cb_microblock_recipe_face2_to_hollow2"));
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeHollow(ItemMicroBlock.create(0, 1, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(8, 1, 1, cobblestone)),
                "cb_microblock_recipe_face1_to_hollow1"));
        // fill hollow
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeSingle(ItemMicroBlock.create(1, 4, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(1, 0, 4, cobblestone)),
                "cb_microblock_recipe_hollow4_to_face4"));
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeSingle(ItemMicroBlock.create(1, 2, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(1, 0, 2, cobblestone)),
                "cb_microblock_recipe_hollow2_to_face2"));
        registry.addRecipe(new EmiMicroblockRecipe(materials,
                recipeSingle(ItemMicroBlock.create(1, 1, cobblestone)),
                EmiStack.of(ItemMicroBlock.createStack(1, 0, 1, cobblestone)),
                "cb_microblock_recipe_hollow1_to_face1"));
        // combine?
    }

    public static class MaterialListEmiIngredient implements EmiIngredient {

        private final List<? extends EmiIngredient> ingredients;
        private final List<EmiStack> fullList;
        private int current;
        private long amount;
        private float chance = 1.0F;

        public MaterialListEmiIngredient(List<? extends EmiIngredient> ingredients, long amount) {
            this.ingredients = ingredients;
            this.fullList = ingredients.stream().flatMap((i) -> i.getEmiStacks().stream()).toList();
            if (this.fullList.isEmpty()) {
                throw new IllegalArgumentException("MaterialListEmiIngredient cannot be empty");
            } else {
                this.amount = amount;
            }
        }

        public MaterialListEmiIngredient(List<ItemStack> ingredients) {
            this(ingredients.stream().map(EmiStack::of).toList(), ingredients.get(0).getCount());
        }

        public void setCurrent(Random random) {
            current = random.nextInt(fullList.size());
        }

        @SuppressWarnings("UnstableApiUsage")
        public boolean equals(Object obj) {
            if (obj instanceof MaterialListEmiIngredient other) {
                return other.getEmiStacks().equals(getEmiStacks());
            }
            if (obj instanceof ListEmiIngredient other) {
                return other.getEmiStacks().equals(getEmiStacks());
            } else {
                return false;
            }
        }

        public int hashCode() {
            return this.fullList.hashCode();
        }

        public EmiIngredient copy() {
            EmiIngredient stack = new MaterialListEmiIngredient(ingredients, amount);
            stack.setChance(chance);
            return stack;
        }

        @Override
        public long getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return "Ingredient" + getEmiStacks();
        }

        @Override
        public List<EmiStack> getEmiStacks() {
            return fullList;
        }

        @Override
        public EmiIngredient setAmount(long amount) {
            this.amount = amount;
            return this;
        }

        @Override
        public float getChance() {
            return chance;
        }

        @Override
        public EmiIngredient setChance(float chance) {
            this.chance = chance;
            return this;
        }

        @Override
        public void render(GuiGraphics draw, int x, int y, float delta, int flags) {
            var currentIngredient = ingredients.get(current);
            if ((flags & 1) != 0) {
                currentIngredient.render(draw, x, y, delta, -3);
            }
            if ((flags & 2) != 0) {
                currentIngredient.copy().setAmount(amount).render(draw, x, y, delta, 2);
            }
            if ((flags & 4) != 0) {
                EmiRender.renderIngredientIcon(this, draw, x, y);
            }
        }

        @Override
        public List<ClientTooltipComponent> getTooltip() {
            List<ClientTooltipComponent> tooltip = Lists.newArrayList();
            tooltip.add(new EmiTextTooltipWrapper(this, EmiPort.ordered(EmiPort.translatable("tooltip.emi.accepts"))));
            tooltip.add(new IngredientTooltipComponent(ingredients));
            tooltip.addAll((ingredients.get(current)).copy().setAmount(amount).getTooltip());
            return tooltip;
        }
    }

    public static class EmiMicroblockRecipe extends EmiPatternCraftingRecipe {

        private final List<MicroMaterial> materials;

        public EmiMicroblockRecipe(List<MicroMaterial> materials, List<EmiIngredient> input, EmiStack output,
                                   String id) {
            super(input.stream().map(ingredient -> {
                var items = ingredient.getEmiStacks();
                return Optional.ofNullable(items.size() == 1 ? items.get(0) : null)
                        .flatMap(i -> CBMicroblockUtils.convertToMaterials(materials, i.getItemStack()))
                        .map(MaterialListEmiIngredient::new)
                        .map(EmiIngredient.class::cast)
                        .orElse(ingredient);
            }).toList(), output, StarTCore.resourceLocation(id).withPrefix("/"), false);
            this.materials = materials;
        }

        @Override
        public SlotWidget getInputWidget(int slot, int x, int y) {
            return new GeneratedSlotWidget((r) -> {
                var ingredient = input.get(slot);
                if (ingredient instanceof MaterialListEmiIngredient listIngredient) {
                    listIngredient.setCurrent(r);
                }
                return ingredient;
            }, unique, x, y);
        }

        @Override
        public SlotWidget getOutputWidget(int x, int y) {
            return new GeneratedSlotWidget((r) -> {
                var material = getMaterial(r);
                var outputStack = output.getEmiStacks().get(0);
                return EmiStack.of(CBMicroblockUtils.convertToMaterial(material, outputStack.getItemStack()));
            }, unique, x, y);
        }

        private MicroMaterial getMaterial(Random random) {
            return materials.get(random.nextInt(materials.size()));
        }
    }

    private static List<EmiIngredient> recipeThinning(EmiIngredient saw, ItemStack block) {
        return recipeThinning(saw, EmiIngredient.of(Ingredient.of(block)));
    }

    private static List<EmiIngredient> recipeThinning(EmiIngredient saw, EmiIngredient block) {
        var empty = EmiStack.EMPTY;
        return List.of(
                empty, empty, empty,
                empty, saw, empty,
                empty, block, empty);
    }

    private static List<EmiIngredient> recipeSplitting(EmiIngredient saw, ItemStack block) {
        return recipeSplitting(saw, EmiIngredient.of(Ingredient.of(block)));
    }

    private static List<EmiIngredient> recipeSplitting(EmiIngredient saw, EmiIngredient block) {
        var empty = EmiStack.EMPTY;
        return List.of(
                empty, empty, empty,
                empty, saw, block,
                empty, empty, empty);
    }

    private static List<EmiIngredient> recipeHollow(ItemStack block) {
        return recipeHollow(EmiIngredient.of(Ingredient.of(block)));
    }

    private static List<EmiIngredient> recipeHollow(EmiIngredient block) {
        var empty = EmiStack.EMPTY;
        return List.of(
                block, block, block,
                block, empty, block,
                block, block, block);
    }

    private static List<EmiIngredient> recipeSingle(ItemStack block) {
        return recipeSingle(EmiIngredient.of(Ingredient.of(block)));
    }

    private static List<EmiIngredient> recipeSingle(EmiIngredient block) {
        var empty = EmiStack.EMPTY;
        return List.of(
                empty, empty, empty,
                empty, block, empty,
                empty, empty, empty);
    }
}
