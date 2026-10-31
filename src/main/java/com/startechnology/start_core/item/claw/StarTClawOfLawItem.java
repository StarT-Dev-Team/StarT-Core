package com.startechnology.start_core.item.claw;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IElectricItem;
import com.gregtechceu.gtceu.api.block.PipeBlock;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.ToolProperty;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.gregtechceu.gtceu.api.item.IGTTool;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.IGTToolDefinition;
import com.gregtechceu.gtceu.api.item.tool.ToolDefinitionBuilder;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.api.sound.SoundEntry;
import com.gregtechceu.gtceu.common.item.tool.behavior.ToolModeSwitchBehavior;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import com.google.common.collect.Multimap;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StarTClawOfLawItem extends Item implements IGTTool {

    public static final String MODE_KEY = "claw_mode";
    public static final String COLORS_KEY = "claw_colors";
    public static final String TOOLS_KEY = "claw_tools";
    public static final float ATTACK_DAMAGE = 7_777_775F;
    public static final int INSTANT_BREAK_COOLDOWN_TICKS = 5;
    private static final int DESTROY_BLOCK_EVENT = 2001;
    public static final boolean FREE_USE = false;
    private static boolean instantBreaking;
    private static final IGTToolDefinition DEFINITION = new ToolDefinitionBuilder()
            .blockBreaking()
            .attacking()
            .baseEfficiency(10F)
            .attackDamage(5F)
            .behaviors(StarTClawBehavior.INSTANCE)
            .build();

    private final int electricTier;
    private final long maxCharge;
    private final long actionCost;
    private final Material defaultMaterial;
    private final Material defaultMalletMaterial;

    private final boolean instantBreak;

    public StarTClawOfLawItem(Properties properties, int electricTier, long maxCharge, long actionCost,
                              boolean instantBreak, Material defaultMaterial,
                              Material defaultMalletMaterial) {
        super(properties);

        this.electricTier = electricTier;
        this.maxCharge = maxCharge;
        this.actionCost = actionCost;
        this.instantBreak = instantBreak;
        this.defaultMaterial = defaultMaterial;
        this.defaultMalletMaterial = defaultMalletMaterial;
    }

    public boolean breaksInstantly() {
        return instantBreak;
    }

    public static void chargeAction(ItemStack stack, @Nullable LivingEntity user) {
        if (FREE_USE || !(stack.getItem() instanceof StarTClawOfLawItem claw)) return;
        if (user instanceof Player player && player.isCreative()) return;

        IElectricItem electricItem = GTCapabilityHelper.getElectricItem(stack);

        if (electricItem != null) {
            electricItem.discharge(claw.actionCost, claw.electricTier, true, false, false);
        }
    }

    public static StarTClawMode getMode(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        return tag == null ? StarTClawMode.NONE : StarTClawMode.getByName(tag.getString(MODE_KEY));
    }

    public static int getColor(ItemStack stack, StarTClawPart part) {
        CompoundTag tag = stack.getTag();

        int defaultColor = defaultColor(stack, part);

        if (tag == null || !tag.contains(COLORS_KEY)) return defaultColor;

        CompoundTag colors = tag.getCompound(COLORS_KEY);

        return colors.contains(part.getKey()) ? colors.getInt(part.getKey()) : defaultColor;
    }

    private static int defaultColor(ItemStack stack, StarTClawPart part) {
        if (part == StarTClawPart.MITT && stack.getItem() instanceof StarTClawOfLawItem claw) {
            return claw.getTierColor();
        }

        return part.getDefaultColor();
    }

    public int getTierColor() {
        return GTValues.VCM[electricTier];
    }

    public static void clearColor(ItemStack stack, StarTClawPart part) {
        CompoundTag tag = stack.getTag();

        if (tag == null || !tag.contains(COLORS_KEY)) return;

        tag.getCompound(COLORS_KEY).remove(part.getKey());
    }

    public static void setColor(ItemStack stack, StarTClawPart part, int color) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag colors = tag.getCompound(COLORS_KEY);

        colors.putInt(part.getKey(), color);
        tag.put(COLORS_KEY, colors);
    }

    public static Material getToolMaterial(ItemStack stack, StarTClawMode mode) {
        CompoundTag tag = stack.getTag();

        CompoundTag tools = tag == null ? new CompoundTag() : tag.getCompound(TOOLS_KEY);

        if (tools.contains(mode.getId())) {
            Material material = GTMaterials.get(tools.getString(mode.getId()));

            if (material != GTMaterials.NULL) return material;
        }

        if (!(stack.getItem() instanceof StarTClawOfLawItem claw)) return GTMaterials.Neutronium;

        return mode == StarTClawMode.SOFT_MALLET ? claw.defaultMalletMaterial : claw.defaultMaterial;
    }

    public static void setToolMaterial(ItemStack stack, StarTClawMode mode, Material material) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag tools = tag.getCompound(TOOLS_KEY);

        tools.putString(mode.getId(), material.getResourceLocation().toString());
        tag.put(TOOLS_KEY, tools);
    }

    public static void setMode(ItemStack stack, StarTClawMode mode) {
        CompoundTag tag = stack.getOrCreateTag();

        tag.putString(MODE_KEY, mode.getId());
        tag.putInt("CustomModelData", mode.ordinal() + 1);
    }

    public static ItemStack getHeldClaw(Player player) {
        if (player.getMainHandItem().getItem() instanceof StarTClawOfLawItem) return player.getMainHandItem();
        if (player.getOffhandItem().getItem() instanceof StarTClawOfLawItem) return player.getOffhandItem();

        return ItemStack.EMPTY;
    }

    public static boolean hasCharge(ItemStack stack) {
        return stack.getItem() instanceof StarTClawOfLawItem claw && hasCharge(stack, claw.actionCost);
    }

    public static boolean hasCharge(ItemStack stack, long amount) {
        if (FREE_USE) return true;

        IElectricItem electricItem = GTCapabilityHelper.getElectricItem(stack);

        return electricItem != null && electricItem.getCharge() >= amount;
    }

    @Override
    public GTToolType getToolType() {
        return GTToolType.PICKAXE;
    }

    @Override
    public Material getMaterial() {
        return defaultMaterial;
    }

    @Override
    public boolean isElectric() {
        return !FREE_USE;
    }

    @Override
    public int getElectricTier() {
        return electricTier;
    }

    @Override
    public IGTToolDefinition getToolStats() {
        return DEFINITION;
    }

    @Nullable
    @Override
    public SoundEntry getSound() {
        return null;
    }

    @Override
    public boolean playSoundOnBlockDestroy() {
        return false;
    }

    @Override
    public Set<GTToolType> getToolClasses(ItemStack stack) {
        Set<GTToolType> toolClasses = new HashSet<>();

        GTToolType toolType = getMode(stack).getToolType();

        if (toolType != null && hasCharge(stack, actionCost)) {
            toolClasses.addAll(toolType.toolClasses);
            toolClasses.add(toolType);
        }

        return toolClasses;
    }

    @Override
    public float getTotalToolSpeed(ItemStack stack) {
        GTToolType toolType = getMode(stack).getToolType();
        ToolProperty property = defaultMaterial.getProperty(PropertyKey.TOOL);

        if (breaksInstantly() || toolType == null || property == null) return 12F;

        IGTToolDefinition definition = toolType.toolDefinition;

        return definition.getEfficiencyMultiplier(stack) * property.getHarvestSpeed() +
                definition.getBaseEfficiency(stack);
    }

    @Override
    public int getTotalHarvestLevel(ItemStack stack) {
        return TierSortingRegistry.getSortedTiers().stream().mapToInt(Tier::getLevel).max().orElse(0);
    }

    @Override
    public float getTotalAttackDamage(ItemStack stack) {
        if (!hasCharge(stack)) return 1F;
        if (breaksInstantly()) return ATTACK_DAMAGE;

        ToolProperty property = defaultMaterial.getProperty(PropertyKey.TOOL);

        return (property == null ? 0F : property.getAttackDamage()) +
                GTToolType.SWORD.toolDefinition.getBaseDamage(stack);
    }

    @Override
    public float getTotalAttackSpeed(ItemStack stack) {
        if (breaksInstantly()) return -2.4F;

        ToolProperty property = defaultMaterial.getProperty(PropertyKey.TOOL);

        return (property == null ? 0F : property.getAttackSpeed()) +
                GTToolType.SWORD.toolDefinition.getAttackSpeed(stack);
    }

    @Override
    public int getTotalMaxDurability(ItemStack stack) {
        return 1_000_000;
    }

    @Override
    public ItemStack get() {
        ItemStack stack = new ItemStack(this);

        stack.getOrCreateTag().putInt(ToolHelper.HIDE_FLAGS, 2);
        stack.getOrCreateTag().putBoolean(ToolHelper.UNBREAKABLE_KEY, true);

        setMode(stack, StarTClawMode.NONE);

        CompoundTag behaviors = ToolHelper.getBehaviorsTag(stack);

        StarTClawBehavior.INSTANCE.addBehaviorNBT(stack, behaviors);

        behaviors.putByte("Mode", (byte) ToolModeSwitchBehavior.WrenchModeType.BOTH.ordinal());

        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && !stack.getOrCreateTag().getBoolean(ToolHelper.UNBREAKABLE_KEY)) {
            stack.getOrCreateTag().putBoolean(ToolHelper.UNBREAKABLE_KEY, true);
        }
    }

    @Override
    public void definition$fillItemCategory(CreativeModeTab category, NonNullList<ItemStack> items) {
        items.add(getDefaultInstance());
    }

    @Override
    public ItemStack getDefaultInstance() {
        return get(maxCharge);
    }

    @Override
    public int getDamage(ItemStack stack) {
        return 0;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return getTotalMaxDurability(stack);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return definition$initCapabilities(stack, nbt);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction action) {
        return getToolClasses(stack).stream().anyMatch(toolType -> toolType.defaultAbilities.contains(action)) ||
                StarTClawBehavior.INSTANCE.canPerformAction(stack, action);
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        GTToolType toolType = getMode(stack).getToolType();

        return toolType != null && hasCharge(stack) && toolType.toolDefinition.doesSneakBypassUse();
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        return definition$onBlockStartBreak(stack, pos, player);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return definition$onItemUseFirst(stack, context);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return definition$onItemUse(context);
    }

    public static boolean canInstantBreak(ItemStack stack, Level level, BlockPos pos) {
        if (!(stack.getItem() instanceof StarTClawOfLawItem claw) || !claw.breaksInstantly() ||
                !hasCharge(stack, claw.actionCost)) {
            return false;
        }

        BlockState state = level.getBlockState(pos);

        if (state.getDestroySpeed(level, pos) < 0) {
            return false;
        }

        Set<GTToolType> toolClasses = claw.getToolClasses(stack);

        return ToolHelper.isToolEffective(state, toolClasses, claw.getTotalHarvestLevel(stack)) ||
                state.getBlock() instanceof PipeBlock<?, ?, ?> pipe && toolClasses.contains(pipe.getPipeTuneTool());
    }

    public static void instantBreak(ServerPlayer player, ItemStack stack, BlockPos pos) {
        boolean broken;
        int stateId = Block.getId(player.level().getBlockState(pos));

        instantBreaking = true;

        try {
            broken = player.gameMode.destroyBlock(pos);
        } finally {
            instantBreaking = false;
        }

        if (!broken) return;

        player.connection
                .send(new ClientboundLevelEventPacket(DESTROY_BLOCK_EVENT, pos, stateId, false));

        IElectricItem electricItem = GTCapabilityHelper.getElectricItem(stack);
        StarTClawOfLawItem claw = (StarTClawOfLawItem) stack.getItem();

        if (!FREE_USE && electricItem != null) {
            electricItem.discharge(claw.actionCost, claw.electricTier, true, false, false);
        }

        player.getCooldowns().addCooldown(stack.getItem(), INSTANT_BREAK_COOLDOWN_TICKS);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        return definition$use(level, player, hand);
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miner) {
        if (instantBreaking) {
            StarTClawBehavior.INSTANCE.onBlockDestroyed(stack, level, state, pos, miner);

            return true;
        }

        return definition$mineBlock(stack, level, state, pos, miner);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return definition$getDestroySpeed(stack, state);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return definition$isCorrectToolForDrops(stack, state);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean damaged = definition$hurtEnemy(stack, target, attacker);

        if (breaksInstantly() && hasCharge(stack) && target.isAlive() && !(target instanceof Player)) {
            target.hurt(target.damageSources().genericKill(), Float.MAX_VALUE);
        }

        return damaged;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return definition$getDefaultAttributeModifiers(slot, stack);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem() || getMode(oldStack) != getMode(newStack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public Component getName(ItemStack stack) {
        StarTClawMode mode = getMode(stack);
        ItemStack tool = StarTClawToolRenderer.getTool(stack, mode);

        MutableComponent tier = Component.literal(GTValues.VN[electricTier])
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(getTierColor())));
        MutableComponent name = Component.literal(" ").append(Component.translatable(getDescriptionId()));

        if (mode != StarTClawMode.NONE) {
            name.append(Component.literal(" - "))
                    .append(tool.isEmpty() ? Component.translatable(mode.getLangKey()) : tool.getHoverName().copy());
        }

        return tier.append(name.withStyle(ChatFormatting.WHITE));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        if (isElectric()) {
            IElectricItem electricItem = GTCapabilityHelper.getElectricItem(stack);

            if (electricItem != null) {
                ElectricStats.addCurrentChargeTooltip(tooltip, electricItem.getCharge(), electricItem.getMaxCharge(),
                        getElectricTier(), false);
            }
        }

        tooltip.add(Component.translatable("item.gtceu.tool.tooltip.attack_damage",
                FormattingUtil.formatNumbers(2 + getTotalAttackDamage(stack))));
        tooltip.add(Component.translatable("item.gtceu.tool.tooltip.attack_speed",
                FormattingUtil.formatNumbers(4 + getTotalAttackSpeed(stack))));

        if (!breaksInstantly() && getMode(stack) != StarTClawMode.NONE) {
            tooltip.add(Component.translatable("item.gtceu.tool.tooltip.mining_speed",
                    FormattingUtil.formatNumbers(getTotalToolSpeed(stack))));
        }

        tooltip.add(CommonComponents.EMPTY);
        tooltip.add(Component.translatable("item.start_core.claw_of_law.tooltip.mode",
                Component.translatable(getMode(stack).getLangKey()).withStyle(ChatFormatting.AQUA)));
        tooltip.add(Component.translatable("item.start_core.claw_of_law.tooltip.action_cost",
                FormattingUtil.formatNumbers(actionCost), GTValues.VNF[GTUtil.getTierByVoltage(actionCost)]));

        StarTClawBehavior.INSTANCE.addInformation(stack, level, tooltip, flag);

        if (getMode(stack) != StarTClawMode.NONE) {
            tooltip.add(Component.translatable("item.gtceu.tool.usable_as",
                    getToolClassNames(stack).stream()
                            .filter(className -> Language.getInstance().has("gtceu.tool.class." + className))
                            .map(className -> Component.translatable("gtceu.tool.class." + className))
                            .collect(Component::empty, FormattingUtil::combineComponents,
                                    FormattingUtil::combineComponents)));
        }

        Component key = Component.literal("[")
                .append(StarTClawKeyMappings.CYCLE.getCombinedDisplayName(KeyModifier.NONE))
                .append("]")
                .withStyle(ChatFormatting.LIGHT_PURPLE);
        Component shift = Component.literal("[SHIFT]").withStyle(ChatFormatting.LIGHT_PURPLE);

        tooltip.add(Component.translatable("item.start_core.claw_of_law.tooltip.hotkey", key)
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.start_core.claw_of_law.tooltip.hotkey_previous", shift, key)
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.start_core.claw_of_law.tooltip.hotkey_menu", key)
                .withStyle(ChatFormatting.GRAY));
    }
}
