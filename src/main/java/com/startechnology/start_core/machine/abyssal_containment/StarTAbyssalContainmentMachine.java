package com.startechnology.start_core.machine.abyssal_containment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.checkerframework.checker.units.qual.s;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.block.IFilterType;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.ICleanroomReceiver;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.SimpleGeneratorMachine;
import com.gregtechceu.gtceu.api.machine.feature.ICleanroomProvider;
import com.gregtechceu.gtceu.api.machine.feature.IDataInfoProvider;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMaintenanceMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMufflerMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.item.PortableScannerBehavior;
import com.gregtechceu.gtceu.common.machine.electric.HullMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.BedrockOreMinerMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.CleanroomMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FluidDrillMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.LargeMinerMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.generator.LargeCombustionEngineMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.generator.LargeTurbineMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.DiodePartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.primitive.CokeOvenMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.primitive.PrimitiveBlastFurnaceMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.primitive.PrimitivePumpMachine;
import com.gregtechceu.gtceu.common.machine.trait.CleanroomLogic;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.BlockInfo;
import com.startechnology.start_core.machine.StarTMachineUtils;
import com.startechnology.start_core.recipe.logic.AbyssalContainmentRoomLogic;

import dev.latvian.mods.kubejs.KubeJS;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.registries.ForgeRegistries;

public class StarTAbyssalContainmentMachine extends CleanroomMachine  {

    public StarTAbyssalContainmentMachine(IMachineBlockEntity metaTileEntityId) {
        super(metaTileEntityId);
    }

    private Material END_AIR = GTMaterials.EnderAir;
    private Material DRAGON_BREATH = GTMaterials.get("dragon_breath");

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTAbyssalContainmentMachine.class,
            CleanroomMachine.MANAGED_FIELD_HOLDER);

    public static final CleanroomType ABYSSAL_CONTAINMENT_ROOM = new CleanroomType("abyssal_containment_room",
            "start_core:abyssal_containment_room.display_name");

    private ItemStack talismanStack = new ItemStack(ForgeRegistries.ITEMS.getValue(KubeJS.id("end_talisman")));
    private CleanroomType cleanroomType;
    private MobEffect abyssalDrain;

    private boolean isSuppliedFluids;
    private AABB cleanroomBoundingBox;
    private int runningTimer = 0;

    public boolean isFluidsSupplied() {
        return isSuppliedFluids;
    }

    @Override
    public Set<CleanroomType> getTypes() {
        return this.cleanroomType == null ? Set.of() : Set.of(this.cleanroomType);
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new AbyssalContainmentRoomLogic(this);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        BlockPos controller = getPos();
        Direction front = getFrontFacing(); 
        Direction up = getUpwardsFacing();      

        BlockPos minWorld = RelativeDirection.offsetPos(controller, front, up, false,
                -9, 7, -14);
        BlockPos maxWorld = RelativeDirection.offsetPos(controller, front, up, false,
                +9, -7, 0);

        this.cleanroomBoundingBox = AABB.of(new BoundingBox(minWorld.getX(), minWorld.getY(), minWorld.getZ(), maxWorld.getX(), maxWorld.getY(), maxWorld.getZ()));

        // Store effect for later.
        this.abyssalDrain = ForgeRegistries.MOB_EFFECTS.getValue(KubeJS.id("abyssal_drain"));
    
        this.cleanroomType = ABYSSAL_CONTAINMENT_ROOM;
        this.getRecipeLogic().setDuration(4000);
    }

    @Override
    public void updateStructureDimensions() {
    }

    @Override
    public @NotNull BlockPattern getPattern() {
        return FactoryBlockPattern.start()
            .aisle("AAAAAAAAAAAAAAAAA", "A######A#A######A", "A####AA###AA####A", "A###A#######A###A", "A##A#########A##A", "A#A###########A#A", "A#A###########A#A", "AA=============AA", "A=============##A", "A=============##A", "A=============##A", "A=============##A", "A=============##A", "AA=============AA", "A#A###########A#A", "A#A###########A#A", "A##A#########A##A", "A###A#######A###A", "A####AA###AA####A", "A######A#A######A", "AAAAAAAAAAAAAAAAA") 
            .aisle("A######A#A######A", "#BBBBBBCDCBBBBBB#", "#BBBBCCCDCCCBBBB#", "#BBBCCCCDCCCCBBB#", "#BBCCCCEEECCCCBB#", "#BCCCEEEEEEECCCB#", "#BCCCEEFFFEECCCB#", "ACCCEEFFDFFEECCCA", "#CCCEEFFFFFEECCC#", "#CCEEFFFDFFFEECC#", "#DDEEFDFDFDFEEDD#", "#CCEEFFFDFFFEECC#", "#CCCEEFFFFFEECCC#", "ACCCEEFFDFFEECCCA", "#BCCCEEFFFEECCCB#", "#BCCCEEEEEEECCCB#", "#BBCCCCEEECCCCBB#", "#BBBCCCCDCCCCBBB#", "#BBBBCCCDCCCBBBB#", "#BBBBBBCDCBBBBBB#", "A######A#A######A") 
            .aisle("A####AA###AA####A", "#BBBBCCCDCCCBBBB#", "#B=============B#", "#B=============B#", "#B=============B#", "AC=============CA", "AC=============CA", "#C=============C#", "#C=============C#", "#C=============C#", "#D=============D#", "#C=============C#", "#C=============C#", "#C=============C#", "AC=============CA", "AC=============CA", "#B=============B#", "#B=============B#", "#B=============B#", "#BBBBCCCDCCCBBBB#", "A####AA###AA####A") 
            .aisle("A###A#######A###A", "#BBBCCCCDCCCCBBB#", "#B=============B#", "#B=============B#", "AC=============CA", "#C=============C#", "#C=============C#", "#C=============C#", "#C=============C#", "#E=============E#", "#E=============E#", "#E=============E#", "#C=============C#", "#C=============C#", "#C=============C#", "#C=============C#", "AC=============CA", "#B=============B#", "#B=============B#", "#BBBCCCCDCCCCBBB#", "A###A#######A###A") 
            .aisle("A##A#########A##A", "#BBCCCCEEECCCCBB#", "#B=============B#", "AC=============CA", "#C=============C#", "#C=============C#", "#C=============C#", "#E=============E#", "#E=============E#", "#E=============E#", "#E=============E#", "#E=============E#", "#E=============E#", "#E=============E#", "#C=============C#", "#C=============C#", "#C=============C#", "AC=============CA", "#B=============B#", "#BBCCCCEEECCCCBB#", "A##A#########A##A") 
            .aisle("A#A###########A#A", "#BCCCEEEEEEECCCB#", "AC=============CA", "#C=============C#", "#C=============C#", "#E=============E#", "#E=============E#", "#E=============E#", "#E=============E#", "#F=============F#", "#F=============F#", "#F=============F#", "#E=============E#", "#E=============E#", "#E=============E#", "#E=============E#", "#C=============C#", "#C=============C#", "AC=============CA", "#BCCCEEEEEEECCCB#", "A#A###########A#A") 
            .aisle("A#A###########A#A", "#BCCCEEFFFEECCCB#", "AC=============CA", "#C=============C#", "#C=============C#", "#E=============E#", "#E=============E#", "#F=============F#", "#F=============F#", "#F=============F#", "#D=============D#", "#F=============F#", "#F=============F#", "#F=============F#", "#E=============E#", "#E=============E#", "#C=============C#", "#C=============C#", "AC=============CA", "#BCCCEEFFFEECCCB#", "A#A###########A#A") 
            .aisle("AA#############AA", "ACCCEEFFFFFEECCCA", "#C=============C#", "#C=============C#", "#E=============E#", "#E=============E#", "#F=============F#", "#F=============F#", "#F=============F#", "#F=============F#", "#F=============F#", "#F=============F#", "#F=============F#", "#F=============F#", "#F=============F#", "#E=============E#", "#E=============E#", "#C=============C#", "#C=============C#", "ACCCEEFFFFFEECCCA", "AA#############AA") 
            .aisle("A###############A", "#DDDEEFFDFFEEDDD#", "#D=============D#", "#D=============D#", "#E=============E#", "#E=============E#", "#F=============F#", "#D=============D#", "#F=============F#", "#D=============D#", "#D=============D#", "#D=============D#", "#F=============F#", "#D=============D#", "#F=============F#", "#E=============E#", "#E=============E#", "#D=============D#", "#D=============D#", "#DDDEEFFDFFEEDDD#", "A###############A") 
            .aisle("AA#############AA", "ACCCEEFFFFFEECCCA", "#C=============C#", "#C=============C#", "#E=============E#", "#E=============E#", "#F=============F#", "#F=============F#", "#F=============F#", "#F=============F#", "#F=============F#", "#F=============F#", "#F=============F#", "#F=============F#", "#F=============F#", "#E=============E#", "#E=============E#", "#C=============C#", "#C=============C#", "ACCCEEFFFFFEECCCA", "AA#############AA") 
            .aisle("A#A###########A#A", "#BCCCEEFFFEECCCB#", "AC=============CA", "#C=============C#", "#C=============C#", "#E=============E#", "#E=============E#", "#F=============F#", "#F=============F#", "#F=============F#", "#D=============D#", "#F=============F#", "#F=============F#", "#F=============F#", "#E=============E#", "#E=============E#", "#C=============C#", "#C=============C#", "AC=============CA", "#BCCCEEFFFEECCCB#", "A#A###########A#A") 
            .aisle("A#A###########A#A", "#BCCCEEEEEEECCCB#", "AC=============CA", "#C=============C#", "#C=============C#", "#E=============E#", "#E=============E#", "#E=============E#", "#E=============E#", "#F=============F#", "#F=============F#", "#F=============F#", "#E=============E#", "#E=============E#", "#E=============E#", "#E=============E#", "#C=============C#", "#C=============C#", "AC=============CA", "#BCCCEEEEEEECCCB#", "A#A###########A#A") 
            .aisle("A##A#########A##A", "#BBCCCCEEECCCCBB#", "#B=============B#", "AC=============CA", "#C=============C#", "#C=============C#", "#C=============C#", "#E=============E#", "#E=============E#", "#E=============E#", "#E=============E#", "#E=============E#", "#E=============E#", "#E=============E#", "#C=============C#", "#C=============C#", "#C=============C#", "AC=============CA", "#B=============B#", "#BBCCCCEEECCCCBB#", "A##A#########A##A") 
            .aisle("A###A#######A###A", "#BBBCCCCDCCCCBBB#", "#B=============B#", "#B=============B#", "AC=============CA", "#C=============C#", "#C=============C#", "#C=============C#", "#C=============C#", "#E=============E#", "#E=============E#", "#E=============E#", "#C=============C#", "#C=============C#", "#C=============C#", "#C=============C#", "AC=============CA", "#B=============B#", "#B=============B#", "#BBBCCCCDCCCCBBB#", "A###A#######A###A") 
            .aisle("A####AA###AA####A", "#BBBBCCCDCCCBBBB#", "#B=============B#", "#B=============B#", "#B=============B#", "AC=============CA", "AC=============CA", "#C=============C#", "#C=============C#", "#C=============C#", "#D=============D#", "#C=============C#", "#C=============C#", "#C=============C#", "AC=============CA", "AC=============CA", "#B=============B#", "#B=============B#", "#B=============B#", "#BBBBCCCDCCCBBBB#", "A####AA###AA####A") 
            .aisle("A######A#A######A", "#BBBBBBCDCBBBBBB#", "#BBBBCCCDCCCBBBB#", "#BBBCCCCDCCCCBBB#", "#BBCCCCEEECCCCBB#", "#BCCCEEEEEEECCCB#", "#BCCCEEFFFEECCCB#", "ACCCEEFFDFFEECCCA", "#CCCEEFFFFFEECCC#", "#CCEEFFFDFFFEECC#", "#DDEEFDF@FDFEEDD#", "#CCEEFFFDFFFEECC#", "#CCCEEFFFFFEECCC#", "ACCCEEFFDFFEECCCA", "#BCCCEEFFFEECCCB#", "#BCCCEEEEEEECCCB#", "#BBCCCCEEECCCCBB#", "#BBBCCCCDCCCCBBB#", "#BBBBCCCDCCCBBBB#", "#BBBBBBCDCBBBBBB#", "A######A#A######A") 
            .aisle("AAAAAAAAAAAAAAAAA", "A######A#A######A", "A####AA###AA####A", "A###A#######A###A", "A##A#########A##A", "A#A###########A#A", "A#A###########A#A", "AA=============AA", "A=============##A", "A=============##A", "A=============##A", "A=============##A", "A=============##A", "AA=============AA", "A#A###########A#A", "A#A###########A#A", "A##A#########A##A", "A###A#######A###A", "A####AA###AA####A", "A######A#A######A", "AAAAAAAAAAAAAAAAA") 
            .where("A",  Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTCEuAPI.materialManager.getMaterial("abyssal_alloy"))))
            .where("#", Predicates.any())
            .where("=", innerPredicate())
            .where("B", Predicates.blocks(StarTMachineUtils.getKjsBlock("draneko_casing")))
            .where("C", Predicates.blocks(GCYMBlocks.CASING_ATOMIC.get()))
            .where("D", Predicates.blocks(StarTMachineUtils.getKjsBlock("draco_ware_casing"))
                .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMaxGlobalLimited(2))
                .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2))
                .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
            .where("E", Predicates.blocks(StarTMachineUtils.getKjsBlock("abyssal_inductor_hull")))
            .where("F", Predicates.blocks(StarTMachineUtils.getKjsBlock("draco_resilient_fusion_glass")))
            .where("@",Predicates.controller(Predicates.blocks(this.getDefinition().get())))
            .build();
    }

    @Override
    public boolean shouldAddPartToController(IMultiPart part) {
       return true;
    }

    private GTRecipe getAbyssalContainmentRecipe() {
        return  GTRecipeBuilder.ofRaw()
            .inputFluids(END_AIR.getFluid(100000))
            .inputFluids(DRAGON_BREATH.getFluid(50))
            .buildRawRecipe();
    }

    private static final String endArmourId(String pieceName) {
        return KubeJS.id("end_" + pieceName).toString();
    }

    @Override
    public boolean onWorking() {
        boolean value = super.onWorking();

        // check every 3.6s 1000 times = 1hr
        if (runningTimer % 72 == 0) {
            // passive boosting recipe.
            GTRecipe abyssalContainmentRecipe = getAbyssalContainmentRecipe();
            this.isSuppliedFluids = abyssalContainmentRecipe.matchRecipe(this).isSuccess() &&
                    abyssalContainmentRecipe.handleRecipeIO(IO.IN, this, this.recipeLogic.getChanceCaches());

            List<Player> playersInside =  this.getLevel().getEntitiesOfClass(Player.class, this.cleanroomBoundingBox);

            // Give all players inside the abyssal drain effect
            playersInside.forEach(player -> {
                // KJS methods to match kjs better.. ULTRA YOU BETTER NOT CHANGE THIS OR I WILL
                // violence...
                /*String headId = player.kjs$getHeadArmorItem().kjs$getId();
                String chestId = player.kjs$getChestArmorItem().kjs$getId();
                String legsId = player.kjs$getLegsArmorItem().kjs$getId();
                String feetId = player.kjs$getFeetArmorItem().kjs$getId();

                // Armour check.
                if  (
                    endArmourId("helmet").equals(headId) && 
                    endArmourId("chestplate").equals(chestId) && 
                    endArmourId("leggings").equals(legsId) &&
                    endArmourId("boots").equals(feetId)  
                ) {
                    player.removeEffect(abyssalDrain);
                    return;
                }

                // Talisman check
                if (player.getInventory().contains(talismanStack)) {
                    player.removeEffect(abyssalDrain);
                    return;
                }

                player.addEffect(new MobEffectInstance(abyssalDrain, 20 * 30));*/
            });
        }

        runningTimer++;
        if (runningTimer > 72000) runningTimer %= 72000; // reset once every hour of running

        return value;
    }

    
    /////////////////////////////////
    /// Initialisation
    
    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    
    // gui stuff
    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);

        if (isFormed()) {

            if (!this.isSuppliedFluids) {
                textList.add(Component.translatable("start_core.abyssal_containment_room.not_provided_fluids").withStyle(ChatFormatting.RED));
            } else {
                 textList.add(Component.translatable("start_core.abyssal_containment_room.provided_fluids").withStyle(ChatFormatting.GREEN));
            }
        }
    }

    public static void init() {
    }
}
