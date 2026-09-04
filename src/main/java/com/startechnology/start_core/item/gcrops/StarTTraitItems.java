package com.startechnology.start_core.item.gcrops;

import com.gregtechceu.gtceu.api.data.tag.TagUtil;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.gcrop.StarTGCropTrait;
import com.startechnology.start_core.data.gcrops.StarTTraitData;
import com.startechnology.start_core.utils.StarTStringUtils;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;

import static com.gregtechceu.gtceu.common.data.models.GTModels.createTextureModel;
import static com.startechnology.start_core.StarTCore.LOGGER;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;
import static com.startechnology.start_core.api.gcrop.StarTGCropTrait.TRAITS;

public class StarTTraitItems {

    public static final Map<String, ItemEntry<ComponentItem>> DNA_STRANDS = new HashMap<>();

    public static TagKey<Item> strandTag = TagUtil.createModItemTag("dna_strand");
    public static TagKey<Item> resourceTag = TagUtil.createModItemTag("dna_strand/resource");
    public static TagKey<Item> productionTag = TagUtil.createModItemTag("dna_strand/production");
    public static TagKey<Item> auxiliaryTag = TagUtil.createModItemTag("dna_strand/auxiliary");
    public static TagKey<Item> climateTag = TagUtil.createModItemTag("dna_strand/climate");

    private static void createDNAStrand(StarTGCropTrait trait) {
        String id = trait.id();
        StarTTraitData.GenomeType type = trait.genomeType();

        TagKey<Item> typeTag;

        switch (type.name().toLowerCase()) {
            case "resource" -> typeTag = resourceTag;
            case "production" -> typeTag = productionTag;
            case "auxiliary" -> typeTag = auxiliaryTag;
            case "climate" -> typeTag = climateTag;
            default -> {
                typeTag = strandTag;
                LOGGER.debug("No type tag found for genome type {}; (trait: {})", type.name(), id);
            }
        }

        ItemEntry<ComponentItem> DNA_STRAND = START_REGISTRATE
                .item(String.format("%s_dna_strand", id), ComponentItem::create)
                .lang(String.format("DNA Strand (%s)", StarTStringUtils.snakeCaseToSentence(id)))
                .model((ctx, prov) -> createTextureModel(ctx, prov,
                        StarTCore.resourceLocation("item/gcrops/dna_strand")))
                .tag(strandTag)
                .tag(typeTag)
                .tag(TagUtil.createModItemTag("traits/" + id))
                .register();

        DNA_STRANDS.put(id, DNA_STRAND);
    }

    public static void init() {
        TRAITS.values().forEach(StarTTraitItems::createDNAStrand);
    }
}
