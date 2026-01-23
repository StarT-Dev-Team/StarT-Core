package com.startechnology.start_core.item;

import static com.startechnology.start_core.StarTCore.START_REGISTRATE;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.client.renderer.cover.ICoverRenderer;
import com.gregtechceu.gtceu.client.renderer.cover.SimpleCoverRenderer;
import com.gregtechceu.gtceu.common.cover.voiding.AdvancedFluidVoidingCover;
import com.gregtechceu.gtceu.common.item.CoverPlaceBehavior;
import com.gregtechceu.gtceu.common.item.DataItemBehavior;
import com.gregtechceu.gtceu.common.item.TooltipBehavior;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.machine.dreamlink.StarTDreamLinkCover;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;

public class StarTDreamLinkCovers {
    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }


    public final static ItemEntry<ComponentItem> UV_DREAM_LINK_COVER = registerDreamLink(GTValues.UV, 2);
    public final static ItemEntry<ComponentItem> UHV_DREAM_LINK_COVER = registerDreamLink(GTValues.UHV, 2);
    public final static ItemEntry<ComponentItem> UEV_DREAM_LINK_COVER = registerDreamLink(GTValues.UEV, 2);
    public final static ItemEntry<ComponentItem> UIV_DREAM_LINK_COVER = registerDreamLink(GTValues.UIV, 2);
    public final static ItemEntry<ComponentItem> UXV_DREAM_LINK_COVER = registerDreamLink(GTValues.UXV, 2);
    public final static ItemEntry<ComponentItem> OPV_DREAM_LINK_COVER = registerDreamLink(GTValues.OpV, 2);
    public final static ItemEntry<ComponentItem> MAX_DREAM_LINK_COVER = registerDreamLink(GTValues.MAX, 2);


    public static ItemEntry<ComponentItem> registerDreamLink(int tier, int amperage) {
        var coverDefResLocation = StarTCore.resourceLocation(GTValues.VN[tier].toLowerCase() + "_" + amperage + "a_dream_link_cover");

        var definition = new CoverDefinition(coverDefResLocation, 
            (CoverDefinition coverDef, ICoverable coverHolder, Direction attachedSide) -> new StarTDreamLinkCover(coverDef, coverHolder, attachedSide, tier, amperage),
                () -> () -> new SimpleCoverRenderer(StarTCore.resourceLocation("block/dreamlink/" + GTValues.VN[tier].toLowerCase() + "_" + amperage + "a_energy_hatch/overlay_front"))
        );

        /* Unfreeze to allow adding */
        GTRegistries.COVERS.register(coverDefResLocation, definition);

        return START_REGISTRATE.item(GTValues.VN[tier].toLowerCase() + "_" + amperage + "a_dream_link_cover_item", ComponentItem::create)
            .lang(GTValues.VNF[tier] + "§r " + FormattingUtil.formatNumbers(amperage) + "§eA§r Dream-Link Energy Cover")
            .onRegister(attach(new CoverPlaceBehavior(definition)))
            .onRegister(attach(new TooltipBehavior(lines -> {
                lines.add(Component.translatable("start_core.dream_link.cover.tooltip", FormattingUtil.formatNumbers(GTValues.V[tier]), GTValues.VNF[tier]));
            })))
            .register();
    }

    

    public static void init() {}
}
