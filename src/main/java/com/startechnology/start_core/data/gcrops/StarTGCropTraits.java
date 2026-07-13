package com.startechnology.start_core.data.gcrops;

import com.startechnology.start_core.data.gcrops.traits.*;
import com.startechnology.start_core.api.gcrop.StarTGCropTrait;

public class StarTGCropTraits {
    public static void init() {
        Tier0Traits.register();
        Tier1Traits.register();
        Tier2Traits.register();
    }

    // Tier 0
    public static StarTGCropTrait Charred;
    public static StarTGCropTrait Vibrant;
    public static StarTGCropTrait Tough;
    public static StarTGCropTrait Fluorescent;

    // tier 1
    public static StarTGCropTrait Metallic;
    public static StarTGCropTrait Crystalline;
    public static StarTGCropTrait Dusty;
    public static StarTGCropTrait Woody;

    // tier 2
    public static StarTGCropTrait Coarse;
    public static StarTGCropTrait Shiny;
}
