package com.startechnology.start_core.client.megastructure;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;

public final class MegastructureMeshes {

    private static VertexBuffer quad;
    private static VertexBuffer cube;

    private MegastructureMeshes() {}

    public static VertexBuffer quad() {
        if (quad == null) {
            var builder = new BufferBuilder(256);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            builder.vertex(-1, -1, 0).endVertex();
            builder.vertex(1, -1, 0).endVertex();
            builder.vertex(1, 1, 0).endVertex();
            builder.vertex(-1, 1, 0).endVertex();
            quad = upload(builder);
        }
        return quad;
    }

    public static VertexBuffer cube() {
        if (cube == null) {
            int[][] square = { { -1, -1 }, { 1, -1 }, { 1, 1 }, { -1, 1 } };
            var builder = new BufferBuilder(512);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
            for (int axis = 0; axis < 3; axis++) {
                for (int sign = -1; sign <= 1; sign += 2) {
                    for (int i = 0; i < 4; i++) {
                        var corner = square[sign < 0 ? i : 3 - i];
                        var position = new float[3];
                        position[axis] = sign;
                        position[(axis + 1) % 3] = corner[0];
                        position[(axis + 2) % 3] = corner[1];
                        builder.vertex(position[0], position[1], position[2]).endVertex();
                    }
                }
            }
            cube = upload(builder);
        }
        return cube;
    }

    public static VertexBuffer upload(BufferBuilder builder) {
        var buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        buffer.bind();
        buffer.upload(builder.end());
        VertexBuffer.unbind();
        return buffer;
    }
}
