package com.startechnology.start_core.materials;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconType;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.startechnology.start_core.utils.StarTStringUtils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.SoundType;

public class StarTTagPrefixes {

    public static final TagPrefix dustBlock = new TagPrefix("dustBlock")
            .defaultTagPath("dust_blocks/%s")
            .unformattedTagPath("dust_blocks")
            .materialAmount(GTValues.M * 9)
            .materialIconType(StarTMaterialIconTypes.dustBlockIconType)
            .miningToolTag(BlockTags.MINEABLE_WITH_SHOVEL)
            .generateBlock(true)
            .generationCondition(material -> material.hasProperty(PropertyKey.DUST))
            .unificationEnabled(true)
            .blockProperties(() -> RenderType::translucent, properties -> properties.sound(SoundType.SAND))
            .fallingBlock();

    public static TagPrefix generateItemTagPrefix(String prefix, MaterialIconType iconType) {
        return new TagPrefix(StarTStringUtils.snakeCaseToCamelCase(prefix))
                .defaultTagPath(prefix + "s/%s")
                .materialIconType(iconType)
                .unificationEnabled(true)
                .enableRecycling()
                .generateItem(true);
    }

    public static final TagPrefix foilReam = generateItemTagPrefix("foil_ream", StarTMaterialIconTypes.foilReam)
            .materialAmount(GTValues.M * 16)
            .generationCondition(material -> material.hasFlag(MaterialFlags.GENERATE_FOIL));

    public static final TagPrefix wireSpool = generateItemTagPrefix("wire_spool", StarTMaterialIconTypes.wireSpool)
            .materialAmount(GTValues.M * 8)
            .generationCondition(material -> material.hasFlag(MaterialFlags.GENERATE_FINE_WIRE));

    public static final TagPrefix ultradensePlate = generateItemTagPrefix("ultradense_plate", StarTMaterialIconTypes.ultraDensePlate)
            .materialAmount(GTValues.M * 36)
            .generationCondition(material -> material.hasFlag(MaterialFlags.GENERATE_DENSE));

    public static final TagPrefix ballBearing = generateItemTagPrefix("ball_bearing", StarTMaterialIconTypes.ballBearing    )
            .materialAmount(GTValues.M)
            .generationCondition(material -> material.hasFlags(MaterialFlags.GENERATE_ROUND, MaterialFlags.GENERATE_RING));


    public static void init() {

    }

}
