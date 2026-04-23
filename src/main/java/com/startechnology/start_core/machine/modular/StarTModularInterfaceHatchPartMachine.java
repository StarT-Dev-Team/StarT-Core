package com.startechnology.start_core.machine.modular;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyTooltip;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IWorkableMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.api.capability.IStarTModularSupportedModules;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.api.gui.StarTGuiTextures;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

public class StarTModularInterfaceHatchPartMachine extends TieredIOPartMachine implements IStarTModularSupportedModules {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTModularInterfaceHatchPartMachine.class,
            TieredIOPartMachine.MANAGED_FIELD_HOLDER);

    private List<ResourceLocation> supportedModules;

    protected long lastCheckTime;
    private static final int MODULAR_CHECK_DURATION = 100;

    @DescSynced
    @Persisted
    protected boolean isSupportedModule;
    protected TickableSubscription tickSubscription;

    @DescSynced
    @Getter
    protected ResourceLocation lastSupportedModuleName;

    @Setter
    @NotNull
    protected Predicate<ResourceLocation> extraSupportedCondition;

    @Setter
    @NotNull
    protected Consumer<IStarTModularSupportedModules> supportedMachineConsumer;

    @Persisted
    @DescSynced
    protected boolean isWaitingForLink = false;

    /* Optional extra modifier that can be added for moodules */
    @Setter
    protected RecipeModifier recipeModifier;

    /* Optional consumer that can be ran after working of the module */
    @Setter
    protected Consumer<IWorkableMultiController> moduleAfterWorkConsumer;

    /* Optional predicate to gate linked module ticks. */
    @Setter
    @NotNull
    protected Predicate<IWorkableMultiController> moduleTickPredicate;

    public StarTModularInterfaceHatchPartMachine(IMachineBlockEntity holder, IO io, int tier) {
        super(holder, tier, io);
        this.lastCheckTime = 0;
        this.supportedModules = null;
        this.extraSupportedCondition = id -> true;
        this.tickSubscription = null;
        this.recipeModifier = RecipeModifier.NO_MODIFIER;
        this.moduleTickPredicate = controller -> true;
        setupTickSubscription();
    }

    private void setupTickSubscription() {
        if (io == IO.IN && tickSubscription == null && getLevel() != null && !getLevel().isClientSide) {
            this.tickSubscription = subscribeServerTick(this.tickSubscription, this::updateSupportedStatus);
        }
    }

    @Override
    public GTRecipe modifyRecipe(GTRecipe recipe) {
        GTRecipe modifiedRecipe = super.modifyRecipe(recipe);

        /* Should be impossible to get a recipe without having a controller right? */
        if (this.controllers == null || this.controllers.size() == 0) {
            return modifiedRecipe;
        }

        return this.recipeModifier.applyModifier(this.controllers.first().self(), modifiedRecipe);
    }

    @Nullable
    public List<ResourceLocation> getSupportedModules() {
        if (supportedModules == null) return null;
        return Collections.unmodifiableList(supportedModules);
    }

    public void setSupportedModules(@NotNull Collection<ResourceLocation> modules) {
        // Guard against some trickery..
        if (this.io == IO.OUT) {
            this.supportedModules = new ArrayList<>(modules);
        } else {
            this.supportedModules = null;
        }
    }

    public void resetSupportedModule() {
        setUnsupported();

        /* Update neighbouring if this is an out interface */
        if (!this.isTerminal()) return;
        BlockPos offsetPos = getPos().relative(getFrontFacing());
        IStarTModularSupportedModules modulesSupportedContainer = StarTCapabilityHelper.getModularSupportedModules(getLevel(), offsetPos, getFrontFacing());
        if (modulesSupportedContainer != null) {
            modulesSupportedContainer.invalidateSupportedModule();
        }
    }

    public void setUnsupported() {
        this.isSupportedModule = false;
    }

    public void updateSupportedModule() {
        /* We need the controller of this machine to get the ID */
        SortedSet<IMultiController> controllers = getControllers();
        if (controllers == null || controllers.size() == 0) {
            setUnsupported();
            return;
        }

        /* Sharing is not supported */
        IMultiController controller = controllers.first() ;
        if (!(controller instanceof MultiblockControllerMachine)) {
            setUnsupported();
            return;
        }

        MultiblockControllerMachine multiblockControllerMachine = (MultiblockControllerMachine)(controller);
        ResourceLocation multiblockId = multiblockControllerMachine.getDefinition().getId();

        /* Get capability from in front to get if we are supported or not! */
        BlockPos offsetPos = getPos().relative(getFrontFacing());
        IStarTModularSupportedModules modulesSupportedContainer = StarTCapabilityHelper.getModularSupportedModules(getLevel(), offsetPos, getFrontFacing());
        if (modulesSupportedContainer == null) {
            setUnsupported();
            return;
        }

        /* Get supported state */
        boolean isSupported = modulesSupportedContainer.isSupportedMultiblockId(multiblockId, getPos());

        /* Changed to true state for supported */
        if (this.isSupportedModule == false && isSupported && modulesSupportedContainer.getOnSupportedConsumer() != null) {
            modulesSupportedContainer.getOnSupportedConsumer().accept(this);
        }

        this.isSupportedModule = isSupported;
    }

    public void updateSupportedStatus() {
        if (getLevel().isClientSide) return;

        if (!this.isFormed()) {
            setUnsupported();
            return;
        }

        if (getOffsetTimer() > (lastCheckTime + MODULAR_CHECK_DURATION) || lastCheckTime == 0) {
            updateSupportedModule();
            lastCheckTime = getOffsetTimer();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (tickSubscription == null) {
            setupTickSubscription();
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();

        if (tickSubscription != null) {
            tickSubscription.unsubscribe();
            tickSubscription = null;
        }
    }

    @Override
    public boolean canShared() {
        return false;
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

    @Override
    public boolean testRecipeTick(IWorkableMultiController controller) {
        if (!this.isSupportedModule) {
            controller.getRecipeLogic().setWaiting(
                    Component.translatable("modular.start_core.no_link").withStyle(ChatFormatting.GRAY)
            );

            return false;
        }

        if (!moduleTickPredicate.test(controller)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean beforeWorking(IWorkableMultiController controller) {
        if (!this.isSupportedModule) {
            return false;
        }

        return super.beforeWorking(controller);
    }


    protected void addComponentPanelText(List<Component> componentList) {
        if (this.isCurrentlyLinked()) {
            componentList.add(Component.translatable("modular.start_core.has_link").withStyle(ChatFormatting.GREEN));

            if (this.io == IO.OUT && lastSupportedModuleName != null) {
                componentList.add(Component.empty());
                componentList.add(Component.translatable("modular.start_core.linked_type").withStyle(ChatFormatting.GOLD));
                componentList.add(Component.translatable("block." + lastSupportedModuleName.getNamespace() + "." + lastSupportedModuleName.getPath()));
            }

        } else {
            componentList.add(Component.translatable("modular.start_core.no_link").withStyle(ChatFormatting.RED));

            if (!this.isFormed()) {
                componentList.add(Component.translatable("modular.start_core.not_formed")
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withHoverEvent(
                                new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("modular.start_core.not_formed_description"))
                        )));
            }
        }

        componentList.add(Component.empty());
        List<ResourceLocation> thisSupportedModules = this.getSupportedModules();

        if (this.io == IO.OUT && thisSupportedModules != null) {
            componentList.add(Component.translatable("modular.start_core.supported_list_title").withStyle(Style.EMPTY.withColor(ChatFormatting.GOLD).withHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("modular.start_core.supported_list_description"))
            )));

            for (ResourceLocation module : thisSupportedModules) {
                componentList.add(Component.translatable("block." + module.getNamespace() + "." + module.getPath()));
            }
        }
    }


    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 182 + 8, 117 + 8);
        group.addWidget(
                new DraggableScrollableWidgetGroup(4, 4, 182, 117).setBackground(GuiTextures.DISPLAY)
                        .addWidget(new LabelWidget(4, 5, this.getTitle()))
                        .addWidget(new ComponentPanelWidget(4, 20, this::addComponentPanelText))
        );

        group.setBackground(GuiTextures.BACKGROUND_INVERSE);
        return group;
    }


    @Override
    public boolean isSupportedMultiblockId(ResourceLocation id, BlockPos fromPos) {
        // Ensure its coming from the "front" block relatively
        boolean test = fromPos.compareTo(getPos().relative(getFrontFacing())) == 0
                && this.extraSupportedCondition.test(id)
                && (this.getSupportedModules() != null)
                && this.getSupportedModules().stream().anyMatch(otherId -> otherId.compareTo(id) == 0)
                && this.isFormed();

        /* We also want the out to display if it was a supported module */
        if (this.io == IO.OUT) {
            lastSupportedModuleName = id;
            lastCheckTime = getOffsetTimer();
            this.isSupportedModule = test;
        }

        return test;
    }

    public boolean isCurrentlyLinked() {
        if (this.io == IO.IN && this.isSupportedModule) {
            return true;
        }

        if (this.io == IO.OUT && this.isSupportedModule && getOffsetTimer() < (lastCheckTime + MODULAR_CHECK_DURATION + 5)) {
            return true;
        }

        return false;
    }

    public boolean isTerminal() {
        return this.io == IO.OUT;
    }

    @Override
    public void attachFancyTooltipsToController(IMultiController controller, TooltipsPanel tooltipsPanel) {
        attachTooltips(tooltipsPanel);
    }

    @Override
    public boolean afterWorking(IWorkableMultiController controller) {
        if (moduleAfterWorkConsumer != null) {
            moduleAfterWorkConsumer.accept(controller);
        }

        return super.afterWorking(controller);
    }

    @Override
    public void attachTooltips(TooltipsPanel tooltipsPanel) {
        super.attachTooltips(tooltipsPanel);
        tooltipsPanel.attachTooltips(
                new IFancyTooltip.Basic(
                        () -> StarTGuiTextures.MODULAR_INTERFACE_MISSING,
                        () -> {
                            var tooltips = new ArrayList<Component>();
                            tooltips.add(Component.translatable("modular.start_core.no_link").withStyle(ChatFormatting.RED));
                            return tooltips;
                        },
                        () -> !this.isTerminal() && !this.isCurrentlyLinked(),
                        () -> null
                )
        );
    }

    @Override
    public Consumer<IStarTModularSupportedModules> getOnSupportedConsumer() {
        return supportedMachineConsumer;
    }

    @Override
    public void invalidateSupportedModule() {
        setUnsupported();
    }
}
