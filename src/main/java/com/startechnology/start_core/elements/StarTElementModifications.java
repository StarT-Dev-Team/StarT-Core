package com.startechnology.start_core.elements;

import com.gregtechceu.gtceu.api.data.chemical.Element;
import com.gregtechceu.gtceu.api.registry.GTRegistries;

public class StarTElementModifications {

    public static void init() {
        setCounts("Tritanium", 125, 198);
        setCounts("Trinium", 150, 251);
        setCounts("Naquadah", 154, 252);
        setCounts("Naquadria", 154, 248);
        setCounts("NaquadahEnriched", 154, 254);
        setCounts("Duranium", 123, 112);
    }

    private static void setCounts(String name, long protons, long neutrons) {
        Element element = GTRegistries.ELEMENTS.get(name);
        if (element != null) {
            element.protons(protons);
            element.neutrons(neutrons);
        }
    }
}
