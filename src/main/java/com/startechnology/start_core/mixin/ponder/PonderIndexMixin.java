package com.startechnology.start_core.mixin.ponder;

import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.integration.ponder.PonderEvents;
import com.startechnology.start_core.integration.ponder.PonderItemTagEventJS;
import com.startechnology.start_core.integration.ponder.PonderRegistryEventJS;
import com.startechnology.start_core.integration.ponder.PonderJSUtils;
import net.createmod.ponder.foundation.PonderIndex;
import net.createmod.ponder.foundation.registration.DefaultPonderTagRegistrationHelper;
import net.createmod.ponder.foundation.registration.PonderLocalization;
import net.createmod.ponder.foundation.registration.PonderSceneRegistry;
import net.createmod.ponder.foundation.registration.PonderTagRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PonderIndex.class, remap = false)
public class PonderIndexMixin {

    @Shadow
    @Final
    private static PonderSceneRegistry SCENES;

    @Shadow
    @Final
    private static PonderTagRegistry TAGS;

    @Shadow
    @Final
    private static PonderLocalization LOCALIZATION;

    @Inject(method = "registerAll", at = @At("RETURN"))
    private static void injectRegisterAll(CallbackInfo ci) {
        PonderJSUtils.TRANSLATED_TAGS.clear();
        PonderJSUtils.TRANSLATED_SCENES.clear();

        PonderEvents.REGISTRY.post(new PonderRegistryEventJS(SCENES));
        var tagRegHelper = new DefaultPonderTagRegistrationHelper(StarTCore.MOD_ID, TAGS, LOCALIZATION);
        PonderEvents.TAGS.post(new PonderItemTagEventJS(tagRegHelper));
    }

}
