package com.startechnology.start_core.mixin.compat;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.integration.modules.jeirei.EncodingHelper;
import appeng.menu.me.common.GridInventoryEntry;
import appeng.menu.me.common.MEStorageMenu;
import com.startechnology.start_core.StarTConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Mixin(value = EncodingHelper.class, remap = false)
public abstract class EncodingHelperMixin {

    @Unique
    private static List<AEItemKey> start$universals;

    /**
     * Ranks universal circuits first, so EMI/JEI transfers encode them wherever the recipe accepts the circuit tag
     */
    @Inject(method = "getIngredientPriorities", at = @At("RETURN"))
    private static void start$preferUniversalCircuits(MEStorageMenu menu, Comparator<GridInventoryEntry> comparator,
                                                      CallbackInfoReturnable<Map<AEKey, Integer>> cir) {
        if (!StarTConfig.INSTANCE.client.preferUniversalCircuits) return;
        Map<AEKey, Integer> priorities = cir.getReturnValue();
        for (AEItemKey key : start$universals()) {
            priorities.put(key, Integer.MAX_VALUE);
        }
    }

    @Unique
    private static List<AEItemKey> start$universals() {
        if (start$universals == null) {
            List<AEItemKey> found = new ArrayList<>();
            for (var e : ForgeRegistries.ITEMS.getEntries()) {
                var id = e.getKey().location();
                if (id.getNamespace().equals("kubejs") && id.getPath().endsWith("_universal_circuit")) {
                    found.add(AEItemKey.of(e.getValue()));
                }
            }
            start$universals = found;
        }
        return start$universals;
    }
}
