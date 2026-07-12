package com.startechnology.start_core.data.gcrops.traits;

import com.startechnology.start_core.api.gcrop.StarTGCropTrait;

public class Tier0Traits {
    public static void register() {
        StarTGCropTrait Charred = new StarTGCropTrait("Charred", "Ch", 0);

        StarTGCropTrait Vibrant = new StarTGCropTrait("Vibrant", "Vi", 0);

        StarTGCropTrait Tough = new StarTGCropTrait("Tough", "Th", 0);

        StarTGCropTrait Fluorescent = new StarTGCropTrait("Fluorescent", "Fl", 0);
    }
}