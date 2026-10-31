package com.startechnology.start_core.item.claw.client;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.item.StarTItems;
import com.startechnology.start_core.item.claw.StarTClawOfLawItem;
import com.startechnology.start_core.item.claw.StarTClawPart;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class StarTClawColorEvents {

    private StarTClawColorEvents() {}

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> switch (tintIndex) {
            case 1 -> toolMaterial(stack).getMaterialARGB();
            case 2 -> secondaryColor(toolMaterial(stack));
            case 4 -> StarTClawOfLawItem.getColor(stack, StarTClawPart.CUFF);
            case 5 -> StarTClawOfLawItem.getColor(stack, StarTClawPart.MITT);
            case 6 -> StarTClawOfLawItem.getColor(stack, StarTClawPart.TOES);
            default -> -1;
        }, StarTItems.CLAW_OF_LAW.get(), StarTItems.CLAW_OF_LAW_UHV.get());
    }

    private static Material toolMaterial(ItemStack stack) {
        return StarTClawOfLawItem.getToolMaterial(stack, StarTClawOfLawItem.getMode(stack));
    }

    private static int secondaryColor(Material material) {
        return material.getMaterialSecondaryARGB() != -1 ? material.getMaterialSecondaryARGB() :
                material.getMaterialARGB();
    }
}
