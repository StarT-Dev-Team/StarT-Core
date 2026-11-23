package com.startechnology.start_core.recipe.logic;

import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType.ICustomRecipeLogic;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.startechnology.start_core.StarTConfig.DifficultyPreset;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.api.custom_tooltips.StarTCustomTooltipsManager;
import com.startechnology.start_core.api.dimensional_finder.DimensionalCoordinateTree;
import com.startechnology.start_core.api.dimensional_finder.DimensionalCoordinateTree.EndKind;
import com.startechnology.start_core.api.dimensional_finder.DimensionalCoordinateTree.Node;
import com.startechnology.start_core.api.dimensional_finder.DimensionalFinderDimension;
import com.startechnology.start_core.data.dimensional_finder.StarTDimensionalFinderData;
import com.startechnology.start_core.item.StarTItems;
import com.startechnology.start_core.item.components.StarTCoordinateCrystalBehaviour;
import com.startechnology.start_core.machine.dimensional_finder.DimensionalFinderMachine;
import com.startechnology.start_core.recipe.StarTRecipeTypes;
import com.startechnology.start_core.utils.StarTCustomLogicUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class DimensionalFinderLogic implements ICustomRecipeLogic {

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        if (!(holder instanceof DimensionalFinderMachine machine) || !machine.isFormed() || machine.getLevel() == null)
            return null;

        if (machine.isActiveRun()) {
            return createContinuationCycleRecipe(machine);
        }

        var itemHandlers = StarTCustomLogicUtils.getItemHandlers(holder);
        if (itemHandlers.isEmpty()) return null;

        var fluidHandlers = StarTCustomLogicUtils.getFluidHandlers(holder);
        List<ItemStack> allItems = StarTCustomLogicUtils.getAllItems(itemHandlers);
        List<FluidStack> allFluids = StarTCustomLogicUtils.getAllFluids(fluidHandlers);

        return createInitialCycleRecipe(machine, allItems, allFluids);
    }

    private @Nullable GTRecipe createInitialCycleRecipe(DimensionalFinderMachine machine, List<ItemStack> allItems,
                                                        List<FluidStack> allFluids) {
        ResourceLocation currentDim = machine.getLevel().dimension().location();
        List<DimensionalFinderDimension> candidates = StarTDimensionalFinderData.getDimensionsForSource(currentDim);
        if (candidates.isEmpty()) return null;

        ItemStack emptyCrystal = null;
        ItemStack failedCrystal = null;

        for (ItemStack stack : allItems) {
            if (!StarTCoordinateCrystalBehaviour.isCoordinateCrystal(stack)) continue;
            if (!StarTCoordinateCrystalBehaviour.isEngraved(stack)) {
                if (emptyCrystal == null) {
                    emptyCrystal = stack;
                }
            } else if (StarTCoordinateCrystalBehaviour.isFailedCrystal(stack)) {
                if (failedCrystal == null &&
                        isTargetingAny(StarTCoordinateCrystalBehaviour.getTargetDimensionId(stack), candidates)) {
                    failedCrystal = stack;
                }
            }
        }

        if (emptyCrystal == null) {
            machine.setRewindInvalid(false);
            machine.setRewindDeadEnd(false);
            return null;
        }

        DimensionalFinderDimension matchedDim = null;
        String failedCrystalDimId = (failedCrystal != null) ?
                StarTCoordinateCrystalBehaviour.getTargetDimensionId(failedCrystal) : null;

        for (DimensionalFinderDimension dimension : candidates) {
            if (failedCrystalDimId != null && !dimension.getDimensionId().toString().equals(failedCrystalDimId)) {
                continue;
            }
            if (matchesAdditionalInputs(dimension, allItems, allFluids)) {
                matchedDim = dimension;
                break;
            }
        }

        if (matchedDim == null) return null;

        // Pin the run to the preset the failed crystal was engraved with (legacy crystals use the current one),
        // so config changes never desync stored paths from the tree they were generated on.
        DifficultyPreset preset = (failedCrystal != null) ?
                StarTCoordinateCrystalBehaviour.getDifficulty(failedCrystal) : null;
        if (preset == null) preset = DimensionalFinderDimension.getCurrentPreset();

        DimensionalCoordinateTree tree = matchedDim.getTree(preset);

        int[] initialPath;
        int initialPrev;
        int initialExcl;

        if (failedCrystal != null) {
            int[] refPath = StarTCoordinateCrystalBehaviour.getCoordinates(failedCrystal);
            int k = machine.getRewindSteps();
            int idx = refPath.length - 1 - k;

            if (idx < 0 || idx + 1 >= refPath.length || tree.getNode(refPath[idx]) == null) {
                machine.setRewindInvalid(true);
                machine.setRewindDeadEnd(false);
                machine.setRewindInvalidPathLen(refPath.length);
                return null;
            }

            initialPath = Arrays.copyOfRange(refPath, 0, idx + 1);
            initialPrev = idx > 0 ? refPath[idx - 1] : -1;
            initialExcl = refPath[idx + 1];

            Node rewindNode = tree.getNode(refPath[idx]);
            if (tree.getAvailableNeighbors(rewindNode, initialPrev, initialExcl).isEmpty()) {
                machine.setRewindInvalid(false);
                machine.setRewindDeadEnd(true);
                return null;
            }

            machine.setRewindInvalid(false);
            machine.setRewindDeadEnd(false);
        } else {
            machine.setRewindInvalid(false);
            machine.setRewindDeadEnd(false);
            Node start = tree.pickRandomLeaf(StarTCore.RNG);
            initialPath = new int[] { start.getCoord() };
            initialPrev = -1;
            initialExcl = -1;
        }

        Node cur = tree.getNode(initialPath[initialPath.length - 1]);
        if (cur == null) {
            cur = tree.pickRandomLeaf(StarTCore.RNG);
            initialPath = new int[] { cur.getCoord() };
            initialPrev = -1;
            initialExcl = -1;
        }

        StepOutcome outcome = computeStepOutcome(tree, cur, initialPath, initialPrev, initialExcl);

        CompoundTag data = new CompoundTag();
        data.putBoolean("df_custom", true);
        data.putString("df_dim", matchedDim.getDimensionId().toString());
        data.putIntArray("df_initial_path", initialPath);
        data.putInt("df_initial_prev", initialPrev);
        data.putInt("df_initial_excl", initialExcl);
        data.putInt("df_next_coord", outcome.nextCoord);
        data.putBoolean("df_terminal", outcome.terminal);
        data.putString("df_difficulty", preset.name());

        GTRecipeBuilder builder = StarTRecipeTypes.DIMENSIONAL_FINDER_RECIPES
                .recipeBuilder("df_start_" + matchedDim.getDimensionId().getPath())
                .inputItems(SizedIngredient.create(StrictNBTIngredient.of(emptyCrystal.copyWithCount(1)), 1))
                .duration(matchedDim.getCycleDuration())
                .EUt(matchedDim.getEut())
                .CWUt(matchedDim.getCwut())
                .dimension(matchedDim.getRequiredDimension());

        if (failedCrystal != null) {
            builder.notConsumable(failedCrystal.copyWithCount(1));
        }

        for (ItemStack extraItem : matchedDim.getAdditionalItems()) {
            builder.inputItems(extraItem.copy());
        }
        for (FluidStack extraFluid : matchedDim.getAdditionalFluids()) {
            builder.inputFluids(extraFluid.copy());
        }

        if (outcome.terminal) {
            ItemStack outCrystal = StarTItems.COORDINATE_CRYSTAL.asStack(1);
            StarTCoordinateCrystalBehaviour.engraveFailure(outCrystal, matchedDim.getDimensionId(),
                    outcome.endPath, outcome.endKind, preset);
            builder.outputItems(outCrystal);
        }

        GTRecipe recipe = builder.buildRawRecipe();
        recipe.data.merge(data);
        return recipe;
    }

    private @Nullable GTRecipe createContinuationCycleRecipe(DimensionalFinderMachine machine) {
        DimensionalFinderDimension dim = StarTDimensionalFinderData.getDimension(machine.getActiveDimensionId());
        if (dim == null) {
            machine.resetRunState();
            return null;
        }

        DifficultyPreset runPreset = machine.getActiveDifficulty();
        if (runPreset == null) runPreset = DimensionalFinderDimension.getCurrentPreset();
        DimensionalCoordinateTree tree = dim.getTree(runPreset);

        if (machine.isIdentifyingShortest()) {
            ItemStack goalCrystal = StarTItems.COORDINATE_CRYSTAL.asStack(1);
            StarTCoordinateCrystalBehaviour.engraveDimension(goalCrystal, dim.getDimensionId(), tree.getAddress());

            CompoundTag data = new CompoundTag();
            data.putBoolean("df_custom", true);
            data.putInt("df_next_coord", -1);
            data.putBoolean("df_terminal", true);

            GTRecipe recipe = StarTRecipeTypes.DIMENSIONAL_FINDER_RECIPES
                    .recipeBuilder("df_shortest_" + dim.getDimensionId().getPath())
                    .outputItems(goalCrystal)
                    .duration(dim.getCycleDuration())
                    .EUt(dim.getEut())
                    .CWUt(dim.getCwut())
                    .dimension(dim.getRequiredDimension())
                    .buildRawRecipe();
            recipe.data.merge(data);
            return recipe;
        }

        int[] path = machine.getCurrentPath();
        if (path == null || path.length == 0) {
            machine.resetRunState();
            return null;
        }

        Node cur = tree.getNode(path[path.length - 1]);
        if (cur == null) {
            machine.resetRunState();
            return null;
        }

        StepOutcome outcome = computeStepOutcome(tree, cur, path, machine.getPrevCoord(), machine.getExcludedCoord());

        CompoundTag data = new CompoundTag();
        data.putBoolean("df_custom", true);
        data.putInt("df_next_coord", outcome.nextCoord);
        data.putBoolean("df_terminal", outcome.terminal);

        GTRecipeBuilder builder = StarTRecipeTypes.DIMENSIONAL_FINDER_RECIPES
                .recipeBuilder("df_step_" + dim.getDimensionId().getPath() + "_" + machine.getCurrentCycle())
                .duration(dim.getCycleDuration())
                .EUt(dim.getEut())
                .CWUt(dim.getCwut())
                .dimension(dim.getRequiredDimension());

        if (outcome.terminal) {
            ItemStack outCrystal = StarTItems.COORDINATE_CRYSTAL.asStack(1);
            StarTCoordinateCrystalBehaviour.engraveFailure(outCrystal, dim.getDimensionId(),
                    outcome.endPath, outcome.endKind, runPreset);
            builder.outputItems(outCrystal);
        }

        GTRecipe recipe = builder.buildRawRecipe();
        recipe.data.merge(data);
        return recipe;
    }

    private StepOutcome computeStepOutcome(DimensionalCoordinateTree tree, Node cur, int[] currentPath,
                                           int prevCoord, int excludedCoord) {
        List<Node> opts = tree.getAvailableNeighbors(cur, prevCoord, excludedCoord);

        if (opts.isEmpty()) {
            EndKind kind = cur.getKind() != null ? cur.getKind() : EndKind.RIFT_ECHO;
            return new StepOutcome(-1, true, currentPath, kind);
        }

        Node next = opts.get(StarTCore.RNG.nextInt(opts.size()));
        int[] nextPath = Arrays.copyOf(currentPath, currentPath.length + 1);
        nextPath[nextPath.length - 1] = next.getCoord();

        if (next == tree.getRoot()) {
            // Goal reached. Next cycle will be the extra "identifying the shortest address" cycle
            return new StepOutcome(next.getCoord(), false, nextPath, null);
        }

        List<Node> nextOpts = tree.getAvailableNeighbors(next, cur.getCoord(), -1);
        if (nextOpts.isEmpty()) {
            EndKind kind = next.getKind() != null ? next.getKind() : EndKind.RIFT_ECHO;
            return new StepOutcome(next.getCoord(), true, nextPath, kind);
        }

        return new StepOutcome(next.getCoord(), false, nextPath, null);
    }

    private static boolean isTargetingAny(@Nullable String dimensionId, List<DimensionalFinderDimension> dimensions) {
        if (dimensionId == null) return false;
        for (DimensionalFinderDimension dimension : dimensions) {
            if (dimension.getDimensionId().toString().equals(dimensionId)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesAdditionalInputs(DimensionalFinderDimension dim, List<ItemStack> allItems,
                                            List<FluidStack> allFluids) {
        for (ItemStack reqItem : dim.getAdditionalItems()) {
            int needed = reqItem.getCount();
            for (ItemStack stack : allItems) {
                if (ItemStack.isSameItemSameTags(stack, reqItem)) {
                    needed -= stack.getCount();
                    if (needed <= 0) break;
                }
            }
            if (needed > 0) return false;
        }

        for (FluidStack reqFluid : dim.getAdditionalFluids()) {
            int needed = reqFluid.getAmount();
            for (FluidStack stack : allFluids) {
                if (stack.isFluidEqual(reqFluid)) {
                    needed -= stack.getAmount();
                    if (needed <= 0) break;
                }
            }
            if (needed > 0) return false;
        }

        return true;
    }

    private record StepOutcome(int nextCoord, boolean terminal, int[] endPath, @Nullable EndKind endKind) {}

    @Override
    public void buildRepresentativeRecipes() {
        for (DimensionalFinderDimension dim : StarTDimensionalFinderData.getAllDimensions()) {
            String dimPath = dim.getDimensionId().getPath();

            // Fresh coordinate crystal scan recipe
            ItemStack emptyInput = StarTItems.COORDINATE_CRYSTAL.asStack(1);
            StarTCustomTooltipsManager.writeCustomTooltipsToItem(emptyInput.getOrCreateTag(),
                    "behaviour.start_core.coordinate_crystal.jei.empty_input",
                    "behaviour.start_core.coordinate_crystal.jei.fresh_start_info");

            ItemStack engravedOutput = StarTItems.COORDINATE_CRYSTAL.asStack(1);
            // dont show the actual address in xei
            StarTCoordinateCrystalBehaviour.engraveDimension(engravedOutput, dim.getDimensionId(), new int[0]);
            StarTCustomTooltipsManager.writeCustomTooltipsToItem(engravedOutput.getOrCreateTag(),
                    "behaviour.start_core.coordinate_crystal.jei.output_header",
                    "behaviour.start_core.coordinate_crystal.jei.output_cycle_info",
                    "behaviour.start_core.coordinate_crystal.jei.output_fail_info",
                    "behaviour.start_core.coordinate_crystal.jei.output_win_info");

            GTRecipeBuilder freshBuilder = StarTRecipeTypes.DIMENSIONAL_FINDER_RECIPES
                    .recipeBuilder("scan_" + dimPath)
                    .inputItems(emptyInput)
                    .outputItems(engravedOutput)
                    .duration(dim.getCycleDuration())
                    .EUt(dim.getEut())
                    .CWUt(dim.getCwut())
                    .dimension(dim.getRequiredDimension());

            for (ItemStack extraItem : dim.getAdditionalItems()) {
                freshBuilder.inputItems(extraItem.copy());
            }
            for (FluidStack extraFluid : dim.getAdditionalFluids()) {
                freshBuilder.inputFluids(extraFluid.copy());
            }

            StarTCustomLogicUtils.handleCustomRecipeLogicEMI(
                    StarTRecipeTypes.DIMENSIONAL_FINDER_RECIPES, freshBuilder.buildRawRecipe());

            // Rewind scan recipe (failed engraved crystal + empty crystal)
            ItemStack rewindEmptyInput = StarTItems.COORDINATE_CRYSTAL.asStack(1);
            StarTCustomTooltipsManager.writeCustomTooltipsToItem(rewindEmptyInput.getOrCreateTag(),
                    "behaviour.start_core.coordinate_crystal.jei.empty_input",
                    "behaviour.start_core.coordinate_crystal.jei.rewind_consumed_info");

            ItemStack failedRefInput = StarTItems.COORDINATE_CRYSTAL.asStack(1);
            StarTCoordinateCrystalBehaviour.engraveFailure(failedRefInput, dim.getDimensionId(),
                    new int[] { 12, 19, 7, 24 }, EndKind.VORTEX);
            StarTCustomTooltipsManager.writeCustomTooltipsToItem(failedRefInput.getOrCreateTag(),
                    "behaviour.start_core.coordinate_crystal.jei.rewind_ref_header",
                    "behaviour.start_core.coordinate_crystal.jei.rewind_ref_info1",
                    "behaviour.start_core.coordinate_crystal.jei.rewind_ref_info2");

            ItemStack rewindOutput = StarTItems.COORDINATE_CRYSTAL.asStack(1);
            StarTCoordinateCrystalBehaviour.engraveDimension(rewindOutput, dim.getDimensionId(), new int[0]);
            StarTCustomTooltipsManager.writeCustomTooltipsToItem(rewindOutput.getOrCreateTag(),
                    "behaviour.start_core.coordinate_crystal.jei.output_header",
                    "behaviour.start_core.coordinate_crystal.jei.output_cycle_info",
                    "behaviour.start_core.coordinate_crystal.jei.output_fail_info",
                    "behaviour.start_core.coordinate_crystal.jei.output_win_info");

            GTRecipeBuilder rewindBuilder = StarTRecipeTypes.DIMENSIONAL_FINDER_RECIPES
                    .recipeBuilder("rewind_" + dimPath)
                    .inputItems(rewindEmptyInput)
                    .notConsumable(failedRefInput)
                    .outputItems(rewindOutput)
                    .duration(dim.getCycleDuration())
                    .EUt(dim.getEut())
                    .CWUt(dim.getCwut())
                    .dimension(dim.getRequiredDimension());

            for (ItemStack extraItem : dim.getAdditionalItems()) {
                rewindBuilder.inputItems(extraItem.copy());
            }
            for (FluidStack extraFluid : dim.getAdditionalFluids()) {
                rewindBuilder.inputFluids(extraFluid.copy());
            }

            StarTCustomLogicUtils.handleCustomRecipeLogicEMI(
                    StarTRecipeTypes.DIMENSIONAL_FINDER_RECIPES, rewindBuilder.buildRawRecipe());
        }
    }
}
