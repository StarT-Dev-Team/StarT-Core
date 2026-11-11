package com.startechnology.start_core.item.claw.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.startechnology.start_core.StarTCore;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.entity.HumanoidArm;

public class StarTClawPawModel {

    public static final ModelLayerLocation PLAYER_LAYER = new ModelLayerLocation(
            StarTCore.resourceLocation("claw_paw"), "main");
    public static final ModelLayerLocation SLIM_PLAYER_LAYER = new ModelLayerLocation(
            StarTCore.resourceLocation("claw_paw_slim"), "main");

    private static final int TEXTURE_WIDTH = 64;
    private static final int TEXTURE_HEIGHT = 128;
    private static final int MITT_V = 0;
    private static final int TOE_V = 16;
    private static final int PAD_V = 32;
    private static final int CUFF_V = 48;
    private static final int CLAW_V = 64;
    private static final int PLATE_V = 80;
    private static final int JOINT_V = 96;
    private static final int LED_V = 112;
    private static final int TOE_COUNT = 4;
    private static final int CELL_SEGMENTS = 5;
    private static final int LOW_CHARGE_COLOR = 0xFF3030;
    private static final int UNLIT_COLOR = 0x2A2D34;
    private static final float LOW_CHARGE = 0.2F;
    private static final float PAW_INFLATE = 0.3F;
    private static final float TOE_GAP = 0.12F;
    private static final float COLUMN_WIDTH = 0.9F;
    private static final float TOE_THICKNESS = 0.8F;
    private static final float CUFF_TOP = 3.4F;
    private static final float CUFF_HEIGHT = 3.0F;
    private static final float WRIST_BOTTOM = 7.1F;
    private static final float MITT_TOP = 7.0F;
    private static final float MITT_BOTTOM = 9.3F;
    private static final float KNUCKLE_Y = 10.9F;
    private static final float ROD_Y = 8.44F;
    private static final float ROD_SLOPE = 0.176F;
    private static final float ROD_CLEARANCE = 1.1F;
    private static final float[] TOE_END = { 12.3F, 12.5F, 12.5F, 12.3F };
    private static final StarTClawPawModel[] CACHE = new StarTClawPawModel[2];

    private final ModelPart rightPaw;
    private final ModelPart leftPaw;

    public StarTClawPawModel(ModelPart root) {
        rightPaw = root.getChild("right_paw");
        leftPaw = root.getChild("left_paw");
    }

    public static StarTClawPawModel get(boolean slim) {
        int index = slim ? 1 : 0;

        if (CACHE[index] == null) {
            EntityModelSet models = Minecraft.getInstance().getEntityModels();

            CACHE[index] = new StarTClawPawModel(models.bakeLayer(slim ? SLIM_PLAYER_LAYER : PLAYER_LAYER));
        }

        return CACHE[index];
    }

    public static LayerDefinition createLayer(boolean slim) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        addPaw(root, "right_paw", false, slim);
        addPaw(root, "left_paw", true, slim);

        return LayerDefinition.create(mesh, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    private static void addPaw(PartDefinition root, String name, boolean left, boolean slim) {
        float armWidth = slim ? 3.0F : 4.0F;
        float armX = left ? -1.0F : (slim ? -2.0F : -3.0F);
        float x = armX - PAW_INFLATE;
        float width = armWidth + PAW_INFLATE * 2.0F;
        float z = -2.0F - PAW_INFLATE;
        float depth = 4.0F + PAW_INFLATE * 2.0F;
        float toeDepth = depth / TOE_COUNT;
        float innerX = left ? x : x + width - COLUMN_WIDTH;
        float outerX = left ? x + width - COLUMN_WIDTH : x;
        float centerX = x + width / 2.0F;
        float rodX = left ? 0.65F : -0.65F;
        float cuffBottom = CUFF_TOP + CUFF_HEIGHT;
        float innerColumnX = left ? innerX - 0.04F : innerX - 0.02F;
        float outerColumnX = left ? outerX - 0.02F : outerX - 0.04F;
        float clawX = left ? innerX + COLUMN_WIDTH + 0.25F : innerX - 0.7F;
        float clawStep = left ? -0.35F : 0.35F;

        PartDefinition paw = root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.ZERO);

        CubeListBuilder cuff = CubeListBuilder.create().texOffs(0, CUFF_V);
        CubeListBuilder mitt = CubeListBuilder.create().texOffs(0, MITT_V);
        CubeListBuilder plates = CubeListBuilder.create().texOffs(0, PLATE_V);
        CubeListBuilder joints = CubeListBuilder.create().texOffs(0, JOINT_V);
        CubeListBuilder leds = CubeListBuilder.create().texOffs(0, LED_V);
        CubeListBuilder pad = CubeListBuilder.create().texOffs(0, PAD_V);
        CubeListBuilder claws = CubeListBuilder.create().texOffs(0, CLAW_V);

        addSoftBox(cuff, x - 0.05F, CUFF_TOP, z - 0.05F, width + 0.1F, CUFF_HEIGHT, depth + 0.1F, 0.4F);

        joints.addBox(x - 0.1F, CUFF_TOP + 0.1F, z - 0.1F, width + 0.2F, 0.22F, depth + 0.2F);
        joints.addBox(x - 0.1F, cuffBottom - 0.35F, z - 0.1F, width + 0.2F, 0.22F, depth + 0.2F);
        joints.addBox(x + 0.2F, cuffBottom - 0.1F, z + 0.2F, width - 0.4F, WRIST_BOTTOM - cuffBottom + 0.2F,
                depth - 0.4F);

        leds.addBox(centerX - 0.8F, CUFF_TOP + 1.2F, z - 0.2F, 1.6F, 0.25F, 0.12F);
        leds.addBox(centerX - 0.8F, CUFF_TOP + 1.2F, z + depth + 0.08F, 1.6F, 0.25F, 0.12F);
        leds.addBox(x - 0.2F, CUFF_TOP + 1.2F, z + depth / 2.0F - 0.8F, 0.12F, 0.25F, 1.6F);
        leds.addBox(x + width + 0.08F, CUFF_TOP + 1.2F, z + depth / 2.0F - 0.8F, 0.12F, 0.25F, 1.6F);

        addSoftBox(mitt, x, MITT_TOP, z, width, MITT_BOTTOM - MITT_TOP, depth, 0.4F);

        plates.addBox(x - 0.12F, MITT_TOP + 0.5F, z + 0.7F, 0.12F, 1.9F, depth - 1.4F);
        plates.addBox(x + width, MITT_TOP + 0.5F, z + 0.7F, 0.12F, 1.9F, depth - 1.4F);
        plates.addBox(x + 0.7F, MITT_TOP + 0.5F, z - 0.12F, width - 1.4F, 1.9F, 0.12F);
        plates.addBox(x + 0.7F, MITT_TOP + 0.5F, z + depth, width - 1.4F, 1.9F, 0.12F);

        leds.addBox(centerX - 0.25F, MITT_TOP + 1.0F, z - 0.2F, 0.5F, 0.7F, 0.1F);
        leds.addBox(centerX - 0.25F, MITT_TOP + 1.0F, z + depth + 0.1F, 0.5F, 0.7F, 0.1F);

        joints.addBox(x + COLUMN_WIDTH - 0.1F, MITT_BOTTOM - 0.1F, z + 0.1F, width - COLUMN_WIDTH * 2.0F + 0.2F,
                TOE_END[0] - TOE_THICKNESS - MITT_BOTTOM + 0.2F, depth - 0.2F);

        for (float collarZ : new float[] { z - 0.5F, z + depth }) {
            float collarY = ROD_Y - ROD_SLOPE * (collarZ + 0.25F);

            joints.addBox(rodX - ROD_CLEARANCE, collarY - ROD_CLEARANCE, collarZ, ROD_CLEARANCE * 2.0F, 0.3F, 0.5F);
            joints.addBox(rodX - ROD_CLEARANCE, collarY + ROD_CLEARANCE - 0.3F, collarZ, ROD_CLEARANCE * 2.0F, 0.3F,
                    0.5F);
            joints.addBox(rodX - ROD_CLEARANCE, collarY - ROD_CLEARANCE + 0.3F, collarZ, 0.3F,
                    ROD_CLEARANCE * 2.0F - 0.6F, 0.5F);
            joints.addBox(rodX + ROD_CLEARANCE - 0.3F, collarY - ROD_CLEARANCE + 0.3F, collarZ, 0.3F,
                    ROD_CLEARANCE * 2.0F - 0.6F, 0.5F);
        }

        for (int toe = 0; toe < TOE_COUNT; toe++) {
            float toeZ = z + toe * toeDepth + TOE_GAP / 2.0F;
            float length = toeDepth - TOE_GAP;
            float end = TOE_END[toe];
            float columnLength = end - TOE_THICKNESS + 0.1F - KNUCKLE_Y - 0.3F;
            float clawZ = toeZ + length / 2.0F;

            paw.addOrReplaceChild("toe_" + toe,
                    CubeListBuilder.create().texOffs(0, TOE_V)
                            .addBox(innerColumnX, MITT_BOTTOM - 0.1F, toeZ, COLUMN_WIDTH + 0.06F,
                                    KNUCKLE_Y - MITT_BOTTOM + 0.1F, length)
                            .addBox(innerColumnX, KNUCKLE_Y + 0.3F, toeZ, COLUMN_WIDTH + 0.06F, columnLength, length)
                            .addBox(x - 0.02F, end - TOE_THICKNESS, toeZ + 0.02F, width + 0.04F, TOE_THICKNESS,
                                    length - 0.04F)
                            .addBox(outerColumnX, MITT_BOTTOM - 0.1F, toeZ, COLUMN_WIDTH + 0.06F,
                                    KNUCKLE_Y - MITT_BOTTOM + 0.1F, length)
                            .addBox(outerColumnX, KNUCKLE_Y + 0.3F, toeZ, COLUMN_WIDTH + 0.06F, columnLength,
                                    length),
                    PartPose.ZERO);

            joints.addBox(innerX - 0.06F, KNUCKLE_Y, toeZ + 0.1F, COLUMN_WIDTH + 0.12F, 0.3F, length - 0.2F);
            joints.addBox(outerX - 0.06F, KNUCKLE_Y, toeZ + 0.1F, COLUMN_WIDTH + 0.12F, 0.3F, length - 0.2F);

            pad.addBox(x + COLUMN_WIDTH + 0.2F, end - 0.02F, toeZ + 0.3F, width - COLUMN_WIDTH * 2.0F - 0.4F, 0.1F,
                    length - 0.6F);

            claws.addBox(clawX, end - 0.05F, clawZ - 0.25F, 0.45F, 0.7F, 0.5F);
            claws.addBox(clawX + clawStep, end + 0.55F, clawZ - 0.2F, 0.35F, 0.55F, 0.4F);
            claws.addBox(clawX + clawStep * 2.0F, end + 1.0F, clawZ - 0.12F, 0.25F, 0.45F, 0.24F);
        }

        float cellPanelX = left ? x + width + 0.1F : x - 0.4F;
        float cellFaceX = left ? x + width + 0.4F : x - 0.4F;
        float cellRailX = left ? cellFaceX : cellFaceX - 0.18F;
        float cellSegmentX = left ? cellFaceX : cellFaceX - 0.14F;

        joints.addBox(cellPanelX, 7.45F, -1.9F, 0.3F, 1.5F, 3.8F);
        joints.addBox(cellRailX, 7.45F, -1.9F, 0.18F, 0.18F, 3.8F);
        joints.addBox(cellRailX, 8.77F, -1.9F, 0.18F, 0.18F, 3.8F);
        joints.addBox(cellRailX, 7.63F, -1.9F, 0.18F, 1.14F, 0.2F);
        joints.addBox(cellRailX, 7.63F, 1.7F, 0.18F, 1.14F, 0.2F);

        for (int segment = 0; segment < CELL_SEGMENTS; segment++) {
            paw.addOrReplaceChild("cell_" + segment,
                    CubeListBuilder.create().texOffs(0, LED_V).addBox(cellSegmentX, 7.65F, -1.5F + segment * 0.62F,
                            0.14F, 1.1F, 0.52F),
                    PartPose.ZERO);
        }

        paw.addOrReplaceChild("cuff", cuff, PartPose.ZERO);
        paw.addOrReplaceChild("mitt", mitt, PartPose.ZERO);
        paw.addOrReplaceChild("plates", plates, PartPose.ZERO);
        paw.addOrReplaceChild("joints", joints, PartPose.ZERO);
        paw.addOrReplaceChild("leds", leds, PartPose.ZERO);
        paw.addOrReplaceChild("pad", pad, PartPose.ZERO);
        paw.addOrReplaceChild("claws", claws, PartPose.ZERO);
    }

    private static void addSoftBox(CubeListBuilder builder, float x, float y, float z, float width, float height,
                                   float depth, float corner) {
        builder.addBox(x, y + corner, z, width, height - corner * 2.0F, depth);
        builder.addBox(x + corner, y, z + 0.02F, width - corner * 2.0F, height, depth - 0.04F);
        builder.addBox(x + corner * 0.4F, y + corner * 0.4F, z + 0.04F, width - corner * 0.8F,
                height - corner * 0.8F, depth - 0.08F);
    }

    public void render(HumanoidArm side, PoseStack poseStack, VertexConsumer vertices, int packedLight,
                       int packedOverlay, int cuffColor, int mittColor, int toesColor, boolean clawsOut, float charge) {
        ModelPart paw = side == HumanoidArm.RIGHT ? rightPaw : leftPaw;

        renderPart(paw.getChild("cuff"), poseStack, vertices, packedLight, packedOverlay, cuffColor);
        renderPart(paw.getChild("mitt"), poseStack, vertices, packedLight, packedOverlay, mittColor);
        renderPart(paw.getChild("plates"), poseStack, vertices, packedLight, packedOverlay, mittColor);
        renderPart(paw.getChild("joints"), poseStack, vertices, packedLight, packedOverlay, 0xFFFFFF);
        renderPart(paw.getChild("pad"), poseStack, vertices, packedLight, packedOverlay, 0xFFFFFF);
        renderPart(paw.getChild("leds"), poseStack, vertices, LightTexture.FULL_BRIGHT, packedOverlay, mittColor);

        if (clawsOut) {
            renderPart(paw.getChild("claws"), poseStack, vertices, packedLight, packedOverlay, 0xFFFFFF);
        }

        int litSegments = charge > 0.0F ? Math.max(1, (int) Math.ceil(charge * CELL_SEGMENTS - 0.001F)) : 0;
        int litColor = charge <= LOW_CHARGE ? LOW_CHARGE_COLOR : mittColor;

        for (int segment = 0; segment < CELL_SEGMENTS; segment++) {
            if (segment < litSegments) {
                renderPart(paw.getChild("cell_" + segment), poseStack, vertices, LightTexture.FULL_BRIGHT,
                        packedOverlay, litColor);
            } else {
                renderPart(paw.getChild("cell_" + segment), poseStack, vertices, packedLight, packedOverlay,
                        UNLIT_COLOR);
            }
        }

        for (int toe = 0; toe < TOE_COUNT; toe++) {
            renderPart(paw.getChild("toe_" + toe), poseStack, vertices, packedLight, packedOverlay, toesColor);
        }
    }

    private static void renderPart(ModelPart part, PoseStack poseStack, VertexConsumer vertices, int packedLight,
                                   int packedOverlay, int color) {
        part.render(poseStack, vertices, packedLight, packedOverlay, (color >> 16 & 255) / 255.0F,
                (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, 1.0F);
    }
}
