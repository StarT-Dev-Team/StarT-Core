package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import net.minecraft.tags.BlockTags;

public class StarTTagPrefixes {

    public static final TagPrefix dustBlock = new TagPrefix("dustBlock")
            .defaultTagPath("dust_blocks/%s")
            .unformattedTagPath("dust_blocks")
            .langValue("Block of %s dust")
            .materialAmount(GTValues.M * 9)
            .materialIconType(StarTMaterialIconTypes.dustBlockIconType)
            .miningToolTag(BlockTags.MINEABLE_WITH_SHOVEL)
            .generateBlock(true)
            .generationCondition(material -> material.hasProperty(PropertyKey.DUST))
            .unificationEnabled(true);

    public static void init() {

    }

}
