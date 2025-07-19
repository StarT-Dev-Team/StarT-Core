package com.startechnology.start_core.machine.dreamlink;

import java.util.Objects;

import javax.annotation.Nullable;

import com.lowdragmc.lowdraglib.gui.editor.configurator.IConfigurableWidget;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

public class StarTDreamWidgetGroup extends WidgetGroup {
        private Runnable removedCallback;

        public StarTDreamWidgetGroup(int x, int y, int width, int height, Runnable removedCallback) {
            super(x, y, width, height);
            this.removedCallback = removedCallback;
        }

        @Override
        protected void onPositionUpdate() {
            ModularUI ui = getGui();

            if (!Objects.isNull(ui)) 
                ui.registerCloseListener(removedCallback);

            super.onPositionUpdate();
        }
    }