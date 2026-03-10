package com.startechnology.start_core.mixin;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterialBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FluidDrillMachine;
import com.startechnology.start_core.machine.StarTMachineUtils;
import dev.latvian.mods.kubejs.KubeJS;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = FluidDrillMachine.class, remap = false)
public class FluidDrillMachineMixin {

    /**
     * @author trulyno
     * @reason adding more drilling rig tiers
     */
    @Overwrite
    public static int getDepletionChance(int tier) {
        return switch(tier) {
            case GTValues.HV -> 2;
            case GTValues.EV -> 8;
            case GTValues.ZPM -> 64;
            default -> 1;
        };
    }

    /**
     * @author trulyno
     * @reason adding more drilling rig tiers
     */
    @Overwrite
    public static int getRigMultiplier(int tier) {
        return switch(tier) {
            case GTValues.HV -> 16;
            case GTValues.EV -> 64;
            case GTValues.ZPM -> 256;
            default -> 1;
        };
    }

    /**
     * @author trulyno
     * @reason adding more drilling rig tiers
     */
    @Overwrite
    public static Block getCasingState(int tier) {
        return switch (tier) {
            case GTValues.HV -> GTBlocks.CASING_TITANIUM_STABLE.get();
            case GTValues.EV -> GTBlocks.CASING_TUNGSTENSTEEL_ROBUST.get();
            case GTValues.ZPM -> StarTMachineUtils.getKjsBlock("enriched_naquadah_machine_casing");
            default -> GTBlocks.CASING_STEEL_SOLID.get();
        };
    }

    /**
     * @author trulyno
     * @reason adding more drilling rig tiers
     */
    @Overwrite
    public static Block getFrameState(int tier) {
        Material material = switch(tier) {
            case GTValues.HV -> GTMaterials.Titanium;
            case GTValues.EV -> GTMaterials.TungstenSteel;
            case GTValues.ZPM -> GTMaterials.NaquadahEnriched;
            default -> GTMaterials.Steel;
        };
        return GTMaterialBlocks.MATERIAL_BLOCKS.get(TagPrefix.frameGt, material).get();
    }

    /**
     * @author trulyno
     * @reason adding more drilling rig tiers
     */
    @Overwrite
    public static ResourceLocation getBaseTexture(int tier) {
        String path = switch(tier) {
            case GTValues.HV -> "block/casings/solid/machine_casing_stable_titanium";
            case GTValues.EV -> "block/casings/solid/machine_casing_robust_tungstensteel";
            case GTValues.ZPM -> "block/casings/naquadah/casing";
            default -> "block/casings/solid/machine_casing_solid_steel";
        };
        return (tier > GTValues.EV) ? KubeJS.id(path) : GTCEu.id(path);
    }

}
