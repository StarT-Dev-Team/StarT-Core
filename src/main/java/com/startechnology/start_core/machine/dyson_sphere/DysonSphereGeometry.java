package com.startechnology.start_core.machine.dyson_sphere;

public final class DysonSphereGeometry {

    public static final int PEDESTAL_RADIUS = 11;
    public static final int PEDESTAL_TOP = 8;
    public static final int CONTROLLER_Y = 1;
    public static final int PYLON_OFFSET = 8;
    public static final int PYLON_COUNT = 4;

    public static final int CENTER_HEIGHT = 62;

    public static final float STAR_RADIUS = 10;
    public static final float RED_GIANT_RADIUS = 14;

    public static final int RING_COUNT = 6;
    public static final float RING_BASE_RADIUS = 34;
    public static final float RING_SPACING = 3.5f;
    public static final float RING_WIDTH = 5;
    public static final float RING_THICKNESS = 0.8f;

    private DysonSphereGeometry() {}

    public static final float VISUAL_REACH = 100;

    public static float ringRadius(int index) {
        return RING_BASE_RADIUS + RING_SPACING * index;
    }
}
