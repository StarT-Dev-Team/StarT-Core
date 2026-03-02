package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.integration.kjs.recipe.GTRecipeSchema;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = GTRecipeSchema.GTRecipeJS.class, remap = false)
@RemapPrefixForJS("kjs$")
public class GTRecipeJSMixin {

    @Shadow
    public GTRecipeSchema.GTRecipeJS addData(String key, int data) {
        return null;
    }

    @Unique
    public GTRecipeSchema.GTRecipeJS kjs$reflectorTier(int tier) {
        return addData("reflector_tier", tier);
    }

    @Unique
    public GTRecipeSchema.GTRecipeJS kjs$vacuumLevel(int vacuum) {
        return addData("vacuum_level", vacuum);
    }

    @Unique
    public GTRecipeSchema.GTRecipeJS kjs$addDataInt(String key, int value) {
        return addData(key, value);
    }

}
