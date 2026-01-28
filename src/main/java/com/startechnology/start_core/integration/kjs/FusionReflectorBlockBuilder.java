package com.startechnology.start_core.integration.kjs;

import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.api.StarTAPI;
import com.startechnology.start_core.api.reflector.FusionReflectorType;
import com.startechnology.start_core.block.FusionReflectorBlock;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@Accessors(chain = true, fluent = true)
public class FusionReflectorBlockBuilder extends BlockBuilder {

    @Setter
    private int tier;

    @NotNull
    public transient Supplier<Material> material = () -> GTMaterials.NULL;

    @Setter
    public transient String texture = "minecraft:missingno";

    public FusionReflectorBlockBuilder(ResourceLocation i) {
        super(i);
        property(GTBlockStateProperties.ACTIVE);
        renderType("cutout_mipped");
        noValidSpawns(true);
    }

    @Override
    protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
        bs.simpleVariant("active=false", newID("block/", "").toString());
        bs.simpleVariant("active=true", newID("block/", "_active").toString());
    }

    @Override
    protected void generateBlockModelJsons(AssetJsonGenerator generator) {
        generator.blockModel(id, m -> {
            m.parent("minecraft:block/cube_all");
            m.texture("all", texture);
        });
        generator.blockModel(id.withSuffix("_active"), m -> {
            m.parent("gtceu:block/cube_2_layer/all");
            m.texture("bot_all", texture);
            m.texture("top_all", texture + "_bloom");
        });
    }

    public FusionReflectorBlockBuilder reflectorMaterial(@NotNull Supplier<Material> material) {
        this.material = material;
        return this;
    }

    @Override
    public Block createObject() {
        var type = new FusionReflectorType(this.id.getPath(), tier, material);
        var result = new FusionReflectorBlock(this.createProperties(), type);
        StarTAPI.FUSION_REFLECTORS.put(type, () -> result);
        return result;
    }
}
