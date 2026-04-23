package com.startechnology.start_core.machine.komaru.client;

import java.util.function.IntSupplier;

public interface ReplaceablePostPass {

    void startcore$replaceAuxAsset(String auxName, IntSupplier auxFramebuffer, int width, int height);

}
