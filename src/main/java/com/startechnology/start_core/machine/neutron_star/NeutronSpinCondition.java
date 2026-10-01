package com.startechnology.start_core.machine.neutron_star;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.startechnology.start_core.recipe.StarTRecipeConditions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import net.minecraft.network.chat.Component;

@NoArgsConstructor
public class NeutronSpinCondition extends RecipeCondition<NeutronSpinCondition> {

    public static final Codec<NeutronSpinCondition> CODEC = RecordCodecBuilder.create(instance -> RecipeCondition
            .isReverse(instance)
            .and(Codec.INT.fieldOf("min_spin").forGetter(condition -> condition.minSpin))
            .apply(instance, NeutronSpinCondition::new));

    @Getter
    private int minSpin;

    public NeutronSpinCondition(int minSpin) {
        this.minSpin = minSpin;
    }

    public NeutronSpinCondition(boolean isReverse, int minSpin) {
        super(isReverse);
        this.minSpin = minSpin;
    }

    @Override
    public RecipeConditionType<NeutronSpinCondition> getType() {
        return StarTRecipeConditions.NEUTRON_SPIN;
    }

    @Override
    public Component getTooltips() {
        return Component.translatable("start_core.recipe.condition.neutron_spin", minSpin,
                NeutronStarBalance.tierName(NeutronStarBalance.spinTier(minSpin)));
    }

    @Override
    protected boolean testCondition(@NotNull GTRecipe recipe, @NotNull RecipeLogic recipeLogic) {
        return recipeLogic.getMachine() instanceof NeutronStarForgeMachine forge && forge.canProcess(minSpin);
    }

    @Override
    public NeutronSpinCondition createTemplate() {
        return new NeutronSpinCondition();
    }
}
