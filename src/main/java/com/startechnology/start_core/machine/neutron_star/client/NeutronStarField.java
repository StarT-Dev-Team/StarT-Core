package com.startechnology.start_core.machine.neutron_star.client;

import org.joml.Matrix3f;
import org.joml.Vector3f;

public final class NeutronStarField {

    public static final int LOOP_COUNT = 8;
    public static final float LOOP_SPREAD = 6.14159f;

    public final Matrix3f star = new Matrix3f();
    public final Matrix3f jet = new Matrix3f();
    public final Matrix3f[] loops = new Matrix3f[LOOP_COUNT];
    private final Matrix3f space = new Matrix3f();
    private final Matrix3f scratch = new Matrix3f();

    public NeutronStarField() {
        for (int i = 0; i < LOOP_COUNT; i++) loops[i] = new Matrix3f();
    }

    public void update(float angle, float tilt, float[] twist) {
        rotXZ(angle, star);
        rotXY(tilt, jet).mul(star);
        rotXY(tilt, space).mul(rotYZ(0.01f, scratch)).mul(star);
        for (int i = 0; i < LOOP_COUNT; i++) {
            var loop = rotXZ(-0.02f, loops[i]).mul(rotYZ(1.6f, scratch));
            loop.mul(rotXZ(twist[i], scratch)).mul(rotXZ(i * LOOP_SPREAD / LOOP_COUNT, scratch)).mul(space);
        }
    }

    public Vector3f jetAxis(Vector3f dest) {
        return jet.getRow(1, dest);
    }

    private static Matrix3f rows(Matrix3f dest, float a00, float a01, float a02, float a10, float a11, float a12,
                                 float a20, float a21, float a22) {
        return dest.set(a00, a10, a20, a01, a11, a21, a02, a12, a22);
    }

    static Matrix3f rotXZ(float a, Matrix3f dest) {
        float c = (float) Math.cos(a), s = (float) Math.sin(a);
        return rows(dest, c, 0, s, 0, 1, 0, -s, 0, c);
    }

    static Matrix3f rotYZ(float a, Matrix3f dest) {
        float c = (float) Math.cos(a), s = (float) Math.sin(a);
        return rows(dest, 1, 0, 0, 0, c, s, 0, -s, c);
    }

    static Matrix3f rotXY(float a, Matrix3f dest) {
        float c = (float) Math.cos(a), s = (float) Math.sin(a);
        return rows(dest, c, s, 0, -s, c, 0, 0, 0, 1);
    }
}
