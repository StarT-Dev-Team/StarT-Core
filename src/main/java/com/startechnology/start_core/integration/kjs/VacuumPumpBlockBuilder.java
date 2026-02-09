package com.startechnology.start_core.integration.kjs;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.startechnology.start_core.api.StarTAPI;
import com.startechnology.start_core.block.VacuumPumpBlock;
import com.startechnology.start_core.api.vacuumpump.IVacuumPumpType;
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
public class VacuumPumpBlockBuilder extends BlockBuilder {

    @Setter
    private transient int tier;

    @Setter
    private transient float rate, cap;

    @NotNull
    public transient Supplier<Material> material = () -> GTMaterials.NULL;

    @Setter
    public transient String texture = "minecraft:missingno";

    public VacuumPumpBlockBuilder(ResourceLocation i) {
        super(i);
        renderType("cutout_mipped");
        noValidSpawns(true);
    }

    @Override
    protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
        bs.simpleVariant("", newID("block/", "").toString());
    }

    @Override
    protected void generateBlockModelJsons(AssetJsonGenerator generator) {
        generator.blockModel(id, m -> {
            m.parent("minecraft:block/cube_all");
            m.texture("all", texture);
        });
    }

    @Override
    public Block createObject() {
        var type = new KjsVacuumPumpType(this.id.getPath(), tier, rate, cap, new ResourceLocation(texture));
        var result = new VacuumPumpBlock(this.createProperties(), type);
        StarTAPI.VACUUM_PUMPS.put(type, () -> result);
        return result;
    }

    public record KjsVacuumPumpType(String name, int tier, float rate, float cap, ResourceLocation texture) implements IVacuumPumpType {

        @Override
        public @NotNull String getName() {
            return this.name;
        }

        @Override
        public float getRate() {
            return this.rate;
        }

        @Override
        public float getCap() {
            return this.cap;
        }

        @Override
        public int getTier() {
            return this.tier;
        }

        @Override
        public ResourceLocation getTexture() {
            return this.texture;
        }

    }
}
