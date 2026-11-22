package com.startechnology.start_core.api.dimensional_finder;

import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.StarTConfig.DifficultyPreset;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

@Getter
public class DimensionalFinderDimension {

    private final ResourceLocation dimensionId;
    private final String translationKey;
    private final int crystalVariant;
    private final int[] address;
    private final ResourceLocation requiredDimension;
    private final long eut;
    private final int cwut;
    private final int cycleDuration;
    private final List<Supplier<ItemStack>> additionalItemSuppliers;
    private final List<Supplier<FluidStack>> additionalFluidSuppliers;

    @Nullable
    private volatile List<ItemStack> resolvedAdditionalItems;
    @Nullable
    private volatile List<FluidStack> resolvedAdditionalFluids;

    private final EnumMap<DifficultyPreset, DimensionalCoordinateTree> cachedTrees = new EnumMap<>(
            DifficultyPreset.class);

    public DimensionalFinderDimension(ResourceLocation dimensionId, String translationKey, int crystalVariant,
                                      int[] address, ResourceLocation requiredDimension, long eut, int cwut,
                                      int cycleDuration, List<Supplier<ItemStack>> additionalItemSuppliers,
                                      List<Supplier<FluidStack>> additionalFluidSuppliers) {
        this.dimensionId = Objects.requireNonNull(dimensionId, "dimensionId");
        this.translationKey = Objects.requireNonNull(translationKey, "translationKey");
        this.crystalVariant = crystalVariant;
        this.address = Arrays.copyOf(Objects.requireNonNull(address, "address"), address.length);
        this.requiredDimension = Objects.requireNonNull(requiredDimension, "requiredDimension");
        this.eut = eut;
        this.cwut = cwut;
        this.cycleDuration = cycleDuration;
        this.additionalItemSuppliers = (additionalItemSuppliers == null) ? Collections.emptyList() :
                List.copyOf(additionalItemSuppliers);
        this.additionalFluidSuppliers = (additionalFluidSuppliers == null) ? Collections.emptyList() :
                List.copyOf(additionalFluidSuppliers);
    }

    public List<ItemStack> getAdditionalItems() {
        List<ItemStack> cached = this.resolvedAdditionalItems;

        if (cached != null) return cached;

        synchronized (this) {
            if (this.resolvedAdditionalItems != null) return this.resolvedAdditionalItems;

            List<ItemStack> resolved = new ArrayList<>(this.additionalItemSuppliers.size());

            for (Supplier<ItemStack> supplier : this.additionalItemSuppliers) {
                ItemStack stack = supplier.get();
                if (stack != null && !stack.isEmpty()) resolved.add(stack.copy());
            }

            this.resolvedAdditionalItems = Collections.unmodifiableList(resolved);

            return this.resolvedAdditionalItems;
        }
    }

    public List<FluidStack> getAdditionalFluids() {
        List<FluidStack> cached = this.resolvedAdditionalFluids;

        if (cached != null) return cached;

        synchronized (this) {
            if (this.resolvedAdditionalFluids != null) return this.resolvedAdditionalFluids;

            List<FluidStack> resolved = new ArrayList<>(this.additionalFluidSuppliers.size());

            for (Supplier<FluidStack> supplier : this.additionalFluidSuppliers) {
                FluidStack stack = supplier.get();
                if (stack != null && !stack.isEmpty()) resolved.add(stack.copy());
            }

            this.resolvedAdditionalFluids = Collections.unmodifiableList(resolved);

            return this.resolvedAdditionalFluids;
        }
    }

    public static DifficultyPreset getCurrentPreset() {
        return (StarTConfig.INSTANCE != null && StarTConfig.INSTANCE.dimensionalFinder != null) ?
                StarTConfig.INSTANCE.dimensionalFinder.difficulty : DifficultyPreset.NORMAL;
    }

    public synchronized DimensionalCoordinateTree getTree(DifficultyPreset preset) {
        return cachedTrees.computeIfAbsent(preset, p -> DimensionalCoordinateTree.generateTree(this.address, p));
    }

    public static Builder builder(ResourceLocation dimensionId, int... address) {
        return new Builder(dimensionId, address);
    }

    public static class Builder {

        private final ResourceLocation dimensionId;
        private final int[] address;
        private String translationKey;
        private int crystalVariant = 0;
        private ResourceLocation requiredDimension = new ResourceLocation("minecraft", "overworld");
        private long eut = 32768;
        private int cwut = 16;
        private int cycleDuration = 100;
        private final List<Supplier<ItemStack>> additionalItems = new ArrayList<>();
        private final List<Supplier<FluidStack>> additionalFluids = new ArrayList<>();

        public Builder(ResourceLocation dimensionId, int... address) {
            this.dimensionId = dimensionId;
            this.address = address;
            this.translationKey = "dimension." + dimensionId.getNamespace() + "." + dimensionId.getPath();
        }

        public Builder translationKey(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        public Builder crystalVariant(int crystalVariant) {
            this.crystalVariant = crystalVariant;
            return this;
        }

        public Builder requiredDimension(ResourceLocation requiredDimension) {
            this.requiredDimension = requiredDimension;
            return this;
        }

        public Builder eut(long eut) {
            this.eut = eut;
            return this;
        }

        public Builder cwut(int cwut) {
            this.cwut = cwut;
            return this;
        }

        public Builder cycleDuration(int cycleDuration) {
            this.cycleDuration = cycleDuration;
            return this;
        }

        public Builder additionalItem(Supplier<ItemStack> stackSupplier) {
            if (stackSupplier != null) {
                this.additionalItems.add(stackSupplier);
            }
            return this;
        }

        public Builder additionalFluid(Supplier<FluidStack> stackSupplier) {
            if (stackSupplier != null) {
                this.additionalFluids.add(stackSupplier);
            }
            return this;
        }

        public DimensionalFinderDimension build() {
            return new DimensionalFinderDimension(dimensionId, translationKey, crystalVariant, address,
                    requiredDimension, eut, cwut, cycleDuration, additionalItems, additionalFluids);
        }
    }
}
