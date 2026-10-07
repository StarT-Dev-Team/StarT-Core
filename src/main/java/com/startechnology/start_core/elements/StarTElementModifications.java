package com.startechnology.start_core.elements;

import org.jetbrains.annotations.NotNull;

import com.gregtechceu.gtceu.api.data.chemical.Element;
import com.gregtechceu.gtceu.common.data.GTElements;

public class StarTElementModifications {

    public static void init() {
        setCounts(GTElements.Tr, 125, 198);
        setCounts(GTElements.Ke, 150, 251);
        setCounts(GTElements.Nq, 154, 252);
        setCounts(GTElements.Nq2, 154, 248);
        setCounts(GTElements.Nq1, 154, 254);
        setCounts(GTElements.Dr, 123, 112);
    }

    private static void setCounts(@NotNull Element element, long protons, long neutrons) {
        element.protons(protons);
        element.neutrons(neutrons);
    }
}
