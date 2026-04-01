package com.startechnology.start_core.integration.kjs;

import com.startechnology.start_core.integration.ponder.PonderErrorHelper;
import net.minecraft.client.Minecraft;

@FunctionalInterface
public interface SafeConsumer<T> {

    void accept(T t);

    default void safeAccept(T t) {
        try {
            accept(t);
        } catch (Exception e) {
            PonderErrorHelper.reportJsPonderError(e);
            Minecraft.getInstance().setScreen(null);
        }
    }

}
