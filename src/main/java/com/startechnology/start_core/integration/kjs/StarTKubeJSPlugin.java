package com.startechnology.start_core.integration.kjs;

import com.startechnology.start_core.integration.ponder.PonderEvents;
import com.startechnology.start_core.integration.ponder.BlockStateFunction;
import com.startechnology.start_core.integration.ponder.PonderJSUtils;
import com.startechnology.start_core.integration.ponder.PonderTickingInstruction;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.rhino.util.wrap.TypeWrappers;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.foundation.PonderTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class StarTKubeJSPlugin extends KubeJSPlugin {

    public static ResourceLocation getKubeId(String name) {
        return new ResourceLocation(name.contains(":") ? name : KubeJS.MOD_ID + ":" + name);
    }

    @Override
    public void init() {
        super.init();
        RegistryInfo.BLOCK.addType("gtceu:fusion_reflector", FusionReflectorBlockBuilder.class, FusionReflectorBlockBuilder::new);
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        if (event.getType().isClient()) {
            event.add("PonderPalette", PonderPalette.class);
            event.add("PonderPointing", Pointing.class);
            event.add("PonderTickingInstruction", PonderTickingInstruction.class);
        }
    }

    @Override
    public void registerEvents() {
        PonderEvents.GROUP.register();
    }

    @Override
    public void registerTypeWrappers(ScriptType type, TypeWrappers typeWrappers) {
        if (type == ScriptType.CLIENT) {
            typeWrappers.registerSimple(Selection.class, PonderJSUtils::convertSelection);
            typeWrappers.registerSimple(PonderTag.class, PonderJSUtils::convertPonderTag);
            typeWrappers.registerSimple(BlockState.class, PonderJSUtils::convertBlockStateOf);
            typeWrappers.register(BlockStateFunction.class, BlockStateFunction::of);
        }
    }
}
