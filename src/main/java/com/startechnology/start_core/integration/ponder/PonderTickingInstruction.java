package com.startechnology.start_core.integration.ponder;

import com.startechnology.start_core.integration.kjs.SafeConsumer;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true, chain = true)
public class PonderTickingInstruction extends TickingInstruction {

    @Setter
    private @Nullable SafeConsumer<PonderScene> onTick;

    @Setter
    private @Nullable SafeConsumer<PonderScene> onFirstTick;

    public PonderTickingInstruction(int duration) {
        this(false, duration);
    }

    public PonderTickingInstruction(boolean blocking, int duration) {
        super(blocking, duration);
    }

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        if (onTick != null) {
            onTick.safeAccept(scene);
        }
    }

    @Override
    protected void firstTick(PonderScene scene) {
        super.firstTick(scene);
        if (onFirstTick != null) {
            onFirstTick.safeAccept(scene);
        }
    }

}
