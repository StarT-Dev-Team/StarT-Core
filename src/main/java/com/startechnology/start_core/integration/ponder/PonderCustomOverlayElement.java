package com.startechnology.start_core.integration.ponder;

import com.startechnology.start_core.integration.kjs.SafeConsumer;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.createmod.ponder.api.element.PonderElement;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.element.AnimatedOverlayElementBase;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@Accessors(fluent = true, chain = true)
@ParametersAreNonnullByDefault
public class PonderCustomOverlayElement extends AnimatedOverlayElementBase {

    @Setter
    @Nullable
    protected SafeConsumer<RenderContext> onRender = null;

    @Setter
    @Nullable
    protected SafeConsumer<ActionContext> onWhileSkipping = null;

    @Setter
    @Nullable
    protected SafeConsumer<ActionContext> onTick = null;

    @Setter
    @Nullable
    protected SafeConsumer<ActionContext> onReset = null;

    @Getter
    private int currentTick = 0;

    @Override
    public void whileSkipping(PonderScene scene) {
        super.whileSkipping(scene);
        if (onWhileSkipping != null) {
            onWhileSkipping.safeAccept(new ActionContext(this, scene));
        }
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        currentTick++;
        if (onTick != null) {
            onTick.safeAccept(new ActionContext(this, scene));
        }
    }

    @Override
    public void reset(PonderScene scene) {
        super.reset(scene);
        currentTick = 0;
        if (onReset != null) {
            onReset.safeAccept(new ActionContext(this, scene));
        }
    }

    @HideFromJS
    @Override
    public void render(PonderScene scene, PonderUI screen, GuiGraphics graphics, float partialTicks, float fade) {
        if (onRender != null) {
            onRender.safeAccept(new RenderContext(this, scene, screen, graphics, partialTicks, fade));
        }
    }

    public record RenderContext(PonderElement getElement, PonderScene getScene, PonderUI getScreen,
                                GuiGraphics getGraphics, float getPartialTicks, float getFade) {
    }

    public record ActionContext(PonderElement getElement, PonderScene getScene) {
    }

}
