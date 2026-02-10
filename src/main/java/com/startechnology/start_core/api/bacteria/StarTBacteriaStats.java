package com.startechnology.start_core.api.bacteria;
import org.apache.commons.lang3.StringUtils;

import com.gregtechceu.gtceu.GTCEu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;


public class StarTBacteriaStats {
    public static final String BACTERIA_PRODUCTION_NBT_TAG = "bacteria_production";
    public static final String BACTERIA_METABOLISM_NBT_TAG = "bacteria_metabolism";
    public static final String BACTERIA_MUTABILITY_NBT_TAG = "bacteria_mutability";
    public static final String BACTERIA_PRIMARY_NBT_TAG = "bacteria_primary";
    public static final String BACTERIA_SECONDARY_NBT_TAG = "bacteria_secondary";
    public static final String BACTERIA_TERTIARY_NBT_TAG = "bacteria_tertiary";
    public static final String BACTERIA_SUPERFLUID_NBT_TAG = "bacteria_superFluid";
    public static final Integer MAX_STAT_VALUE = 5;

    private Integer production;
    private Integer metabolism;
    private Integer mutability;
    private Fluid primary;
    private Fluid secondary;
    private Fluid tertiary;
    private Fluid superFluid;

    public Integer getProduction() {
        return production;
    }

    public Integer getMetabolism() {
        return metabolism;
    }

    public Integer getMutability() {
        return mutability;
    }

    public Fluid getPrimary() {
        return primary;
    }

    public Fluid getSecondary() {
        return secondary;
    }

    public Fluid getTertiary() {
        return tertiary;
    }

    public Fluid getSuperFluid() {
        return superFluid;
    }

    public String getProductionPretty() {
        return StarTBacteriaStats.getPrettyStatHighBias(production);
    }

    public String getMetabolismPretty() {
        return StarTBacteriaStats.getPrettyStatLowBias(metabolism);
    }

    public MutableComponent getFluidPretty(Fluid fluid) {
        if (fluid == null) {
            return Component.translatable("behaviour.start_core.bacteria.affinity_none");
        }
        return Component.translatable(fluid.getFluidType().getDescriptionId());
    }

    public String getMutabilityPretty() {
        return String.format("§f%s§r", StarTBacteriaStats.getStatRectangleForm(mutability));
    }

    public static String getPrettyStatHighBias(Integer stat) {
        String colourCode = switch (stat) {
            case 1 -> "§4";
            case 2 -> "§c";
            case 3 -> "§e";
            case 4 -> "§2";
            case 5 -> "§a";
            default -> " §e";
        };

        return String.format("%s%s§r", colourCode, StarTBacteriaStats.getStatRectangleForm(stat));
    }


    public static String getPrettyStatLowBias(Integer stat) {
        String colourCode = switch (stat) {
            case 5 -> "§4";
            case 4 -> "§c";
            case 3 -> "§e";
            case 2 -> "§2";
            case 1 -> "§a";
            default -> " §e";
        };

        return String.format("%s%s§r", colourCode, StarTBacteriaStats.getStatRectangleForm(stat));
    }

    public static String getStatRectangleForm(Integer stat) {
        return StringUtils.repeat('■', stat) + StringUtils.repeat('□', MAX_STAT_VALUE - stat);
    }

    public StarTBacteriaStats(Integer production, Integer metabolism, Integer mutability, Fluid primary, Fluid secondary, Fluid tertiary, Fluid superFluid) {
        this.production = production;
        this.metabolism = metabolism;
        this.mutability = mutability;
        this.primary = primary;
        this.secondary = secondary;
        this.tertiary = tertiary;
        this.superFluid = superFluid;
    }

    public StarTBacteriaStats(Integer production, Integer metabolism, Integer mutability,  Fluid primary, Fluid secondary, Fluid tertiary) {
        this.production = production;
        this.metabolism = metabolism;
        this.mutability = mutability;
        this.primary = primary;
        this.secondary = secondary;
        this.tertiary = tertiary;
        this.superFluid = null;
    }

    public StarTBacteriaStats(CompoundTag bacteriaStatsCompound) {
        this.production = bacteriaStatsCompound.getInt(BACTERIA_PRODUCTION_NBT_TAG);
        this.metabolism = bacteriaStatsCompound.getInt(BACTERIA_METABOLISM_NBT_TAG);
        this.mutability = bacteriaStatsCompound.getInt(BACTERIA_MUTABILITY_NBT_TAG);
        
        String affinityString = bacteriaStatsCompound.getString(BACTERIA_PRIMARY_NBT_TAG);
        String[] resourceSplit = affinityString.split(":");
        ResourceLocation bacteriaAffinityLocation = new ResourceLocation(resourceSplit[0], resourceSplit[1]);
        
        if (ForgeRegistries.FLUIDS.containsKey(bacteriaAffinityLocation)) {
            this.primary = ForgeRegistries.FLUIDS.getValue(bacteriaAffinityLocation);
        }

        affinityString = bacteriaStatsCompound.getString(BACTERIA_SECONDARY_NBT_TAG);
        resourceSplit = affinityString.split(":");
        bacteriaAffinityLocation = new ResourceLocation(resourceSplit[0], resourceSplit[1]);
        
        if (ForgeRegistries.FLUIDS.containsKey(bacteriaAffinityLocation)) {
            this.secondary = ForgeRegistries.FLUIDS.getValue(bacteriaAffinityLocation);
        }

        affinityString = bacteriaStatsCompound.getString(BACTERIA_TERTIARY_NBT_TAG);
        resourceSplit = affinityString.split(":");
        bacteriaAffinityLocation = new ResourceLocation(resourceSplit[0], resourceSplit[1]);
        
        if (ForgeRegistries.FLUIDS.containsKey(bacteriaAffinityLocation)) {
            this.tertiary = ForgeRegistries.FLUIDS.getValue(bacteriaAffinityLocation);
        }

        affinityString = bacteriaStatsCompound.getString(BACTERIA_SUPERFLUID_NBT_TAG);
        resourceSplit = affinityString.split(":");
        bacteriaAffinityLocation = new ResourceLocation(resourceSplit[0], resourceSplit[1]);

        if (ForgeRegistries.FLUIDS.containsKey(bacteriaAffinityLocation)) {
            this.superFluid = ForgeRegistries.FLUIDS.getValue(bacteriaAffinityLocation);
        }
    }

    public CompoundTag toCompoundTag() {
        CompoundTag bacteriaStatsCompound = new CompoundTag();

        bacteriaStatsCompound.putInt(BACTERIA_PRODUCTION_NBT_TAG, this.production);
        bacteriaStatsCompound.putInt(BACTERIA_METABOLISM_NBT_TAG, this.metabolism);
        bacteriaStatsCompound.putInt(BACTERIA_MUTABILITY_NBT_TAG, this.mutability);

        if (primary != null) {
            bacteriaStatsCompound.putString(
                BACTERIA_PRIMARY_NBT_TAG, 
                ForgeRegistries.FLUIDS.getKey(primary).toString()
            );
        }

        if (secondary != null) {
            bacteriaStatsCompound.putString(
                BACTERIA_SECONDARY_NBT_TAG, 
                ForgeRegistries.FLUIDS.getKey(secondary).toString()
            );
        }

        if (tertiary != null) {
            bacteriaStatsCompound.putString(
                BACTERIA_TERTIARY_NBT_TAG, 
                ForgeRegistries.FLUIDS.getKey(tertiary).toString()
            );
        }

        if (superFluid != null) {
            bacteriaStatsCompound.putString(
                    BACTERIA_SUPERFLUID_NBT_TAG,
                    ForgeRegistries.FLUIDS.getKey(superFluid).toString()
            );
        }

        return bacteriaStatsCompound;
    }
}
