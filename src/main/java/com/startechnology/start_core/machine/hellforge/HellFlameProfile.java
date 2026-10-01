package com.startechnology.start_core.machine.hellforge;

import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public enum HellFlameProfile {

    HELL_FORGE(5, 23.5f, 11, 2.3f, 1.3f, 0.30f, 1, 11, 2, 29, 6, new Vector3f(1, 0.42f, 0.08f),
            new Vector3f(0.5f, 0.05f, 0.015f), 1, 1),
    FIRE(8, 24.5f, 16, 3.3f, 0.45f, 0.35f, 2, 18, 2, 37, 10, new Vector3f(0.25f, 0.65f, 1),
            new Vector3f(0.24f, 0.08f, 0.6f), 0.7f, 0);

    private static final int FULL_BURST_GAIN = 250;
    private static final float MIN_BURST = 0.3f;
    private static final float BOUND_BOTTOM = 0.25f;

    public final int axisBack;
    public final float baseUp;
    public final float height;
    public final float radius;
    public final float baseRadius;
    public final float belly;
    public final float boundRadius;
    public final float boundHeight;
    public final float bakeReach;
    public final int extentFront, extentBack, extentDown, extentUp, halfWidth;
    public final Vector3fc theme, fringe;
    public final float coreScale;
    public final float smoke;

    HellFlameProfile(int axisBack, float baseUp, float height, float radius, float baseRadius, float belly,
                     int extentFront, int extentBack, int extentDown, int extentUp, int halfWidth, Vector3fc theme,
                     Vector3fc fringe, float coreScale, float smoke) {
        this.axisBack = axisBack;
        this.baseUp = baseUp;
        this.height = height;
        this.radius = radius;
        this.baseRadius = baseRadius;
        this.belly = belly;
        this.boundRadius = 1.2f * radius + 1;
        this.boundHeight = 1.62f * height + 1;
        this.bakeReach = 1.27f * radius + 0.3f;
        this.extentFront = extentFront;
        this.extentBack = extentBack;
        this.extentDown = extentDown;
        this.extentUp = extentUp;
        this.halfWidth = halfWidth;
        this.theme = theme;
        this.fringe = fringe;
        this.coreScale = coreScale;
        this.smoke = smoke;
    }

    public float radiusAt(float h) {
        if (h < belly) {
            float t = Math.max(h / belly, 0);
            return baseRadius + (radius - baseRadius) * t * t * (3 - 2 * t);
        }
        double t = (h - belly) / (1 - belly);
        return radius * (float) Math.pow(Math.max(1 - Math.pow(t, 1.6), 0), 0.7);
    }

    public Vector3d base(double x, double y, double z, Vector3fc back, Vector3fc up) {
        return new Vector3d(x + back.x() * axisBack + up.x() * baseUp, y + back.y() * axisBack + up.y() * baseUp,
                z + back.z() * axisBack + up.z() * baseUp);
    }

    public static Matrix3f basis(Vector3fc up, Matrix3f dest) {
        var across = Math.abs(up.x()) < 0.5f ? new Vector3f(1, 0, 0) : new Vector3f(0, 0, 1);
        var along = across.cross(up, new Vector3f());
        return dest.set(across.x, up.x(), along.x, across.y, up.y(), along.y, across.z, up.z(), along.z);
    }

    public Matrix4f boxMatrix(float x, float y, float z, Matrix3fc basis, Matrix4f dest) {
        float half = (boundHeight + BOUND_BOTTOM) / 2;
        return dest.set(basis).transpose3x3().setTranslation(x, y, z).translate(0, half - BOUND_BOTTOM, 0)
                .scale(boundRadius, half, boundRadius);
    }

    public static int displayHeat(int temperature, int heatMax) {
        return Math.round(255f * Math.min(Math.max(temperature, 0), heatMax) / heatMax);
    }

    public static int encodeBurst(int heatGain, int rgb) {
        float strength = Math.min(Math.max((float) heatGain / FULL_BURST_GAIN, MIN_BURST), 1);
        return Math.round(255 * strength) << 24 | (rgb & 0xFFFFFF);
    }

    public static float burstStrength(int burst) {
        return (burst >>> 24) / 255f;
    }
}
