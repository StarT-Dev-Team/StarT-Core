package com.startechnology.start_core.data.gcrops.traits;

import com.startechnology.start_core.api.gcrop.StarTGCropTrait;

public class Tier1Traits {
    public static void register() {
        StarTGCropTrait Metallic = new StarTGCropTrait("Metallic", "Me", 1, 3000);

        StarTGCropTrait Crystalline = new StarTGCropTrait("Crystalline", "Cr", 1, 3000);

        StarTGCropTrait Dusty = new StarTGCropTrait("Dusty", "Du", 1, 3000);

        StarTGCropTrait Woody = new StarTGCropTrait("Woody", "Wo", 1, 3000);
    }
}
