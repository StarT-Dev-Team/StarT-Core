package com.startechnology.start_core.api.gcrop;

public class StarTGCropTrait {
    private String name;

    private String symbol;

    private Integer tier;

    private Integer frequency;

    public String getTraitName() {
        return name;
    }

    public String getTraitSymbol() {
        return symbol;
    }

    public Integer getTraitTier() {
        return tier;
    }

    public Integer getTraitFrequency() {
        return frequency;
    }

    public StarTGCropTrait(String name, String symbol, Integer tier, Integer frequency) {
        this.name = name;
        this.symbol = symbol;
        this.tier = tier;
        this.frequency = frequency;
    }
}
