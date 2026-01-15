package com.startechnology.start_core.machine.dreamlink;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyTooltip;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
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

public class StarTDreamLinkHatchPartMachine extends TieredIOPartMachine implements IStarTDreamLinkNetworkMachine, IStarTDreamLinkNetworkRecieveEnergy {

    /*
     * As far as i can understand, the Managed Field Holder allows this class
     * to persist/save data onto the world using NBT with the @Persisted field annotation
     */
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTDreamLinkHatchPartMachine.class,
        TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    /* This will persist the NotifiableDreamLinkContainer using nbt. */
    @Persisted
    protected StarTNotifiableDreamLinkContainer container;

    /* Store the network of the hatch too */
    @Persisted
    protected String network;

    @Getter @Setter
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
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
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
        
        if (this.getHolder().getOwner() != null) {
            componentList.add(Component
            .translatable("start_core.machine.dream_link.owned_title")
            .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    Component.translatable("start_core.machine.dream_link.hatch.owned_hover")))));

            MutableComponent ownerComponent = Component.literal(this.getHolder().getOwner().getName());

            componentList.add(Component
                .translatable("start_core.machine.dream_link.owner", ownerComponent)
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.dream_link.hatch.owned_hover")))));
    
        }

        MutableComponent inAmountComponent = Component.literal(FormattingUtil.formatNumbers(this.container.getInputPerSec() / 20))
            .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN));
        componentList.add(Component
                .translatable("start_core.machine.dream_link.input_per_sec", inAmountComponent)
                .withStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        Component.translatable("start_core.machine.dream_link.hatch.input_per_sec_hover")))));

            
    }   

    @Override
    public Widget createUIWidget() {
        this.tempNetwork = network;
        StarTDreamWidgetGroup group = new StarTDreamWidgetGroup(0, 0, 182 + 8, 117 + 8, this::closeUI);
        group.addWidget(
            new DraggableScrollableWidgetGroup(4, 4, 182, 117).setBackground(GuiTextures.DISPLAY)
                .addWidget(new LabelWidget(4, 5, "Dream-Link Hatch"))
                .addWidget(new LabelWidget(4, 20, "§7Dream-Network Identifier"))
                .addWidget(
                    new TextFieldWidget(4, 32, 182 - 8, 12, this::getTempNetwork, this::setTempNetwork)
                        .setMaxStringLength(64)
                        .setValidator(input -> {
                            if (input == null ) return "";
                            return input;
                        })
                        .setHoverTooltips(Component.translatable("start_core.machine.dream_link.network_set_hover"))
                ).addWidget(new ComponentPanelWidget(4, 52, this::addComponentPanelText)
                )
        );
        
        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }
    
    private void closeUI() {
        this.network = this.tempNetwork;
        if (this.tempNetwork.isBlank()) this.network = IStarTDreamLinkNetworkMachine.DEFAULT_NETWORK;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        ModularUI ui = new ModularUI(198, 208, this, entityPlayer).widget(new FancyMachineUIWidget(this, 198, 208));
        return ui;
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
    public InteractionResult onCopyShiftUse(Player player, ItemStack copyItem) {
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
    public final InteractionResult onCopyUse(Player player, ItemStack copyItem) {
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
    public long recieveEnergy(long recieved) {
        return this.container.changeEnergy(Math.min(recieved, container.getInputVoltage() * container.getInputAmperage()));
    }

    @Override
    public BlockPos devicePos() {
        return this.getPos();
    }

    @Override
    public boolean canRecieve(StarTDreamLinkTransmissionMachine tower, Boolean checkDimension) {
        if (!Objects.equals(this.getNetwork(), tower.getNetwork()))
            return false;

        if (!Objects.equals(IStarTGetMachineUUIDSafe.getUUIDSafeMetaMachine(this), IStarTGetMachineUUIDSafe.getUUIDSafeMetaMachine(tower)))
            return false;

        if (checkDimension) {
            if (!Objects.equals(this.getLevel().dimensionTypeId(), tower.getLevel().dimensionTypeId()))
                return false;
        }

        return true;
    }
}
