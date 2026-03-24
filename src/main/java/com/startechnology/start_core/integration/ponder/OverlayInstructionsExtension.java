package com.startechnology.start_core.integration.ponder;

import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import net.createmod.ponder.foundation.instruction.FadeInOutInstruction;

@RemapPrefixForJS("startcore$")
public interface OverlayInstructionsExtension {

    @HideFromJS
    PonderSceneBuilder startcore$builder();

    default PonderCustomOverlayElement startcore$addElement(int ticks) {
        var element = new PonderCustomOverlayElement();
        element.setVisible(false);
        startcore$builder().addInstruction(new FadeInOutInstruction(ticks) {
            @Override
            protected void show(PonderScene scene) {
                scene.addElement(element);
                element.setVisible(true);
            }

            @Override
            protected void hide(PonderScene scene) {
                element.setVisible(false);
            }

            @Override
            protected void applyFade(PonderScene scene, float fade) {
                element.setFade(fade);
            }
        });

        return element;
    }

    default PonderCustomOverlayElement startcore$addElement() {
        var element = new PonderCustomOverlayElement();
        startcore$builder().addInstruction(ponderScene -> {
            ponderScene.addElement(element);
            element.setVisible(true);
        });

        return element;
    }

}
