package com.startechnology.start_core.api;

import com.startechnology.start_core.api.reflector.FusionReflectorType;
import com.startechnology.start_core.block.FusionReflectorBlock;
import com.startechnology.start_core.block.VacuumPumpBlock;
import com.startechnology.start_core.api.vacuumpump.IVacuumPumpType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StarTAPI {

    public static final Map<FusionReflectorType, Supplier<FusionReflectorBlock>> FUSION_REFLECTORS = new HashMap<>();

    public static final Map<IVacuumPumpType, Supplier<VacuumPumpBlock>> VACUUM_PUMPS = new HashMap<>();

}
