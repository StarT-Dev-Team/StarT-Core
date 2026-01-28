package com.startechnology.start_core.machine.modular;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.MultiblockControllerMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.startechnology.start_core.api.capability.IStarTModularSupportedModules;
import com.startechnology.start_core.api.capability.StarTCapabilityHelper;

import net.minecraft.resources.ResourceLocation;

public class StarTModularInterfaceHatchPartMachine extends TieredIOPartMachine implements IStarTModularSupportedModules {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(StarTModularInterfaceHatchPartMachine.class,
        TieredIOPartMachine.MANAGED_FIELD_HOLDER);
    
    private List<ResourceLocation> supportedModules;
    
    protected long lastCheckTime;
    protected boolean isSupportedModule;

    public StarTModularInterfaceHatchPartMachine(IMachineBlockEntity holder, IO io, int tier) {
        super(holder, tier, io);
        this.lastCheckTime = 0;
        this.supportedModules = null;
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
        
        /* Get capability from in front to get if we are supported or not list ! */
        IStarTModularSupportedModules modulesSupportedContainer = StarTCapabilityHelper.getModularSupportedModules(getLevel(), getPos(), getFrontFacing());
        if (modulesSupportedContainer == null) return false;

        List<ResourceLocation> supportedModules = modulesSupportedContainer.getSupportedMultiblockIds();
        if (supportedModules == null || supportedModules.size() == 0) return false;

        return supportedModules.stream().anyMatch(otherMultiblockId -> multiblockId.compareTo(otherMultiblockId) == 0);
    }

    public void updateSupportedStatus() {
        this.isSupportedModule = checkSupportedModule();
    }

    @Override
    public void onLoad() {
        this.checkSupportedModule();
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
    public GTRecipe modifyRecipe(GTRecipe recipe) {
        if (getOffsetTimer() > (lastCheckTime + 1200) || lastCheckTime == 0) {
            this.updateSupportedStatus();
        }

        // cancel recipe by returning null if not marked as supported
        if (this.isSupportedModule) {
            return super.modifyRecipe(recipe);
        } else {
            return null;
        }
    }

    @Override
    @Nullable
    public List<ResourceLocation> getSupportedMultiblockIds() {
        return this.getSupportedModules();
    }
}
