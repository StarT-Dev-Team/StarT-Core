package com.startechnology.start_core.machine.runic_circuitry;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeHandler;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.MarkerMaterial;
import com.gregtechceu.gtceu.api.data.chemical.material.MarkerMaterials;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.data.tag.TagUtil;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeFailureReason;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.api.machine.IStarTJadeDisplayable;
import com.startechnology.start_core.machine.redstone.IRedstoneIndicatorMachine;
import com.startechnology.start_core.machine.redstone.RedstoneIndicatorRecord;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RunicCircuitryAssemblyStationMachine extends WorkableElectricMultiblockMachine
                                                  implements IRedstoneIndicatorMachine, IStarTJadeDisplayable {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            RunicCircuitryAssemblyStationMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private static final TagKey<Item> PARENT_LENS_TAG = TagUtil.createItemTag("lenses");

    public record LensColorEntry(int signalLevel, MarkerMaterial markerMaterial, TagKey<Item> colorTag,
                                 ChatFormatting displayColor, String langKey) {

        public Component getColoredName() {
            return Component.translatable(langKey).withStyle(displayColor);
        }
    }

    public enum LensValidationStatus {

        READY,
        MISSING_REQUIRED_LENS,
        WRONG_LENS_PRESENT;

        private static final LensValidationStatus[] VALUES = values();

        public static LensValidationStatus of(int ordinal) {
            if (ordinal < 0 || ordinal >= VALUES.length) {
                return READY;
            }
            return VALUES[ordinal];
        }
    }

    private static final List<LensColorEntry> LENS_COLORS = List.of(
            createLensEntry(DyeColor.ORANGE, ChatFormatting.GOLD),
            createLensEntry(DyeColor.MAGENTA, ChatFormatting.LIGHT_PURPLE),
            createLensEntry(DyeColor.LIGHT_BLUE, ChatFormatting.AQUA),
            createLensEntry(DyeColor.YELLOW, ChatFormatting.YELLOW),
            createLensEntry(DyeColor.LIME, ChatFormatting.GREEN),
            createLensEntry(DyeColor.PINK, ChatFormatting.LIGHT_PURPLE),
            createLensEntry(DyeColor.GRAY, ChatFormatting.DARK_GRAY),
            createLensEntry(DyeColor.LIGHT_GRAY, ChatFormatting.GRAY),
            createLensEntry(DyeColor.CYAN, ChatFormatting.DARK_AQUA),
            createLensEntry(DyeColor.PURPLE, ChatFormatting.DARK_PURPLE),
            createLensEntry(DyeColor.BLUE, ChatFormatting.BLUE),
            createLensEntry(DyeColor.BROWN, ChatFormatting.GOLD),
            createLensEntry(DyeColor.GREEN, ChatFormatting.DARK_GREEN),
            createLensEntry(DyeColor.RED, ChatFormatting.RED),
            createLensEntry(DyeColor.BLACK, ChatFormatting.DARK_GRAY));

    @Persisted
    @DescSynced
    @Getter
    private int requiredLensIndex = -1;

    @Persisted
    @DescSynced
    @Getter
    private int activeLensIndex = -1;

    @Persisted
    @DescSynced
    @Getter
    private LensValidationStatus validationStatus = LensValidationStatus.READY;

    private boolean pendingLookaheadRoll = false;

    public RunicCircuitryAssemblyStationMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    private static LensColorEntry createLensEntry(DyeColor dyeColor, ChatFormatting displayColor) {
        int signalLevel = dyeColor.getId();
        MarkerMaterial markerMaterial = Objects.requireNonNull(
                MarkerMaterials.Color.COLORS.get(dyeColor),
                () -> "Missing MarkerMaterial for dye color " + dyeColor.getName());
        TagKey<Item> colorTag = Objects.requireNonNull(
                ChemicalHelper.getTag(TagPrefix.lens, markerMaterial),
                () -> "Missing lens tag for marker material " + markerMaterial.getName());
        return new LensColorEntry(signalLevel, markerMaterial, colorTag, displayColor,
                "color.minecraft." + dyeColor.getName());
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote() && isFormed() && isValidLensIndex(this.requiredLensIndex)) {
            this.updateRedstoneIndicator();
        }
    }

    @Override
    public void onStructureFormed() {
        if (!isRemote() && !isValidLensIndex(this.requiredLensIndex)) {
            this.requiredLensIndex = this.rollNextLensIndex(-1);
        }

        this.pendingLookaheadRoll = false;
        this.validationStatus = LensValidationStatus.READY;

        super.onStructureFormed();

        if (!isRemote()) {
            updateRedstoneIndicator();
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.activeLensIndex = -1;
        this.pendingLookaheadRoll = false;
        this.validationStatus = LensValidationStatus.READY;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe == null || !super.beforeWorking(recipe)) {
            return false;
        }

        if (isRemote()) return true;

        if (!isValidLensIndex(requiredLensIndex)) {
            this.requiredLensIndex = this.rollNextLensIndex(-1);
            this.updateRedstoneIndicator();
        }

        LensColorEntry requiredEntry = LENS_COLORS.get(requiredLensIndex - 1);

        List<Ingredient> recipeItemInputs = new ArrayList<>();
        for (Content content : recipe.getInputContents(ItemRecipeCapability.CAP)) {
            recipeItemInputs.add(ItemRecipeCapability.CAP.of(content.content));
        }

        boolean hasRequiredLens = false;
        boolean hasWrongLens = false;

        List<IRecipeHandler<?>> handlers = this.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP);
        for (int h = 0, handlerCount = handlers.size(); h < handlerCount && !hasWrongLens; h++) {
            IRecipeHandler<?> handler = handlers.get(h);

            if (!handler.shouldSearchContent()) continue;

            List<Object> contents = handler.getContents();
            for (int count = 0, contentCount = contents.size(); count < contentCount; count++) {
                Object content = contents.get(count);
                if (!(content instanceof ItemStack stack) || stack.isEmpty()) {
                    continue;
                }
                if (isConflictingLens(stack, requiredEntry) && !matchesAny(stack, recipeItemInputs)) {
                    hasWrongLens = true;
                    break;
                }
                if (stack.is(requiredEntry.colorTag())) {
                    hasRequiredLens = true;
                }
            }
        }

        if (hasWrongLens) {
            this.validationStatus = LensValidationStatus.WRONG_LENS_PRESENT;
            this.pendingLookaheadRoll = false;
            RecipeLogic.putFailureReason(getRecipeLogic(), recipe, new RecipeFailureReason(
                    Component.translatable("start_core.machine.runic_circuitry.error.wrong_lens",
                            requiredEntry.getColoredName()),
                    false));
            return false;
        }

        if (!hasRequiredLens) {
            this.validationStatus = LensValidationStatus.MISSING_REQUIRED_LENS;
            this.pendingLookaheadRoll = false;
            RecipeLogic.putFailureReason(getRecipeLogic(), recipe, new RecipeFailureReason(
                    Component.translatable("start_core.machine.runic_circuitry.error.missing_lens",
                            requiredEntry.getColoredName()),
                    false));
            return false;
        }

        this.validationStatus = LensValidationStatus.READY;
        this.pendingLookaheadRoll = true;
        return true;
    }

    @Override
    public boolean onWorking() {
        boolean working = super.onWorking();
        if (working && !isRemote() && this.pendingLookaheadRoll) {
            this.pendingLookaheadRoll = false;
            this.activeLensIndex = this.requiredLensIndex;
            this.requiredLensIndex = this.rollNextLensIndex(this.activeLensIndex);
            this.updateRedstoneIndicator();
        }
        return working;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        this.activeLensIndex = -1;
        this.pendingLookaheadRoll = false;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;

        appendLensLines(this.requiredLensIndex, this.activeLensIndex, isCrafting(), this.validationStatus,
                textList::add);
    }

    private boolean isCrafting() {
        return this.getRecipeLogic().isWorking() || this.getRecipeLogic().isWaiting();
    }

    private static @Nullable LensColorEntry getLensEntry(int lensIndex) {
        return isValidLensIndex(lensIndex) ? LENS_COLORS.get(lensIndex - 1) : null;
    }

    private static void appendLensLines(int requiredIndex, int activeIndex, boolean crafting,
                                        LensValidationStatus status, Consumer<Component> out) {
        LensColorEntry requiredEntry = getLensEntry(requiredIndex);
        if (requiredEntry == null) return;

        if (crafting) {
            LensColorEntry activeEntry = getLensEntry(activeIndex);
            if (activeEntry != null) {
                out.accept(Component.translatable("ui.start_core.runic_circuitry.active_lens",
                        activeEntry.getColoredName()).withStyle(ChatFormatting.GRAY));
            }
            out.accept(Component.translatable("ui.start_core.runic_circuitry.next_lens",
                    requiredEntry.getColoredName(),
                    requiredEntry.signalLevel()).withStyle(ChatFormatting.GRAY));
            return;
        }

        out.accept(Component.translatable("ui.start_core.runic_circuitry.required_lens",
                requiredEntry.getColoredName(),
                requiredEntry.signalLevel()).withStyle(ChatFormatting.GRAY));

        if (status == LensValidationStatus.MISSING_REQUIRED_LENS) {
            out.accept(Component.translatable("start_core.machine.runic_circuitry.error.missing_lens",
                    requiredEntry.getColoredName()).withStyle(ChatFormatting.RED));
        } else if (status == LensValidationStatus.WRONG_LENS_PRESENT) {
            out.accept(Component.translatable("start_core.machine.runic_circuitry.error.wrong_lens",
                    requiredEntry.getColoredName()).withStyle(ChatFormatting.RED));
        }
    }

    public int getCurrentRedstoneSignal() {
        if (!isFormed() || !isValidLensIndex(this.requiredLensIndex)) {
            return 0;
        }
        return this.requiredLensIndex;
    }

    @Override
    public List<RedstoneIndicatorRecord> getInitialIndicators() {
        return List.of(new RedstoneIndicatorRecord(
                "variadic.start_core.indicator.runic_circuitry.required_lens",
                Component.translatable("variadic.start_core.indicator.runic_circuitry.required_lens"),
                Component.translatable("variadic.start_core.description.runic_circuitry.required_lens")
                        .withStyle(ChatFormatting.GRAY),
                getCurrentRedstoneSignal(),
                0));
    }

    private void updateRedstoneIndicator() {
        setIndicatorValue("variadic.start_core.indicator.runic_circuitry.required_lens", getCurrentRedstoneSignal());
    }

    private static boolean isValidLensIndex(int index) {
        return index >= 1 && index <= 15;
    }

    private static boolean matchesAny(ItemStack stack, List<Ingredient> ingredients) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.test(stack)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isConflictingLens(ItemStack stack, LensColorEntry requiredEntry) {
        if (stack.is(PARENT_LENS_TAG) && !stack.is(requiredEntry.colorTag())) {
            return true;
        }
        for (int i = 0, size = LENS_COLORS.size(); i < size; i++) {
            LensColorEntry entry = LENS_COLORS.get(i);
            if (entry != requiredEntry && stack.is(entry.colorTag())) {
                return true;
            }
        }
        return false;
    }

    private int rollNextLensIndex(int excludedIndex) {
        RandomSource random = (this.getLevel() != null) ? this.getLevel().getRandom() : GTValues.RNG;
        if (!isValidLensIndex(excludedIndex)) {
            return 1 + random.nextInt(LENS_COLORS.size());
        }
        int next = 1 + random.nextInt(LENS_COLORS.size() - 1);
        if (next >= excludedIndex) {
            next++;
        }
        return next;
    }

    @Override
    public void writeJadeData(CompoundTag data) {
        data.putBoolean("formed", this.isFormed());
        data.putInt("requiredLensIndex", this.getRequiredLensIndex());
        data.putInt("activeLensIndex", this.getActiveLensIndex());
        data.putBoolean("working", isCrafting());
        data.putInt("validationStatus", this.getValidationStatus().ordinal());
    }

    @Override
    public void appendJadeTooltip(CompoundTag data, Consumer<Component> tooltip) {
        if (!data.contains("formed") || !data.getBoolean("formed") || !data.contains("requiredLensIndex") ||
                !data.contains("activeLensIndex") || !data.contains("working") || !data.contains("validationStatus")) {
            return;
        }

        appendLensLines(data.getInt("requiredLensIndex"), data.getInt("activeLensIndex"),
                data.getBoolean("working"), LensValidationStatus.of(data.getInt("validationStatus")), tooltip);
    }
}
