package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_FINE_WIRE ;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_DENSE;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_PLATE;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_FRAME;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_ROUND;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_FOIL;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_RING;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_BOLT_SCREW;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_ROD;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_SMALL_GEAR;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.GENERATE_GEAR;

public class StarTMaterialFlags {
    
    public static final MaterialFlag GENERATE_WIRE_SPOOL = new MaterialFlag.Builder("generate_wire_spool")
            .requireFlags(GENERATE_FINE_WIRE).requireProps(PropertyKey.INGOT).build();

    public static final MaterialFlag GENERATE_HYPERDENSE_PLATE = new MaterialFlag.Builder("generate_hyperdense_plate")
            .requireFlags(GENERATE_DENSE).requireProps(PropertyKey.INGOT).build();

    public static final MaterialFlag GENERATE_REINFORCED_BEAM = new MaterialFlag.Builder("generate_reinforced_beam")
            .requireFlags(GENERATE_PLATE, GENERATE_FRAME).requireProps(PropertyKey.INGOT).build();

    public static final MaterialFlag GENERATE_BALL_BEARING = new MaterialFlag.Builder("generate_ball_bearing")
            .requireFlags(GENERATE_RING, GENERATE_ROUND).requireProps(PropertyKey.INGOT).build();

    public static final MaterialFlag GENERATE_RIVET = new MaterialFlag.Builder("generate_rivet")
            .requireFlags(GENERATE_RING, GENERATE_BOLT_SCREW).requireProps(PropertyKey.INGOT).build();

    public static final MaterialFlag GENERATE_MESH = new MaterialFlag.Builder("generate_mesh")
            .requireFlags(GENERATE_FOIL, GENERATE_ROD).requireProps(PropertyKey.INGOT).build();

    public static final MaterialFlag GENERATE_FOIL_REAM = new MaterialFlag.Builder("generate_foil_ream")
            .requireFlags(GENERATE_FOIL).requireProps(PropertyKey.INGOT).build();

    public static final MaterialFlag GENERATE_GEAR_SHIFT = new MaterialFlag.Builder("generate_gear_shift")
            .requireFlags(GENERATE_SMALL_GEAR, GENERATE_GEAR).requireProps(PropertyKey.INGOT).build();

    // === Tag Creation ===

        //Is = 128 Fine Wire + misc
    public static final TagPrefix wire_spool = new TagPrefix("wire_spool")
            .idPattern("%s_wire_spool")
            .defaultTagPath("wire_spool/%s")
            .unformattedTagPath("wire_spool")
            .langValue("%s Wire Spool")
            .materialAmount(GTValues.M * 16)
            .materialIconType(StarTMaterialSet.wire_spool)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(StarTMaterialFlags.GENERATE_WIRE_SPOOL));

        //Is = 4 Dense Plate + misc
    public static final TagPrefix hyperdense_plate = new TagPrefix("hyperdense_plate")
            .idPattern("hyperdense_%s_plate")
            .defaultTagPath("hyperdense_plate/%s")
            .unformattedTagPath("hyperdense_plate")
            .langValue("Hyperdense %s Plate")
            .materialAmount(GTValues.M * 36)
            .materialIconType(StarTMaterialSet.hyperdense_plate)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(StarTMaterialFlags.GENERATE_HYPERDENSE_PLATE));

        //Is = 1 Frame + 4 Plates + misc
    public static final TagPrefix reinforced_beam = new TagPrefix("reinforced_beam")
            .idPattern("reinforced_%s_beam")
            .defaultTagPath("reinforced_beam/%s")
            .unformattedTagPath("reinforced_beam")
            .langValue("Reinforced %s Beam")
            .materialAmount(GTValues.M * 6)
            .materialIconType(StarTMaterialSet.reinforced_beam)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(StarTMaterialFlags.GENERATE_REINFORCED_BEAM));

        //Is = 2 Rings + 1 Rod (ring forces) + 6 Rounds + misc
    public static final TagPrefix ball_bearing = new TagPrefix("ball_bearing")
            .idPattern("%s_ball_bearing")
            .defaultTagPath("ball_bearing/%s")
            .unformattedTagPath("ball_bearing")
            .langValue("%s Ball Bearing")
            .materialAmount(GTValues.M * 1)
            .materialIconType(StarTMaterialSet.ball_bearing)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(StarTMaterialFlags.GENERATE_BALL_BEARING));

        //Is = 1 Rings + 1 Bolt + misc
    public static final TagPrefix rivet = new TagPrefix("rivet")
            .idPattern("%s_rivet")
            .defaultTagPath("rivet/%s")
            .unformattedTagPath("rivet")
            .langValue("%s Rivet")
            .materialAmount(GTValues.M / 4)
            .materialIconType(StarTMaterialSet.rivet)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(StarTMaterialFlags.GENERATE_RIVET));

        //Is = 4 Rods + 2 Foils + misc
    public static final TagPrefix mesh = new TagPrefix("mesh")
            .idPattern("%s_mesh")
            .defaultTagPath("mesh/%s")
            .unformattedTagPath("mesh")
            .langValue("%s Mesh")
            .materialAmount(GTValues.M * 2)
            .materialIconType(StarTMaterialSet.mesh)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(StarTMaterialFlags.GENERATE_MESH));

        //Is = 64 Foils + misc
    public static final TagPrefix foil_ream = new TagPrefix("foil_ream")
            .idPattern("%s_foil_ream")
            .defaultTagPath("foil_ream/%s")
            .unformattedTagPath("foil_ream")
            .langValue("%s Foil Ream")
            .materialAmount(GTValues.M * 16)
            .materialIconType(StarTMaterialSet.foil_ream)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(StarTMaterialFlags.GENERATE_FOIL_REAM));

        //Is = 2 Small Gear + 1 Gear + 2 Rod (included in small gear) + misc
    public static final TagPrefix gear_shift = new TagPrefix("gear_shift")
            .idPattern("%gear_shift")
            .defaultTagPath("gear_shift/%s")
            .unformattedTagPath("gear_shift")
            .langValue("%s Gear Shift")
            .materialAmount(GTValues.M * 7)
            .materialIconType(StarTMaterialSet.gear_shift)
            .unificationEnabled(true)
            .generateItem(true)
            .generationCondition(mat -> mat.hasFlag(StarTMaterialFlags.GENERATE_GEAR_SHIFT));

    public static void init() {}

}