package com.startechnology.start_core.item.curios;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IElectricItem;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.common.machine.owner.MachineOwner;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkMachine;
import com.startechnology.start_core.api.capability.IStarTDreamLinkNetworkRecieveEnergy;
import com.startechnology.start_core.api.capability.IStarTGetMachineUUIDSafe;
import com.startechnology.start_core.item.StarTItems;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkManager;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkTransmissionMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class LucinducerCurioItem implements ICurioItem {
    private static final String SLOT_ID = "lucinducer";
    private static final String DREAM_NETWORK_KEY = "dream_network";
    private static final Map<ReceiverKey, InventoryChargingReceiver> RECEIVERS = new HashMap<>();

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        ICurioItem.super.curioTick(slotContext, stack);
        if (isClientSide(slotContext.entity())) return;
        syncReceiver(slotContext, stack);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
        ICurioItem.super.onEquip(slotContext, prevStack, stack);
        if (isClientSide(slotContext.entity())) return;
        syncReceiver(slotContext, stack);
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
        ICurioItem.super.onUnequip(slotContext, newStack, stack);
        if (isClientSide(slotContext.entity())) return;
        removeReceiver(ReceiverKey.from(slotContext));
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        if (!slotContext.identifier().equals(SLOT_ID)) {
            return false;
        }

        return !(slotContext.entity() instanceof Player player) || !hasEquippedLucinducer(player, slotContext);
    }

    public static void removeAllFor(LivingEntity entity) {
        if (entity == null || isClientSide(entity)) {
            return;
        }

        var playerId = entity.getUUID();
        RECEIVERS.entrySet().removeIf(entry -> {
            if (!entry.getKey().playerId().equals(playerId)) {
                return false;
            }

            entry.getValue().discard();
            return true;
        });
    }

    private static void syncReceiver(SlotContext slotContext, ItemStack stack) {
        var player = (ServerPlayer) slotContext.entity();
        var key = ReceiverKey.from(slotContext);
        var network = getCopiedNetwork(stack);

        if (network.isEmpty() || hasOtherRegisteredReceiver(player, key)) {
            removeReceiver(key);
            return;
        }

        var receiver = RECEIVERS.computeIfAbsent(key, ignored -> new InventoryChargingReceiver());
        receiver.update(
                player,
                stack,
                player.blockPosition(),
                network.get(),
                getDreamLinkOwner(player),
                player.level().dimensionTypeId());
    }

    private static void removeReceiver(ReceiverKey key) {
        var receiver = RECEIVERS.remove(key);
        if (receiver != null) {
            receiver.discard();
        }
    }

    private static Optional<String> getCopiedNetwork(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null || !tag.contains(DREAM_NETWORK_KEY)) {
            return Optional.empty();
        }

        var network = tag.getString(DREAM_NETWORK_KEY);
        return network.isBlank() ? Optional.empty() : Optional.of(network);
    }

    private static UUID getDreamLinkOwner(Player player) {
        var owner = MachineOwner.getOwner(player.getUUID());
        if (owner == null || owner.getUUID() == null || owner.getUUID().equals(MachineOwner.EMPTY)) {
            return player.getUUID();
        }
        return owner.getUUID();
    }

    private static boolean hasOtherRegisteredReceiver(Player player, ReceiverKey currentKey) {
        return RECEIVERS.keySet().stream()
                .anyMatch(key -> key.playerId().equals(player.getUUID()) && !key.equals(currentKey));
    }

    private static boolean hasEquippedLucinducer(Player player, SlotContext ignoredSlot) {
        return CuriosApi.getCuriosInventory(player)
                .map(handler -> {
                    for (var entry : handler.getCurios().entrySet()) {
                        var stacks = entry.getValue().getStacks();

                        for (int slot = 0; slot < stacks.getSlots(); slot++) {
                            if (entry.getKey().equals(ignoredSlot.identifier()) && slot == ignoredSlot.index()) {
                                continue;
                            }
                            if (stacks.getStackInSlot(slot).is(StarTItems.TOOL_DREAM_COPY_ITEM.asItem())) {
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .orElse(false);
    }

    private record ReceiverKey(UUID playerId, String identifier, int index) {
        private static ReceiverKey from(SlotContext slotContext) {
            return new ReceiverKey(slotContext.entity().getUUID(), slotContext.identifier(), slotContext.index());
        }
    }

    private static final class InventoryChargingReceiver implements IStarTDreamLinkNetworkRecieveEnergy {
        private ServerPlayer player;
        private ItemStack lucinducer = ItemStack.EMPTY;
        private BlockPos position = BlockPos.ZERO;
        private String network = IStarTDreamLinkNetworkMachine.DEFAULT_NETWORK;
        private UUID ownerId = MachineOwner.EMPTY;
        private ResourceKey<DimensionType> dimension;
        private boolean registered;

        private void update(ServerPlayer player, ItemStack lucinducer, BlockPos position, String network, UUID ownerId,
                            ResourceKey<DimensionType> dimension) {
            var changed = this.player != player ||
                    !this.position.equals(position) ||
                    !this.network.equals(network) ||
                    !this.ownerId.equals(ownerId) ||
                    !this.dimension.equals(dimension);

            if (registered && changed) {
                unregister();
            }

            this.player = player;
            this.lucinducer = lucinducer;
            this.position = position;
            this.network = network;
            this.ownerId = ownerId;
            this.dimension = dimension;

            if (!registered) {
                StarTDreamLinkManager.addDevice(this, ownerId);
                registered = true;
            }
        }

        private void discard() {
            unregister();
            player = null;
            lucinducer = ItemStack.EMPTY;
            position = BlockPos.ZERO;
            network = IStarTDreamLinkNetworkMachine.DEFAULT_NETWORK;
            ownerId = MachineOwner.EMPTY;
            dimension = null;
        }

        private void unregister() {
            if (registered) {
                StarTDreamLinkManager.removeDevice(this, ownerId);
                registered = false;
            }
        }

        @Override
        public long recieveEnergy(long recieved) {
            if (recieved <= 0 || !isActive()) {
                return 0;
            }

            var accepted = chargeCarriedItems(recieved);
            if (accepted > 0) {
                player.getInventory().setChanged();
                player.containerMenu.broadcastChanges();
            }
            return accepted;
        }

        @Override
        public BlockPos devicePos() {
            return position;
        }

        @Override
        public boolean canRecieve(StarTDreamLinkTransmissionMachine tower, Boolean checkDimension) {
            if (!isActive()) {
                return false;
            }

            if (!network.equals(tower.getNetwork())) {
                return false;
            }

            if (!ownerId.equals(IStarTGetMachineUUIDSafe.getUUIDSafeMetaMachine(tower))) {
                return false;
            }

            return !checkDimension || dimension.equals(tower.getLevel().dimensionTypeId());
        }

        private boolean isActive() {
            return player != null &&
                    !player.isRemoved() &&
                    player.getServer() != null &&
                    player.getServer().getPlayerList().getPlayer(player.getUUID()) == player &&
                    !lucinducer.isEmpty() &&
                    lucinducer.is(StarTItems.TOOL_DREAM_COPY_ITEM.asItem()) &&
                    getCopiedNetwork(lucinducer).map(network::equals).orElse(false);
        }

        private long chargeCarriedItems(long availableEU) {
            var remaining = availableEU;

            remaining = chargeIterable(player.getInventory().items, remaining);
            remaining = chargeIterable(player.getInventory().armor, remaining);
            remaining = chargeIterable(player.getInventory().offhand, remaining);
            remaining = chargeCurios(remaining);

            return availableEU - remaining;
        }

        private long chargeIterable(Iterable<ItemStack> stacks, long availableEU) {
            var remaining = availableEU;

            for (var stack : stacks) {
                remaining -= chargeStack(stack, remaining);
                if (remaining <= 0) {
                    return 0;
                }
            }

            return remaining;
        }

        private long chargeCurios(long availableEU) {
            var curios = CuriosApi.getCuriosInventory(player)
                    .<IItemHandler>map(ICuriosItemHandler::getEquippedCurios)
                    .orElse(EmptyHandler.INSTANCE);
            var remaining = availableEU;

            for (int slot = 0; slot < curios.getSlots(); slot++) {
                remaining -= chargeStack(curios.getStackInSlot(slot), remaining);
                if (remaining <= 0) {
                    return 0;
                }
            }

            return remaining;
        }

        private long chargeStack(ItemStack stack, long availableEU) {
            if (availableEU <= 0 || stack.isEmpty() || stack.is(StarTItems.TOOL_DREAM_COPY_ITEM.asItem())) {
                return 0;
            }

            var electricItem = GTCapabilityHelper.getElectricItem(stack);
            if (electricItem != null) {
                return electricItem.charge(availableEU, Integer.MAX_VALUE, true, false);
            }

            var energyStorage = GTCapabilityHelper.getForgeEnergyItem(stack);
            if (energyStorage == null ||
                    !energyStorage.canReceive() ||
                    energyStorage.getEnergyStored() >= energyStorage.getMaxEnergyStored()) {
                return 0;
            }

            return FeCompat.insertEu(energyStorage, availableEU, false);
        }
    }

    public static boolean isClientSide(Entity entity) {
        return entity.level().isClientSide;
    }
}
