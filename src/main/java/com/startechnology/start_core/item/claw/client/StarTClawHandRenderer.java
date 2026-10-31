package com.startechnology.start_core.item.claw.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.item.claw.StarTClawOfLawItem;
import com.startechnology.start_core.item.claw.StarTClawToolRenderer;
import com.startechnology.start_core.item.claw.StarTClawPart;
import org.joml.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

public final class StarTClawHandRenderer {

    private static final ResourceLocation PAW_TEXTURE = StarTCore
            .resourceLocation("textures/item/claw_of_law/paw_model.png");

    private StarTClawHandRenderer() {}

    public static void render(AbstractClientPlayer player, ItemStack claw, HumanoidArm side, PoseStack poseStack,
                              MultiBufferSource buffers, int packedLight) {
        renderPaw(player, claw, side, poseStack, buffers, packedLight);
        renderTool(player, claw, side, poseStack, buffers, packedLight);
    }

    public static void renderTool(AbstractClientPlayer player, ItemStack claw, HumanoidArm side, PoseStack poseStack,
                                  MultiBufferSource buffers, int packedLight) {
        ItemStack tool = StarTClawToolRenderer.getTool(claw, StarTClawOfLawItem.getMode(claw));

        if (tool.isEmpty()) return;

        boolean leftHand = side == HumanoidArm.LEFT;

        poseStack.pushPose();

        applyHoldTransform(poseStack, leftHand);

        Minecraft.getInstance().getItemRenderer().renderStatic(player, tool, handContext(leftHand), leftHand,
                poseStack, buffers, player.level(), packedLight, OverlayTexture.NO_OVERLAY, player.getId());

        poseStack.popPose();
    }

    public static void renderPaw(AbstractClientPlayer player, ItemStack claw, HumanoidArm side,
                                 PoseStack poseStack, MultiBufferSource buffers, int packedLight) {
        StarTClawPawModel.get(player.getModelName().equals("slim")).render(side, poseStack,
                buffers.getBuffer(RenderType.entityCutoutNoCull(PAW_TEXTURE)), packedLight,
                OverlayTexture.NO_OVERLAY, StarTClawOfLawItem.getColor(claw, StarTClawPart.CUFF),
                StarTClawOfLawItem.getColor(claw, StarTClawPart.MITT),
                StarTClawOfLawItem.getColor(claw, StarTClawPart.TOES));
    }

    public static Matrix4f handToToolSpace(BakedModel model, HumanoidArm side) {
        boolean leftHand = side == HumanoidArm.LEFT;

        PoseStack poseStack = new PoseStack();

        poseStack.translate((leftHand ? -1.0F : 1.0F) / 16.0F, 0.0F, 0.0F);

        applyHoldTransform(poseStack, leftHand);

        ForgeHooksClient.handleCameraTransforms(poseStack, model, handContext(leftHand), leftHand);

        poseStack.translate(-0.5F, -0.5F, -0.5F);

        return new Matrix4f(poseStack.last().pose()).invert();
    }

    private static void applyHoldTransform(PoseStack poseStack, boolean leftHand) {
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.translate((leftHand ? -1.0F : 1.0F) / 16.0F, 0.125F, -0.625F);
    }

    private static ItemDisplayContext handContext(boolean leftHand) {
        return leftHand ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }
}
