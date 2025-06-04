package com.startechnology.start_core.api.capability;

import com.gregtechceu.gtceu.api.machine.feature.IDataStickInteractable;

public interface IStarTDreamLinkNetworkMachine extends IDataStickInteractable {
    String DEFAULT_NETWORK = "Untitled Network";

    /* Set the dream link network of this machine */
    public void setNetwork(String network);

    /* Get the network of this machine */
    public String getNetwork();
    
    /* Get if the machine is currently in the Dreaming (active) state */
    public boolean isDreaming();
}
