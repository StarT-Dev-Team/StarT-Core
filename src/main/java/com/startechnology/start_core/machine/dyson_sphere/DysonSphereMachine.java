package com.startechnology.start_core.machine.dyson_sphere;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockDisplayText;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.StarTConfig;
import com.startechnology.start_core.item.StarTItems;
import com.startechnology.start_core.machine.black_hole.BlackHoleSeeds;
import com.startechnology.start_core.machine.megastructure.LaserOutput;
import com.startechnology.start_core.machine.megastructure.StarColor;
import com.startechnology.start_core.sound.StarTSounds;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.CENTER_HEIGHT;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.CONTROLLER_Y;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.PEDESTAL_RADIUS;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.PEDESTAL_TOP;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.PYLON_OFFSET;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DysonSphereMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            DysonSphereMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public static final int DEFAULT_COLOR = 0xFFD27F;

    private static final int STEP_TICKS = 20;
    private static final int IGNITION_SOUND_TICK = 96;
    private static final int SUPERNOVA_SOUND_TICK = 8;
    private static final int DISPLAY_MASS_REFRESH_TICKS = 40;
    private static final double DISPLAY_MASS_THRESHOLD = 0.005;

    @Persisted
    @DescSynced
    @Getter
    private int starColor = DEFAULT_COLOR;
    @Persisted
    @DescSynced
    @Getter
    private int targetMass = StellarBalance.DEFAULT_TARGET_MASS;

    @DescSynced
    private int phase;
    @DescSynced
    @Getter
    private long phaseStartGameTime;
    @Persisted
    @DescSynced
    private int assembly;
    @Persisted
    private long assemblyElapsedTicks;
    @DescSynced
    @Getter
    private long assemblyStartGameTime;

    @DescSynced
    @Getter
    private float displayMass;
    @DescSynced
    @Getter
    private int displayOutput;
    @DescSynced
    @Getter
    private int displaySupply;
    @DescSynced
    @Getter
    private int displayCore;

    private final StellarState state = new StellarState();
    private final List<ItemStack> pendingOutputs = new ArrayList<>();
    private final LaserOutput laserOutput = new LaserOutput();
    private EnergyContainerList energyInputs = new EnergyContainerList(List.of());
    private long lastEmitted;
    private long lastDisplayMassUpdate;
    private TickableSubscription tickSubscription;

    public DysonSphereMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    public StellarPhase getPhase() {
        return StellarPhase.byId(phase);
    }

    public RingAssembly getAssembly() {
        return RingAssembly.byId(assembly);
    }

    private Direction relative(RelativeDirection direction) {
        return direction.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }

    public BlockPos getCenter() {
        return getPos().relative(relative(RelativeDirection.BACK), PEDESTAL_RADIUS)
                .relative(relative(RelativeDirection.UP), PEDESTAL_TOP - CONTROLLER_Y + CENTER_HEIGHT);
    }

    private BlockPos getAnchorBase() {
        return getPos().relative(relative(RelativeDirection.BACK), PEDESTAL_RADIUS)
                .relative(relative(RelativeDirection.DOWN), CONTROLLER_Y);
    }

    public Vec3 getAnchorTip() {
        return Vec3.atCenterOf(getAnchorBase().above(PEDESTAL_TOP)).add(0, 0.5, 0);
    }

    public Vec3 getPylonTip(int index) {
        int x = index % 2 == 0 ? PYLON_OFFSET : -PYLON_OFFSET;
        int z = index < 2 ? PYLON_OFFSET : -PYLON_OFFSET;
        return Vec3.atCenterOf(getAnchorBase().offset(x, PEDESTAL_TOP, z)).add(0, 0.5, 0);
    }

    public void setStarColor(int color) {
        if (!StarColor.isValid(color) || color == starColor) return;
        starColor = color;
        markDirty();
    }

    public void setTargetMass(int mass) {
        if (!isTargetMassEditable()) return;
        int clamped = Mth.clamp(mass, StellarBalance.M_MIN, StellarBalance.M_MAX);
        if (clamped == targetMass) return;
        targetMass = clamped;
        markDirty();
    }

    public boolean isTargetMassEditable() {
        var current = getPhase();
        return current == StellarPhase.IDLE || current == StellarPhase.PROTOSTAR;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (isRemote()) return;
        long gameTime = getLevel().getGameTime();
        phase = state.phase.ordinal();
        phaseStartGameTime = gameTime - state.phaseTicks;
        assemblyStartGameTime = gameTime - assemblyElapsedTicks;
        displayMass = (float) state.mass;
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
        laserOutput.collect(getParts());
        var containers = new ArrayList<IEnergyContainer>();
        for (var handler : getCapabilitiesFlat(IO.IN, EURecipeCapability.CAP)) {
            if (handler instanceof IEnergyContainer container) containers.add(container);
        }
        energyInputs = new EnergyContainerList(containers);
        if (!getAssembly().isStanding()) setAssembly(RingAssembly.ASSEMBLING);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        laserOutput.clear();
        energyInputs = new EnergyContainerList(List.of());
        if (isStructureLoaded()) breakApart();
    }

    @Override
    public boolean isRecipeLogicAvailable() {
        return false;
    }

    private boolean isStructureLoaded() {
        var base = getAnchorBase();
        return getLevel().hasChunksAt(base.offset(-PEDESTAL_RADIUS, 0, -PEDESTAL_RADIUS),
                base.offset(PEDESTAL_RADIUS, PEDESTAL_TOP, PEDESTAL_RADIUS));
    }

    private void breakApart() {
        state.disperse();
        if (getAssembly().isStanding()) setAssembly(RingAssembly.DISASSEMBLING);
        syncPhase();
    }

    private void tick() {
        if (!isFormed()) {
            if (!isStructureLoaded()) return;
            breakApart();
            tickAssembly();
            if (state.phase.isEnding()) {
                state.tick(targetMass);
                syncPhase();
            }
            return;
        }
        if (getMultiblockState().hasError()) return;

        tickAssembly();
        boolean working = isWorkingEnabled();
        if (working) {
            long owed = state.energyRequest(targetMass);
            if (owed > 0) state.pay(energyInputs.removeEnergy(Math.min(owed, energyInputs.getEnergyStored())));
        }
        lastEmitted = state.phase.isBurning() ? laserOutput.emit((long) state.output) : 0;

        var before = state.phase;
        long ticksBefore = state.phaseTicks;
        if (working || before != StellarPhase.PROTOSTAR) state.tick(targetMass);
        if (getOffsetTimer() % STEP_TICKS == 0) {
            int speed = StarTConfig.INSTANCE.debug.dysonLifecycleSpeed;
            double request = state.fuelRequest(targetMass, speed);
            double consumed = working && request > 0 ? drainFuel(state.fuel(), (int) Math.ceil(request)) : 0;
            state.step(STEP_TICKS / 20.0, consumed, speed);
            pushPendingOutputs();
            updateDisplay();
            markDirty();
        }
        if (state.phase != before && state.phase == StellarPhase.SUPERNOVA) {
            switch (StellarBalance.remnant(state.mass)) {
                case NEUTRON_STAR -> pendingOutputs.add(StarTItems.NEUTRON_STAR_REMNANT.asStack());
                case SINGULARITY_SEED -> pendingOutputs.add(
                        StarTItems.SINGULARITY_SEEDS.get(BlackHoleSeeds.STELLAR_REMNANT.id()).asStack());
                case NONE -> {}
            }
        }
        phaseSounds(before, ticksBefore);
        syncPhase();
    }

    private void phaseSounds(StellarPhase before, long ticksBefore) {
        if (reached(StellarPhase.IGNITING, IGNITION_SOUND_TICK, before, ticksBefore)) {
            playSound(StarTSounds.DYSON_IGNITION.get());
        }
        if (reached(StellarPhase.SUPERNOVA, SUPERNOVA_SOUND_TICK, before, ticksBefore)) {
            playSound(StarTSounds.DYSON_SUPERNOVA.get());
        }
    }

    private boolean reached(StellarPhase phase, int tick, StellarPhase before, long ticksBefore) {
        if (state.phase != phase || state.phaseTicks < tick) return false;
        return before != phase || ticksBefore < tick;
    }

    private void playSound(SoundEvent sound) {
        if (isMuffled()) return;
        var center = Vec3.atCenterOf(getCenter());
        getLevel().playSound(null, center.x, center.y, center.z, sound, SoundSource.BLOCKS, 1, 1);
    }

    private void tickAssembly() {
        var current = getAssembly();
        if (current != RingAssembly.ASSEMBLING && current != RingAssembly.DISASSEMBLING) return;
        assemblyElapsedTicks++;
        boolean assembling = current == RingAssembly.ASSEMBLING;
        if (assemblyElapsedTicks >= (assembling ? StellarBalance.ASSEMBLY_TICKS : StellarBalance.DISASSEMBLY_TICKS)) {
            setAssembly(assembling ? RingAssembly.ASSEMBLED : RingAssembly.NONE);
        }
    }

    private void setAssembly(RingAssembly next) {
        if (next == RingAssembly.ASSEMBLING) playSound(StarTSounds.DYSON_ASSEMBLY.get());
        if (next == RingAssembly.DISASSEMBLING) playSound(StarTSounds.DYSON_DISASSEMBLY.get());
        assembly = next.ordinal();
        assemblyElapsedTicks = 0;
        assemblyStartGameTime = getLevel().getGameTime();
        markDirty();
    }

    private void syncPhase() {
        if (phase == state.phase.ordinal()) return;
        phase = state.phase.ordinal();
        phaseStartGameTime = getLevel().getGameTime() - state.phaseTicks;
        if (state.phase == StellarPhase.IDLE) {
            displayMass = 0;
            displayOutput = 0;
            displaySupply = 0;
            displayCore = 0;
        } else {
            displayMass = (float) state.mass;
            lastDisplayMassUpdate = getLevel().getGameTime();
        }
        markDirty();
    }

    private int drainFuel(StellarState.Fuel fuel, int amount) {
        var target = fuelFluid(fuel);
        if (target == null) return 0;
        int remaining = amount;
        for (var handler : getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP)) {
            if (!(handler instanceof NotifiableFluidTank tank)) continue;
            for (int i = 0; i < tank.getTanks() && remaining > 0; i++) {
                var stored = tank.getFluidInTank(i);
                if (stored.isEmpty() || !stored.getFluid().isSame(target)) continue;
                var drained = tank.drainInternal(new FluidStack(stored, Math.min(remaining, stored.getAmount())),
                        IFluidHandler.FluidAction.EXECUTE);
                remaining -= drained.getAmount();
            }
            if (remaining <= 0) break;
        }
        return amount - remaining;
    }

    @Nullable
    private static Fluid fuelFluid(StellarState.Fuel fuel) {
        return switch (fuel) {
            case HYDROGEN -> GTMaterials.Hydrogen.getFluid();
            case HELIUM_PLASMA -> GTMaterials.Helium.getFluid(FluidStorageKeys.PLASMA);
            case NONE -> null;
        };
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

    private void updateDisplay() {
        long gameTime = getLevel().getGameTime();
        if (Math.abs(state.mass - displayMass) > displayMass * DISPLAY_MASS_THRESHOLD ||
                gameTime - lastDisplayMassUpdate >= DISPLAY_MASS_REFRESH_TICKS) {
            displayMass = (float) state.mass;
            lastDisplayMassUpdate = gameTime;
        }
        double peak = StellarBalance.nominalOutput(state.mass) * StellarBalance.RG_BOOST;
        displayOutput = peak > 0 ? quantize(state.output / peak) : 0;
        displaySupply = quantize(state.phase.isBurning() ? state.supply : 0);
        displayCore = quantize(switch (state.phase) {
            case MAIN_SEQUENCE -> state.coreHydrogen;
            case RED_GIANT_TRANSITION, RED_GIANT -> state.coreHelium;
            default -> 0;
        });
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
        star.putDouble("coreHydrogen", state.coreHydrogen);
        star.putDouble("coreHelium", state.coreHelium);
        star.putDouble("lastSupply", state.lastSupply);
        star.putDouble("supply", state.supply);
        star.putInt("starvedTicks", state.starvedTicks);
        star.putLong("ignitionPaid", state.ignitionPaid);
        star.putDouble("output", state.output);
        var outputs = new ListTag();
        for (var stack : pendingOutputs) outputs.add(stack.save(new CompoundTag()));
        star.put("pendingOutputs", outputs);
        tag.put("star", star);
    }

    @Override
    public void loadCustomPersistedData(CompoundTag tag) {
        super.loadCustomPersistedData(tag);
        var star = tag.getCompound("star");
        state.phase = StellarPhase.byId(star.getInt("phase"));
        state.phaseTicks = star.getLong("phaseTicks");
        state.mass = star.getDouble("mass");
        state.coreHydrogen = star.getDouble("coreHydrogen");
        state.coreHelium = star.getDouble("coreHelium");
        state.lastSupply = star.getDouble("lastSupply");
        state.supply = star.getDouble("supply");
        state.starvedTicks = star.getInt("starvedTicks");
        state.ignitionPaid = star.getLong("ignitionPaid");
        state.output = star.getDouble("output");
        pendingOutputs.clear();
        for (var entry : star.getList("pendingOutputs", Tag.TAG_COMPOUND)) {
            var stack = ItemStack.of((CompoundTag) entry);
            if (!stack.isEmpty()) pendingOutputs.add(stack);
        }
    }

    public long getOutputEUt() {
        return state.phase.isBurning() ? (long) state.output : 0;
    }

    public double getMass() {
        return state.mass;
    }

    @Override
    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {
        super.attachConfigurators(configuratorPanel);
        configuratorPanel.attachConfigurators(new DysonStarConfigurator(this));
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        var current = state.phase;
        MultiblockDisplayText.builder(textList, isFormed())
                .setWorkingStatus(isWorkingEnabled(), current.isBurning())
                .addPatternErrorLine(getMultiblockState().error)
                .addWorkingStatusLine();
        if (!isFormed() && current == StellarPhase.IDLE) return;

        textList.add(Component.translatable("start_core.machine.dyson_sphere.phase", phaseName(current)));
        textList.add(Component.translatable("start_core.machine.dyson_sphere.star_color",
                Component.literal(StarColor.format(starColor)).withStyle(style -> style.withColor(starColor))));

        switch (current) {
            case IDLE -> textList.add(Component.translatable("start_core.machine.dyson_sphere.idle", targetMass)
                    .withStyle(ChatFormatting.GRAY));
            case PROTOSTAR, IGNITING -> addProtostarLines(textList);
            case MAIN_SEQUENCE, RED_GIANT_TRANSITION, RED_GIANT -> addBurningLines(textList);
            default -> {}
        }
        if (!pendingOutputs.isEmpty()) {
            textList.add(Component.translatable("start_core.machine.dyson_sphere.pending", pendingOutputs.size())
                    .withStyle(ChatFormatting.YELLOW));
        }
    }

    private void addProtostarLines(List<Component> textList) {
        textList.add(Component.translatable("start_core.machine.dyson_sphere.mass_target",
                FormattingUtil.formatNumber2Places((float) state.mass), targetMass).withStyle(ChatFormatting.GRAY));
        long cost = StellarBalance.ignitionCost(Math.max(state.mass, targetMass));
        textList.add(Component.translatable("start_core.machine.dyson_sphere.ignition",
                FormattingUtil.formatNumbers(Math.min(state.ignitionPaid, cost)), FormattingUtil.formatNumbers(cost))
                .withStyle(state.ignitionPaid >= cost ? ChatFormatting.GREEN : ChatFormatting.AQUA));
        addFateLine(textList);
    }

    private void addBurningLines(List<Component> textList) {
        textList.add(Component.translatable("start_core.machine.dyson_sphere.mass",
                FormattingUtil.formatNumber2Places((float) state.mass)).withStyle(ChatFormatting.GRAY));

        boolean mainSequence = state.phase == StellarPhase.MAIN_SEQUENCE;
        double core = mainSequence ? state.coreHydrogen : state.coreHelium;
        textList.add(Component.translatable(mainSequence ? "start_core.machine.dyson_sphere.core_hydrogen" :
                "start_core.machine.dyson_sphere.core_helium", Math.round(core * 100), remainingLife())
                .withStyle(ChatFormatting.GRAY));

        long produced = (long) state.output;
        textList.add(Component.translatable("start_core.machine.dyson_sphere.output",
                FormattingUtil.formatNumbers(produced),
                FormattingUtil.formatNumber2Places((float) state.output / StellarBalance.V_UXV))
                .withStyle(ChatFormatting.GOLD));
        textList.add(Component.translatable("start_core.machine.dyson_sphere.delivered",
                FormattingUtil.formatNumbers(lastEmitted))
                .withStyle(lastEmitted < produced ? ChatFormatting.RED : ChatFormatting.GRAY));

        double need = state.need();
        var fuel = Component.translatable("start_core.machine.dyson_sphere.fuel." +
                state.fuel().name().toLowerCase());
        textList.add(Component.translatable("start_core.machine.dyson_sphere.fuel",
                fuel, FormattingUtil.formatNumbers((long) (need * state.lastSupply)),
                FormattingUtil.formatNumbers((long) Math.ceil(need)))
                .withStyle(state.lastSupply >= 0.999 ? ChatFormatting.GREEN : ChatFormatting.YELLOW));
        if (state.starvedTicks > 0) {
            textList.add(Component.translatable("start_core.machine.dyson_sphere.starving",
                    formatDuration((StellarBalance.STARVE_LIMIT - state.starvedTicks) / 20.0))
                    .withStyle(ChatFormatting.RED, ChatFormatting.BOLD));
        }
        addFateLine(textList);
    }

    private void addFateLine(List<Component> textList) {
        double mass = state.phase.hasStar() && state.phase != StellarPhase.PROTOSTAR ? state.mass :
                Math.max(state.mass, targetMass);
        String fate = switch (StellarBalance.remnant(mass)) {
            case SINGULARITY_SEED -> "seed";
            case NEUTRON_STAR -> "neutron_star";
            case NONE -> "nebula";
        };
        textList.add(Component.translatable("start_core.machine.dyson_sphere.fate",
                Component.translatable("start_core.machine.dyson_sphere.fate." + fate))
                .withStyle(ChatFormatting.DARK_PURPLE));
    }

    private Component remainingLife() {
        if (state.phase == StellarPhase.RED_GIANT_TRANSITION) {
            return Component.literal(formatDuration(
                    (StellarBalance.RG_TRANSITION_TICKS - state.phaseTicks) / 20.0));
        }
        double burn = StellarBalance.luminosity(state.mass) * state.supply *
                StarTConfig.INSTANCE.debug.dysonLifecycleSpeed / state.mass;
        if (burn < 1e-9) return Component.translatable("start_core.machine.dyson_sphere.stalled");
        double seconds = state.phase == StellarPhase.MAIN_SEQUENCE ? state.coreHydrogen * StellarBalance.E_H / burn :
                state.coreHelium * StellarBalance.E_HE / (burn * StellarBalance.RG_BOOST);
        return Component.literal(formatDuration(seconds));
    }

    private static String formatDuration(double seconds) {
        long total = Math.max(0, Math.round(seconds));
        long hours = total / 3600;
        long minutes = total / 60 % 60;
        return hours > 0 ? hours + "h " + minutes + "m" : minutes + "m " + total % 60 + "s";
    }

    public static Component phaseName(StellarPhase phase) {
        return Component.translatable("start_core.machine.dyson_sphere.phase." + phase.name().toLowerCase())
                .withStyle(switch (phase) {
                    case IDLE -> ChatFormatting.GRAY;
                    case PROTOSTAR, IGNITING -> ChatFormatting.GOLD;
                    case MAIN_SEQUENCE -> ChatFormatting.GREEN;
                    case RED_GIANT_TRANSITION, RED_GIANT -> ChatFormatting.RED;
                    default -> ChatFormatting.LIGHT_PURPLE;
                });
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
