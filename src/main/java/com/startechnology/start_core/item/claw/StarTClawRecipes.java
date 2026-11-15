package com.startechnology.start_core.item.claw;

import com.startechnology.start_core.StarTCore;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class StarTClawRecipes {

    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister
            .create(ForgeRegistries.RECIPE_SERIALIZERS, StarTCore.MOD_ID);

    public static final RegistryObject<RecipeSerializer<StarTClawDyeRecipe>> CLAW_DYE = SERIALIZERS
            .register("claw_dye", () -> new SimpleCraftingRecipeSerializer<>(StarTClawDyeRecipe::new));

    public static final RegistryObject<RecipeSerializer<StarTClawToolRecipe>> CLAW_TOOL = SERIALIZERS
            .register("claw_tool", () -> new SimpleCraftingRecipeSerializer<>(StarTClawToolRecipe::new));

    public static void register(IEventBus modEventBus) {
        SERIALIZERS.register(modEventBus);
    }

    static ItemStack copyClaw(CraftingContainer container) {
        ItemStack claw = ItemStack.EMPTY;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (container.getItem(slot).getItem() instanceof StarTClawOfLawItem) {
                claw = container.getItem(slot);
            }
        }

        return claw.copyWithCount(1);
    }
}
