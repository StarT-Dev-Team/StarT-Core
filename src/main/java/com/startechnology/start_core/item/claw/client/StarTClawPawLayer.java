package com.startechnology.start_core.item.claw.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.startechnology.start_core.item.claw.StarTClawOfLawItem;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class StarTClawPawLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public StarTClawPawLayer(PlayerRenderer renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffers, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (player.isInvisible()) return;

        renderHand(player.getMainHandItem(), player.getMainArm(), player, poseStack, buffers, packedLight);
        renderHand(player.getOffhandItem(), player.getMainArm().getOpposite(), player, poseStack, buffers,
                packedLight);
    }

    private void renderHand(ItemStack held, HumanoidArm side, AbstractClientPlayer player, PoseStack poseStack,
                            MultiBufferSource buffers, int packedLight) {
        if (!(held.getItem() instanceof StarTClawOfLawItem)) return;

        PlayerModel<AbstractClientPlayer> model = getParentModel();
        ModelPart arm = side == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;

        poseStack.pushPose();
        arm.translateAndRotate(poseStack);

        StarTClawHandRenderer.renderPaw(player, held, side, poseStack, buffers, packedLight);

        poseStack.popPose();

        poseStack.pushPose();
        model.translateToHand(side, poseStack);

        StarTClawHandRenderer.renderTool(player, held, side, poseStack, buffers, packedLight);

        poseStack.popPose();
    }
}
