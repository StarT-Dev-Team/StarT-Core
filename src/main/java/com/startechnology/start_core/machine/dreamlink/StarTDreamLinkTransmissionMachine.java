package com.startechnology.start_core.machine.dreamlink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkMachine;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkRecieveEnergy;
import com.startechnology.start_core.api.capability.StarTNotifiableDreamLinkContainer;

import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import rx.Observable;

public class StarTDreamLinkTransmissionMachine extends WorkableMultiblockMachine implements IStarTDreamLinkNetworkMachine, IFancyUIMachine, IDisplayUIMachine {

    private EnergyContainerList inputHatches;
    protected ConditionalSubscriptionHandler tickSubscription;
    protected TickableSubscription tryTickSub;

    private Observable<Entry<IStarTDreamLinkNetworkRecieveEnergy, Geometry>> recieverCache;
    private boolean isReadyToTransmit;

    /* Store the network of the tower */
    @Persisted
    protected String network;

    private Integer range;
    private Integer recieverCount;
    private Boolean checkDimension;

    public StarTDreamLinkTransmissionMachine(IMachineBlockEntity holder, Integer range, Boolean checkDimension) {
        super(holder);
        this.tickSubscription = new ConditionalSubscriptionHandler(this, this::transferEnergyTick, this::isFormed);
        this.isReadyToTransmit = false;
        this.inputHatches = new EnergyContainerList(new ArrayList<>());
        this.network = IStarTDreamLinkNetworkMachine.DEFAULT_NETWORK;
        this.range = range;
        this.checkDimension = checkDimension;
        this.recieverCount = 0;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        List<IEnergyContainer> inputs = new ArrayList<>();
        Map<Long, IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap", Long2ObjectMaps::emptyMap);
        
        // Add update subscription to EUCap trait
        // TODO: Changed in gt 1.7
        for (IMultiPart part : getParts()) {
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.IN);
            if (io == IO.NONE) continue;

            for (var handler : part.getRecipeHandlers()) {
                var handlerIO = handler.getHandlerIO();
                // If IO not compatible
                if (io != IO.IN && handlerIO != IO.IN && io != handlerIO) continue;
                if (handler.getCapability() == EURecipeCapability.CAP &&
                        handler instanceof IEnergyContainer container) {
                    
                    inputs.add(container);
                    traitSubscriptions.add(handler.addChangedListener(tickSubscription::updateSubscription));
                }
            }
        }

        this.inputHatches = new EnergyContainerList(inputs);
        this.isReadyToTransmit = true;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (getLevel().isClientSide)
            return;

        tryTickSub = subscribeServerTick(tryTickSub, this::tryTransferEnergy);
    }

    @Override
    public void onUnload() {
        super.onUnload();
    
        if (getLevel().isClientSide)
            return;

        if (tryTickSub != null) {
            tryTickSub.unsubscribe();
            tryTickSub = null;

            tickSubscription.unsubscribe();
            tickSubscription = null;
        }
    }

    protected void tryTransferEnergy() {
        // Lower frequency every second try transfer.
        // This is so that if we have all our buffers full
        // we can still transfer out/attempt after etc.
        if (getOffsetTimer() % 60 == 0 && this.isReadyToTransmit) {
            updateTransferCache();
            transferEnergyTick();
        }
    }

    private void updateTransferCache() {
        if (getLevel().isClientSide)
            return;
       
        if (this.isReadyToTransmit && isWorkingEnabled()) {
            // Centre of this transmission machine
            BlockPos centre = getPos();

            int x = centre.getX();
            int z = centre.getZ();

            Observable<Entry<IStarTDreamLinkNetworkRecieveEnergy, Geometry>> machines;

            UUID thisUUID = this.getHolder().getOwner().getUUID();

            // Get dream-link hatches
            if (this.range != -1)
                machines = StarTDreamLinkManager.getDevices(x + range, z + range, x - range, z - range, thisUUID);
            else
                machines = StarTDreamLinkManager.getAllDevices(thisUUID);

            machines = machines.filter(machine -> {
                    return machine.value().canRecieve(this, this.checkDimension);
                }).sorted((machinea, machineb) -> {
                    IStarTDreamLinkNetworkRecieveEnergy parta = machinea.value();
                    IStarTDreamLinkNetworkRecieveEnergy partb = machineb.value();

                    // Manhatten distnace of each
                    BlockPos aPos = parta.devicePos();
                    BlockPos bPos = partb.devicePos();
                    
                    return this.getSquaredDistanceToThis(aPos).compareTo(this.getSquaredDistanceToThis(bPos));
                });
            
            recieverCount = machines.count().toBlocking().first();
            recieverCache = machines;
        }
    }

    private Double getSquaredDistanceToThis(BlockPos otherPos) {
        return otherPos.getCenter().distanceToSqr(this.getPos().getCenter());
    }

    protected void transferEnergyTick() {
        // Here we will transfer energy while we are formed.
        if (getLevel().isClientSide)
            return;

        if (this.isReadyToTransmit && isWorkingEnabled()) {
            if (recieverCache == null) {
                updateTransferCache();
            }

            recieverCache.forEach((entry) -> {
                IStarTDreamLinkNetworkRecieveEnergy device = entry.value();

                long energyStored = inputHatches.getEnergyStored();

                long hatchEnergyChange = device.recieveEnergy(energyStored);

                inputHatches.removeEnergy(hatchEnergyChange);
            });
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        IDisplayUIMachine.super.addDisplayText(textList);

        if (isFormed() && this.isReadyToTransmit) {
            if (this.inputHatches.getOutputPerSec() > 0) 
                textList.add(Component.translatable("start_core.machine.dream_link.active"));
            else
                textList.add(Component.translatable("start_core.machine.dream_link.not_active"));

            // Stats display
            addTowerStatsDisplay(textList); 
        }

        getDefinition().getAdditionalDisplay().accept(this, textList);
    }

    private void addTowerStatsDisplay(List<Component> textList) {
        /* Owner display */
        MutableComponent ownerComponent = Component.literal(this.getHolder().getOwner().getName());
        
        textList.add(Component
            .translatable("start_core.machine.dream_link.owner_title")
            .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Component.translatable("start_core.machine.dream_link.tower.owner_hover")))));

        textList.add(Component
            .translatable("start_core.machine.dream_link.owner", ownerComponent)
            .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Component.translatable("start_core.machine.dream_link.tower.owner_hover")))));

        MutableComponent inAmountComponent = Component.literal(FormattingUtil.formatNumbers(this.inputHatches.getInputPerSec() / 20))
            .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
        textList.add(Component
                .translatable("start_core.machine.dream_link.input_per_sec", inAmountComponent)
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.dream_link.tower.input_per_sec_hover")))));

        MutableComponent outAmountComponent = Component.literal(FormattingUtil.formatNumbers(this.inputHatches.getOutputPerSec() / 20))
            .setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
        textList.add(Component
                .translatable("start_core.machine.dream_link.output_per_sec", outAmountComponent)
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.dream_link.tower.output_per_sec_hover")))));

        MutableComponent totalBufferComponent = Component.literal(FormattingUtil.formatNumbers(this.inputHatches.getEnergyStored()))
            .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));

        textList.add(Component
            .translatable("start_core.machine.dream_link.total_buffer")
            .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Component.translatable("start_core.machine.dream_link.tower.total_buffer_hover")))));

        textList.add(
            Component.translatable("start_core.machine.dream_link.total_buffer_value", totalBufferComponent)
            .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Component.translatable("start_core.machine.dream_link.tower.total_buffer_hover")))));

        if (this.range != -1) {
            MutableComponent rangeComponent = Component.literal(FormattingUtil.formatNumbers(this.range))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD));
            textList.add(Component
                    .translatable("start_core.machine.dream_link.range", rangeComponent)
                    .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Component.translatable("start_core.machine.dream_link.tower.range_hover")))));
        } else {
            if (this.checkDimension) {
                textList.add(Component
                        .translatable("start_core.machine.dream_link.unlimited_range")
                        .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Component.translatable("start_core.machine.dream_link.tower.range_hover")))));
            } else {
                textList.add(Component
                .translatable("start_core.machine.dream_link.paragon_range")
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.dream_link.tower.range_hover")))));
            }
        }

        MutableComponent totalHatchesComponent = Component.literal(FormattingUtil.formatNumbers(this.recieverCount))
            .setStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE));

        textList.add(Component
            .translatable("start_core.machine.dream_link.total_hatches", totalHatchesComponent)
            .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Component.translatable("start_core.machine.dream_link.tower.total_hatches_hover")))));

    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(
            new DraggableScrollableWidgetGroup(4, 4, 182, 117).setBackground(GuiTextures.DISPLAY)
                .addWidget(new LabelWidget(4, 5, "Dream-link Transmission Node"))
                .addWidget(new LabelWidget(4, 20, "§7Dream-Network Identifier"))
                .addWidget(
                    new TextFieldWidget(4, 32, 182 - 8, 12, this::getNetwork, this::setNetwork)
                        .setMaxStringLength(64)
                        .setValidator(input -> {
                            if (input == null || input.isBlank()) return IStarTDreamLinkNetworkMachine.DEFAULT_NETWORK + "";
                            return input;
                        })
                        .setHoverTooltips(Component.translatable("start_core.machine.dream_link.network_set_hover"))
                ).addWidget(new ComponentPanelWidget(4, 52, this::addDisplayText))
        );
        
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
        return getParts().stream().filter(IFancyUIProvider.class::isInstance).map(IFancyUIProvider.class::cast)
                .toList();
    }

    @Override
    public void attachTooltips(TooltipsPanel tooltipsPanel) {
        for (IMultiPart part : getParts()) {
            part.attachFancyTooltipsToController(this, tooltipsPanel);
        }
    }

    @Override
    public void setNetwork(String network) {
        this.network = network;
    }

    @Override
    public String getNetwork() {
        return this.network;
    }
    
    @Override
    public final InteractionResult onDataStickShiftUse(Player player, ItemStack dataStick) {
        if (!isRemote()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("dream_network", this.getNetwork());
            dataStick.setTag(tag);
            dataStick.setHoverName(Component.translatable("start_core.machine.dream_link.data_stick.name", this.getNetwork()));
            player.sendSystemMessage(Component.translatable("start_core.machine.dream_link.copy_network"));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public final InteractionResult onDataStickUse(Player player, ItemStack dataStick) {
        CompoundTag tag = dataStick.getTag();
        if (tag == null || !tag.contains("dream_network")) {
            return InteractionResult.PASS;
        }

        if (!isRemote()) {
            String network = tag.getString("dream_network");
            this.setNetwork(network);
            player.sendSystemMessage(Component.translatable("start_core.machine.dream_link.set_network"));
        }
        return InteractionResult.sidedSuccess(isRemote());
    }

    @Override
    public boolean isDreaming() {
        return this.inputHatches.getOutputPerSec() > 0;
    }
}
