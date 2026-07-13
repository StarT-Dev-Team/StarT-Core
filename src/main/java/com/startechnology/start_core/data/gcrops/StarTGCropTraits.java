package com.startechnology.start_core.data.gcrops;

import com.startechnology.start_core.api.gcrop.StarTGCropTrait;

public class StarTGCropTraits {
    public static void init() {
        Charred = new StarTGCropTrait("Charred", "Ch", 0, 3000);

        Vibrant = new StarTGCropTrait("Vibrant", "Vi", 0, 3000);

        Tough = new StarTGCropTrait("Tough", "Th", 0, 3000);

        Fluorescent = new StarTGCropTrait("Fluorescent", "Fl", 0, 3000);

        Metallic = new StarTGCropTrait("Metallic", "Me", 1, 3000);

        Crystalline = new StarTGCropTrait("Crystalline", "Cr", 1, 3000);

        Dusty = new StarTGCropTrait("Dusty", "Du", 1, 3000);

        Woody = new StarTGCropTrait("Woody", "Wo", 1, 3000);

        Coarse = new StarTGCropTrait("Coarse", "Co", 2, 3000);

        Shiny = new StarTGCropTrait("Shiny", "Sh", 2, 3000);
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
