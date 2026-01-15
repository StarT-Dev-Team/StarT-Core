package com.startechnology.start_core.api.capability;

import com.startechnology.start_core.api.dreamlink.IStarTCopyInteractable;

public interface IStarTDreamLinkNetworkMachine extends IStarTCopyInteractable {
    String DEFAULT_NETWORK = "Untitled Network";

    /* Set the dream link network of this machine */
    public void setNetwork(String network);

    /* Get the network of this machine */
    public String getNetwork();
    
    /* Get if the machine is currently in the Dreaming (active) state */
    public boolean isDreaming();
}
