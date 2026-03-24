package com.startechnology.start_core.integration.ponder;

import com.startechnology.start_core.integration.kjs.SafeConsumer;
import dev.latvian.mods.rhino.util.HideFromJS;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.createmod.ponder.api.element.PonderElement;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.element.AnimatedSceneElementBase;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@Accessors(fluent = true, chain = true)
@ParametersAreNonnullByDefault
public class PonderCustomSceneElement extends AnimatedSceneElementBase {

    @Setter
    @Nullable
    protected SafeConsumer<RenderContext> onRenderFirst = null;

    @Setter
    @Nullable
    protected SafeConsumer<LayerRenderContext> onRenderWorldLayer = null;

    @Setter
    @Nullable
    protected SafeConsumer<RenderContext> onRenderLast = null;

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
    public void renderFirst(PonderLevel world, MultiBufferSource buffer, GuiGraphics graphics, float fade, float pt) {
        super.renderFirst(world, buffer, graphics, fade, pt);
        if (onRenderFirst != null) {
            var ctx = new RenderContext(this, world, buffer, graphics, pt, fade);
            onRenderFirst.safeAccept(ctx);
        }
    }

    @HideFromJS
    @Override
    public void renderLayer(PonderLevel world, MultiBufferSource buffer, RenderType type, GuiGraphics graphics, float fade, float pt) {
        super.renderLayer(world, buffer, type, graphics, fade, pt);
        if (onRenderWorldLayer != null) {
            var ctx = new LayerRenderContext(this, world, buffer, type, graphics, pt, fade);
            onRenderWorldLayer.safeAccept(ctx);
        }
    }

    @HideFromJS
    @Override
    public void renderLast(PonderLevel world, MultiBufferSource buffer, GuiGraphics graphics, float fade, float pt) {
        super.renderLast(world, buffer, graphics, fade, pt);
        if (onRenderLast != null) {
            var ctx = new RenderContext(this, world, buffer, graphics, pt, fade);
            onRenderLast.safeAccept(ctx);
        }
    }

    public record LayerRenderContext(PonderElement getElement, PonderLevel getWorld, MultiBufferSource getBuffer,
                         RenderType getType, GuiGraphics getGraphics, float getPartialTicks, float getFade) {
    }

    public record RenderContext(PonderElement getElement, PonderLevel getWorld, MultiBufferSource getBuffer,
                         GuiGraphics getGraphics, float getPartialTicks, float getFade) {
    }

    public record ActionContext(PonderElement getElement, PonderScene getScene) {
    }

}
