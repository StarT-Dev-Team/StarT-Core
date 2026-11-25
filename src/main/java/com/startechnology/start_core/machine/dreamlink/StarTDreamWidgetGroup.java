package com.startechnology.start_core.machine.dreamlink;

import com.gregtechceu.gtceu.api.gui.util.UICloseHandler;
import com.lowdragmc.lowdraglib2.gui.ui.UIElement;
import com.lowdragmc.lowdraglib2.gui.ui.event.UIEvents;

public class StarTDreamWidgetGroup extends UIElement {

    private boolean closeHandlerRegistered;

    public StarTDreamWidgetGroup(Runnable removedCallback) {
        addEventListener(UIEvents.MUI_CHANGED, event -> {
            var modularUI = getModularUI();

            if (!closeHandlerRegistered && modularUI != null) {
                closeHandlerRegistered = true;
                UICloseHandler.onClose(modularUI.ui.rootElement, removedCallback);
            }
        });
    }
}
