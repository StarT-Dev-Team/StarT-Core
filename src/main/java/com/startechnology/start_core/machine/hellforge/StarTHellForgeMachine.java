package com.startechnology.start_core.machine.hellforge;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.machine.hellforge.client.HellFlameEmbers;
import com.startechnology.start_core.machine.redstone.IRedstoneIndicatorMachine;
import com.startechnology.start_core.machine.redstone.RedstoneIndicatorRecord;
import com.startechnology.start_core.materials.StarTHellForgeHeatingLiquids;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StarTHellForgeMachine extends WorkableElectricMultiblockMachine implements IRedstoneIndicatorMachine {

    /*
     * persist/save data onto the world using NBT with the @Persisted field
     * annotation
     */
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTHellForgeMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @Getter
    protected int temperature;

    /* The hell forge cannot go below this base Temperature */
    @Getter
    private final int baseTemperature;

    private final int baseTempLoss;
    private final int dormantTempLoss;

    protected TickableSubscription tryTickSub;
    private boolean startHeatLoss;

    private boolean isWorking;

    @Getter
    private final HellFlameProfile profile;
    @DescSynced
    @Getter
    private int displayHeat;
    @DescSynced
    @Getter
    private long heatBurstGameTime = Long.MIN_VALUE / 2;
    @DescSynced
    @Getter
    private int heatBurst;
    @DescSynced
    @Getter
    private long kindleGameTime = Long.MIN_VALUE / 2;
    @DescSynced
    @Getter
    private long gutterGameTime = Long.MIN_VALUE / 2;
    @Persisted
    private boolean flameLit;

    public StarTHellForgeMachine(IMachineBlockEntity holder, HellFlameProfile profile, int baseTemperature,
                                 int baseTempLoss, int dormantTempLoss, Object... args) {
        super(holder, args);
        this.profile = profile;
        this.temperature = baseTemperature;
        this.baseTemperature = baseTemperature;
        this.baseTempLoss = baseTempLoss;
        this.dormantTempLoss = dormantTempLoss;

        this.startHeatLoss = false;
        this.isWorking = false;
    }

    /* A map of fluids to their maximum heat cap for the Hell Forge */
    public static Map<Material, Integer> fluidsMap = Map.of(
            StarTHellForgeHeatingLiquids.FlamewakeSolvent, 900,
            StarTHellForgeHeatingLiquids.EmberheartNectar, 1800,
            StarTHellForgeHeatingLiquids.IgniferousElixir, 2700,
            StarTHellForgeHeatingLiquids.BlazingPhlogiston, 3600,
            StarTHellForgeHeatingLiquids.CinderbrewSolvent, 1350,
            StarTHellForgeHeatingLiquids.CorefireNectar, 2250,
            StarTHellForgeHeatingLiquids.InfernumElixir, 3150,
            StarTHellForgeHeatingLiquids.HellfireEssence, 4050);

    private static final int HEAT_MAX = Collections.max(fluidsMap.values());

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        isWorking = false;
        startHeatLoss = true;
        temperatureChanged();
        if (getLevel() instanceof ServerLevel level && !flameLit) {
            flameLit = true;
            kindleGameTime = level.getGameTime();
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        isWorking = false;
        if (!(getLevel() instanceof ServerLevel level) || !isStructureLoaded()) return;
        temperature = baseTemperature;
        if (flameLit) {
            flameLit = false;
            gutterGameTime = level.getGameTime();
        }
    }

    private boolean isStructureLoaded() {
        var pos = getPos();
        var box = BoundingBox.fromCorners(
                pos.relative(relative(RelativeDirection.FRONT), profile.extentFront)
                        .relative(relative(RelativeDirection.DOWN), profile.extentDown)
                        .relative(relative(RelativeDirection.LEFT), profile.halfWidth),
                pos.relative(relative(RelativeDirection.BACK), profile.extentBack)
                        .relative(relative(RelativeDirection.UP), profile.extentUp)
                        .relative(relative(RelativeDirection.RIGHT), profile.halfWidth));
        return getLevel().hasChunksAt(box.minX(), box.minY(), box.minZ(), box.maxX(), box.maxY(), box.maxZ());
    }

    private Direction relative(RelativeDirection direction) {
        return direction.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
    }

    public Direction getFlameUp() {
        return relative(RelativeDirection.UP);
    }

    public Vec3 getFlameBase() {
        var center = Vec3.atCenterOf(getPos());
        var base = profile.base(center.x, center.y, center.z, relative(RelativeDirection.BACK).step(),
                getFlameUp().step());
        return new Vec3(base.x, base.y, base.z);
    }

    public String getCrucibleUIKey() {
        String uiKey = "ui.start_core.hellforge_crucible";
        if (baseTemperature > 0) {
            uiKey = "ui.start_core.fornaxs_crucible";
        }

        return uiKey;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        textList.add(
                Component.translatable(getCrucibleUIKey(), this.temperature));
    }

    /**
     * Retrieves the appropriate heating fluid for the Hellforge based on the
     * required temperature.
     * The method selects the fluid with the highest temperature cap that is still
     * equal to or greater than the input temperature.
     *
     * @param temperature The required temperature of the Hellforge.
     * @return The Material representing the heating fluid, or null if no fluid can
     *         meet the required temperature.
     */
    public static Material getHellforgeHeatingLiquid(int temperature) {
        Material selectedFluid = null;
        int smallestCapAboveTemperature = Integer.MAX_VALUE;

        // Iterate over all fluid entries
        for (Map.Entry<Material, Integer> entry : fluidsMap.entrySet()) {
            int fluidCap = entry.getValue();

            // Select the fluid if its cap is >= required temperature AND is the smallest
            // such cap found so far
            if (fluidCap >= temperature && fluidCap < smallestCapAboveTemperature) {
                smallestCapAboveTemperature = fluidCap;
                selectedFluid = entry.getKey();
            }
        }

        return selectedFluid; // May be null if no suitable fluid is found
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (isRemote()) return;
        displayHeat = HellFlameProfile.displayHeat(temperature, HEAT_MAX);
        tryTickSub = subscribeServerTick(tryTickSub, this::tryRemoveHeat);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void clientTick() {
        super.clientTick();
        HellFlameEmbers.tick(this);
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
    }

    protected void tryRemoveHeat() {
        if (getOffsetTimer() % 200 == 0 && this.startHeatLoss) {

            boolean machineActive = getRecipeLogic().isWorking();

            if (machineActive) {
                this.temperature = Math.max(this.temperature - baseTempLoss, baseTemperature);
            } else {
                this.temperature = Math.max(this.temperature - dormantTempLoss, baseTemperature);
            }

            this.temperatureChanged();
        }
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        boolean isWorking = super.beforeWorking(recipe);

        if (isWorking) {
            this.isWorking = true;
        }

        return isWorking;
    }

    private void temperatureChanged() {
        displayHeat = HellFlameProfile.displayHeat(temperature, HEAT_MAX);
        fluidsMap.forEach((key1, temperature) -> this.setIndicatorValue(
                "variadic.start_core.indicator.hellforge." + temperature.toString(),
                (int) Math.floor(redstonePercentageOfTemp(temperature))));
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        this.isWorking = false;
        GTRecipe lastRecipe = getRecipeLogic().getLastRecipe();

        List<Content> content = lastRecipe.getInputContents(GTRecipeCapabilities.FLUID);

        if (content.isEmpty()) return;

        if (content.get(0).getContent() instanceof FluidIngredient ingredient) {
            FluidStack ingredientFluid = ingredient.getStacks()[0];
            Material material = ChemicalHelper.getMaterial(ingredientFluid.getFluid());

            if (fluidsMap.containsKey(material)) {
                int maxHeat = fluidsMap.get(material);

                if (this.temperature < maxHeat) {

                    int addTemperature = ingredientFluid.getFluid().getFluidType().getTemperature() / 1_000_000;

                    int amountToAdd = (int) (double) (ingredientFluid.getAmount() / 1000);
                    int before = temperature;
                    this.temperature = Math.min(temperature + addTemperature * amountToAdd, maxHeat);
                    this.temperatureChanged();

                    if (temperature > before) {
                        heatBurstGameTime = getLevel().getGameTime();
                        heatBurst = HellFlameProfile.encodeBurst(temperature - before, material.getMaterialRGB());
                    }
                }
            }
        }
    }

    public double redstonePercentageOfTemp(double temperature) {
        return Math.min((this.temperature / temperature) * 15.0, 15.0);
    }

    @Override
    public List<RedstoneIndicatorRecord> getInitialIndicators() {
        return fluidsMap.values().stream().map(temperature -> {
            String temperatureString = temperature.toString();

            return new RedstoneIndicatorRecord(
                    "variadic.start_core.indicator.hellforge." + temperatureString,
                    Component.translatable("variadic.start_core.indicator.hellforge",
                            Component.literal(temperatureString + "MK").withStyle(ChatFormatting.RED)),
                    Component.translatable("variadic.start_core.description.hellforge", temperatureString)
                            .withStyle(ChatFormatting.GRAY),
                    (int) Math.floor(redstonePercentageOfTemp(temperature)),
                    temperature);
        }).toList();
    }
}
