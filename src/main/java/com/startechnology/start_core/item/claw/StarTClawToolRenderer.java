package com.startechnology.start_core.item.claw;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class StarTClawToolRenderer {

    private static final Map<String, ItemStack> TOOL_STACKS = new HashMap<>();

    private StarTClawToolRenderer() {}

    public static ItemStack getTool(ItemStack claw, StarTClawMode mode) {
        GTToolType toolType = mode.getToolType();

        if (toolType == null) return ItemStack.EMPTY;

        Material material = StarTClawOfLawItem.getToolMaterial(claw, mode);

        return TOOL_STACKS.computeIfAbsent(mode.getId() + "/" + material.getResourceLocation(), key -> {
            ItemStack tool = ToolHelper.get(toolType, material);

            return tool.isEmpty() ? ToolHelper.get(toolType, GTMaterials.Steel) : tool;
        });
    }
}
