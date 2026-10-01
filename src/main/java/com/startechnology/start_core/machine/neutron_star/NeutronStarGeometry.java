package com.startechnology.start_core.machine.neutron_star;

public final class NeutronStarGeometry {

    public static final float STAR_RADIUS = 4;
    public static final float SCALE = STAR_RADIUS / 0.2f;
    public static final float MAGNETOSPHERE_REACH = 0.62f * SCALE;
    public static final float HALO_BOUND = 1.2f;

    public static final float JET_TILT = 0.2f;
    public static final float JET_TILT_MAX = 0.25f;

    public static final int COLLECTOR_DISTANCE = 28;
    public static final int DISH_RADIUS = 10;
    public static final int DISH_DEPTH = 3;

    public static final int BASE_RADIUS = 12;
    public static final int CONTROLLER_Y = 1;
    public static final int PYLON_RADIUS = 16;
    public static final int PYLON_OFFSET = Math.round(PYLON_RADIUS / (float) Math.sqrt(2));

    public static final int BOTTOM_DISH_SURFACE = 5;
    public static final int CENTER_HEIGHT = BOTTOM_DISH_SURFACE + COLLECTOR_DISTANCE;
    public static final int TOP_DISH_SURFACE = CENTER_HEIGHT + COLLECTOR_DISTANCE;
    public static final int TOP = TOP_DISH_SURFACE + 1;
    public static final int HALF_WIDTH = PYLON_OFFSET + 1;

    public static final float SHELL_REACH = 2.2f;

    public static final float HORIZONTAL_REACH = Math.max(Math.max(HALO_BOUND, SHELL_REACH) * SCALE, PYLON_RADIUS + 1);
    public static final float VERTICAL_REACH = Math.max(COLLECTOR_DISTANCE + 3, SHELL_REACH * SCALE);

    private NeutronStarGeometry() {}

    public static int dishRise(int x, int z) {
        return Math.round(DISH_DEPTH * (x * x + z * z) / (float) (DISH_RADIUS * DISH_RADIUS));
    }

    public static boolean inDish(int x, int z) {
        return x * x + z * z <= DISH_RADIUS * DISH_RADIUS;
    }

    public static char structureAt(int x, int y, int z) {
        if (x == 0 && y == CONTROLLER_Y && z == -BASE_RADIUS) return '@';

        int ax = Math.abs(x);
        int az = Math.abs(z);
        if (Math.abs(ax - PYLON_OFFSET) <= 1 && Math.abs(az - PYLON_OFFSET) <= 1) return 'S';

        if (inDish(ax, az)) {
            int rise = dishRise(ax, az);
            int bottom = BOTTOM_DISH_SURFACE - 1 + rise;
            int top = TOP_DISH_SURFACE - rise;
            if (y == bottom || y == top) return 'S';
            if (y >= 3 && y < bottom || y > top) return 'c';
        }
        if (y >= TOP - 1 && Math.abs(ax - az) <= 1 && !inDish(ax, az) && Math.max(ax, az) < PYLON_OFFSET) return 'S';

        return switch (y) {
            case 0 -> inOctagon(ax, az, BASE_RADIUS, 2 * PYLON_OFFSET - 2) ? 'C' : ' ';
            case 1 -> inOctagon(ax, az, BASE_RADIUS, 17) ? 'C' : ' ';
            case 2 -> inOctagon(ax, az, DISH_RADIUS + 1, 15) ? 'C' : ' ';
            default -> ' ';
        };
    }

    private static boolean inOctagon(int ax, int az, int radius, int cut) {
        return ax <= radius && az <= radius && ax + az <= cut;
    }
}
