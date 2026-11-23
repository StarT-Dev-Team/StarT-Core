package com.startechnology.start_core.machine.dreamlink;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.UITemplate;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.gui.util.UICloseHandler;
import com.gregtechceu.gtceu.api.gui.widget.ComponentPanel;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ScrollerView;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField;
import com.lowdragmc.lowdraglib2.gui.util.ClickData;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkMachine;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkReceiveEnergy;
import com.startechnology.start_core.api.capability.StarTGetMachineUUIDSafe;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StarTDreamLinkTransmissionMachine extends WorkableMultiblockMachine implements
                                               IStarTDreamLinkNetworkMachine, IFancyUIMachine, IDisplayUIMachine {

    private static final String RANGE_TOGGLE_MESSAGE = "start_core:dream_link_range_toggle";

    private EnergyContainerList inputHatches;
    protected ConditionalSubscriptionHandler tickSubscription;
    protected TickableSubscription tryTickSub;

    private final ArrayList<IStarTDreamLinkNetworkReceiveEnergy> receiverCache = new ArrayList<>();
    private boolean isReadyToTransmit;

    @Persisted
    protected String network;

    @Getter
    @Setter
    protected String tempNetwork;

    private final int range;
    private final int connections;
    private int receiverCount;
    private final boolean checkDimension;

    public StarTDreamLinkTransmissionMachine(IMachineBlockEntity holder, int range, int connections,
                                             boolean checkDimension) {
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

        Long2ObjectMap<IO> ioMap = getMultiblockState().getMatchContext().getOrCreate("ioMap",
                Long2ObjectMaps::emptyMap);

        for (IMultiPart part : getParts()) {
            IO io = ioMap.getOrDefault(part.self().getPos().asLong(), IO.BOTH);
            if (io == IO.NONE) continue;

            var handlerLists = part.getRecipeHandlers();
            for (var handlerList : handlerLists) {
                if (!handlerList.isValid(io)) continue;

                var containers = handlerList.getCapability(EURecipeCapability.CAP).stream()
                        .filter(IEnergyContainer.class::isInstance)
                        .map(IEnergyContainer.class::cast)
                        .toList();

                if (handlerList.getHandlerIO().support(IO.IN)) {
                    inputs.addAll(containers);
                }

                traitSubscriptions
                        .add(handlerList.subscribe(tickSubscription::updateSubscription, EURecipeCapability.CAP));
            }
        }

        this.inputHatches = new EnergyContainerList(inputs);
        this.isReadyToTransmit = true;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();

        // Toggle off render on structure invalid if it exists
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> StarTDreamLinkRangeRenderer.toggleOffBoxAtPositionWithRange(this.getPos(), this.range));
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
        // Transfer energy tick only every 3 seconds, should keep up fine just averaged over 3 seconds instead of every
        // tick/second
        // and help save TPS a bit due to GTM update handlers on recipe logic
        // from hatches receiving power very often?
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
        UUID thisUUID = StarTGetMachineUUIDSafe.getUUIDSafeMetaMachine(this);

        Observable<Entry<IStarTDreamLinkNetworkReceiveEnergy, Geometry>> machines;

        // Get dream-link hatches
        if (this.range != -1) {
            machines = StarTDreamLinkManager.getDevices(x + range, z + range, x - range, z - range, thisUUID);
        } else {
            machines = StarTDreamLinkManager.getAllDevices(thisUUID);
        }

        // Convert Observable to List once and cache it
        List<Entry<IStarTDreamLinkNetworkReceiveEnergy, Geometry>> deviceEntries = machines
                .filter(machine -> machine.value().canReceive(this, this.checkDimension))
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
        for (Entry<IStarTDreamLinkNetworkReceiveEnergy, Geometry> entry : deviceEntries) {
            receiverCache.add(entry.value());
        }

        receiverCount = receiverCache.size();
    }

    private double getSquaredDistanceToThis(BlockPos otherPos) {
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

        // Batch energy removal to reduce method calls over iteration
        long totalEnergyTransferred = 0;

        // Cache the array reference for fastest possible access
        final Object[] receivers = receiverCache.toArray();

        // Use array access with type casting
        for (int i = 0; i < receiverCount && energyStored > 0; i++) {
            IStarTDreamLinkNetworkReceiveEnergy device = (IStarTDreamLinkNetworkReceiveEnergy) receivers[i];

            long energyToTransfer = device.receiveEnergy(energyStored);
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
        MutableComponent ownerComponent = Component.literal(this.getOwner().getName());

        textList.add(Component
                .translatable("start_core.machine.dream_link.owner_title")
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.dream_link.tower.owner_hover")))));

        textList.add(Component
                .translatable("start_core.machine.dream_link.owner", ownerComponent)
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.dream_link.tower.owner_hover")))));

        MutableComponent inAmountComponent = Component
                .literal(FormattingUtil.formatNumbers(this.inputHatches.getInputPerSec() / 20))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
        textList.add(Component
                .translatable("start_core.machine.dream_link.input_per_sec", inAmountComponent)
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.dream_link.tower.input_per_sec_hover")))));

        MutableComponent outAmountComponent = Component
                .literal(FormattingUtil.formatNumbers(this.inputHatches.getOutputPerSec() / 20))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.RED));
        textList.add(Component
                .translatable("start_core.machine.dream_link.output_per_sec", outAmountComponent)
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.dream_link.tower.output_per_sec_hover")))));

        MutableComponent totalBufferComponent = Component
                .literal(FormattingUtil.formatNumbers(this.inputHatches.getEnergyStored()))
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
            textList.add(
                    ComponentPanel.withButton(
                            Component.translatable("start_core.machine.dream_link.tower.range_show")
                                    .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            Component.translatable(
                                                    "start_core.machine.dream_link.tower.range_button_hover")))),
                            "range"));
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

        MutableComponent connectionsDisplay = Component.empty()
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
    public void onDreamLinkComponentPanelClicked(ComponentPanel panel, String componentData, ClickData clickData) {
        if (Objects.equals(componentData, "range")) {
            panel.sendMessage(RANGE_TOGGLE_MESSAGE);
        }
    }

    private void toggleRangeBox() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> StarTDreamLinkRangeRenderer.toggleBoxAtPositionWithRange(this.getPos(), this.range));
    }

    @Override
    public UIElement createUIWidget() {
        var title = new Label();
        title.setText(Component.literal("Dream-link Transmission Node"));

        var subtitle = new Label();
        subtitle.setText(Component.literal("§7Dream-Network Identifier"));

        var networkField = new TextField().setAnyString();
        networkField.setTextValidator(text -> text.length() <= 64);
        networkField.style(style -> style
                .tooltips(Component.translatable("start_core.machine.dream_link.network_set_hover")));
        networkField.bind(DataBindingBuilder
                .string(this::getTempNetwork, input -> setTempNetwork(input == null ? "" : input)).build());

        var panel = new ComponentPanel(this::addDisplayText).setMaxWidthLimit(174);
        panel.clickHandler((componentData, clickData) -> onDreamLinkComponentPanelClicked(panel, componentData,
                clickData));
        panel.onMessage(RANGE_TOGGLE_MESSAGE, tag -> toggleRangeBox());

        var content = new UIElement();
        content.layout(layout -> layout.paddingAll(4).gapAll(3));
        content.addChildren(title, subtitle, networkField, panel);

        var screen = new ScrollerView();
        screen.layout(layout -> layout.width(182).height(117));
        screen.style(style -> style.background(GuiTextures.DISPLAY));
        screen.addScrollViewChild(content);

        var group = new UIElement();
        group.layout(layout -> layout.paddingAll(4));
        group.style(style -> style.background(GuiTextures.BACKGROUND_INVERSE));
        group.addChild(screen);
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
        var widget = new FancyMachineUIWidget(this, 198, 208);
        UICloseHandler.onClose(widget, this::closeUI);
        return UITemplate.modularUI(widget, entityPlayer);
    }

    @Override
    public List<IFancyUIProvider> getSubTabs() {
        return getParts().stream().filter(Objects::nonNull).map(IFancyUIProvider.class::cast)
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
            copyItem.setHoverName(
                    Component.translatable("start_core.machine.dream_link.lucinducer.name", this.getNetwork()));
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
}
