package com.startechnology.start_core.machine.dimensional_destabiliser;

public final class DestabiliserRift {

    public static final int AXIS_BACK = 6;
    public static final int CENTER_UP = 6;
    public static final float HALF_HEIGHT = 3.5f;
    public static final float HALF_WIDTH = 0.9f;
    public static final float LENS_REACH = 2.2f;
    public static final float BOUND_RADIUS = 2.5f;
    public static final float BOUND_HALF_HEIGHT = 4.5f;

    public static final int EXTENT_FRONT = 0;
    public static final int EXTENT_BACK = 12;
    public static final int EXTENT_DOWN = 0;
    public static final int EXTENT_UP = 13;
    public static final int EXTENT_HALF_WIDTH = 6;

    public static final int PROGRESS_STEPS = 16;
    public static final int SHORT_RECIPE_TICKS = 32;
    public static final int PULSE_MIN_INTERVAL = 20;

    private DestabiliserRift() {}

    public static int displayProgress(int progress, int duration) {
        if (duration < SHORT_RECIPE_TICKS) return PROGRESS_STEPS;
        int steps = Math.round(PROGRESS_STEPS * (float) progress / duration);
        return Math.max(0, Math.min(PROGRESS_STEPS, steps));
    }

    public static boolean completed(int progress, int duration) {
        return duration > 0 && progress >= duration;
    }

    public static boolean pulseAllowed(long gameTime, long lastPulseGameTime) {
        return gameTime - lastPulseGameTime >= PULSE_MIN_INTERVAL;
    }

    public static double centreX(int controllerX, int frontStepX) {
        return controllerX + 0.5 - frontStepX * AXIS_BACK;
    }

    public static double centreY(int controllerY) {
        return controllerY + 0.5 + CENTER_UP;
    }

    public static double centreZ(int controllerZ, int frontStepZ) {
        return controllerZ + 0.5 - frontStepZ * AXIS_BACK;
    }
}
