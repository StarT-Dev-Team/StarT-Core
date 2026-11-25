package com.startechnology.start_core.machine.dreamlink;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.UITemplate;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.widget.ComponentPanel;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib2.gui.sync.bindings.impl.DataBindingBuilder;
import com.lowdragmc.lowdraglib2.gui.ui.ModularUI;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.elements.Label;
import com.lowdragmc.lowdraglib2.gui.ui.elements.ScrollerView;
import com.lowdragmc.lowdraglib2.gui.ui.elements.TextField;
import com.lowdragmc.lowdraglib2.syncdata.annotation.Persisted;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkMachine;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkReceiveEnergy;
import com.startechnology.start_core.api.capability.StarTGetMachineUUIDSafe;
import com.startechnology.start_core.api.capability.StarTNotifiableDreamLinkContainer;
import com.startechnology.start_core.item.StarTItems;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.Objects;

public class StarTDreamLinkHatchPartMachine extends TieredIOPartMachine implements IStarTDreamLinkNetworkMachine,
                                            IStarTDreamLinkNetworkReceiveEnergy, IMachineLife {

    /*
     * As far as i can understand, the Managed Field Holder allows this class
     * to persist/save data onto the world using NBT with the @Persisted field annotation
     */
    /* This will persist the NotifiableDreamLinkContainer using nbt. */
    @Persisted
    protected StarTNotifiableDreamLinkContainer container;

    /* Store the network of the hatch too */
    @Persisted
    protected String network;

    @Getter
    @Setter
    protected String tempNetwork;

    public StarTDreamLinkHatchPartMachine(IMachineBlockEntity holder, int tier, int amperage) {
        super(holder, tier, IO.IN);

        this.container = StarTNotifiableDreamLinkContainer.receiverContainer(this, GTValues.V[tier] * 64L * amperage,
                GTValues.V[tier], amperage);

        this.network = IStarTDreamLinkNetworkMachine.DEFAULT_NETWORK;
        this.tempNetwork = network;
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return true;
    }

    @Override
    public int tintColor(int index) {
        if (index == 2) {
            return GTValues.VC[getTier()];
        }
        return super.tintColor(index);
    }

    private void addComponentPanelText(List<Component> componentList) {
        if (this.container.getInputPerSec() > 0)
            componentList.add(Component.translatable("start_core.machine.dream_link.active"));
        else
            componentList.add(Component.translatable("start_core.machine.dream_link.not_active"));

        if (this.getOwner() != null) {
            componentList.add(Component
                    .translatable("start_core.machine.dream_link.owned_title")
                    .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Component.translatable("start_core.machine.dream_link.hatch.owned_hover")))));

            MutableComponent ownerComponent = Component.literal(this.getOwner().getName());

            componentList.add(Component
                    .translatable("start_core.machine.dream_link.owner", ownerComponent)
                    .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Component.translatable("start_core.machine.dream_link.hatch.owned_hover")))));

        }

        MutableComponent inAmountComponent = Component
                .literal(FormattingUtil.formatNumbers(this.container.getInputPerSec() / 20))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
        componentList.add(Component
                .translatable("start_core.machine.dream_link.input_per_sec", inAmountComponent)
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.dream_link.hatch.input_per_sec_hover")))));
    }

    @Override
    public UIElement createUIWidget() {
        this.tempNetwork = network;

        var title = new Label();
        title.setText(Component.literal("Dream-Link Hatch"));

        var subtitle = new Label();
        subtitle.setText(Component.literal("§7Dream-Network Identifier"));

        var networkField = new TextField().setAnyString();
        networkField.setTextValidator(text -> text.length() <= 64);
        networkField.style(style -> style
                .tooltips(Component.translatable("start_core.machine.dream_link.network_set_hover")));
        networkField.bind(DataBindingBuilder
                .string(this::getTempNetwork, input -> setTempNetwork(input == null ? "" : input)).build());

        var content = new UIElement();
        content.layout(layout -> layout.paddingAll(4).gapAll(3));
        content.addChildren(title, subtitle, networkField,
                new ComponentPanel(this::addComponentPanelText).setMaxWidthLimit(174));

        var screen = new ScrollerView();
        screen.layout(layout -> layout.width(182).height(117));
        screen.style(style -> style.background(GuiTextures.DISPLAY));
        screen.addScrollViewChild(content);

        var group = new StarTDreamWidgetGroup(this::closeUI);
        group.layout(layout -> layout.paddingAll(4));
        group.style(style -> style.background(GuiTextures.BACKGROUND_INVERSE));
        group.addChild(screen);
        return group;
    }

    private void closeUI() {
        this.network = this.tempNetwork;
        if (this.tempNetwork.isBlank()) this.network = IStarTDreamLinkNetworkMachine.DEFAULT_NETWORK;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        return UITemplate.modularUI(new FancyMachineUIWidget(this, 198, 208), entityPlayer);
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
    public void onMachinePlaced(@Nullable LivingEntity player, ItemStack stack) {
        IMachineLife.super.onMachinePlaced(player, stack);

        if (player == null) return;

        var playerOffhandItem = player.getOffhandItem();

        if (playerOffhandItem.is(StarTItems.TOOL_DREAM_COPY_ITEM.asItem())) {
            onDreamCopyUse((Player) player, playerOffhandItem);
        }
    }

    @Override
    public InteractionResult onDreamCopyShiftUse(Player player, ItemStack copyItem) {
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
        return this.container.getInputPerSec() > 0;
    }

    @Override
    public long receiveEnergy(long received) {
        return this.container
                .changeEnergy(Math.min(received, container.getInputVoltage() * container.getInputAmperage()));
    }

    @Override
    public BlockPos devicePos() {
        return this.getPos();
    }

    @Override
    public boolean canReceive(StarTDreamLinkTransmissionMachine tower, boolean checkDimension) {
        if (!Objects.equals(this.getNetwork(), tower.getNetwork()))
            return false;

        if (!Objects.equals(StarTGetMachineUUIDSafe.getUUIDSafeMetaMachine(this),
                StarTGetMachineUUIDSafe.getUUIDSafeMetaMachine(tower)))
            return false;

        if (checkDimension) {
            return Objects.equals(this.getLevel().dimensionTypeId(), tower.getLevel().dimensionTypeId());
        }

        return true;
    }
}
