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
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkMachine;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkRecieveEnergy;
import com.startechnology.start_core.api.capability.IStarTGetMachineUUIDSafe;
import com.startechnology.start_core.api.capability.StarTNotifiableDreamLinkContainer;

import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import lombok.Getter;
import lombok.Setter;
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

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTDreamLinkTransmissionMachine.class,
        WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);

    private EnergyContainerList inputHatches;
    protected ConditionalSubscriptionHandler tickSubscription;
    protected TickableSubscription tryTickSub;

    private ArrayList<IStarTDreamLinkNetworkRecieveEnergy> receiverCache = new ArrayList<>();
    private boolean isReadyToTransmit;

    @Persisted
    protected String network;

    @Getter @Setter
    protected String tempNetwork;

    private Integer range;
    private Integer connections;
    private Integer receiverCount;
    private Boolean checkDimension;

    public StarTDreamLinkTransmissionMachine(IMachineBlockEntity holder, Integer range, Integer connections, Boolean checkDimension) {
        super(holder);
        this.tickSubscription = new ConditionalSubscriptionHandler(this, this::transferEnergyTick, this::isFormed);
        this.isReadyToTransmit = false;
        this.inputHatches = new EnergyContainerList(new ArrayList<>());
        this.network = IStarTDreamLinkNetworkMachine.DEFAULT_NETWORK;
        this.range = range;
        this.checkDimension = checkDimension;
        this.receiverCount = 0;
        this.connections = connections;
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
    public void onStructureInvalid() {
        super.onStructureInvalid();

        // Toggle off render on structure invalid if it exists
        StarTDreamLinkRangeRenderer.toggleOffBoxAtPositionWithRange(this.getPos(), this.range);
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

        // Clear cache on unload
        receiverCache.clear();
    }

    protected void tryTransferEnergy() {
        // Transfer energy tick only every 3 seconds, should keep up fine just averaged over 3 seconds instead of every tick/second
        // and help save TPS a bit due to GTM update handlers on recipe logic
        // from hatches recieving power very often?
        if (getOffsetTimer() % 60 == 0 && this.isReadyToTransmit) {
            updateTransferCache();
            transferEnergyTick();
        }
    }

    private void updateTransferCache() {
        if (getLevel().isClientSide || !this.isReadyToTransmit || !isWorkingEnabled())
            return;
       
        BlockPos centre = getPos();
        int x = centre.getX();
        int z = centre.getZ();
        UUID thisUUID = IStarTGetMachineUUIDSafe.getUUIDSafeMetaMachine(this);

        Observable<Entry<IStarTDreamLinkNetworkRecieveEnergy, Geometry>> machines;

        // Get dream-link hatches
        if (this.range != -1) {
            machines = StarTDreamLinkManager.getDevices(x + range, z + range, x - range, z - range, thisUUID);
        } else {
            machines = StarTDreamLinkManager.getAllDevices(thisUUID);
        }

        // Convert Observable to List once and cache it
        List<Entry<IStarTDreamLinkNetworkRecieveEnergy, Geometry>> deviceEntries = machines
            .filter(machine -> machine.value().canRecieve(this, this.checkDimension))
            .toList()
            .toBlocking()
            .single();

        // Sort by distance (squared distance for performance)
        deviceEntries.sort((entryA, entryB) -> {
            BlockPos posA = entryA.value().devicePos();
            BlockPos posB = entryB.value().devicePos();
            return Double.compare(getSquaredDistanceToThis(posA), getSquaredDistanceToThis(posB));
        });

        // Limit the number of connections if not infinite (-1)
        if (this.connections != -1 && deviceEntries.size() > this.connections) {
            deviceEntries = deviceEntries.subList(0, this.connections);
        }

        // Extract just the devices we need
        receiverCache.clear();
        receiverCache.ensureCapacity(deviceEntries.size()); // Pre-allocate capacity
        for (Entry<IStarTDreamLinkNetworkRecieveEnergy, Geometry> entry : deviceEntries) {
            receiverCache.add(entry.value());
        }

        receiverCount = receiverCache.size();
    }

    private Double getSquaredDistanceToThis(BlockPos otherPos) {
        Vec3 thisCenter = this.getPos().getCenter();
        Vec3 otherCenter = otherPos.getCenter();
        return thisCenter.distanceToSqr(otherCenter);
    }

    protected void transferEnergyTick() {
        if (getLevel().isClientSide || !this.isReadyToTransmit || !isWorkingEnabled())
            return;

        final int receiverCount = receiverCache.size();
        if (receiverCount == 0) return;

        long energyStored = inputHatches.getEnergyStored();
        if (energyStored <= 0) return;

        //  Batch energy removal to reduce method calls over iteration 
        long totalEnergyTransferred = 0;
        
        // Cache the array reference for fastest possible access
        final Object[] receivers = receiverCache.toArray();
        
        // Use array access with type casting
        for (int i = 0; i < receiverCount && energyStored > 0; i++) {
            IStarTDreamLinkNetworkRecieveEnergy device = (IStarTDreamLinkNetworkRecieveEnergy) receivers[i];
            
            long energyToTransfer = device.recieveEnergy(energyStored);
            if (energyToTransfer > 0) {
                totalEnergyTransferred += energyToTransfer;
                energyStored -= energyToTransfer;
            }
        }
        
        // Single batch energy removal at the end
        if (totalEnergyTransferred > 0) {
            inputHatches.removeEnergy(totalEnergyTransferred);
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

            addTowerStatsDisplay(textList); 
        }

        getDefinition().getAdditionalDisplay().accept(this, textList);
    }

    private void addTowerStatsDisplay(List<Component> textList) {
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

            /* Button for showing the range of the dream-link, no need on unlimited range stuff. */
            textList.add(ComponentPanelWidget.withButton(Component.translatable("start_core.machine.dream_link.tower.range_show").withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Component.translatable("start_core.machine.dream_link.tower.range_button_hover")))
            ), "range"));
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

        MutableComponent currentConnections = Component.literal(FormattingUtil.formatNumbers(this.receiverCount))
            .setStyle(Style.EMPTY.withColor(ChatFormatting.LIGHT_PURPLE));

        MutableComponent maxConnections = Component.literal(
                this.connections == -1 ? "∞" : FormattingUtil.formatNumbers(this.connections))
            .setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA));

        MutableComponent connectionsDisplay = Component.literal("")
            .append(currentConnections)
            .append(Component.literal(" / ").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)))
            .append(maxConnections);

        textList.add(Component
            .translatable("start_core.machine.dream_link.connections_display", connectionsDisplay)
            .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Component.translatable("start_core.machine.dream_link.tower.connections_display_hover")))));

    }

    /* Triggered by clicking on anything that is a button in the component panel. */
    public void onDreamLinkComponentPanelClicked(String componentData, ClickData clickData) {
        if (clickData.isRemote) {
            if (Objects.equals(componentData, "range")) {
                StarTDreamLinkRangeRenderer.toggleBoxAtPositionWithRange(this.getPos(), this.range);
            }
        }
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(
            new DraggableScrollableWidgetGroup(4, 4, 182, 117).setBackground(GuiTextures.DISPLAY)
                .addWidget(new LabelWidget(4, 5, "Dream-link Transmission Node"))
                .addWidget(new LabelWidget(4, 20, "§7Dream-Network Identifier"))
                .addWidget(
                    new TextFieldWidget(4, 32, 182 - 8, 12, this::getTempNetwork, this::setTempNetwork)
                        .setMaxStringLength(64)
                        .setValidator(input -> {
                            if (input == null) return "";
                            return input;
                        })
                        .setHoverTooltips(Component.translatable("start_core.machine.dream_link.network_set_hover"))
                )
                .addWidget(new ComponentPanelWidget(4, 50, this::addDisplayText)
                    .clickHandler(this::onDreamLinkComponentPanelClicked)
                )
        );

        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return true;
    }

    private void closeUI() {
        this.network = this.tempNetwork;
        if (this.tempNetwork.isBlank()) this.network = IStarTDreamLinkNetworkMachine.DEFAULT_NETWORK;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        this.tempNetwork = network;
        ModularUI ui = new ModularUI(198, 208, this, entityPlayer).widget(new FancyMachineUIWidget(this, 198, 208));
        ui.registerCloseListener(this::closeUI);
        return ui;
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
    public final InteractionResult onDreamCopyShiftUse(Player player, ItemStack copyItem) {
        if (!isRemote()) {
            CompoundTag tag = new CompoundTag();
            tag.putString("dream_network", this.getNetwork());
            copyItem.setTag(tag);
            copyItem.setHoverName(Component.translatable("start_core.machine.dream_link.lucinducer.name", this.getNetwork()));
            player.sendSystemMessage(Component.translatable("start_core.machine.dream_link.copy_network"));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public final InteractionResult onDreamCopyUse(Player player, ItemStack copyItem) {
        CompoundTag tag = copyItem.getTag();
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

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}