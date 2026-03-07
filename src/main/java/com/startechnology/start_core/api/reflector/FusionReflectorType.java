package com.startechnology.start_core.api.reflector;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import lombok.Getter;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class FusionReflectorType implements StringRepresentable {

    @Getter
    private final String name;

    @Getter
    private final int tier;

    @NotNull
    @Getter
    private final Supplier<Material> material;

    public FusionReflectorType(String name, int tier, @NotNull Supplier<Material> material) {
        this.name = name;
        this.tier = tier;
        this.material = material;
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }

    @Override
    @NotNull
    public String getSerializedName() {
        return name;
    }
}
