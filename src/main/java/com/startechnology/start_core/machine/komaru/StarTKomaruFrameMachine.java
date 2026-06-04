package com.startechnology.start_core.machine.komaru;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IWorkableMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableLaserContainer;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.machine.multiblock.part.LaserHatchPartMachine;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.modular.StarTModularConduitAutoScalingHatchPartMachine;
import com.startechnology.start_core.machine.redstone.IRedstoneIndicatorMachine;
import com.startechnology.start_core.machine.redstone.RedstoneIndicatorRecord;
import com.startechnology.start_core.mixin.LaserHatchPartMachineAccessor;
import lombok.Getter;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class StarTKomaruFrameMachine extends WorkableElectricMultiblockMachine implements IRedstoneIndicatorMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StarTKomaruFrameMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private static final List<ResourceLocation> BASIC_MODULE_ID = new ArrayList<>();
    private static final List<ResourceLocation> ADVANCED_MODULE_ID = new ArrayList<>();
    private static final ResourceLocation FAEMATTER_TAG_ID = StarTCore.resourceLocation("komaru/faematter");
    private static final Pattern FILAMENT_TAG_PATTERN = Pattern.compile("^start_core:komaru/filaments/tier_(\\d+)$");

    private static final int CONSUMPTION_CYCLE_TICKS = 200;
    private static final double DEFAULT_PREVIOUS_C = 10.0;
    private static final List<Integer> C_REDSTONE_MARKERS = List.of(10, 20, 30);
    private static final List<Integer> FAE_SCALING_REDSTONE_MARKERS = List.of(2, 5, 10);

    protected List<StarTModularConduitAutoScalingHatchPartMachine> basicTerminals = new ArrayList<>();
    protected List<StarTModularConduitAutoScalingHatchPartMachine> advancedTerminals = new ArrayList<>();

    /* Lists for easy transfer to terminals */
    protected EnergyContainerList terminals;
    protected List<NotifiableEnergyContainer> terminalContainers = new ArrayList<>();
    protected boolean readyToUpdate = false;

    private NotifiableLaserContainer inputLaserContainer = null;

    protected TickableSubscription tryTickSub;
    private TickableSubscription consumptionTickSub;

    @Persisted
    @DescSynced
    private double currentC = 0.0;

    @Persisted
    @DescSynced
    private double previousCycleC = DEFAULT_PREVIOUS_C;

    @Persisted
    @DescSynced
    private double currentFaeScaling = 1.0;

    @Persisted
    @DescSynced
    private int lastFilamentCount = 0;

    @Persisted
    @DescSynced
    private int lastFilamentTier = -1;

    @Persisted
    @DescSynced
    private String lastFilamentBreakdown = "-";

    @Persisted
    @DescSynced
    private int lastBasicModules = 0;

    @Persisted
    @DescSynced
    private int lastAdvancedModules = 0;

    @Persisted
    @DescSynced
    private int lastFaematterRequired = 0;

    @Persisted
    @DescSynced
    private int lastFaematterConsumed = 0;

    @Persisted
    @DescSynced
    private double lastModuleScaling = 1.0;

    @Persisted
    @DescSynced
    private double lastFilamentScaling = 1.0;

    @Persisted
    @DescSynced
    private boolean stabilizationPaid = false;

    // client only
    @Getter
    private int rendererAnimationType;
    @Getter
    private int rendererAnimationTicks;
    private int formedStatus = -1;

    public StarTKomaruFrameMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        if (formedStatus == -1) {
            // start opened or closed
            formedStatus = isFormed ? 1 : 0;
            rendererAnimationType = isFormed ? 1 : 2;
            rendererAnimationTicks = 5 * 20;
        } else {
            if (isFormed && formedStatus != 1) {
                // start open animation
                rendererAnimationType = 1;
                rendererAnimationTicks = -1;
                formedStatus = 1;
            } else if (!isFormed && formedStatus != 0) {
                // start closing animation
                rendererAnimationType = 2;
                rendererAnimationTicks = -1;
                formedStatus = 0;
            }
        }
        rendererAnimationTicks++;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        this.readyToUpdate = false;

        /* Gather the different terminals for each type */
        basicTerminals = this.getMultiblockState().getMatchContext()
                .getOrDefault(StarTKomaruPredicates.BASIC_STORAGE_KEY, new ArrayList<>());
        advancedTerminals = this.getMultiblockState().getMatchContext()
                .getOrDefault(StarTKomaruPredicates.ADVANCED_STORAGE_KEY, new ArrayList<>());

        /* Get the first laser hatch for input to KOMARU */
        inputLaserContainer = null;
        for (IMultiPart part : getParts()) {
            if (part instanceof LaserHatchPartMachine laserHatch) {
                inputLaserContainer = ((LaserHatchPartMachineAccessor) laserHatch).start_core$getLaserContainer();
                inputLaserContainer.addChangedListener(this::transferModuleInterfacesTick);
                break;
            }
        }

        terminalContainers = new ArrayList<>();
        this.setupTerminals();
        terminals = new EnergyContainerList(terminalContainers);
        this.readyToUpdate = true;
        updateStabilizationIndicators();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.readyToUpdate = false;
        this.inputLaserContainer = null;
        this.terminals = null;
        this.terminalContainers = new ArrayList<>();
        this.basicTerminals = new ArrayList<>();
        this.advancedTerminals = new ArrayList<>();
        this.currentC = 0.0;
        this.previousCycleC = DEFAULT_PREVIOUS_C;
        this.currentFaeScaling = 1.0;
        this.lastFilamentCount = 0;
        this.lastFilamentTier = -1;
        this.lastFilamentBreakdown = "-";
        this.lastBasicModules = 0;
        this.lastAdvancedModules = 0;
        this.lastFaematterRequired = 0;
        this.lastFaematterConsumed = 0;
        this.lastModuleScaling = 1.0;
        this.lastFilamentScaling = 1.0;
        this.stabilizationPaid = false;
        updateStabilizationIndicators();
    }

    private long getBasicAmperage() {
        long inputAmperage = this.inputLaserContainer == null ? 0 : this.inputLaserContainer.getInputAmperage();

        if (inputAmperage < 1024) {
            return 4;
        } else if (inputAmperage < 4096) {
            return 16;
        } else {
            return 64;
        }
    }

    private long getAdvancedAmperage() {
        long inputAmperage = this.inputLaserContainer == null ? 0 : this.inputLaserContainer.getInputAmperage();

        if (inputAmperage < 1024) {
            return 16;
        } else if (inputAmperage < 4096) {
            return 64;
        } else {
            return 256;
        }
    }

    private long getModuleVoltage() {
        return this.inputLaserContainer == null ? 0 : this.inputLaserContainer.getInputVoltage();
    }

    private ModifierFunction moduleRecipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (!stabilizationPaid || currentC <= 0) {
            return ModifierFunction.cancel(Component.translatable("start_core.machine.komaru.stabilization_unpaid"));
        }
        return ModifierFunction.builder().eutMultiplier(currentC / 11.0).build();
    }

    private boolean canModuleTick(IWorkableMultiController controller) {
        if (stabilizationPaid && currentC > 0) {
            return true;
        }

        controller.getRecipeLogic().setWaiting(
                Component.translatable("start_core.machine.komaru.stabilization_unpaid")
                        .withStyle(ChatFormatting.GRAY));
        return false;
    }

    private void afterBasicModuleWorking(IWorkableMultiController controller) {
        /* Reserved for future basic module side effects. */
    }

    private void afterAdvancedModuleWorking(IWorkableMultiController controller) {
        /* Reserved for future advanced module side effects. */
    }

    private void setupTerminals() {
        for (StarTModularConduitAutoScalingHatchPartMachine basicTerminal : basicTerminals) {
            basicTerminal.setSupportedModules(BASIC_MODULE_ID);
            basicTerminal.resetSupportedModule();
            tryScaleTerminal(basicTerminal, false);

            basicTerminal.setSupportedMachineConsumer(basicNode -> {
                if (basicNode instanceof StarTModularConduitAutoScalingHatchPartMachine autoScalingHatch) {
                    tryScaleTerminal(autoScalingHatch, false);
                    autoScalingHatch.setRecipeModifier(this::moduleRecipeModifier);
                    autoScalingHatch.setModuleAfterWorkConsumer(this::afterBasicModuleWorking);
                    autoScalingHatch.setModuleTickPredicate(this::canModuleTick);
                    markStabilizationUnpaid();
                }
            });
        }

        for (StarTModularConduitAutoScalingHatchPartMachine advancedTerminal : advancedTerminals) {
            advancedTerminal.setSupportedModules(ADVANCED_MODULE_ID);
            advancedTerminal.resetSupportedModule();
            tryScaleTerminal(advancedTerminal, true);

            advancedTerminal.setSupportedMachineConsumer(advancedNode -> {
                if (advancedNode instanceof StarTModularConduitAutoScalingHatchPartMachine autoScalingHatch) {
                    tryScaleTerminal(autoScalingHatch, true);
                    autoScalingHatch.setRecipeModifier(this::moduleRecipeModifier);
                    autoScalingHatch.setModuleAfterWorkConsumer(this::afterAdvancedModuleWorking);
                    autoScalingHatch.setModuleTickPredicate(this::canModuleTick);
                    markStabilizationUnpaid();
                }
            });
        }
    }

    private void tryScaleTerminal(StarTModularConduitAutoScalingHatchPartMachine terminal, boolean advanced) {
        if (this.inputLaserContainer == null) {
            return;
        }

        terminal.scaleNewEnergyContainer(getModuleVoltage(), advanced ? getAdvancedAmperage() : getBasicAmperage());
        if (terminal.isTerminal()) {
            terminalContainers.add(terminal.getEnergyContainer());
        }
    }

    private void markStabilizationUnpaid() {
        this.stabilizationPaid = false;
        updateStabilizationIndicators();
        this.getRecipeLogic().updateTickSubscription();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (isRemote()) return;

        tryTickSub = subscribeServerTick(tryTickSub, this::tryTransferTerminalEnergy);
        consumptionTickSub = subscribeServerTick(consumptionTickSub, this::checkConsumption);
    }

    @Override
    public void onUnload() {
        super.onUnload();

        if (isRemote()) return;

        if (tryTickSub != null) {
            tryTickSub.unsubscribe();
            tryTickSub = null;
        }

        if (consumptionTickSub != null) {
            consumptionTickSub.unsubscribe();
            consumptionTickSub = null;
        }
    }

    protected void tryTransferTerminalEnergy() {
        if (checkOffset(100)) {
            transferModuleInterfacesTick();
        }
    }

    protected void transferModuleInterfacesTick() {
        if (isRemote()) return;
        if (!readyToUpdate || !isWorkingEnabled() || inputLaserContainer == null || terminals == null) {
            return;
        }

        long energyStored = inputLaserContainer.getEnergyStored();
        if (energyStored <= 0) return;

        long totalEnergyTransferred = terminals.changeEnergy(energyStored);

        if (totalEnergyTransferred > 0) {
            inputLaserContainer.removeEnergy(totalEnergyTransferred);
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        if (!isFormed()) {
            return;
        }

        textList.add(Component.empty());
        textList.add(Component.translatable("ui.start_core.komaru.stabilization"));
        textList.add(Component.translatable(stabilizationPaid
                ? "ui.start_core.komaru.stabilization_paid"
                : "ui.start_core.komaru.stabilization_unpaid"));
        textList.add(Component.translatable("ui.start_core.komaru.modules",
                        Component.literal(Integer.toString(lastBasicModules)),
                        Component.literal(Integer.toString(lastAdvancedModules)))
                .withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable("ui.start_core.komaru.c",
                        formatted(currentC))
                .withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable("ui.start_core.komaru.previous_c",
                        formatted(previousCycleC))
                .withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable("ui.start_core.komaru.required_faematter",
                        Component.literal(Integer.toString(lastFaematterRequired)),
                        formatted(lastFaematterRequired / 1000.0))
                .withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable("ui.start_core.komaru.consumed_faematter",
                        Component.literal(Integer.toString(lastFaematterConsumed)),
                        formatted(lastFaematterConsumed / 1000.0))
                .withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable("ui.start_core.komaru.fae_scaling",
                formatted(currentFaeScaling)).withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable("ui.start_core.komaru.filaments",
                Component.literal(lastFilamentBreakdown),
                Component.literal(Integer.toString(lastFilamentCount)),
                displayTier(lastFilamentTier)).withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable("ui.start_core.komaru.scaling",
                        formatted(lastModuleScaling),
                        formatted(lastFilamentScaling),
                        formatted(currentFaeScaling))
                .withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable("ui.start_core.komaru.eu_multiplier",
                        formatted(currentC / 11.0))
                .withStyle(ChatFormatting.GRAY));
    }

    private static class KomaruMath {
        private static double moduleScaling(int basicModules, int advancedModules) {
            return Math.pow(0.99, basicModules) * Math.pow(0.98, advancedModules);
        }

        private static double filamentStabilizing(int filamentTier, int availableFilament) {
            if (availableFilament <= 0) {
                return 1.0;
            }

            var base = Math.max(0.0, 1.0 - 0.025 * filamentTier);
            return Math.pow(base, availableFilament * 0.2);
        }

        private static double filamentStabilizing(FilamentStats filaments) {
            var scaling = 1.0;

            for (var entry : filaments.countsByTier().entrySet()) {
                scaling *= filamentStabilizing(entry.getKey() - 1, entry.getValue());
            }

            return scaling;
        }

        private static double faeScaling(int basicModules, int advancedModules) {
            var weightedModules = basicModules + 3 * advancedModules;
            return Math.pow(1.025, weightedModules);
        }

        private static double consumption(FilamentStats filaments, int basicModules,
                                          int advancedModules, double requiredFaeMatter) {
            var consumption = requiredFaeMatter
                    * moduleScaling(basicModules, advancedModules)
                    * filamentStabilizing(filaments)
                    * faeScaling(basicModules, advancedModules);
            return Mth.clamp(consumption, 10.0, 50.0);
        }
    }

    private void checkConsumption() {
        if (isRemote() || !isFormed()) return;
        if (!checkOffset(CONSUMPTION_CYCLE_TICKS)) return;

        int basicModules = countLinked(basicTerminals);
        int advancedModules = countLinked(advancedTerminals);
        if (basicModules + advancedModules <= 0) {
            this.currentC = 0.0;
            this.currentFaeScaling = 1.0;
            this.lastFilamentCount = 0;
            this.lastFilamentTier = -1;
            this.lastFilamentBreakdown = "-";
            this.lastBasicModules = 0;
            this.lastAdvancedModules = 0;
            this.lastFaematterRequired = 0;
            this.lastFaematterConsumed = 0;
            this.lastModuleScaling = 1.0;
            this.lastFilamentScaling = 1.0;
            this.stabilizationPaid = true;
            updateStabilizationIndicators();
            return;
        }

        var availableFilaments = findFilaments(false);
        var nextModuleScaling = KomaruMath.moduleScaling(basicModules, advancedModules);
        var nextFilamentScaling = KomaruMath.filamentStabilizing(availableFilaments);
        var nextFaeScaling = KomaruMath.faeScaling(basicModules, advancedModules);
        var nextC = KomaruMath.consumption(availableFilaments, basicModules, advancedModules, previousCycleC);
        var faematterRequired = (int) Math.ceil(nextC * 1000.0);

        this.currentC = nextC;
        this.currentFaeScaling = nextFaeScaling;
        this.lastBasicModules = basicModules;
        this.lastAdvancedModules = advancedModules;
        this.lastFaematterRequired = faematterRequired;
        this.lastFaematterConsumed = 0;
        this.lastModuleScaling = nextModuleScaling;
        this.lastFilamentScaling = nextFilamentScaling;
        this.lastFilamentCount = availableFilaments.count();
        this.lastFilamentTier = availableFilaments.highestTier();
        this.lastFilamentBreakdown = availableFilaments.formatBreakdown();

        if (drainFaematter(faematterRequired, FluidAction.SIMULATE) < faematterRequired) {
            this.stabilizationPaid = false;
            updateStabilizationIndicators();
            this.getRecipeLogic().updateTickSubscription();
            return;
        }

        this.lastFaematterConsumed = drainFaematter(faematterRequired, FluidAction.EXECUTE);
        var consumedFilaments = findFilaments(true);
        this.lastFilamentCount = consumedFilaments.count();
        this.lastFilamentTier = consumedFilaments.highestTier();
        this.lastFilamentBreakdown = consumedFilaments.formatBreakdown();
        this.previousCycleC = nextC;
        this.stabilizationPaid = true;
        updateStabilizationIndicators();
        this.getRecipeLogic().updateTickSubscription();
    }

    private static int countLinked(List<StarTModularConduitAutoScalingHatchPartMachine> terminals) {
        if (terminals.isEmpty()) return 0;

        int count = 0;
        for (StarTModularConduitAutoScalingHatchPartMachine terminal : terminals) {
            if (terminal != null && terminal.isCurrentlyLinked()) {
                count++;
            }
        }
        return count;
    }

    private int drainFaematter(int amount, FluidAction action) {
        int remaining = amount;

        for (NotifiableFluidTank tank : getInputFluidTanks()) {
            for (int i = 0; i < tank.getTanks() && remaining > 0; i++) {
                FluidStack fluidInTank = tank.getFluidInTank(i);
                if (fluidInTank.isEmpty() || !isFaematter(fluidInTank)) {
                    continue;
                }

                FluidStack request = fluidInTank.copy();
                request.setAmount(Math.min(remaining, fluidInTank.getAmount()));
                FluidStack drained = tank.drainInternal(request, action);
                remaining -= drained.getAmount();
            }

            if (remaining <= 0) {
                break;
            }
        }

        return amount - remaining;
    }

    private List<NotifiableFluidTank> getInputFluidTanks() {
        return this.getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP)
                .stream()
                .filter(NotifiableFluidTank.class::isInstance)
                .map(NotifiableFluidTank.class::cast)
                .toList();
    }

    private boolean isFaematter(FluidStack stack) {
        return !stack.isEmpty() && stack.getFluid().defaultFluidState().getTags()
                .anyMatch(tag -> tag.location().equals(FAEMATTER_TAG_ID));
    }

    private FilamentStats findFilaments(boolean consume) {
        var count = 0;
        var highestTier = -1;
        var countsByTier = new TreeMap<Integer, Integer>();

        for (var handler : getInputItemHandlers()) {
            for (var slot = 0; slot < handler.getSlots(); slot++) {
                var stack = handler.getStackInSlot(slot);
                if (stack.isEmpty()) {
                    continue;
                }

                var tier = getFilamentTier(stack);
                if (tier < 0) {
                    continue;
                }

                var stackCount = stack.getCount();
                if (consume) {
                    stackCount = handler.extractItemInternal(slot, stackCount, false).getCount();
                }

                if (stackCount <= 0) {
                    continue;
                }

                count += stackCount;
                highestTier = Math.max(highestTier, tier);
                countsByTier.merge(tier, stackCount, Integer::sum);
            }
        }

        return new FilamentStats(count, highestTier, countsByTier);
    }

    private List<NotifiableItemStackHandler> getInputItemHandlers() {
        return this.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP)
                .stream()
                .filter(NotifiableItemStackHandler.class::isInstance)
                .map(NotifiableItemStackHandler.class::cast)
                .toList();
    }

    private static int getFilamentTier(ItemStack stack) {
        int[] highestTier = {-1};
        stack.getTags().forEach(tag -> {
            Matcher matcher = FILAMENT_TAG_PATTERN.matcher(tag.location().toString());
            if (matcher.matches()) {
                try {
                    highestTier[0] = Math.max(highestTier[0], Integer.parseInt(matcher.group(1)));
                } catch (NumberFormatException ignored) {
                    /* Ignore malformed tier tags. */
                }
            }
        });
        return highestTier[0];
    }

    private record FilamentStats(int count, int highestTier, Map<Integer, Integer> countsByTier) {
        private FilamentStats {
            countsByTier = new TreeMap<>(countsByTier);
        }

        private String formatBreakdown() {
            if (count <= 0 || countsByTier.isEmpty()) {
                return "-";
            }

            var parts = new ArrayList<String>();
            for (var entry : countsByTier.entrySet()) {
                parts.add("T" + entry.getKey() + " x" + entry.getValue());
            }
            return String.join(", ", parts);
        }
    }

    private static Component formatted(double value) {
        return Component.literal(String.format(Locale.ROOT, "%.2f", value));
    }

    private static Component displayTier(int tier) {
        return Component.literal(tier < 0 ? "-" : Integer.toString(tier));
    }

    private void updateStabilizationIndicators() {
        for (int marker : C_REDSTONE_MARKERS) {
            this.setIndicatorValue(cIndicatorKey(marker), redstoneRatio(currentC, marker));
        }

        for (int marker : FAE_SCALING_REDSTONE_MARKERS) {
            this.setIndicatorValue(faeScalingIndicatorKey(marker), redstoneRatio(currentFaeScaling, marker));
        }
    }

    private static int redstoneRatio(double value, double marker) {
        if (marker <= 0 || Double.isNaN(value) || value <= 0) {
            return 0;
        }
        return (int) Math.floor(Math.max(0.0, Math.min((value / marker) * 15.0, 15.0)));
    }

    private static String cIndicatorKey(int marker) {
        return "variadic.start_core.indicator.komaru.c." + marker;
    }

    private static String faeScalingIndicatorKey(int marker) {
        return "variadic.start_core.indicator.komaru.fae_scaling." + marker;
    }

    @Override
    public List<RedstoneIndicatorRecord> getInitialIndicators() {
        var indicators = new ArrayList<RedstoneIndicatorRecord>();

        for (int marker : C_REDSTONE_MARKERS) {
            indicators.add(new RedstoneIndicatorRecord(
                    cIndicatorKey(marker),
                    Component.translatable("variadic.start_core.indicator.komaru.c", marker),
                    Component.translatable("variadic.start_core.description.komaru.c", marker),
                    redstoneRatio(currentC, marker),
                    marker));
        }

        for (int marker : FAE_SCALING_REDSTONE_MARKERS) {
            indicators.add(new RedstoneIndicatorRecord(
                    faeScalingIndicatorKey(marker),
                    Component.translatable("variadic.start_core.indicator.komaru.fae_scaling", marker),
                    Component.translatable("variadic.start_core.description.komaru.fae_scaling", marker),
                    redstoneRatio(currentFaeScaling, marker),
                    100 + marker));
        }

        return indicators;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    private boolean checkOffset(int ticks) {
        return getOffsetTimer() % ticks == 0;
    }

    public static void addModule(String resourceLocation, String moduleType) {
        ResourceLocation parsedResourceLocation = ResourceLocation.tryParse(resourceLocation);

        if (parsedResourceLocation != null) {
            if (moduleType.equalsIgnoreCase("basic")) {
                BASIC_MODULE_ID.add(parsedResourceLocation);
            } else if (moduleType.equalsIgnoreCase("advanced")) {
                ADVANCED_MODULE_ID.add(parsedResourceLocation);
            }
        }
    }
}
