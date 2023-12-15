package com.elfoteo.crysis.mixin;

import com.elfoteo.crysis.CrysisMod;
import com.elfoteo.crysis.CrysisRenders;
import com.elfoteo.crysis.NanosuitModes;
import com.elfoteo.crysis.capability.INanosuitCapability;
import com.elfoteo.crysis.capability.NanosuitModeProvider;
import com.elfoteo.crysis.utils.CrysisUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static net.minecraft.client.renderer.entity.LivingRenderer.getOverlayCoords;

@Mixin(LivingRenderer.class)
public abstract class LivingEntityRendererInject<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements IEntityRenderer<T, M> {
    @Shadow public abstract M getModel();

    @Shadow protected abstract float getAttackAnim(T p_77040_1_, float p_77040_2_);

    @Shadow protected M model;

    @Shadow protected abstract float getBob(T p_77044_1_, float p_77044_2_);

    @Shadow protected abstract void setupRotations(T p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_);

    @Shadow protected abstract void scale(T p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_);

    @Shadow protected abstract boolean isBodyVisible(T p_225622_1_);

    @Shadow protected abstract float getWhiteOverlayProgress(T p_225625_1_, float p_225625_2_);

    private static Minecraft mc = Minecraft.getInstance();
    protected LivingEntityRendererInject(EntityRendererManager p_i46179_1_) {
        super(p_i46179_1_);
    }

    @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            at = @At("RETURN"))
    public void render(T entity, float partialTicks, float rotationYaw, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, CallbackInfo ci){
        if (mc.player != null){
            try{
                INanosuitCapability nanosuitCapability = mc.player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
                if (CrysisMod.isWearingFullNanosuit(mc.player) && nanosuitCapability.getMode().equals(NanosuitModes.VISOR)) {
                    doStuff(entity, 0, matrixStack, buffer, packedLight);
                }
            }
            catch (Exception exception){

            }

        }
    }

    public void doStuff(T entity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        matrixStack.pushPose();

        this.model.attackTime = this.getAttackAnim(entity, partialTicks);

        boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
        this.model.riding = shouldSit;
        this.model.young = entity.isBaby();

        float bodyRotation = MathHelper.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float headRotation = MathHelper.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
        float headBodyDifference = headRotation - bodyRotation;

        if (shouldSit && entity.getVehicle() instanceof LivingEntity) {
            LivingEntity ridingEntity = (LivingEntity) entity.getVehicle();
            bodyRotation = MathHelper.rotLerp(partialTicks, ridingEntity.yBodyRotO, ridingEntity.yBodyRot);
            headBodyDifference = headRotation - bodyRotation;

            float headPitch = MathHelper.wrapDegrees(headBodyDifference);

            if (headPitch < -85.0F) {
                headPitch = -85.0F;
            }

            if (headPitch >= 85.0F) {
                headPitch = 85.0F;
            }

            bodyRotation = headRotation - headPitch;

            if (headPitch * headPitch > 2500.0F) {
                bodyRotation += headPitch * 0.2F;
            }

            headBodyDifference = headRotation - bodyRotation;
        }

        float pitch = MathHelper.lerp(partialTicks, entity.xRotO, entity.xRot);

        if (entity.getPose() == Pose.SLEEPING) {
            Direction direction = entity.getBedOrientation();

            if (direction != null) {
                float eyeHeight = entity.getEyeHeight(Pose.STANDING) - 0.1F;
                matrixStack.translate((double) ((float) (-direction.getStepX()) * eyeHeight), 0.0D, (double) ((float) (-direction.getStepZ()) * eyeHeight));
            }
        }

        float bob = this.getBob(entity, partialTicks);
        this.setupRotations(entity, matrixStack, bob, bodyRotation, partialTicks);

        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entity, matrixStack, partialTicks);
        matrixStack.translate(0.0D, (double) -1.501F, 0.0D);

        float animationSpeed = 0.0F;
        float animationPosition = 0.0F;

        if (!shouldSit && entity.isAlive()) {
            animationSpeed = MathHelper.lerp(partialTicks, entity.animationSpeedOld, entity.animationSpeed);
            animationPosition = entity.animationPosition - entity.animationSpeed * (1.0F - partialTicks);

            if (entity.isBaby()) {
                animationPosition *= 3.0F;
            }

            if (animationSpeed > 1.0F) {
                animationSpeed = 1.0F;
            }
        }

        this.model.prepareMobModel(entity, animationPosition, animationSpeed, partialTicks);
        this.model.setupAnim(entity, animationPosition, animationSpeed, bob, headBodyDifference, pitch);

        float f = (float)entity.tickCount + partialTicks;
        IVertexBuilder vertexBuilder = buffer.getBuffer(CrysisRenders.GLOWING);
        int overlayCoords = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));

        Color color = CrysisUtils.getGlowColorForEntity(entity);

        this.model.renderToBuffer(matrixStack, vertexBuilder, packedLight, overlayCoords,
                (float) color.getRed() /255, (float) color.getGreen() /255,
                (float) color.getBlue() /255, 0.5F);


        matrixStack.popPose();
    }

}
