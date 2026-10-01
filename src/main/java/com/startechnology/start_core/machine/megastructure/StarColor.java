package com.startechnology.start_core.machine.megastructure;

import java.util.Locale;
import java.util.OptionalInt;

public final class StarColor {

    private StarColor() {}

    public static OptionalInt parse(String text) {
        var value = text.trim();
        if (value.contains(",")) return parseComponents(value);

        if (value.startsWith("#")) {
            value = value.substring(1);
        } else if (value.startsWith("0x") || value.startsWith("0X")) {
            value = value.substring(2);
        }
        if (value.length() != 6 || !consistsOf(value, "0123456789abcdefABCDEF")) return OptionalInt.empty();
        return validated(Integer.parseInt(value, 16));
    }

    private static OptionalInt parseComponents(String value) {
        var parts = value.split(",", -1);
        if (parts.length != 3) return OptionalInt.empty();
        int rgb = 0;
        for (var part : parts) {
            var component = part.trim();
            if (component.isEmpty() || component.length() > 3 || !consistsOf(component, "0123456789")) {
                return OptionalInt.empty();
            }
            int channel = Integer.parseInt(component);
            if (channel > 255) return OptionalInt.empty();
            rgb = rgb << 8 | channel;
        }
        return validated(rgb);
    }

    private static boolean consistsOf(String value, String allowed) {
        for (int i = 0; i < value.length(); i++) {
            if (allowed.indexOf(value.charAt(i)) < 0) return false;
        }
        return true;
    }

    private static OptionalInt validated(int rgb) {
        return isValid(rgb) ? OptionalInt.of(rgb) : OptionalInt.empty();
    }

    public static boolean isValid(int rgb) {
        return rgb > 0 && rgb <= 0xFFFFFF;
    }

    public static String format(int rgb) {
        return String.format(Locale.ROOT, "#%06X", rgb & 0xFFFFFF);
    }
}
