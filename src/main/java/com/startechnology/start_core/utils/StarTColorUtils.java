package com.startechnology.start_core.utils;

public class StarTColorUtils {

    /**
     * Generate a vibrant color for a thread based on its index
     * 
     * @param threadIndex The zero-based thread index
     * @return ARGB color integer (0xAARRGGBB format)
     */
    public static int generateThreadColor(int threadIndex) {
        float goldenRatio = 0.618033988749895f;
        float hue = ((threadIndex + 1) * goldenRatio) % 1.0f;

        float saturation = 0.85f;
        float value = 0.85f;

        int rgb = hsvToRgb(hue, saturation, value);

        // Add alpha channel
        return 0xFF000000 | rgb;
    }

    /**
     * Convert HSV color to RGB
     * 
     * @param h Hue [0.0, 1.0]
     * @param s Saturation [0.0, 1.0]
     * @param v Value [0.0, 1.0]
     * @return RGB color as integer (without alpha)
     */
    public static int hsvToRgb(float h, float s, float v) {
        int hi = (int) (h * 6);
        float f = h * 6 - hi;
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);

        float r, g, b;
        switch (hi % 6) {
            case 0:
                r = v;
                g = t;
                b = p;
                break;
            case 1:
                r = q;
                g = v;
                b = p;
                break;
            case 2:
                r = p;
                g = v;
                b = t;
                break;
            case 3:
                r = p;
                g = q;
                b = v;
                break;
            case 4:
                r = t;
                g = p;
                b = v;
                break;
            case 5:
                r = v;
                g = p;
                b = q;
                break;
            default:
                r = g = b = 0;
                break;
        }

        int red = (int) (r * 255);
        int green = (int) (g * 255);
        int blue = (int) (b * 255);

        return (red << 16) | (green << 8) | blue;
    }
}
