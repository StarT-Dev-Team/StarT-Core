package com.startechnology.start_core.machine.komaru;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.gregtechceu.gtceu.GTCEu;
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
import com.startechnology.start_core.machine.redstone.IStarTRedstoneIndicatorMachine;
import com.startechnology.start_core.machine.redstone.StarTRedstoneIndicatorRecord;
import com.startechnology.start_core.mixin.LaserHatchPartMachineAccessor;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class StarTKomaruFrameMachine extends WorkableElectricMultiblockMachine implements IStarTRedstoneIndicatorMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StarTKomaruFrameMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private static final ResourceLocation BASIC_MODULE_ID = GTCEu.id("basic_type_module");
    private static final ResourceLocation ADVANCED_MODULE_ID = GTCEu.id("advanced_type_module");
    private static final ResourceLocation FAEMATTER_TAG_ID = StarTCore.resourceLocation("komaru/faematter");
    private static final Pattern FILAMENT_TAG_PATTERN = Pattern.compile("^start_core:komaru/filaments/tier_(\\d+)$");

    private static final int CONSUMPTION_CYCLE_TICKS = 200;
    private static final double DEFAULT_PREVIOUS_C = 3.0;
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
    private boolean stabilizationPaid = false;

    public StarTKomaruFrameMachine(IMachineBlockEntity holder) {
        super(holder);
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

        return ModifierFunction.builder()
                .eutMultiplier(currentC / 6.0)
                .build();
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
            basicTerminal.setSupportedModules(List.of(BASIC_MODULE_ID));
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
            advancedTerminal.setSupportedModules(List.of(ADVANCED_MODULE_ID));
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

        if (getLevel().isClientSide)
            return;

        tryTickSub = subscribeServerTick(tryTickSub, this::tryTransferTerminalEnergy);
        consumptionTickSub = subscribeServerTick(consumptionTickSub, this::checkConsumption);
    }

    @Override
    public void onUnload() {
        super.onUnload();

        if (getLevel().isClientSide)
            return;

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
        if (getLevel().isClientSide || !this.readyToUpdate || !isWorkingEnabled() || inputLaserContainer == null
                || terminals == null) {
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
        textList.add(Component.translatable("ui.start_core.komaru.c",
                        Component.literal(String.format(Locale.ROOT, "%.2f", currentC)))
                .withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable("ui.start_core.komaru.fae_scaling",
                Component.literal(String.format(Locale.ROOT, "%.2f", currentFaeScaling))).withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable("ui.start_core.komaru.filaments",
                Component.literal(Integer.toString(lastFilamentCount)),
                Component.literal(Integer.toString(lastFilamentTier))).withStyle(ChatFormatting.GRAY));
        textList.add(Component.translatable(stabilizationPaid
                ? "ui.start_core.komaru.stabilization_paid"
                : "ui.start_core.komaru.stabilization_unpaid"));
    }

    private static class KomaruMath {
        public static final double PHI = (1 + Math.sqrt(5)) / 2;
        private static final double LOG_2 = Math.log(2.0);

        private static double moduleScaling(int filamentTier, int basicModules, int advancedModules) {
            return Math.pow(1.05 - 0.0075 * filamentTier, basicModules)
                    * Math.pow(1.10 - 0.015 * filamentTier, advancedModules);
        }

        private static double filamentStabilizing(int availableFilament) {
            return availableFilament <= 0
                    ? Double.POSITIVE_INFINITY
                    : 1.0 - (Math.log(PHI * availableFilament / 100.0) / LOG_2) / 10.0;
        }

        private static double faeScaling(int basicModules, int advancedModules, double requiredFaeMatter) {
            int modules = basicModules + advancedModules;
            return modules <= 0
                    ? 1.0
                    : Math.pow(1 + Math.sqrt(requiredFaeMatter / 3.0) / (10.0 * modules), modules);
        }

        private static double consumption(int filamentTier, int availableFilament, int basicModules,
                                          int advancedModules, double requiredFaeMatter) {
            return 3.0 * Math.max(1.0, Math.min(40.0,
                    moduleScaling(filamentTier, basicModules, advancedModules)
                            * filamentStabilizing(availableFilament)
                            * faeScaling(basicModules, advancedModules, requiredFaeMatter)));
        }
    }

    private void checkConsumption() {
        if (!checkOffset(CONSUMPTION_CYCLE_TICKS) || getLevel().isClientSide || !isFormed()) {
            return;
        }

        int basicModules = countLinked(basicTerminals);
        int advancedModules = countLinked(advancedTerminals);
        if (basicModules + advancedModules <= 0) {
            this.currentC = 0.0;
            this.currentFaeScaling = 1.0;
            this.lastFilamentCount = 0;
            this.lastFilamentTier = -1;
            this.stabilizationPaid = true;
            updateStabilizationIndicators();
            return;
        }

        FilamentStats availableFilaments = findFilaments(false);
        double nextFaeScaling = KomaruMath.faeScaling(basicModules, advancedModules, previousCycleC);
        double nextC = KomaruMath.consumption(availableFilaments.tierOffset(), availableFilaments.count(),
                basicModules, advancedModules, previousCycleC);
        int faematterRequired = (int) Math.ceil(nextC * 1000.0);

        this.currentC = nextC;
        this.currentFaeScaling = nextFaeScaling;
        this.lastFilamentCount = availableFilaments.count();
        this.lastFilamentTier = availableFilaments.highestTier();

        if (drainFaematter(faematterRequired, FluidAction.SIMULATE) < faematterRequired) {
            this.stabilizationPaid = false;
            updateStabilizationIndicators();
            this.getRecipeLogic().updateTickSubscription();
            return;
        }

        drainFaematter(faematterRequired, FluidAction.EXECUTE);
        FilamentStats consumedFilaments = findFilaments(true);
        this.lastFilamentCount = consumedFilaments.count();
        this.lastFilamentTier = consumedFilaments.highestTier();
        this.previousCycleC = nextC;
        this.stabilizationPaid = true;
        updateStabilizationIndicators();
        this.getRecipeLogic().updateTickSubscription();
    }

    private static int countLinked(List<StarTModularConduitAutoScalingHatchPartMachine> terminals) {
        if (terminals == null || terminals.isEmpty()) {
            return 0;
        }

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
        int count = 0;
        int highestTier = -1;

        for (NotifiableItemStackHandler handler : getInputItemHandlers()) {
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (stack.isEmpty()) {
                    continue;
                }

                int tier = getFilamentTier(stack);
                if (tier < 0) {
                    continue;
                }

                count += stack.getCount();
                highestTier = Math.max(highestTier, tier);
                if (consume) {
                    handler.extractItemInternal(slot, stack.getCount(), false);
                }
            }
        }

        return new FilamentStats(count, highestTier);
    }

    private List<NotifiableItemStackHandler> getInputItemHandlers() {
        return this.getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP)
                .stream()
                .filter(NotifiableItemStackHandler.class::isInstance)
                .map(NotifiableItemStackHandler.class::cast)
                .toList();
    }

    private static int getFilamentTier(ItemStack stack) {
        int[] highestTier = { -1 };
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

    private record FilamentStats(int count, int highestTier) {
        private int tierOffset() {
            return highestTier < 0 ? -1 : highestTier - 1;
        }
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
    public List<StarTRedstoneIndicatorRecord> getInitialIndicators() {
        List<StarTRedstoneIndicatorRecord> indicators = new ArrayList<>();

        for (int marker : C_REDSTONE_MARKERS) {
            indicators.add(new StarTRedstoneIndicatorRecord(
                    cIndicatorKey(marker),
                    Component.translatable("variadic.start_core.indicator.komaru.c", marker),
                    Component.translatable("variadic.start_core.description.komaru.c", marker),
                    redstoneRatio(currentC, marker),
                    marker));
        }

        for (int marker : FAE_SCALING_REDSTONE_MARKERS) {
            indicators.add(new StarTRedstoneIndicatorRecord(
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
}
