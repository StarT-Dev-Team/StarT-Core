package com.startechnology.start_core.machine.gcrop;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;
import com.startechnology.start_core.api.gcrop.IClimateProvider;
import com.startechnology.start_core.api.gcrop.StarTClimateType;
import net.minecraft.MethodsReturnNonnullByDefault;
import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class StarTClimateHatchPartMachine extends TieredPartMachine implements IClimateProvider {

    @Getter
    private final Set<StarTClimateType> providedClimates;

    public StarTClimateHatchPartMachine(IMachineBlockEntity holder, int tier, StarTClimateType... climates) {
        super(holder, tier);
        this.providedClimates = Collections.unmodifiableSet(Set.of(climates));
    }
}
