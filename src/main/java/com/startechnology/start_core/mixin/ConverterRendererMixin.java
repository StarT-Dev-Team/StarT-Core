package com.startechnology.start_core.mixin;

//import static com.gregtechceu.gtceu.client.renderer.machine.OverlayEnergyIORenderer.ENERGY_IN_64A;
//import static com.gregtechceu.gtceu.client.renderer.machine.OverlayEnergyIORenderer.ENERGY_OUT_64A;
//
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import com.gregtechceu.gtceu.client.renderer.machine.ConverterRenderer;

//@Mixin(ConverterRenderer.class)
//public abstract class ConverterRendererMixin {
//
//
//    @Inject(method = "<init>", at = @At("TAIL"))
//    private void injectConstructor(int tier, int baseAmp, CallbackInfo ci) {
//        if (baseAmp == 64) {
//            // Modify ENERGY_IN and ENERGY_OUT (reflection might be needed if private)
//            try {
//                java.lang.reflect.Field energyInField = ConverterRenderer.class.getDeclaredField("ENERGY_IN");
//                java.lang.reflect.Field energyOutField = ConverterRenderer.class.getDeclaredField("ENERGY_OUT");
//
//                energyInField.setAccessible(true);
//                energyOutField.setAccessible(true);
//
//                energyInField.set(this, ENERGY_IN_64A);
//                energyOutField.set(this, ENERGY_OUT_64A);
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
