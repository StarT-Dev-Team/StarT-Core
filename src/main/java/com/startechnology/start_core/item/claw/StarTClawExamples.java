package com.startechnology.start_core.item.claw;

import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.item.StarTItems;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

import java.util.List;

public final class StarTClawExamples {

    private static final int GRID_SIZE = 3;

    private StarTClawExamples() {}

    public static List<ShapedRecipe> create() {
        ItemStack claw = StarTItems.CLAW_OF_LAW.get().getDefaultInstance();

        ItemStack redToes = claw.copy();
        StarTClawOfLawItem.setColor(redToes, StarTClawPart.TOES, dyeColor(DyeColor.RED));

        ItemStack blueMitt = claw.copy();
        StarTClawOfLawItem.setColor(blueMitt, StarTClawPart.MITT, dyeColor(DyeColor.BLUE));

        ItemStack greenCuff = claw.copy();
        StarTClawOfLawItem.setColor(greenCuff, StarTClawPart.CUFF, dyeColor(DyeColor.GREEN));

        ItemStack axe = ToolHelper.get(GTToolType.AXE, GTMaterials.Steel);
        ItemStack skinned = claw.copy();
        StarTClawOfLawItem.setToolMaterial(skinned, StarTClawMode.AXE, GTMaterials.Steel);
        StarTClawOfLawItem.setMode(skinned, StarTClawMode.AXE);

        return List.of(
                recipe("dye_toes", redToes, claw, 3, new ItemStack(Items.RED_DYE), 1),
                recipe("dye_mitt", blueMitt, claw, 3, new ItemStack(Items.BLUE_DYE), 4),
                recipe("dye_cuff", greenCuff, claw, 3, new ItemStack(Items.GREEN_DYE), 7),
                recipe("reset_toes", claw, redToes, 3, new ItemStack(Items.SNOWBALL), 1),
                recipe("tool_skin", skinned, claw, 3, axe, 4));
    }

    private static ShapedRecipe recipe(String name, ItemStack result, ItemStack clawInput, int clawSlot,
                                       ItemStack other, int otherSlot) {
        NonNullList<Ingredient> ingredients = NonNullList.withSize(GRID_SIZE * GRID_SIZE, Ingredient.EMPTY);

        ingredients.set(clawSlot, Ingredient.of(clawInput));
        ingredients.set(otherSlot, Ingredient.of(other));

        return new ShapedRecipe(StarTCore.resourceLocation("/claw/example_" + name), "", CraftingBookCategory.MISC,
                GRID_SIZE, GRID_SIZE, ingredients, result);
    }

    private static int dyeColor(DyeColor color) {
        float[] rgb = color.getTextureDiffuseColors();

        return (Math.round(rgb[0] * 255.0F) << 16) | (Math.round(rgb[1] * 255.0F) << 8) |
                Math.round(rgb[2] * 255.0F);
    }
}
