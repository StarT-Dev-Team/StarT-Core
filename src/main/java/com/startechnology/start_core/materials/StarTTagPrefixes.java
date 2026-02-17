package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

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
            .unificationEnabled(true)
            .blockProperties(() -> RenderType::translucent, properties -> properties.sound(SoundType.SAND))
            .fallingBlock();

    public static void init() {

    }

}
