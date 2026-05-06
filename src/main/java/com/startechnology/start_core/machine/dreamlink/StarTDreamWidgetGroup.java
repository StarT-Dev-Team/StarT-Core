package com.startechnology.start_core.machine.dreamlink;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import java.util.Objects;

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