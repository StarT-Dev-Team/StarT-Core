package com.startechnology.start_core.data.dimensional_finder;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.api.dimensional_finder.DimensionalFinderDimension;
import com.startechnology.start_core.materials.StarTMaterials;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StarTDimensionalFinderData {

    public static final int VARIANT_BLANK = 0;
    public static final int VARIANT_ABYDOS = 1;
    public static final int VARIANT_NETHER = 2;
    public static final int VARIANT_END = 3;
    public static final int VARIANT_LANTEA = 4;
    public static final int VARIANT_CAVUM = 5;
    public static final int VARIANT_SEA = 6;
    public static final int VARIANT_VOID = 7;

    private static final Map<ResourceLocation, DimensionalFinderDimension> DIMENSIONS = new LinkedHashMap<>();

    public static DimensionalFinderDimension register(DimensionalFinderDimension dimension) {
        DIMENSIONS.put(dimension.getDimensionId(), dimension);
        return dimension;
    }

    public static Collection<DimensionalFinderDimension> getAllDimensions() {
        return Collections.unmodifiableCollection(DIMENSIONS.values());
    }

    public static @Nullable DimensionalFinderDimension getDimension(String dimensionId) {
        return (dimensionId == null || dimensionId.isEmpty()) ? null :
                DIMENSIONS.get(new ResourceLocation(dimensionId));
    }

    public static List<DimensionalFinderDimension> getDimensionsForSource(ResourceLocation sourceDimension) {
        List<DimensionalFinderDimension> matches = new ArrayList<>();
        for (DimensionalFinderDimension dim : DIMENSIONS.values()) {
            if (dim.getRequiredDimension().equals(sourceDimension)) {
                matches.add(dim);
            }
        }
        return matches;
    }

    public static final DimensionalFinderDimension ABYDOS = register(
            DimensionalFinderDimension.builder(new ResourceLocation("sgjourney", "abydos"), 26, 6, 14, 31, 11, 29)
                    .translationKey("dimension.start_core.abydos")
                    .crystalVariant(VARIANT_ABYDOS)
                    .requiredDimension(new ResourceLocation("minecraft", "overworld"))
                    .additionalItem(() -> GTItems.SENSOR_ZPM.asStack(16))
                    .additionalFluid(() -> GTMaterials.Naquadria.getFluid(7200))
                    .eut(GTValues.VHA[GTValues.UV])
                    .cwut(48)
                    .cycleDuration(250)
                    .build());

    public static final DimensionalFinderDimension NETHER = register(
            DimensionalFinderDimension.builder(new ResourceLocation("minecraft", "the_nether"), 27, 23, 4, 34, 12, 28)
                    .translationKey("dimension.start_core.the_nether")
                    .crystalVariant(VARIANT_NETHER)
                    .requiredDimension(new ResourceLocation("sgjourney", "abydos"))
                    .additionalItem(() -> GTItems.SENSOR_UV.asStack(16))
                    .additionalFluid(() -> GTMaterials.Lava.getFluid(50000))
                    .eut(GTValues.VHA[GTValues.UEV])
                    .cwut(192)
                    .cycleDuration(500)
                    .build());

    public static final DimensionalFinderDimension END = register(
            DimensionalFinderDimension.builder(new ResourceLocation("minecraft", "the_end"), 13, 24, 2, 19, 3, 30)
                    .translationKey("dimension.start_core.the_end")
                    .crystalVariant(VARIANT_END)
                    .requiredDimension(new ResourceLocation("minecraft", "the_nether"))
                    .additionalItem(() -> GTItems.SENSOR_UHV.asStack(16))
                    .additionalFluid(() -> StarTMaterials.Echo.getFluid(7200))
                    .eut(GTValues.VA[GTValues.UIV])
                    .cwut(384)
                    .cycleDuration(750)
                    .build());

    public static void init() {}
}
