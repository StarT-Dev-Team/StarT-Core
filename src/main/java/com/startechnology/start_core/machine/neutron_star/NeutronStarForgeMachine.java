package com.startechnology.start_core.machine.neutron_star;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.item.StarTItems;
import com.startechnology.start_core.machine.black_hole.BlackHoleSeeds;
import com.startechnology.start_core.machine.megastructure.StarColor;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.recipe.recipes.NeutronStarRecipes;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.BASE_RADIUS;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.CENTER_HEIGHT;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.CONTROLLER_Y;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.HALF_WIDTH;
import static com.startechnology.start_core.machine.neutron_star.NeutronStarGeometry.TOP;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class NeutronStarForgeMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            NeutronStarForgeMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private static final GTRecipeType[] RECIPE_TYPES = { StarTRecipeTypes.NEUTRON_STAR_FORGE_RECIPES };
    private static final int STEP_TICKS = 20;
    private static final int MAX_FUEL_RECIPES_PER_STEP = 4;
    private static final int DISPLAY_THRESHOLD = 8;

    @Persisted
    @DescSynced
    @Getter
    private int starColor = NeutronStarBalance.DEFAULT_COLOR;
    @Persisted
    @DescSynced
    @Getter
    private int targetSpin = NeutronStarBalance.DEFAULT_TARGET_SPIN;

    @DescSynced
    private int phase;
    @DescSynced
    @Getter
    private long phaseStartGameTime;
    @DescSynced
    @Getter
    private long glitchStartGameTime = Long.MIN_VALUE / 2;
    @DescSynced
    @Getter
    private int displaySpin;
    @DescSynced
    @Getter
    private int displayMass;
    @DescSynced
    @Getter
    private int displayWork;
    @DescSynced
    @Getter
    private int displayFeed;

    private final NeutronStarState state = new NeutronStarState();
    private final List<ItemStack> pendingOutputs = new ArrayList<>();
    private final List<ItemStack> captureRefund = new ArrayList<>();
    private long captureEUt;
    private long capturePaid;
    private double feed;
    private long lastGlitchWindow = -1;
    private TickableSubscription tickSubscription;

    public NeutronStarForgeMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    public NeutronStarPhase getPhase() {
        return NeutronStarPhase.byId(phase);
    }

    public double getSpin() {
        return state.spin;
    }

    public double getMass() {
        return state.mass;
    }

    @Override
    public GTRecipeType[] getRecipeTypes() {
        return RECIPE_TYPES;
    }

    @Override
    public GTRecipeType getRecipeType() {
        return StarTRecipeTypes.NEUTRON_STAR_FORGE_RECIPES;
    }

    private Direction relative(RelativeDirection direction) {
        return direction.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }

    public BlockPos getAxisBase() {
        return getPos().relative(relative(RelativeDirection.BACK), BASE_RADIUS)
                .relative(relative(RelativeDirection.DOWN), CONTROLLER_Y);
    }

    public Vec3 getCenter() {
        return Vec3.atBottomCenterOf(getAxisBase().above(CENTER_HEIGHT));
    }

    public void setStarColor(int color) {
        if (!StarColor.isValid(color) || color == starColor) return;
        starColor = color;
        markDirty();
    }

    public void setTargetSpin(int spin) {
        int clamped = Mth.clamp(spin, 0, (int) NeutronStarBalance.F_MAX);
        if (clamped == targetSpin) return;
        targetSpin = clamped;
        markDirty();
    }

    public static int minSpin(GTRecipe recipe) {
        for (var condition : recipe.conditions) {
            if (condition instanceof NeutronSpinCondition spinCondition) return spinCondition.getMinSpin();
        }
        return 0;
    }

    public boolean canProcess(int minSpin) {
        return getPhase() == NeutronStarPhase.ACTIVE && state.spin >= minSpin;
    }

    public static ModifierFunction recipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof NeutronStarForgeMachine forge)) {
            return RecipeModifier.nullWrongType(NeutronStarForgeMachine.class, machine);
        }
        double speed = NeutronStarBalance.processingSpeed(forge.state.spin, minSpin(recipe));
        return speed > 1 ? ModifierFunction.builder().durationMultiplier(1 / speed).build() :
                ModifierFunction.IDENTITY;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        return getPhase() == NeutronStarPhase.ACTIVE && super.beforeWorking(recipe);
    }

    @Override
    public boolean onWorking() {
        return getPhase() == NeutronStarPhase.ACTIVE && super.onWorking();
    }

    private double recipeDraw() {
        var recipe = getRecipeLogic().getLastRecipe();
        if (!getRecipeLogic().isWorking() || recipe == null) return 0;
        return recipe.data.getFloat(NeutronStarRecipes.RECIPE_DATA_SPIN_DRAW);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (isRemote()) return;
        phase = state.phase.ordinal();
        phaseStartGameTime = getLevel().getGameTime() - state.phaseTicks;
        updateDisplay(true);
        tickSubscription = subscribeServerTick(tickSubscription, this::tick);
    }

    @Override
    public void onUnload() {
        super.onUnload();
        unsubscribe(tickSubscription);
        tickSubscription = null;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (isStructureLoaded()) disrupt();
    }

    private boolean isStructureLoaded() {
        var base = getAxisBase();
        return getLevel().hasChunksAt(base.offset(-HALF_WIDTH, 0, -HALF_WIDTH),
                base.offset(HALF_WIDTH, TOP, HALF_WIDTH));
    }

    private void disrupt() {
        if (state.phase == NeutronStarPhase.CAPTURING) refundCapture();
        state.disrupt();
        syncPhase();
    }

    private void tick() {
        if (!isFormed()) {
            if (!isStructureLoaded()) return;
            disrupt();
            if (state.phase.isEnding()) {
                state.tick();
                syncPhase();
            }
            return;
        }
        if (getMultiblockState().hasError()) return;

        var before = state.phase;
        if (before == NeutronStarPhase.IDLE) {
            if (getOffsetTimer() % STEP_TICKS == 0) tryCapture();
        } else {
            if (before == NeutronStarPhase.CAPTURING) payCapture();
            state.tick();
            if (before == NeutronStarPhase.CAPTURING && state.phase == NeutronStarPhase.ACTIVE) {
                captureRefund.clear();
                capturePaid = 0;
            }
        }
        if (getOffsetTimer() % STEP_TICKS == 0) step();
        if (state.phase != before && state.phase == NeutronStarPhase.COLLAPSING) {
            pendingOutputs.add(StarTItems.SINGULARITY_SEEDS.get(BlackHoleSeeds.PULSAR_COLLAPSE.id()).asStack());
        }
        syncPhase();
    }

    private void step() {
        int speed = StarTConfig.INSTANCE.debug.neutronStarLifecycleSpeed;
        double dt = STEP_TICKS / 20.0;
        double draw = recipeDraw();
        feed = state.phase == NeutronStarPhase.ACTIVE && isWorkingEnabled() ? accrete(draw, dt, speed) / dt : 0;
        state.step(dt, draw, speed);
        rollGlitch(speed);
        pushPendingOutputs();
        updateDisplay(false);
        markDirty();
    }

    private double accrete(double draw, double dt, int speed) {
        double wanted = state.spinRequest(targetSpin, draw, dt, speed);
        double budget = NeutronStarBalance.ACCRETION_RATE * speed * dt;
        double used = 0;
        for (int i = 0; i < MAX_FUEL_RECIPES_PER_STEP && wanted > 0 && used < budget; i++) {
            var recipe = findRecipe(StarTRecipeTypes.NEUTRON_STAR_ACCRETION_RECIPES);
            if (recipe == null) break;
            int mb = fluidAmount(recipe);
            float massPerMb = recipe.data.getFloat(NeutronStarRecipes.RECIPE_DATA_MASS_PER_MB);
            float spinPerMb = recipe.data.getFloat(NeutronStarRecipes.RECIPE_DATA_SPIN_PER_MB);
            double spinPerRecipe = mb * state.spinPerMb(spinPerMb);
            if (mb <= 0 || spinPerRecipe <= 0) break;

            int limit = (int) Math.min(Math.ceil(wanted / spinPerRecipe), Math.floor((budget - used) / mb));
            if (limit <= 0) break;
            int parallel = ParallelLogic.getMaxByInput(this, recipe, limit, List.of());
            if (parallel <= 0) break;
            var scaled = parallel == 1 ? recipe : recipe.copy(ContentModifier.multiplier(parallel), false);
            if (!RecipeHelper.handleRecipeIO(this, scaled, IO.IN, getRecipeLogic().getChanceCaches()).isSuccess()) {
                break;
            }
            state.accrete(parallel * mb, massPerMb, spinPerMb);
            wanted -= parallel * spinPerRecipe;
            used += parallel * mb;
        }
        return used;
    }

    private static int fluidAmount(GTRecipe recipe) {
        int amount = 0;
        for (var content : recipe.getInputContents(FluidRecipeCapability.CAP)) {
            amount += FluidRecipeCapability.CAP.of(content.content).getAmount();
        }
        return amount;
    }

    private void rollGlitch(int speed) {
        if (state.phase != NeutronStarPhase.ACTIVE) return;
        long gameTime = getLevel().getGameTime();
        long window = gameTime / Math.max(STEP_TICKS, NeutronStarBalance.GLITCH_PERIOD / speed);
        if (window == lastGlitchWindow) return;
        lastGlitchWindow = window;
        if (state.glitch(NeutronStarBalance.glitchRoll(getPos().asLong(), window))) {
            glitchStartGameTime = gameTime;
            displaySpin = NeutronStarBalance.encodeSpin(state.spin);
        }
    }

    private void tryCapture() {
        if (!isWorkingEnabled() || energyContainer == null) return;
        var recipe = findRecipe(StarTRecipeTypes.NEUTRON_STAR_CAPTURE_RECIPES);
        if (recipe == null) return;
        var held = new ArrayList<ItemStack>();
        for (var content : recipe.getInputContents(ItemRecipeCapability.CAP)) {
            var items = ItemRecipeCapability.CAP.of(content.content).getItems();
            if (items.length > 0) held.add(items[0].copy());
        }
        if (!RecipeHelper.handleRecipeIO(this, recipe, IO.IN, getRecipeLogic().getChanceCaches()).isSuccess()) return;

        captureRefund.clear();
        captureRefund.addAll(held);
        captureEUt = recipe.getInputEUt().getTotalEU();
        capturePaid = 0;
        double mass = recipe.data.contains(NeutronStarRecipes.RECIPE_DATA_MASS) ?
                recipe.data.getFloat(NeutronStarRecipes.RECIPE_DATA_MASS) : NeutronStarBalance.M_INITIAL;
        double spin = recipe.data.contains(NeutronStarRecipes.RECIPE_DATA_SPIN) ?
                recipe.data.getFloat(NeutronStarRecipes.RECIPE_DATA_SPIN) : NeutronStarBalance.F_INITIAL;
        state.capture(mass, spin);
    }

    private void payCapture() {
        long due = captureEUt * (state.phaseTicks + 1);
        long owed = due - capturePaid;
        if (owed > 0 && isWorkingEnabled() && energyContainer != null) {
            capturePaid += energyContainer.removeEnergy(Math.min(owed, energyContainer.getEnergyStored()));
        }
        if (due - capturePaid > captureEUt * NeutronStarBalance.CAPTURE_GRACE_TICKS) {
            refundCapture();
            state.reset();
        }
    }

    private void refundCapture() {
        pendingOutputs.addAll(captureRefund);
        captureRefund.clear();
        capturePaid = 0;
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

    private void pushPendingOutputs() {
        if (pendingOutputs.isEmpty()) return;
        for (var handler : getCapabilitiesFlat(IO.OUT, ItemRecipeCapability.CAP)) {
            if (!(handler instanceof NotifiableItemStackHandler bus)) continue;
            var iterator = pendingOutputs.listIterator();
            while (iterator.hasNext()) {
                var remaining = iterator.next();
                for (int slot = 0; slot < bus.getSlots() && !remaining.isEmpty(); slot++) {
                    remaining = bus.insertItemInternal(slot, remaining, false);
                }
                if (remaining.isEmpty()) {
                    iterator.remove();
                } else {
                    iterator.set(remaining);
                }
            }
            if (pendingOutputs.isEmpty()) return;
        }
    }

    private void syncPhase() {
        if (phase == state.phase.ordinal()) return;
        phase = state.phase.ordinal();
        phaseStartGameTime = getLevel().getGameTime() - state.phaseTicks;
        updateDisplay(true);
        markDirty();
    }

    private void updateDisplay(boolean force) {
        int speed = StarTConfig.INSTANCE.debug.neutronStarLifecycleSpeed;
        displaySpin = NeutronStarBalance.encodeSpin(state.spin);
        displayMass = quantize(state.mass / NeutronStarBalance.M_TOV);
        double draw = recipeDraw();
        int work = draw > 0 || getRecipeLogic().isWorking() ?
                quantize(0.3 + 0.7 * draw / NeutronStarBalance.DRAW_REFERENCE) : 0;
        int flow = quantize(feed / (NeutronStarBalance.ACCRETION_RATE * speed));
        if (force || significant(displayWork, work)) displayWork = work;
        if (force || significant(displayFeed, flow)) displayFeed = flow;
    }

    private static boolean significant(int current, int next) {
        return Math.abs(next - current) >= DISPLAY_THRESHOLD || (next == 0) != (current == 0);
    }

    private static int quantize(double value) {
        return Mth.clamp((int) Math.round(value * 255), 0, 255);
    }

    @Override
    public void saveCustomPersistedData(CompoundTag tag, boolean forDrop) {
        super.saveCustomPersistedData(tag, forDrop);
        if (forDrop) return;
        var star = new CompoundTag();
        star.putInt("phase", state.phase.ordinal());
        star.putLong("phaseTicks", state.phaseTicks);
        star.putDouble("mass", state.mass);
        star.putDouble("spin", state.spin);
        star.putInt("glitchCount", state.glitchCount);
        star.putDouble("braking", state.braking);
        star.putDouble("draw", state.draw);
        star.putDouble("spinUp", state.spinUp);
        star.putDouble("massRate", state.massRate);
        star.putDouble("feed", feed);
        star.putLong("captureEUt", captureEUt);
        star.putLong("capturePaid", capturePaid);
        star.put("captureRefund", saveStacks(captureRefund));
        star.put("pendingOutputs", saveStacks(pendingOutputs));
        tag.put("neutronStar", star);
    }

    @Override
    public void loadCustomPersistedData(CompoundTag tag) {
        super.loadCustomPersistedData(tag);
        var star = tag.getCompound("neutronStar");
        state.phase = NeutronStarPhase.byId(star.getInt("phase"));
        state.phaseTicks = star.getLong("phaseTicks");
        state.mass = star.getDouble("mass");
        state.spin = star.getDouble("spin");
        state.glitchCount = star.getInt("glitchCount");
        state.braking = star.getDouble("braking");
        state.draw = star.getDouble("draw");
        state.spinUp = star.getDouble("spinUp");
        state.massRate = star.getDouble("massRate");
        feed = star.getDouble("feed");
        captureEUt = star.getLong("captureEUt");
        capturePaid = star.getLong("capturePaid");
        loadStacks(star.getList("captureRefund", Tag.TAG_COMPOUND), captureRefund);
        loadStacks(star.getList("pendingOutputs", Tag.TAG_COMPOUND), pendingOutputs);
    }

    private static ListTag saveStacks(List<ItemStack> stacks) {
        var list = new ListTag();
        for (var stack : stacks) list.add(stack.save(new CompoundTag()));
        return list;
    }

    private static void loadStacks(ListTag list, List<ItemStack> target) {
        target.clear();
        for (var entry : list) {
            var stack = ItemStack.of((CompoundTag) entry);
            if (!stack.isEmpty()) target.add(stack);
        }
    }

    @Override
    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {
        super.attachConfigurators(configuratorPanel);
        configuratorPanel.attachConfigurators(new NeutronStarConfigurator(this));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        var current = state.phase;
        if (!isFormed() && current == NeutronStarPhase.IDLE) return;

        textList.add(Component.translatable("start_core.machine.neutron_star_forge.phase", phaseName(current)));
        textList.add(Component.translatable("start_core.machine.neutron_star_forge.star_color",
                Component.literal(StarColor.format(starColor)).withStyle(style -> style.withColor(starColor))));
        switch (current) {
            case IDLE -> textList.add(Component.translatable("start_core.machine.neutron_star_forge.idle")
                    .withStyle(ChatFormatting.GRAY));
            case CAPTURING -> textList.add(Component.translatable("start_core.machine.neutron_star_forge.capturing",
                    Math.round(100.0 * state.phaseTicks / NeutronStarPhase.CAPTURING.duration),
                    FormattingUtil.formatNumbers(captureEUt)).withStyle(ChatFormatting.AQUA));
            case ACTIVE -> addActiveLines(textList);
            default -> {}
        }
        if (!pendingOutputs.isEmpty()) {
            textList.add(Component.translatable("start_core.machine.neutron_star_forge.pending",
                    pendingOutputs.size()).withStyle(ChatFormatting.YELLOW));
        }
    }

    private void addActiveLines(List<Component> textList) {
        int speed = StarTConfig.INSTANCE.debug.neutronStarLifecycleSpeed;
        int tier = NeutronStarBalance.spinTier(state.spin);
        textList.add(Component.translatable("start_core.machine.neutron_star_forge.spin",
                FormattingUtil.formatNumber2Places((float) state.spin), NeutronStarBalance.tierName(tier))
                .withStyle(ChatFormatting.GOLD));

        double net = state.spinUp - state.braking - state.draw;
        var trend = Math.abs(net) < 0.005 ?
                Component.translatable("start_core.machine.neutron_star_forge.steady") :
                Component.translatable(net > 0 ? "start_core.machine.neutron_star_forge.spinning_up" :
                        "start_core.machine.neutron_star_forge.spinning_down",
                        FormattingUtil.formatNumber2Places((float) Math.abs(net)));
        textList.add(trend.withStyle(net < -0.005 ? ChatFormatting.YELLOW : ChatFormatting.GREEN));
        textList.add(Component.translatable("start_core.machine.neutron_star_forge.losses",
                FormattingUtil.formatNumber2Places((float) state.braking),
                FormattingUtil.formatNumber2Places((float) state.draw)).withStyle(ChatFormatting.GRAY));

        double share = state.mass / NeutronStarBalance.M_TOV;
        textList.add(Component.translatable("start_core.machine.neutron_star_forge.mass",
                FormattingUtil.formatNumber2Places((float) state.mass), Math.round(share * 100))
                .withStyle(share >= NeutronStarBalance.COLLAPSE_WARNING ? ChatFormatting.RED : ChatFormatting.GRAY));
        if (share >= NeutronStarBalance.COLLAPSE_WARNING) {
            textList.add(Component.translatable("start_core.machine.neutron_star_forge.collapse_warning")
                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        }

        textList.add((targetSpin > 0 ?
                Component.translatable("start_core.machine.neutron_star_forge.accretion",
                        FormattingUtil.formatNumbers(Math.round(feed)), targetSpin) :
                Component.translatable("start_core.machine.neutron_star_forge.accretion.off"))
                .withStyle(ChatFormatting.AQUA));
        if (!isWorkingEnabled()) {
            textList.add(Component.translatable("start_core.machine.neutron_star_forge.paused")
                    .withStyle(ChatFormatting.YELLOW));
        }

        double death = NeutronStarBalance.timeToDeath(state.spin, state.spinUp, state.draw / speed, speed);
        if (Double.isFinite(death)) {
            textList.add(Component.translatable("start_core.machine.neutron_star_forge.time_to_death",
                    formatDuration(death)).withStyle(ChatFormatting.RED));
        }
        if (state.massRate > 0) {
            textList.add(Component.translatable("start_core.machine.neutron_star_forge.time_to_collapse",
                    formatDuration((NeutronStarBalance.M_TOV - state.mass) / state.massRate))
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
        if (state.glitchCount > 0) {
            textList.add(Component.translatable("start_core.machine.neutron_star_forge.glitches", state.glitchCount)
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    private static String formatDuration(double seconds) {
        long total = Math.max(0, Math.round(seconds));
        long hours = total / 3600;
        long minutes = total / 60 % 60;
        return hours > 0 ? hours + "h " + minutes + "m" : minutes + "m " + total % 60 + "s";
    }

    public static Component phaseName(NeutronStarPhase phase) {
        return Component.translatable("start_core.machine.neutron_star_forge.phase." + phase.name().toLowerCase())
                .withStyle(switch (phase) {
                    case IDLE -> ChatFormatting.GRAY;
                    case CAPTURING -> ChatFormatting.AQUA;
                    case ACTIVE -> ChatFormatting.GREEN;
                    default -> ChatFormatting.LIGHT_PURPLE;
                });
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
