package com.startechnology.start_core.utils;

public class StarTMath {
    
    public static int max(int... values) {
        int max = Integer.MIN_VALUE;
        for (int value : values) {
            if (value > max) max = value;
        }
        return max;
    }

    public static int min(int... values) {
        int min = Integer.MAX_VALUE;
        for (int value : values) {
            if (value < min) min = value;
        }
        return min;
    }

}
