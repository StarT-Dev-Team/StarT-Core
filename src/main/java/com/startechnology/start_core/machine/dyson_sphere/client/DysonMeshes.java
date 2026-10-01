package com.startechnology.start_core.machine.dyson_sphere.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.startechnology.start_core.client.megastructure.MegastructureMeshes;
import org.joml.Vector3f;

import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.RING_COUNT;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.RING_THICKNESS;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.RING_WIDTH;
import static com.startechnology.start_core.machine.dyson_sphere.DysonSphereGeometry.ringRadius;

final class DysonMeshes {

    private static final int STAR_SUBDIVISIONS = 5;
    private static final int RING_SEGMENTS = 128;
    private static final int BREAK_SEGMENTS = 8;

    private static VertexBuffer star;
    private static VertexBuffer[] rings;

    private DysonMeshes() {}

    static VertexBuffer star() {
        if (star == null) star = MegastructureMeshes.upload(buildStar());
        return star;
    }

    static VertexBuffer ring(int index) {
        if (rings == null) {
            rings = new VertexBuffer[RING_COUNT];
            for (int i = 0; i < RING_COUNT; i++) rings[i] = MegastructureMeshes.upload(buildRing(ringRadius(i)));
        }
        return rings[index];
    }

    private static BufferBuilder buildStar() {
        float t = (1 + Mth.sqrt(5)) / 2;
        var v = new Vector3f[] {
                new Vector3f(-1, t, 0), new Vector3f(1, t, 0), new Vector3f(-1, -t, 0), new Vector3f(1, -t, 0),
                new Vector3f(0, -1, t), new Vector3f(0, 1, t), new Vector3f(0, -1, -t), new Vector3f(0, 1, -t),
                new Vector3f(t, 0, -1), new Vector3f(t, 0, 1), new Vector3f(-t, 0, -1), new Vector3f(-t, 0, 1) };
        int[][] faces = { { 0, 11, 5 }, { 0, 5, 1 }, { 0, 1, 7 }, { 0, 7, 10 }, { 0, 10, 11 }, { 1, 5, 9 },
                { 5, 11, 4 }, { 11, 10, 2 }, { 10, 7, 6 }, { 7, 1, 8 }, { 3, 9, 4 }, { 3, 4, 2 }, { 3, 2, 6 },
                { 3, 6, 8 }, { 3, 8, 9 }, { 4, 9, 5 }, { 2, 4, 11 }, { 6, 2, 10 }, { 8, 6, 7 }, { 9, 8, 1 } };
        List<Vector3f[]> triangles = new ArrayList<>();
        for (var face : faces) {
            triangles.add(new Vector3f[] { new Vector3f(v[face[0]]).normalize(), new Vector3f(v[face[1]]).normalize(),
                    new Vector3f(v[face[2]]).normalize() });
        }
        for (int level = 0; level < STAR_SUBDIVISIONS; level++) {
            List<Vector3f[]> next = new ArrayList<>(triangles.size() * 4);
            for (var tri : triangles) {
                var ab = new Vector3f(tri[0]).add(tri[1]).normalize();
                var bc = new Vector3f(tri[1]).add(tri[2]).normalize();
                var ca = new Vector3f(tri[2]).add(tri[0]).normalize();
                next.add(new Vector3f[] { tri[0], ab, ca });
                next.add(new Vector3f[] { tri[1], bc, ab });
                next.add(new Vector3f[] { tri[2], ca, bc });
                next.add(new Vector3f[] { ab, bc, ca });
            }
            triangles = next;
        }

        var builder = new BufferBuilder(triangles.size() * 3 * DefaultVertexFormat.POSITION.getVertexSize());
        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION);
        for (var tri : triangles) {
            for (var p : tri) builder.vertex(p.x, p.y, p.z).endVertex();
        }
        return builder;
    }

    private static BufferBuilder buildRing(float radius) {
        var format = DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL;
        var builder = new BufferBuilder(RING_SEGMENTS * 4 * 6 * format.getVertexSize());
        builder.begin(VertexFormat.Mode.TRIANGLES, format);
        float outer = radius + RING_THICKNESS;
        float half = RING_WIDTH / 2;
        int perBreak = RING_SEGMENTS / BREAK_SEGMENTS;
        for (int s = 0; s < RING_SEGMENTS; s++) {
            int segment = s / perBreak;
            float t0 = s / (float) RING_SEGMENTS;
            float t1 = (s + 1) / (float) RING_SEGMENTS;
            float a0 = t0 * Mth.TWO_PI;
            float a1 = t1 * Mth.TWO_PI;
            float c0 = Mth.cos(a0), s0 = Mth.sin(a0), c1 = Mth.cos(a1), s1 = Mth.sin(a1);
            int red = segment * 32;

            quad(builder, red, 255, -c0, -s0, -c1, -s1, 0,
                    c0 * radius, s0 * radius, -half, t0, 0, c1 * radius, s1 * radius, -half, t1, 0,
                    c1 * radius, s1 * radius, half, t1, 1, c0 * radius, s0 * radius, half, t0, 1);
            quad(builder, red, 0, c0, s0, c1, s1, 0,
                    c0 * outer, s0 * outer, -half, t0, 0, c1 * outer, s1 * outer, -half, t1, 0,
                    c1 * outer, s1 * outer, half, t1, 1, c0 * outer, s0 * outer, half, t0, 1);
            quad(builder, red, 0, 0, 0, 0, 0, 1,
                    c0 * radius, s0 * radius, half, t0, 1, c1 * radius, s1 * radius, half, t1, 1,
                    c1 * outer, s1 * outer, half, t1, 1, c0 * outer, s0 * outer, half, t0, 1);
            quad(builder, red, 0, 0, 0, 0, 0, -1,
                    c0 * radius, s0 * radius, -half, t0, 0, c1 * radius, s1 * radius, -half, t1, 0,
                    c1 * outer, s1 * outer, -half, t1, 0, c0 * outer, s0 * outer, -half, t0, 0);
        }
        return builder;
    }

    private static void quad(BufferBuilder b, int red, int green, float nx0, float ny0, float nx1, float ny1, float nz,
                             float x0, float y0, float z0, float u0, float v0,
                             float x1, float y1, float z1, float u1, float v1,
                             float x2, float y2, float z2, float u2, float v2,
                             float x3, float y3, float z3, float u3, float v3) {
        vertex(b, x0, y0, z0, u0, v0, red, green, nx0, ny0, nz);
        vertex(b, x1, y1, z1, u1, v1, red, green, nx1, ny1, nz);
        vertex(b, x2, y2, z2, u2, v2, red, green, nx1, ny1, nz);
        vertex(b, x0, y0, z0, u0, v0, red, green, nx0, ny0, nz);
        vertex(b, x2, y2, z2, u2, v2, red, green, nx1, ny1, nz);
        vertex(b, x3, y3, z3, u3, v3, red, green, nx0, ny0, nz);
    }

    private static void vertex(BufferBuilder b, float x, float y, float z, float u, float v, int red, int green,
                               float nx, float ny, float nz) {
        b.vertex(x, y, z).uv(u, v).color(red, green, 0, 255).normal(nx, ny, nz).endVertex();
    }
}
