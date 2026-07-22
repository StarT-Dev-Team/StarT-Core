package com.startechnology.start_core.block.arboreal_extractor;

import com.startechnology.start_core.machine.StarTMachineUtils;
import lombok.Getter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.material.Fluid;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@MethodsReturnNonnullByDefault
public enum TreeType implements StringRepresentable {

    RESIN_PRODUCING("resin", StarTMachineUtils.lazyFluid("thermal:resin")),
    LATEX_PRODUCING("latex", StarTMachineUtils.lazyFluid("thermal:latex")),
    SAP_PRODUCING("sap", StarTMachineUtils.lazyFluid("thermal:sap"));

    @Getter
    private final String name;

    @Getter
    private final Supplier<Fluid> fluid;

    TreeType(String name, Supplier<Fluid> fluid) {
        this.name = name;
        this.fluid = fluid;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    private static final Map<String, TreeType> treeTypeMap = Arrays.stream(values())
            .collect(Collectors.toMap(TreeType::getName, treeType -> treeType));

    public static @Nullable TreeType of(String name) {
        return treeTypeMap.get(name);
    }
}
