package com.startechnology.start_core.integration.ponder;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public interface PonderEvents {

    EventGroup GROUP = EventGroup.of("Ponder");

    EventHandler REGISTRY = GROUP.client("registry", () -> PonderRegistryEventJS.class);
    EventHandler TAGS = GROUP.client("tags", () -> PonderItemTagEventJS.class);

}
