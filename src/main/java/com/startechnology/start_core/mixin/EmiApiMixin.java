package com.startechnology.start_core.mixin;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = EmiApi.class, remap = false)
public class EmiApiMixin {
    @ModifyVariable(
            method = "displayUses",
            at = @At(
                    value = "INVOKE",
                    target = "Ldev/emi/emi/api/stack/EmiIngredient;isEmpty()Z"
            ),
            argsOnly = true
    )
    private static EmiIngredient modifyUses(EmiIngredient original) {
        return original.isEmpty() ? original : start$bucketFluid(original);
    }

    @ModifyVariable(
            method = "displayRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;size()I"
            ),
            argsOnly = true
    )
    private static EmiIngredient modifyRecipes(EmiIngredient original) {
        return original.getEmiStacks().isEmpty() ? original : start$bucketFluid(original);
    }

    @Unique
    private static EmiIngredient start$bucketFluid(EmiIngredient ingredient) {
        if (!(ingredient instanceof EmiStack emiStack)) return ingredient;
        Fluid fluid = null;
        // Works with gt buckets since GTBucketItem extends the BucketItem class
        if (emiStack.getKey() instanceof BucketItem bucketItem) {
            fluid = bucketItem.getFluid();
        }
        return fluid == null ? ingredient : EmiStack.of(fluid);
    }
}
