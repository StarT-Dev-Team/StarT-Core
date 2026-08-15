package com.startechnology.start_core.item.gcrops;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.utils.StarTStringUtils;
import com.tterrag.registrate.util.entry.ItemEntry;

import java.util.HashMap;

import static com.gregtechceu.gtceu.common.data.models.GTModels.createTextureModel;
import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

public class StarTFruitItems {

    public static final HashMap<String, ItemEntry<ComponentItem>> FRUIT_SLICES = new HashMap<>();
    public static final HashMap<String, ItemEntry<ComponentItem>> POPPED_FRUITS = new HashMap<>();

    public static void createNeededProcessingItems(String id, int tier) {
        id = id.equals("sheldonite") ? "cooperite" : id;
        Material itemMaterial = GTMaterials.get(id);
        String fruitResource = StarTStringUtils.snakeCaseToSentence(id);

        if (tier == 3) {
            ItemEntry<ComponentItem> fruitSliceItem = START_REGISTRATE
                    .item(String.format("%s_fruit_slice", id), ComponentItem::create)
                    .lang(String.format("%s Fruit Slice", fruitResource))
                    .properties(prop -> prop.stacksTo(64))
                    .model((ctx, prov) -> createTextureModel(ctx, prov,
                            StarTCore.resourceLocation("item/gcrops/materials/fruit_slice")))
                    .color(() -> () -> (itemStack, index) -> itemMaterial.getLayerARGB(index))
                    .register();

            FRUIT_SLICES.put(id, fruitSliceItem);
        }
        if (tier == 5 || tier >= 7) {
            ItemEntry<ComponentItem> poppedFruitItem = START_REGISTRATE
                    .item(String.format("popped_%s_fruit", id), ComponentItem::create)
                    .lang(String.format("Popped %s Fruit", fruitResource))
                    .properties(prop -> prop.stacksTo(64))
                    .model((ctx, prov) -> createTextureModel(ctx, prov,
                            StarTCore.resourceLocation("item/gcrops/materials/popped_fruit")))
                    .color(() -> () -> (itemStack, index) -> itemMaterial.getLayerARGB(index))
                    .register();

            POPPED_FRUITS.put(id, poppedFruitItem);
        }
    }
}
