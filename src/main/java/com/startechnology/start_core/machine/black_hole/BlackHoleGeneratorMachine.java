package com.startechnology.start_core.machine.black_hole;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.machine.megastructure.LaserOutput;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.COLLAPSE_TICKS;
import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.EVAPORATION_TICKS;
import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.IGNITION_EUT_PER_STABILIZER;
import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.IGNITION_TICKS;
import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.V_UIV;
import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.WARNING_STABILITY;
import static com.startechnology.start_core.machine.black_hole.BlackHoleBalance.accretionLimit;
import static com.startechnology.start_core.machine.black_hole.StarTBlackHoleMachines.CONTROLLER_DROP;
import static com.startechnology.start_core.machine.black_hole.StarTBlackHoleMachines.STABILIZER_COUNT;
import static com.startechnology.start_core.machine.black_hole.StarTBlackHoleMachines.STABILIZER_DIRECTIONS;
import static com.startechnology.start_core.machine.black_hole.StarTBlackHoleMachines.STABILIZER_DISTANCE;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlackHoleGeneratorMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            BlackHoleGeneratorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private static final String[] AXIS_NAMES = { "+X", "-X", "+Y", "-Y", "+Z", "-Z" };
    private static final int STEP_TICKS = 20;
    private static final int DISPLAY_MASS_REFRESH_TICKS = 40;
    private static final double DISPLAY_MASS_THRESHOLD = 0.005;
    private static final int MAX_FEED_RECIPES_PER_STEP = 8;

    @Persisted
    @DescSynced
    private int phase;
    @Persisted
    private long phaseElapsedTicks;
    @DescSynced
    @Getter
    private long phaseStartGameTime;
    @Persisted
    @DescSynced
    @Getter
    private String seedProfileId = "";
    @Persisted
    @DescSynced
    @Getter
    private float spin;

    @Persisted
    @DescSynced
    @Getter
    private float displayMass;
    @Persisted
    @DescSynced
    @Getter
    private int displayGrowth;
    @Persisted
    @DescSynced
    @Getter
    private int displayStability;
    @Persisted
    @DescSynced
    @Getter
    private int displayFeed;
    @Persisted
    @DescSynced
    @Getter
    private int beamLevels;

    private final BlackHoleState state = new BlackHoleState();
    private double reservoir;
    private final double[] supply = { 1, 1, 1, 1, 1, 1 };
    private final long[] supplied = new long[STABILIZER_COUNT];
    private int suppliedTicks;
    private long lastEmitted;
    private long lastDisplayMassUpdate;

    private final BlackHoleStabilizerPartMachine[] stabilizers = new BlackHoleStabilizerPartMachine[STABILIZER_COUNT];
    private final LaserOutput laserOutput = new LaserOutput();
    private TickableSubscription tickSubscription;

    public BlackHoleGeneratorMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    public BlackHolePhase getPhase() {
        return BlackHolePhase.byId(phase);
    }

    private Direction relative(RelativeDirection direction) {
        return direction.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }

    public BlockPos getCenter() {
        return getPos().relative(relative(RelativeDirection.BACK), STABILIZER_DISTANCE)
                .relative(relative(RelativeDirection.UP), CONTROLLER_DROP);
    }

    public Direction getStabilizerDirection(int index) {
        return relative(STABILIZER_DIRECTIONS[index]);
    }

    public int getBeamLevel(int index) {
        return (beamLevels >> (index * 4)) & 0xF;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (isRemote()) return;
        phaseStartGameTime = getLevel().getGameTime() - phaseElapsedTicks;
        tickSubscription = subscribeServerTick(tickSubscription, this::tick);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        unsubscribe(tickSubscription);
        tickSubscription = null;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        var center = getCenter();
        for (int i = 0; i < STABILIZER_COUNT; i++) {
            var pos = center.relative(getStabilizerDirection(i), STABILIZER_DISTANCE);
            stabilizers[i] = getParts().stream()
                    .filter(part -> part.self().getPos().equals(pos))
                    .filter(BlackHoleStabilizerPartMachine.class::isInstance)
                    .map(BlackHoleStabilizerPartMachine.class::cast)
                    .findFirst()
                    .orElse(null);
        }

        laserOutput.collect(getParts());
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        laserOutput.clear();
        Arrays.fill(stabilizers, null);
        if (isHoldingSingularity() && isStructureLoaded()) {
            setPhase(BlackHolePhase.COLLAPSING);
        }
    }

    @Override
    public boolean isRecipeLogicAvailable() {
        return false;
    }

    private boolean isHoldingSingularity() {
        var current = getPhase();
        return current == BlackHolePhase.IGNITING || current == BlackHolePhase.STABLE ||
                current == BlackHolePhase.EVAPORATING;
    }

    private boolean isStructureLoaded() {
        var center = getCenter();
        return getLevel().hasChunksAt(center.offset(-STABILIZER_DISTANCE, -STABILIZER_DISTANCE, -STABILIZER_DISTANCE),
                center.offset(STABILIZER_DISTANCE, STABILIZER_DISTANCE, STABILIZER_DISTANCE));
    }

    private boolean areStabilizersReady() {
        for (var stabilizer : stabilizers) {
            if (stabilizer == null || stabilizer.isInValid()) return false;
        }
        return true;
    }

    private void tick() {
        var current = getPhase();
        if (current == BlackHolePhase.IDLE) {
            if (getOffsetTimer() % STEP_TICKS == 0) tryIgnite();
            return;
        }

        if (current != BlackHolePhase.COLLAPSING) {
            if (!isFormed()) {
                if (isStructureLoaded()) setPhase(BlackHolePhase.COLLAPSING);
                return;
            }
            if (getMultiblockState().hasError() || !areStabilizersReady()) return;
        }

        phaseElapsedTicks++;
        switch (current) {
            case IGNITING -> tickIgniting();
            case STABLE -> tickStable();
            case EVAPORATING -> {
                if (phaseElapsedTicks >= EVAPORATION_TICKS) finish();
            }
            case COLLAPSING -> {
                if (phaseElapsedTicks >= COLLAPSE_TICKS) finish();
            }
        }
    }

    private void tryIgnite() {
        if (!isFormed() || getMultiblockState().hasError() || !isWorkingEnabled() || !areStabilizersReady()) return;
        for (var stabilizer : stabilizers) {
            if (stabilizer.getEnergyContainer().getEnergyStored() < IGNITION_EUT_PER_STABILIZER * IGNITION_TICKS) {
                return;
            }
        }

        var recipe = findRecipe(StarTRecipeTypes.BLACK_HOLE_IGNITION_RECIPES);
        if (recipe == null) return;
        var profile = BlackHoleSeeds.get(recipe.data.getString(BlackHoleSeeds.RECIPE_DATA_SEED));
        if (profile == null) return;
        if (!RecipeHelper.handleRecipeIO(this, recipe, IO.IN, getRecipeLogic().getChanceCaches()).isSuccess()) return;

        seedProfileId = profile.id();
        spin = profile.spin();
        state.reset(profile.initialMass());
        reservoir = 0;
        displayMass = (float) profile.initialMass();
        displayStability = 255;
        setBeamLevels(15);
        setPhase(BlackHolePhase.IGNITING);
    }

    private void tickIgniting() {
        for (var stabilizer : stabilizers) {
            if (drain(stabilizer, IGNITION_EUT_PER_STABILIZER) < IGNITION_EUT_PER_STABILIZER) {
                setPhase(BlackHolePhase.COLLAPSING);
                return;
            }
        }
        if (phaseElapsedTicks >= IGNITION_TICKS) {
            resetSupply();
            setPhase(BlackHolePhase.STABLE);
        }
    }

    private void tickStable() {
        long perStabilizer = (long) Math.ceil(state.demandPerStabilizer());
        for (int i = 0; i < STABILIZER_COUNT; i++) {
            supplied[i] += drain(stabilizers[i], perStabilizer);
        }
        suppliedTicks++;
        lastEmitted = laserOutput.emit((long) state.output);

        if (phaseElapsedTicks % STEP_TICKS != 0) return;

        double requested = perStabilizer * (double) suppliedTicks;
        for (int i = 0; i < STABILIZER_COUNT; i++) {
            supply[i] = requested > 0 ? supplied[i] / requested : 1;
        }
        resetSupply();

        double cap = accretionLimit(state.mass);
        if (reservoir < cap && isWorkingEnabled()) {
            reservoir += consumeMatter(cap - reservoir);
        }
        double fed = Math.min(reservoir, cap);
        reservoir -= fed;

        var outcome = state.step(STEP_TICKS / 20.0, fed, supply);
        updateDisplay(cap);
        markDirty();

        switch (outcome) {
            case EVAPORATE -> setPhase(BlackHolePhase.EVAPORATING);
            case COLLAPSE -> setPhase(BlackHolePhase.COLLAPSING);
            default -> {}
        }
    }

    private void finish() {
        state.reset(0);
        reservoir = 0;
        seedProfileId = "";
        spin = 0;
        displayMass = 0;
        displayGrowth = 0;
        displayStability = 0;
        displayFeed = 0;
        setBeamLevels(0);
        setPhase(BlackHolePhase.IDLE);
    }

    private void setPhase(BlackHolePhase next) {
        phase = next.ordinal();
        phaseElapsedTicks = 0;
        phaseStartGameTime = getLevel().getGameTime();
        lastEmitted = 0;
        if (next != BlackHolePhase.STABLE && next != BlackHolePhase.IGNITING) setBeamLevels(0);
        markDirty();
    }

    private void resetSupply() {
        suppliedTicks = 0;
        Arrays.fill(supplied, 0);
    }

    private static long drain(BlackHoleStabilizerPartMachine stabilizer, long amount) {
        var container = stabilizer.getEnergyContainer();
        long drained = Math.min(container.getEnergyStored(), amount);
        if (drained > 0) container.removeEnergy(drained);
        return drained;
    }

    private double consumeMatter(double budget) {
        double consumed = 0;
        for (int i = 0; i < MAX_FEED_RECIPES_PER_STEP && consumed < budget; i++) {
            var recipe = findRecipe(StarTRecipeTypes.BLACK_HOLE_FEEDING_RECIPES);
            if (recipe == null) break;
            float mass = recipe.data.getFloat(BlackHoleSeeds.RECIPE_DATA_MASS);
            if (mass <= 0) break;

            int limit = (int) Math.min(Math.ceil((budget - consumed) / mass), Integer.MAX_VALUE);
            int parallel = ParallelLogic.getMaxByInput(this, recipe, limit, List.of());
            if (parallel <= 0) break;
            var scaled = parallel == 1 ? recipe : recipe.copy(ContentModifier.multiplier(parallel), false);
            if (!RecipeHelper.handleRecipeIO(this, scaled, IO.IN, getRecipeLogic().getChanceCaches()).isSuccess()) {
                break;
            }
            consumed += parallel * (double) mass;
        }
        return consumed;
    }

    @Nullable
    private GTRecipe findRecipe(GTRecipeType type) {
        var iterator = type.searchRecipe(this, recipe -> RecipeHelper.matchContents(this, recipe).isSuccess());
        while (iterator.hasNext()) {
            var recipe = iterator.next();
            if (recipe != null) return recipe;
        }
        return null;
    }

    private void updateDisplay(double cap) {
        long gameTime = getLevel().getGameTime();
        if (Math.abs(state.mass - displayMass) > displayMass * DISPLAY_MASS_THRESHOLD ||
                gameTime - lastDisplayMassUpdate >= DISPLAY_MASS_REFRESH_TICKS) {
            displayMass = (float) state.mass;
            lastDisplayMassUpdate = gameTime;
        }
        displayGrowth = Mth.clamp((int) Math.round(state.dMdt / cap * 127), -127, 127);
        displayStability = Mth.clamp((int) Math.round(state.stability * 255), 0, 255);
        displayFeed = Mth.clamp((int) Math.round(state.feedRate / cap * 255), 0, 255);

        int packed = 0;
        for (int i = 0; i < STABILIZER_COUNT; i++) {
            int level = Mth.clamp((int) Math.round(supply[i] * 15), 0, 15);
            packed |= level << (i * 4);
            stabilizers[i].setPowerLevel(level);
        }
        beamLevels = packed;
    }

    private void setBeamLevels(int level) {
        int packed = 0;
        for (int i = 0; i < STABILIZER_COUNT; i++) {
            packed |= level << (i * 4);
            if (stabilizers[i] != null) stabilizers[i].setPowerLevel(level);
        }
        beamLevels = packed;
    }

    @Override
    public void saveCustomPersistedData(CompoundTag tag, boolean forDrop) {
        super.saveCustomPersistedData(tag, forDrop);
        if (forDrop) return;
        var hole = new CompoundTag();
        hole.putDouble("mass", state.mass);
        hole.putDouble("stability", state.stability);
        hole.putDouble("feedRate", state.feedRate);
        hole.putDouble("dMdt", state.dMdt);
        hole.putDouble("demand", state.demand);
        hole.putDouble("output", state.output);
        hole.putDouble("reservoir", reservoir);
        tag.put("blackHole", hole);
    }

    @Override
    public void loadCustomPersistedData(CompoundTag tag) {
        super.loadCustomPersistedData(tag);
        var hole = tag.getCompound("blackHole");
        state.mass = hole.getDouble("mass");
        state.stability = hole.getDouble("stability");
        state.feedRate = hole.getDouble("feedRate");
        state.dMdt = hole.getDouble("dMdt");
        state.demand = hole.getDouble("demand");
        state.output = hole.getDouble("output");
        reservoir = hole.getDouble("reservoir");
    }

    public long getOutputEUt() {
        return getPhase() == BlackHolePhase.STABLE ? (long) state.output : 0;
    }

    public double getMass() {
        return state.mass;
    }

    public double getStability() {
        return state.stability;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        var current = getPhase();
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(isWorkingEnabled(), current.isActive())
                .addPatternErrorLine(getMultiblockState().error)
                .addWorkingStatusLine();
        if (!isFormed() && current == BlackHolePhase.IDLE) return;

        textList.add(Component.translatable("start_core.machine.black_hole_generator.phase",
                Component.translatable("start_core.machine.black_hole_generator.phase." +
                        current.name().toLowerCase()).withStyle(phaseColor(current))));

        switch (current) {
            case IDLE -> {
                textList.add(Component.translatable("start_core.machine.black_hole_generator.ignition_cost",
                        FormattingUtil.formatNumbers(IGNITION_EUT_PER_STABILIZER * IGNITION_TICKS))
                        .withStyle(ChatFormatting.GRAY));
                addStabilizerLines(textList);
            }
            case IGNITING -> {
                textList.add(Component.translatable("start_core.machine.black_hole_generator.progress",
                        Math.min(100, phaseElapsedTicks * 100 / IGNITION_TICKS)).withStyle(ChatFormatting.GRAY));
                addStabilizerLines(textList);
            }
            case STABLE -> addStableLines(textList);
            default -> {}
        }
    }

    private void addStableLines(List<Component> textList) {
        var profile = BlackHoleSeeds.get(seedProfileId);
        if (profile != null) {
            textList.add(Component.translatable("start_core.machine.black_hole_generator.seed",
                    profile.displayName()).withStyle(ChatFormatting.GRAY));
        }

        String trend = state.dMdt > 1e-4 ? "growing" : state.dMdt < -1e-4 ? "shrinking" : "steady";
        textList.add(Component.translatable("start_core.machine.black_hole_generator.mass",
                FormattingUtil.formatNumber2Places((float) state.mass),
                Component.translatable("start_core.machine.black_hole_generator." + trend,
                        FormattingUtil.formatNumber2Places((float) Math.abs(state.dMdt))))
                .withStyle(ChatFormatting.GRAY));

        double cap = accretionLimit(state.mass);
        textList.add(Component.translatable("start_core.machine.black_hole_generator.feed",
                FormattingUtil.formatNumber2Places((float) state.feedRate),
                FormattingUtil.formatNumber2Places((float) cap)).withStyle(ChatFormatting.GRAY));

        var color = state.stability < WARNING_STABILITY ? ChatFormatting.RED :
                state.stability < 0.9 ? ChatFormatting.YELLOW : ChatFormatting.GREEN;
        textList.add(Component.translatable("start_core.machine.black_hole_generator.stability",
                Component.literal(stabilityBar(state.stability)).withStyle(color),
                Component.literal(Math.round(state.stability * 100) + "%").withStyle(color)));
        if (state.stability < WARNING_STABILITY) {
            textList.add(Component.translatable("start_core.machine.black_hole_generator.warning")
                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        }

        long produced = (long) state.output;
        textList.add(Component.translatable("start_core.machine.black_hole_generator.output",
                FormattingUtil.formatNumbers(produced),
                FormattingUtil.formatNumber2Places((float) state.output / V_UIV)).withStyle(ChatFormatting.GOLD));
        textList.add(Component.translatable("start_core.machine.black_hole_generator.delivered",
                FormattingUtil.formatNumbers(lastEmitted))
                .withStyle(lastEmitted < produced ? ChatFormatting.RED : ChatFormatting.GRAY));
        textList.add(Component.translatable("start_core.machine.black_hole_generator.demand",
                FormattingUtil.formatNumbers((long) state.demand)).withStyle(ChatFormatting.AQUA));

        for (int i = 0; i < STABILIZER_COUNT; i += 2) {
            textList.add(Component.translatable("start_core.machine.black_hole_generator.supply",
                    AXIS_NAMES[i], supplyText(supply[i]), AXIS_NAMES[i + 1], supplyText(supply[i + 1])));
        }
    }

    private void addStabilizerLines(List<Component> textList) {
        for (int i = 0; i < STABILIZER_COUNT; i++) {
            var stabilizer = stabilizers[i];
            if (stabilizer == null) {
                textList.add(Component.translatable("start_core.machine.black_hole_generator.stabilizer_missing",
                        AXIS_NAMES[i]).withStyle(ChatFormatting.RED));
                continue;
            }
            var container = stabilizer.getEnergyContainer();
            textList.add(Component.translatable("start_core.machine.black_hole_generator.stabilizer",
                    AXIS_NAMES[i],
                    FormattingUtil.formatNumbers(container.getEnergyStored()),
                    FormattingUtil.formatNumbers(container.getEnergyCapacity())).withStyle(ChatFormatting.GRAY));
        }
    }

    private static Component supplyText(double ratio) {
        var color = ratio >= 0.999 ? ChatFormatting.GREEN : ratio >= 0.5 ? ChatFormatting.YELLOW : ChatFormatting.RED;
        return Component.literal(Math.round(ratio * 100) + "%").withStyle(color);
    }

    private static String stabilityBar(double stability) {
        int filled = (int) Math.round(stability * 20);
        return "|".repeat(filled) + ".".repeat(20 - filled);
    }

    private static ChatFormatting phaseColor(BlackHolePhase phase) {
        return switch (phase) {
            case IDLE -> ChatFormatting.GRAY;
            case IGNITING -> ChatFormatting.GOLD;
            case STABLE -> ChatFormatting.GREEN;
            case EVAPORATING -> ChatFormatting.AQUA;
            case COLLAPSING -> ChatFormatting.RED;
        };
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
