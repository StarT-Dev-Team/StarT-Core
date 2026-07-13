package com.startechnology.start_core.data.gcrops.traits;

import com.startechnology.start_core.api.gcrop.StarTGCropTrait;

public class Tier2Traits {
    public static void register() {
        StarTGCropTrait Coarse = new StarTGCropTrait("Coarse", "Co", 1, 3000);

        StarTGCropTrait Shiny = new StarTGCropTrait("Shiny", "Sh", 1, 3000);
    }
}
