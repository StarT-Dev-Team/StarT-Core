package com.startechnology.start_core.machine.dreeam;

import appeng.blockentity.qnb.QuantumBridgeBlockEntity;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.gui.widget.BlockableSlotWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.machine.multiblock.part.LaserHatchPartMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.mixin.LaserHatchPartMachineAccessor;
import lombok.Getter;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.startechnology.start_core.utils.StarTTooltipUtils.withHover;

public class StarTDreeamMachine extends WorkableMultiblockMachine implements
                                IFancyUIMachine, IDisplayUIMachine, IMachineLife {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            StarTDreeamMachine.class,
            WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    public enum Role {
        NONE,
        TRANSMITTER,
        RECEIVER,
        MIXED
    }

    private static final int TRANSFER_INTERVAL = 10;
    private static final long LOSS_DIVISOR = 100;

    @Persisted
    protected final CustomItemStackHandler singularitySlot;

    @Getter
    private Role role = Role.NONE;
    private EnergyContainerList hatches = new EnergyContainerList(new ArrayList<>());
    private long registeredKey = 0;
    protected TickableSubscription tryTickSub;

    public StarTDreeamMachine(IMachineBlockEntity holder) {
        super(holder);

        this.singularitySlot = new CustomItemStackHandler(1) {

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                return !isSlotLocked() && QuantumBridgeBlockEntity.isValidEntangledSingularity(stack);
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (isSlotLocked()) return ItemStack.EMPTY;

                return super.extractItem(slot, amount, simulate);
            }

            @Override
            public void onContentsChanged(int slot) {
                super.onContentsChanged(slot);

                refreshLink();
            }
        };
    }

    private boolean isSlotLocked() {
        return isFormed() && isWorkingEnabled() && !singularitySlot.getStackInSlot(0).isEmpty();
    }

    private long linkKey() {
        ItemStack stack = singularitySlot.getStackInSlot(0);

        if (!QuantumBridgeBlockEntity.isValidEntangledSingularity(stack)) return 0;

        return stack.getTag().getLong(QuantumBridgeBlockEntity.TAG_FREQUENCY);
    }

    protected void refreshLink() {
        if (getLevel() == null || getLevel().isClientSide) return;

        if (registeredKey != 0) {
            StarTDreeamLinkManager.unregister(registeredKey, this);

            registeredKey = 0;
        }

        long key = linkKey();

        if (key != 0 && (role == Role.TRANSMITTER || role == Role.RECEIVER)) {
            StarTDreeamLinkManager.register(key, this);

            registeredKey = key;
        }
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        List<IEnergyContainer> targets = new ArrayList<>();
        List<IEnergyContainer> sources = new ArrayList<>();

        for (IMultiPart part : getParts()) {
            if (part instanceof LaserHatchPartMachine laserHatch) {
                var container = ((LaserHatchPartMachineAccessor) laserHatch).start_core$getLaserContainer();

                if (container.getOutputVoltage() > 0) sources.add(container);
                else if (container.getInputVoltage() > 0) targets.add(container);
            }
        }

        if (!targets.isEmpty() && !sources.isEmpty()) {
            role = Role.MIXED;
            hatches = new EnergyContainerList(new ArrayList<>());
        } else if (!targets.isEmpty()) {
            role = Role.TRANSMITTER;
            hatches = new EnergyContainerList(targets);
        } else if (!sources.isEmpty()) {
            role = Role.RECEIVER;
            hatches = new EnergyContainerList(sources);
        } else {
            role = Role.NONE;
            hatches = new EnergyContainerList(new ArrayList<>());
        }

        refreshLink();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();

        role = Role.NONE;
        hatches = new EnergyContainerList(new ArrayList<>());

        refreshLink();
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (getLevel().isClientSide) return;

        tryTickSub = subscribeServerTick(tryTickSub, this::transferEnergyTick);
    }

    @Override
    public void onUnload() {
        super.onUnload();

        if (getLevel().isClientSide) return;

        if (tryTickSub != null) {
            tryTickSub.unsubscribe();
            tryTickSub = null;
        }

        if (registeredKey != 0) {
            StarTDreeamLinkManager.unregister(registeredKey, this);

            registeredKey = 0;
        }
    }

    protected void transferEnergyTick() {
        if (role != Role.TRANSMITTER || !isFormed() || !isWorkingEnabled()) return;
        if (getOffsetTimer() % TRANSFER_INTERVAL != 0) return;

        StarTDreeamMachine partner = StarTDreeamLinkManager.partnerOf(registeredKey, this);

        if (partner == null) return;

        long stored = hatches.getEnergyStored();

        if (stored <= 0) return;

        long offered = stored - stored / LOSS_DIVISOR;
        long accepted = partner.receiveEnergyTick(offered);

        if (accepted <= 0) return;

        hatches.removeEnergy(Math.min(stored, accepted + (accepted + 98) / 99));
    }

    protected long receiveEnergyTick(long received) {
        if (role != Role.RECEIVER || !isFormed() || !isWorkingEnabled()) return 0;

        return hatches.changeEnergy(received);
    }

    @Override
    public void onMachineRemoved() {
        if (isRemote()) return;

        ItemStack stack = singularitySlot.getStackInSlot(0);

        if (stack.isEmpty()) return;

        var pos = getPos();

        Containers.dropItemStack(getLevel(), pos.getX(), pos.getY(), pos.getZ(), stack.copy());

        singularitySlot.setStackInSlot(0, ItemStack.EMPTY);
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        IDisplayUIMachine.super.addDisplayText(textList);

        if (!isFormed()) return;

        textList.add(withHover(
                Component.translatable("start_core.machine.dreeam.role." + role.name().toLowerCase(Locale.ROOT)),
                "start_core.machine.dreeam.role.hover"));
        textList.add(withHover(
                Component.translatable(
                        "start_core.machine.dreeam.state." + getLinkState().name().toLowerCase(Locale.ROOT)),
                "start_core.machine.dreeam.state.hover"));

        if (role == Role.TRANSMITTER) {
            textList.add(withHover(
                    Component.translatable("start_core.machine.dream_link.output_per_sec",
                            Component.literal(FormattingUtil.formatNumbers(hatches.getOutputPerSec() / 20))
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED))),
                    "start_core.machine.dreeam.output_per_sec.hover"));
        } else if (role == Role.RECEIVER) {
            textList.add(withHover(
                    Component.translatable("start_core.machine.dream_link.input_per_sec",
                            Component.literal(FormattingUtil.formatNumbers(hatches.getInputPerSec() / 20))
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN))),
                    "start_core.machine.dreeam.input_per_sec.hover"));
        }

        if (role == Role.TRANSMITTER || role == Role.RECEIVER) {
            textList.add(withHover(
                    Component.translatable("start_core.machine.dreeam.buffer",
                            Component.literal(FormattingUtil.formatNumbers(hatches.getEnergyStored()))
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD))),
                    "start_core.machine.dreeam.buffer.hover"));
        }

        if (isSlotLocked()) {
            textList.add(withHover(Component.translatable("start_core.machine.dreeam.slot_locked"),
                    "start_core.machine.dreeam.slot_locked.hover"));
        }
    }

    public StarTDreeamLinkManager.LinkState getLinkState() {
        return registeredKey == 0 ? StarTDreeamLinkManager.LinkState.UNLINKED :
                StarTDreeamLinkManager.stateOf(registeredKey, this);
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);

        group.addWidget(
                new DraggableScrollableWidgetGroup(4, 4, 182, 117).setBackground(GuiTextures.DISPLAY)
                        .addWidget(new LabelWidget(4, 5, "DREEAM"))
                        .addWidget(new LabelWidget(4, 20, "§7Entangled Singularity"))
                        .addWidget(new BlockableSlotWidget(singularitySlot, 0, 4, 32, true, true)
                                .setIsBlocked(this::isSlotLocked)
                                .setBackgroundTexture(GuiTextures.SLOT)
                                .setHoverTooltips(Component.translatable("start_core.machine.dreeam.slot.hover")))
                        .addWidget(new ComponentPanelWidget(4, 54, this::addDisplayText)));

        group.setBackground(GuiTextures.BACKGROUND_INVERSE);

        return group;
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return true;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return new ModularUI(198, 208, this, entityPlayer).widget(new FancyMachineUIWidget(this, 198, 208));
    }

    @Override
    public List<IFancyUIProvider> getSubTabs() {
        return getParts().stream().filter(Objects::nonNull).map(IFancyUIProvider.class::cast).toList();
    }

    @Override
    public void attachTooltips(TooltipsPanel tooltipsPanel) {
        for (IMultiPart part : getParts()) {
            part.attachFancyTooltipsToController(this, tooltipsPanel);
        }
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
