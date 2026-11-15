package com.startechnology.start_core.item.claw.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.startechnology.start_core.StarTCore;
import com.startechnology.start_core.item.claw.StarTClawOfLawItem;
import com.startechnology.start_core.item.claw.StarTClawToolRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = StarTCore.MOD_ID, value = Dist.CLIENT)
public final class StarTClawFirstPersonRenderEvents {

    private StarTClawFirstPersonRenderEvents() {}

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        ItemStack claw = event.getItemStack();

        if (!(claw.getItem() instanceof StarTClawOfLawItem)) return;

        Minecraft minecraft = Minecraft.getInstance();
        AbstractClientPlayer player = minecraft.player;

        if (player == null) return;

        HumanoidArm side = event.getHand() == InteractionHand.MAIN_HAND ? player.getMainArm() :
                player.getMainArm().getOpposite();
        ItemStack tool = StarTClawToolRenderer.getTool(claw, StarTClawOfLawItem.getMode(claw));

        event.setCanceled(true);

        if (tool.isEmpty()) {
            renderBareHand(event, player, claw, side);
        } else {
            renderHeldTool(event, player, tool, side);
        }
    }

    private static void renderHeldTool(RenderHandEvent event, AbstractClientPlayer player, ItemStack tool,
                                       HumanoidArm side) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffers = event.getMultiBufferSource();

        int packedLight = event.getPackedLight();
        boolean leftHand = side == HumanoidArm.LEFT;

        ItemDisplayContext context = leftHand ? ItemDisplayContext.FIRST_PERSON_LEFT_HAND :
                ItemDisplayContext.FIRST_PERSON_RIGHT_HAND;
        BakedModel model = itemRenderer.getModel(tool, player.level(), player, player.getId());

        poseStack.pushPose();

        applyItemTransform(poseStack, side, event.getEquipProgress(), event.getSwingProgress());

        poseStack.pushPose();

        ForgeHooksClient.handleCameraTransforms(poseStack, model, context, leftHand);

        poseStack.translate(-0.5F, -0.5F, -0.5F);
        poseStack.mulPoseMatrix(StarTClawHandRenderer.handToToolSpace(model, side));

        StarTClawHandRenderer.renderPaw(player, event.getItemStack(), side, poseStack, buffers, packedLight);

        poseStack.popPose();

        itemRenderer.renderStatic(player, tool, context, leftHand, poseStack, buffers, player.level(), packedLight,
                OverlayTexture.NO_OVERLAY, player.getId());

        poseStack.popPose();
    }

    private static void renderBareHand(RenderHandEvent event, AbstractClientPlayer player, ItemStack claw,
                                       HumanoidArm side) {
        PlayerRenderer renderer = (PlayerRenderer) Minecraft.getInstance().getEntityRenderDispatcher()
                .getRenderer(player);
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource buffers = event.getMultiBufferSource();

        int packedLight = event.getPackedLight();

        poseStack.pushPose();

        applyArmTransform(poseStack, side, event.getEquipProgress(), event.getSwingProgress());

        if (side == HumanoidArm.RIGHT) {
            renderer.renderRightHand(poseStack, buffers, packedLight, player);
        } else {
            renderer.renderLeftHand(poseStack, buffers, packedLight, player);
        }

        PlayerModel<AbstractClientPlayer> model = renderer.getModel();
        ModelPart arm = side == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;

        arm.translateAndRotate(poseStack);

        StarTClawHandRenderer.render(player, claw, side, poseStack, buffers, packedLight);

        poseStack.popPose();
    }

    private static void applyItemTransform(PoseStack poseStack, HumanoidArm side, float equipProgress,
                                           float swingProgress) {
        float direction = side == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        float rootSwing = Mth.sqrt(swingProgress);
        float attackRoot = Mth.sin(rootSwing * (float) Math.PI);

        poseStack.translate(
                direction * -0.4F * attackRoot,
                0.2F * Mth.sin(rootSwing * ((float) Math.PI * 2.0F)),
                -0.2F * Mth.sin(swingProgress * (float) Math.PI));
        poseStack.translate(direction * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
        poseStack.mulPose(Axis.YP.rotationDegrees(
                direction * (45.0F + Mth.sin(swingProgress * swingProgress * (float) Math.PI) * -20.0F)));
        poseStack.mulPose(Axis.ZP.rotationDegrees(direction * attackRoot * -20.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(attackRoot * -80.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(direction * -45.0F));
    }

    private static void applyArmTransform(PoseStack poseStack, HumanoidArm side, float equipProgress,
                                          float swingProgress) {
        float direction = side == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        float rootSwing = Mth.sqrt(swingProgress);

        poseStack.translate(
                direction * (-0.3F * Mth.sin(rootSwing * (float) Math.PI) + 0.64000005F),
                0.4F * Mth.sin(rootSwing * ((float) Math.PI * 2.0F)) - 0.6F + equipProgress * -0.6F,
                -0.4F * Mth.sin(swingProgress * (float) Math.PI) - 0.71999997F);
        poseStack.mulPose(Axis.YP.rotationDegrees(direction * 45.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(
                direction * Mth.sin(rootSwing * (float) Math.PI) * 70.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(
                direction * Mth.sin(swingProgress * swingProgress * (float) Math.PI) * -20.0F));
        poseStack.translate(direction * -1.0F, 3.6F, 3.5F);
        poseStack.mulPose(Axis.ZP.rotationDegrees(direction * 120.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(200.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(direction * -135.0F));
        poseStack.translate(direction * 5.6F, 0.0F, 0.0F);
    }
}
