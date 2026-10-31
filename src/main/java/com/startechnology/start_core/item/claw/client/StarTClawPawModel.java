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
import net.minecraft.world.entity.HumanoidArm;

public class StarTClawPawModel {

    public static final ModelLayerLocation PLAYER_LAYER = new ModelLayerLocation(
            StarTCore.resourceLocation("claw_paw"), "main");
    public static final ModelLayerLocation SLIM_PLAYER_LAYER = new ModelLayerLocation(
            StarTCore.resourceLocation("claw_paw_slim"), "main");

    private static final int TEXTURE_SIZE = 64;
    private static final int FUR_V = 0;
    private static final int TOE_V = 16;
    private static final int PAD_V = 32;
    private static final int CUFF_V = 48;
    private static final int TOE_COUNT = 3;
    private static final float PAW_INFLATE = 0.4F;
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

    public static LayerDefinition createPlayerLayer() {
        return createLayer(false);
    }

    public static LayerDefinition createSlimPlayerLayer() {
        return createLayer(true);
    }

    private static LayerDefinition createLayer(boolean slim) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        addPaw(root, "right_paw", false, slim);
        addPaw(root, "left_paw", true, slim);

        return LayerDefinition.create(mesh, TEXTURE_SIZE, TEXTURE_SIZE);
    }

    private static void addPaw(PartDefinition root, String name, boolean left, boolean slim) {
        float armWidth = slim ? 3.0F : 4.0F;
        float armX = left ? -1.0F : (slim ? -2.0F : -3.0F);
        float x = armX - PAW_INFLATE;
        float width = armWidth + PAW_INFLATE * 2.0F;
        float z = -2.0F - PAW_INFLATE;
        float depth = 4.0F + PAW_INFLATE * 2.0F;
        float palmSide = left ? x - 0.25F : x + width;
        float toeDepth = depth / TOE_COUNT;

        PartDefinition paw = root.addOrReplaceChild(name, CubeListBuilder.create(), PartPose.ZERO);

        paw.addOrReplaceChild("cuff",
                CubeListBuilder.create().texOffs(0, CUFF_V)
                        .addBox(x - 0.08F, 3.6F, z - 0.08F, width + 0.16F, 2.9F, depth + 0.16F),
                PartPose.ZERO);
        paw.addOrReplaceChild("mitt",
                CubeListBuilder.create().texOffs(0, FUR_V)
                        .addBox(x, 6.4F, z, width, 3.4F, depth),
                PartPose.ZERO);
        paw.addOrReplaceChild("pad",
                CubeListBuilder.create().texOffs(0, PAD_V)
                        .addBox(palmSide, 7.0F, -1.5F, 0.25F, 2.4F, 3.0F),
                PartPose.ZERO);

        for (int toe = 0; toe < TOE_COUNT; toe++) {
            float toeZ = z + toe * toeDepth;
            float length = toe == 1 ? 1.9F : 1.5F;

            paw.addOrReplaceChild("toe_" + toe,
                    CubeListBuilder.create().texOffs(0, TOE_V)
                            .addBox(x, 9.8F, toeZ + 0.02F, width, length, toeDepth - 0.04F),
                    PartPose.ZERO);
            paw.addOrReplaceChild("bean_" + toe,
                    CubeListBuilder.create().texOffs(0, PAD_V)
                            .addBox(palmSide, 9.9F, toeZ + 0.3F, 0.25F, 0.9F, toeDepth - 0.6F),
                    PartPose.ZERO);
        }
    }

    public void render(HumanoidArm side, PoseStack poseStack, VertexConsumer vertices, int packedLight,
                       int packedOverlay, int cuffColor, int mittColor, int toesColor) {
        ModelPart paw = side == HumanoidArm.RIGHT ? rightPaw : leftPaw;

        renderPart(paw.getChild("cuff"), poseStack, vertices, packedLight, packedOverlay, cuffColor);
        renderPart(paw.getChild("mitt"), poseStack, vertices, packedLight, packedOverlay, mittColor);
        renderPart(paw.getChild("pad"), poseStack, vertices, packedLight, packedOverlay, 0xFFFFFF);

        for (int toe = 0; toe < TOE_COUNT; toe++) {
            renderPart(paw.getChild("toe_" + toe), poseStack, vertices, packedLight, packedOverlay, toesColor);
            renderPart(paw.getChild("bean_" + toe), poseStack, vertices, packedLight, packedOverlay, 0xFFFFFF);
        }
    }

    private static void renderPart(ModelPart part, PoseStack poseStack, VertexConsumer vertices, int packedLight,
                                   int packedOverlay, int color) {
        part.render(poseStack, vertices, packedLight, packedOverlay, (color >> 16 & 255) / 255.0F,
                (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F, 1.0F);
    }
}
