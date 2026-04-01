package com.startechnology.start_core.mixin.ponder;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.startechnology.start_core.integration.ponder.PonderJSUtils;
import net.createmod.catnip.data.Couple;
import net.createmod.ponder.foundation.registration.PonderLocalization;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.throwables.MixinException;

import java.util.Map;

@Mixin(value = PonderLocalization.class, remap = false)
public abstract class PonderLocalizationMixin {

    @Shadow
    @Final
    public Map<ResourceLocation, Map<String, String>> specific;

    @Shadow
    @Final
    public Map<ResourceLocation, Couple<String>> tag;

    @Shadow
    protected static String langKeyForTag(ResourceLocation k) {
        throw new MixinException("not implemented");
    }

    @Shadow
    protected static String langKeyForTagDescription(ResourceLocation k) {
        throw new MixinException("not implemented");
    }

    @Shadow
    protected static String langKeyForSpecific(ResourceLocation sceneId, String k) {
        throw new MixinException("not implemented");
    }

    @WrapOperation(method = "getTagName", at = @At(value = "INVOKE", target = "Lnet/createmod/ponder/foundation/PonderIndex;editingModeActive()Z"))
    public boolean injectGetTagName(Operation<Boolean> original, ResourceLocation key) {
        return original.call() || (PonderJSUtils.TRANSLATED_TAGS.contains(key) && !I18n.exists(langKeyForTag(key)));
    }

    @WrapOperation(method = "getTagDescription", at = @At(value = "INVOKE", target = "Lnet/createmod/ponder/foundation/PonderIndex;editingModeActive()Z"))
    public boolean injectGetTagDescription(Operation<Boolean> original, ResourceLocation tagId) {
        return original.call() || (PonderJSUtils.TRANSLATED_TAGS.contains(tagId) && !I18n.exists(langKeyForTagDescription(tagId)));
    }

    @WrapOperation(method = "getSpecific(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/createmod/ponder/foundation/PonderIndex;editingModeActive()Z"))
    public boolean injectGetSpecific(Operation<Boolean> original, ResourceLocation sceneId, String k) {
        return original.call() || (PonderJSUtils.TRANSLATED_SCENES.contains(sceneId) && !I18n.exists(langKeyForSpecific(sceneId, k)));
    }

    @WrapOperation(method = "getSpecific(Lnet/minecraft/resources/ResourceLocation;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/createmod/ponder/foundation/PonderIndex;editingModeActive()Z"))
    public boolean injectGetSpecific(Operation<Boolean> original, ResourceLocation sceneId, String k, Object[] params) {
        return original.call() || (PonderJSUtils.TRANSLATED_SCENES.contains(sceneId) && !I18n.exists(langKeyForSpecific(sceneId, k)));
    }

}
