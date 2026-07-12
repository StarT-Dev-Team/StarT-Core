package com.startechnology.start_core.api.gcrop;

public class StarTGCropTrait {
    private String name;

    private String symbol;

    private Integer tier;

    public String getTraitName() {
        return name;
    }

    public String getTraitSymbol() {
        return symbol;
    }

    public Integer getTraitTier() {
        return tier;
    }

    public StarTGCropTrait(String name, String symbol, Integer tier) {
        this.name = name;
        this.symbol = symbol;
        this.tier = tier;
    }
}
