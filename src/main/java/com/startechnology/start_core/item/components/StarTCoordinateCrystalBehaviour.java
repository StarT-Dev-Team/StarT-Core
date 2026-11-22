package com.startechnology.start_core.item.components;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.api.dimensional_finder.DimensionalCoordinateTree.EndKind;
import com.startechnology.start_core.api.dimensional_finder.DimensionalFinderDimension;
import com.startechnology.start_core.data.dimensional_finder.StarTDimensionalFinderData;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.StringJoiner;

public class StarTCoordinateCrystalBehaviour extends StarTNBTTooltipsBehaviour {

    public static @Nullable StarTCoordinateCrystalBehaviour getBehaviour(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;

        Item item = stack.getItem();
        if (!(item instanceof ComponentItem componentItem)) return null;

        for (IItemComponent component : componentItem.getComponents()) {
            if (component instanceof StarTCoordinateCrystalBehaviour behaviour) {
                return behaviour;
            }
        }

        return null;
    }

    public static boolean isCoordinateCrystal(ItemStack stack) {
        return getBehaviour(stack) != null;
    }

    public static boolean isEngraved(ItemStack stack) {
        if (!isCoordinateCrystal(stack) || !stack.hasTag()) return false;

        CompoundTag tag = stack.getTag();

        return tag != null && tag.getBoolean("engraved");
    }

    public static boolean isDimensionCrystal(ItemStack stack) {
        if (!isEngraved(stack)) return false;

        CompoundTag tag = stack.getTag();

        return tag != null && tag.getBoolean("is_dimension");
    }

    public static boolean isFailedCrystal(ItemStack stack) {
        return isEngraved(stack) && !isDimensionCrystal(stack);
    }

    public static int[] getCoordinates(ItemStack stack) {
        if (!isEngraved(stack)) return new int[0];

        CompoundTag tag = stack.getTag();

        return (tag != null) ? tag.getIntArray("coordinates") : new int[0];
    }

    public static @Nullable String getTargetDimensionId(ItemStack stack) {
        if (!isEngraved(stack)) return null;

        CompoundTag tag = stack.getTag();

        if (tag == null || !tag.contains("dimension")) return null;

        String dim = tag.getString("dimension");

        return dim.isEmpty() ? null : dim;
    }

    public static @Nullable EndKind getEndKind(ItemStack stack) {
        if (!isEngraved(stack)) return null;

        CompoundTag tag = stack.getTag();

        if (tag == null || !tag.contains("end_kind")) return null;

        return EndKind.byName(tag.getString("end_kind"));
    }

    public static void engraveDimension(ItemStack stack, ResourceLocation dimensionId, int[] address) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("engraved", true);
        tag.putBoolean("is_dimension", true);
        tag.putString("dimension", dimensionId.toString());
        tag.putIntArray("coordinates", address);
        tag.remove("end_kind");
    }

    public static void engraveFailure(ItemStack stack, ResourceLocation dimensionId, int[] path, EndKind endKind) {
        engraveFailure(stack, dimensionId, path, endKind, DimensionalFinderDimension.getCurrentPreset());
    }

    public static @Nullable StarTConfig.DifficultyPreset getDifficulty(ItemStack stack) {
        if (!isEngraved(stack)) return null;

        CompoundTag tag = stack.getTag();

        if (tag == null || !tag.contains("difficulty")) return null;

        try {
            return StarTConfig.DifficultyPreset.valueOf(tag.getString("difficulty"));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void engraveFailure(ItemStack stack, ResourceLocation dimensionId, int[] path, EndKind endKind,
                                      StarTConfig.DifficultyPreset preset) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString("difficulty", preset.name());
        tag.putBoolean("engraved", true);
        tag.putBoolean("is_dimension", false);
        tag.putString("dimension", dimensionId.toString());
        tag.putIntArray("coordinates", path);
        if (endKind != null) {
            tag.putString("end_kind", endKind.name());
        }
    }

    public static String formatCoordinates(int[] coords) {
        if (coords == null || coords.length == 0) return "";

        StringJoiner joiner = new StringJoiner("-");

        for (int c : coords) {
            joiner.add(Integer.toString(c));
        }

        return joiner.toString();
    }

    public static MutableComponent getDimensionDisplayName(String dimensionId) {
        if (dimensionId == null || dimensionId.isEmpty()) {
            return Component.translatable("behaviour.start_core.coordinate_crystal.unknown_dimension");
        }

        DimensionalFinderDimension dimension = StarTDimensionalFinderData.getDimension(dimensionId);
        if (dimension != null) {
            return Component.translatable(dimension.getTranslationKey());
        }

        ResourceLocation location = ResourceLocation.tryParse(dimensionId);
        if (location != null) {
            return Component.translatable("dimension." + location.getNamespace() + "." + location.getPath());
        }

        return Component.literal(dimensionId);
    }

    public static int getCrystalVariant(ItemStack stack) {
        if (!isDimensionCrystal(stack)) {
            return StarTDimensionalFinderData.VARIANT_BLANK;
        }

        String dimId = getTargetDimensionId(stack);
        if (dimId == null) {
            return StarTDimensionalFinderData.VARIANT_BLANK;
        }

        DimensionalFinderDimension dimension = StarTDimensionalFinderData.getDimension(dimId);
        if (dimension != null && dimension.getCrystalVariant() > 0) {
            return dimension.getCrystalVariant();
        }

        String lower = dimId.toLowerCase();
        if (lower.contains("abydos")) return StarTDimensionalFinderData.VARIANT_ABYDOS;
        if (lower.contains("nether")) return StarTDimensionalFinderData.VARIANT_NETHER;
        if (lower.contains("end")) return StarTDimensionalFinderData.VARIANT_END;
        if (lower.contains("lantea")) return StarTDimensionalFinderData.VARIANT_LANTEA;
        if (lower.contains("cavum")) return StarTDimensionalFinderData.VARIANT_CAVUM;
        if (lower.contains("sea")) return StarTDimensionalFinderData.VARIANT_SEA;
        if (lower.contains("void")) return StarTDimensionalFinderData.VARIANT_VOID;
        return StarTDimensionalFinderData.VARIANT_BLANK;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        if (!isEngraved(stack)) {
            tooltipComponents.add(Component.translatable("behaviour.start_core.coordinate_crystal.not_engraved"));
        } else {
            int[] coords = getCoordinates(stack);
            tooltipComponents.add(Component.translatable("behaviour.start_core.coordinate_crystal.address",
                    Component.literal(formatCoordinates(coords)).withStyle(ChatFormatting.AQUA)));

            String dimId = getTargetDimensionId(stack);
            if (isDimensionCrystal(stack)) {
                tooltipComponents.add(Component.translatable("behaviour.start_core.coordinate_crystal.target_dimension",
                        getDimensionDisplayName(dimId).withStyle(ChatFormatting.GOLD)));
            } else {
                EndKind kind = getEndKind(stack);
                MutableComponent kindComponent = (kind != null) ?
                        Component.translatable(kind.getTranslationKey()).withStyle(ChatFormatting.LIGHT_PURPLE) :
                        Component.translatable("behaviour.start_core.coordinate_crystal.kind.unknown")
                                .withStyle(ChatFormatting.LIGHT_PURPLE);
                tooltipComponents.add(Component.translatable("behaviour.start_core.coordinate_crystal.target_object",
                        kindComponent));
                if (dimId != null) {
                    tooltipComponents.add(Component.translatable("behaviour.start_core.coordinate_crystal.sector",
                            getDimensionDisplayName(dimId).withStyle(ChatFormatting.YELLOW)));
                }
            }
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
