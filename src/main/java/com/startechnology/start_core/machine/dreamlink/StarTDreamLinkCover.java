package com.startechnology.start_core.machine.dreamlink;

import java.util.Objects;
import java.util.UUID;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.cover.IUICover;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.IExplosionMachine;
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
import com.startechnology.start_core.api.dreamlink.IStarTCopyInteractable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class StarTDreamLinkCover extends CoverBehavior implements IStarTDreamLinkNetworkRecieveEnergy, IStarTCopyInteractable, IUICover {
    
    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTDreamLinkCover.class,
        CoverBehavior.MANAGED_FIELD_HOLDER);

    private int tier;
    private int amperage;

    @Persisted
    private String network;
    private TickableSubscription addTickSubscription;
    private UUID ownerUUID;

    public StarTDreamLinkCover(CoverDefinition definition, ICoverable coverHolder, Direction attachedSide, int tier, int amperage) {
        super(definition, coverHolder, attachedSide);
        this.network = IStarTDreamLinkNetworkMachine.DEFAULT_NETWORK;
        this.tier = tier;
        this.amperage = amperage;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (this.coverHolder.getLevel().isClientSide)
            return;

        addTickSubscription = this.coverHolder.subscribeServerTick(addTickSubscription, this::addToTreeSubscription);
    }

    protected void addToTreeSubscription() {
        if (this.coverHolder.getOffsetTimer() % 5 == 0) {
            var machine = coverHolder.getLevel().getBlockEntity(coverHolder.getPos());

            if (machine instanceof MetaMachineBlockEntity metaMachineBlockEntity) {
                UUID ownerUUID = IStarTGetMachineUUIDSafe.getUUIDSafeMetaMachineBlockEntity(metaMachineBlockEntity);
                this.ownerUUID = ownerUUID;
                StarTDreamLinkManager.addDevice(this, ownerUUID);
            }
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();

        if (this.coverHolder.getLevel().isClientSide)
            return;

        StarTDreamLinkManager.removeDevice(this, ownerUUID);
    
        if (Objects.nonNull(this.addTickSubscription)) {
            this.addTickSubscription.unsubscribe();
        }
    }

    protected IEnergyContainer getEnergyContainer() {
        return GTCapabilityHelper.getEnergyContainer(coverHolder.getLevel(), coverHolder.getPos(), attachedSide);
    }

    public void stupidExplosion(IExplosionMachine machine, BlockPos centrePos) {
        double gap = 20.0;
        double maxRadius = (((this.tier - GTValues.UV) + 1) * gap)/2;
        int explosionPower = 128;
        
        for (double radius = 0; radius <= maxRadius; radius += gap) {
            if (radius == 0) {
                // Central explosion
                machine.doExplosion(centrePos, explosionPower);
                continue;
            }
        
            // Vertical divisions (latitude-like)
            int latSteps = (int) Math.ceil(Math.PI * radius / gap);
            for (int i = 0; i <= latSteps; i++) {
                double theta = Math.PI * i / latSteps; // 0 (top) to π (bottom)
        
                // Horizontal divisions at this latitude (longitude-like)
                double sinTheta = Math.sin(theta);
                if (sinTheta == 0) {
                    // Avoid division by zero (top/bottom poles)
                    int x = centrePos.getX();
                    int y = centrePos.getY() + (int) Math.round(radius * Math.cos(theta));
                    int z = centrePos.getZ();
                    BlockPos pos = new BlockPos(x, y, z);
                    machine.doExplosion(pos, explosionPower);
                    continue;
                }
        
                int lonSteps = (int) Math.ceil(2 * Math.PI * radius * sinTheta / gap);
                for (int j = 0; j < lonSteps; j++) {
                    double phi = 2 * Math.PI * j / lonSteps;
        
                    int x = centrePos.getX() + (int) Math.round(radius * sinTheta * Math.cos(phi));
                    int y = centrePos.getY() + (int) Math.round(radius * Math.cos(theta));
                    int z = centrePos.getZ() + (int) Math.round(radius * sinTheta * Math.sin(phi));
        
                    BlockPos pos = new BlockPos(x, y, z);
                    machine.doExplosion(pos, explosionPower);
                }
            }
        }        
    }

    @Override
    public void onRemoved() {
        if (this.coverHolder.getLevel().isClientSide)
            return;

        StarTDreamLinkManager.removeDevice(this, ownerUUID);

        if (Objects.nonNull(this.addTickSubscription)) {
            this.addTickSubscription.unsubscribe();
        }
    }

    @Override
    public long recieveEnergy(long recieved) {
        IEnergyContainer container = this.getEnergyContainer();

        if (Objects.isNull(container)) {
            return 0;
        }

        if (container.getInputVoltage() < GTValues.V[this.tier] && recieved > container.getInputVoltage()) {
            var entity = coverHolder.getLevel().getBlockEntity(coverHolder.getPos());

            if (entity instanceof MetaMachineBlockEntity metaMachineBlockEntity) {
                BlockPos centrePos = metaMachineBlockEntity.getBlockPos();
                if (metaMachineBlockEntity.getMetaMachine() instanceof IExplosionMachine explode) {
                    stupidExplosion(explode, centrePos);
                }
            }
        }


        return container.changeEnergy(Math.min(Math.min(recieved, container.getInputVoltage() * container.getInputAmperage()), this.amperage * GTValues.V[this.tier]));
    }

    @Override
    public BlockPos devicePos() {
        return coverHolder.getPos();
    }

    @Override
    public boolean canRecieve(StarTDreamLinkTransmissionMachine tower, Boolean checkDimension) {
        if (!Objects.equals(this.network, tower.getNetwork()))
            return false;

        var entity = coverHolder.getLevel().getBlockEntity(coverHolder.getPos());

        if (entity instanceof MetaMachineBlockEntity machine) {
            if (!Objects.equals(IStarTGetMachineUUIDSafe.getUUIDSafeMetaMachineBlockEntity(machine), IStarTGetMachineUUIDSafe.getUUIDSafeMetaMachine(tower)))
                return false;            
        } else {
            return false;
        }

        if (checkDimension) {
            if (!Objects.equals(coverHolder.getLevel().dimensionTypeId(), tower.getLevel().dimensionTypeId()))
                return false;
        }

        return true;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
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
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(
            new DraggableScrollableWidgetGroup(4, 4, 182, 117)
                .addWidget(new LabelWidget(4, 5, "Dream-Link Cover"))
                .addWidget(new LabelWidget(4, 20, "Dream-Network Identifier"))
                .addWidget(
                    new TextFieldWidget(4, 32, 182 - 8, 12, this::getNetwork, this::setNetwork)
                        .setMaxStringLength(64)
                        .setValidator(input -> {
                            if (input == null || input.isBlank()) return IStarTDreamLinkNetworkMachine.DEFAULT_NETWORK + "";
                            return input;
                        })
                        .setHoverTooltips(Component.translatable("start_core.machine.dream_link.network_set_hover"))
                )
        );
        
        return group;
    }   
    
    public void setNetwork(String network) {
        this.network = network;
    }

    public String getNetwork() {
        return this.network;
    }
}
