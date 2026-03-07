package com.startechnology.start_core.machine.modular;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyTooltip;
import com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IWorkableMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.gui.widget.DraggableScrollableWidgetGroup;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.api.capability.IStarTModularSupportedModules;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;
import com.startechnology.start_core.api.gui.StarTGuiTextures;
import com.startechnology.start_core.machine.dreamlink.StarTDreamWidgetGroup;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class StarTModularInterfaceHatchPartMachine extends TieredIOPartMachine implements IStarTModularSupportedModules {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTModularInterfaceHatchPartMachine.class,
        TieredIOPartMachine.MANAGED_FIELD_HOLDER);
    
    private List<ResourceLocation> supportedModules;
    
    protected long lastCheckTime;
    private static final int MODULAR_CHECK_DURATION = 100;
    
    @DescSynced
    protected boolean isSupportedModule;
    protected TickableSubscription tickSubscription;

    @DescSynced
    @Getter
    protected ResourceLocation lastSupportedModuleName;

    @Setter
    @NotNull
    protected Predicate<ResourceLocation> extraSupportedCondition;

    public StarTModularInterfaceHatchPartMachine(IMachineBlockEntity holder, IO io, int tier) {
        super(holder, tier, io);
        this.lastCheckTime = 0;
        this.supportedModules = null;
        this.extraSupportedCondition = id -> true;
        this.tickSubscription = null;
        setupTickSubscription();
    }

    private void setupTickSubscription() {
        if (io == IO.IN && tickSubscription == null && getLevel() != null && !getLevel().isClientSide) {
            this.tickSubscription = subscribeServerTick(this.tickSubscription, this::updateSupportedStatus);
        }
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

    public boolean checkSupportedModule() {
        /* We need the controller of this machine to get the ID */
        SortedSet<IMultiController> controllers = getControllers();
        if (controllers == null || controllers.size() == 0) return false;

        /* Sharing is not supported */
        IMultiController controller = controllers.first() ;
        if (!(controller instanceof MultiblockControllerMachine)) return false;

        MultiblockControllerMachine multiblockControllerMachine = (MultiblockControllerMachine)(controller);
        ResourceLocation multiblockId = multiblockControllerMachine.getDefinition().getId();
        
        /* Get capability from in front to get if we are supported or not! */
        BlockPos offsetPos = getPos().relative(getFrontFacing());
        IStarTModularSupportedModules modulesSupportedContainer = StarTCapabilityHelper.getModularSupportedModules(getLevel(), offsetPos, getFrontFacing());
        if (modulesSupportedContainer == null) return false;

        return modulesSupportedContainer.isSupportedMultiblockId(multiblockId, getPos());
    }

    public void updateSupportedStatus() {
        if (getLevel().isClientSide) return;

        if (!this.isFormed()) {
            this.isSupportedModule = false;
        }

        if (getOffsetTimer() > (lastCheckTime + MODULAR_CHECK_DURATION) || lastCheckTime == 0) {
            this.isSupportedModule = checkSupportedModule();
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
    public boolean onWorking(IWorkableMultiController controller) {
        if (!this.isSupportedModule) {
            return false;
        }

        return super.onWorking(controller);
    }

    @Override
    public boolean beforeWorking(IWorkableMultiController controller) {
        if (!this.isSupportedModule) {
            return false;
        }

        return super.beforeWorking(controller);
    }


    private void addComponentPanelText(List<Component> componentList) {
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
}
