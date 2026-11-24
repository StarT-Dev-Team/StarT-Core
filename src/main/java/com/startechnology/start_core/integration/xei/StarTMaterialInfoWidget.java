package com.startechnology.start_core.integration.xei;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.startechnology.start_core.materials.StarTTagPrefixes;

import static com.gregtechceu.gtceu.integration.xei.widgets.GTMaterialInfoWidget.*;

public class StarTMaterialInfoWidget {

    public static void init() {
        addSlot(Section.RAW, StarTTagPrefixes.dustBlock);

        addToSlot(Section.PARTS, TagPrefix.plate, StarTTagPrefixes.ultradensePlate);
        addToSlot(Section.PARTS, TagPrefix.foil, StarTTagPrefixes.foilReam);
        addToSlot(Section.PARTS, TagPrefix.wireFine, StarTTagPrefixes.wireSpool);
    }
}
