package com.elfoteo.crysis.mixin;

import com.elfoteo.crysis.CrysisMod;
import com.elfoteo.crysis.CrysisRenders;
import com.elfoteo.crysis.NanosuitModes;
import com.elfoteo.crysis.capability.INanosuitCapability;
import com.elfoteo.crysis.capability.NanosuitModeProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererInject extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
    public PlayerRendererInject(EntityRendererManager p_i50965_1_, PlayerModel<AbstractClientPlayerEntity> p_i50965_2_, float p_i50965_3_) {
        super(p_i50965_1_, p_i50965_2_, p_i50965_3_);
    }

    @Inject(method = "render(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V",
            at = @At("HEAD"),
            cancellable = true)
    public void injectAtHead(AbstractClientPlayerEntity entity, float partialTicks, float rotationYaw, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, CallbackInfo ci) {
        try{
            INanosuitCapability nanosuitCapability = entity.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
            if (CrysisMod.isWearingFullNanosuit(entity)) {
                if (entity.hasEffect(Effects.INVISIBILITY)) {
                    matrixStack.pushPose();
                    crysisMod$renderInvisibilityOverlay(matrixStack, buffer, entity, 0, packedLight, nanosuitCapability.getInvisTransition());
                    ci.cancel();
                }
                else if (nanosuitCapability.getMode().equals(NanosuitModes.ARMOR)){
                    matrixStack.pushPose();
                    crysisMod$renderArmorOverlay(matrixStack, buffer, entity, 0, packedLight);
                }
            }
        } catch (Exception ignored){

        }
    }

    @Unique
    public void crysisMod$renderInvisibilityOverlay(MatrixStack matrixStack, IRenderTypeBuffer buffer, AbstractClientPlayerEntity entity, float partialTicks, int packedLight, float alpha){
        float f = (float)entity.tickCount + partialTicks;

        this.model.attackTime = this.getAttackAnim(entity, partialTicks);

        boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
        this.model.riding = shouldSit;
        this.model.young = entity.isBaby();

        float yaw = MathHelper.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float headYaw = MathHelper.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
        float headPitch = headYaw - yaw;

        if (shouldSit && entity.getVehicle() instanceof LivingEntity) {
            LivingEntity ridingEntity = (LivingEntity) entity.getVehicle();
            yaw = MathHelper.rotLerp(partialTicks, ridingEntity.yBodyRotO, ridingEntity.yBodyRot);
            headPitch = headYaw - yaw;

            float pitchWrapped = MathHelper.wrapDegrees(headPitch);
            if (pitchWrapped < -85.0F) {
                pitchWrapped = -85.0F;
            }

            if (pitchWrapped >= 85.0F) {
                pitchWrapped = 85.0F;
            }

            yaw = headYaw - pitchWrapped;

            if (pitchWrapped * pitchWrapped > 2500.0F) {
                yaw += pitchWrapped * 0.2F;
            }

            headPitch = headYaw - yaw;
        }

        float pitch = MathHelper.lerp(partialTicks, entity.xRotO, entity.xRot);

        if (entity.getPose() == Pose.SLEEPING) {
            Direction bedOrientation = entity.getBedOrientation();

            if (bedOrientation != null) {
                float eyeHeight = entity.getEyeHeight(Pose.STANDING) - 0.1F;
                matrixStack.translate((float) (-bedOrientation.getStepX()) * eyeHeight, 0.0D, (double) ((float) (-bedOrientation.getStepZ()) * eyeHeight));
            }
        }

        float limbSwing = this.getBob(entity, partialTicks);
        this.setupRotations(entity, matrixStack, limbSwing, yaw, partialTicks);
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entity, matrixStack, partialTicks);
        matrixStack.translate(0.0D, (double) -1.501F, 0.0D);

        float animationSpeedOld = 0.0F;
        float animationPosition = 0.0F;

        if (!shouldSit && entity.isAlive()) {
            animationSpeedOld = MathHelper.lerp(partialTicks, entity.animationSpeedOld, entity.animationSpeed);
            animationPosition = entity.animationPosition - entity.animationSpeed * (1.0F - partialTicks);

            if (entity.isBaby()) {
                animationPosition *= 3.0F;
            }

            if (animationSpeedOld > 1.0F) {
                animationSpeedOld = 1.0F;
            }
        }

        this.model.prepareMobModel(entity, animationPosition, animationSpeedOld, partialTicks);
        this.model.setupAnim(entity, animationPosition, animationSpeedOld, limbSwing, headPitch, pitch);

        IVertexBuilder vertexBuilder = buffer.getBuffer(CrysisRenders.energySwirl(getOverlayTexture(entity), f * 0.01F, f * 0.01F));
        this.model.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, alpha);

        matrixStack.popPose();
    }

    @Unique
    public void crysisMod$renderArmorOverlay(MatrixStack matrixStack, IRenderTypeBuffer buffer, AbstractClientPlayerEntity entity, float partialTicks, int packedLight){
        float f = (float)entity.tickCount + partialTicks;

        this.model.attackTime = this.getAttackAnim(entity, partialTicks);

        boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null && entity.getVehicle().shouldRiderSit());
        this.model.riding = shouldSit;
        this.model.young = entity.isBaby();

        float yaw = MathHelper.rotLerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        float headYaw = MathHelper.rotLerp(partialTicks, entity.yHeadRotO, entity.yHeadRot);
        float headPitch = headYaw - yaw;

        if (shouldSit && entity.getVehicle() instanceof LivingEntity) {
            LivingEntity ridingEntity = (LivingEntity) entity.getVehicle();
            yaw = MathHelper.rotLerp(partialTicks, ridingEntity.yBodyRotO, ridingEntity.yBodyRot);
            headPitch = headYaw - yaw;

            float pitchWrapped = MathHelper.wrapDegrees(headPitch);
            if (pitchWrapped < -85.0F) {
                pitchWrapped = -85.0F;
            }

            if (pitchWrapped >= 85.0F) {
                pitchWrapped = 85.0F;
            }

            yaw = headYaw - pitchWrapped;

            if (pitchWrapped * pitchWrapped > 2500.0F) {
                yaw += pitchWrapped * 0.2F;
            }

            headPitch = headYaw - yaw;
        }

        float pitch = MathHelper.lerp(partialTicks, entity.xRotO, entity.xRot);

        if (entity.getPose() == Pose.SLEEPING) {
            Direction bedOrientation = entity.getBedOrientation();

            if (bedOrientation != null) {
                float eyeHeight = entity.getEyeHeight(Pose.STANDING) - 0.1F;
                matrixStack.translate((float) (-bedOrientation.getStepX()) * eyeHeight, 0.0D, (double) ((float) (-bedOrientation.getStepZ()) * eyeHeight));
            }
        }

        float limbSwing = this.getBob(entity, partialTicks);
        this.setupRotations(entity, matrixStack, limbSwing, yaw, partialTicks);
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entity, matrixStack, partialTicks);
        matrixStack.translate(0.0D, (double) -1.501F, 0.0D);

        float animationSpeedOld = 0.0F;
        float animationPosition = 0.0F;

        if (!shouldSit && entity.isAlive()) {
            animationSpeedOld = MathHelper.lerp(partialTicks, entity.animationSpeedOld, entity.animationSpeed);
            animationPosition = entity.animationPosition - entity.animationSpeed * (1.0F - partialTicks);

            if (entity.isBaby()) {
                animationPosition *= 3.0F;
            }

            if (animationSpeedOld > 1.0F) {
                animationSpeedOld = 1.0F;
            }
        }

        this.model.prepareMobModel(entity, animationPosition, animationSpeedOld, partialTicks);
        this.model.setupAnim(entity, animationPosition, animationSpeedOld, limbSwing, headPitch, pitch);

        matrixStack.scale(1.4f, 1.2f, 1.4f);
        matrixStack.translate(0, -0.05, 0);

        IVertexBuilder vertexBuilder = buffer.getBuffer(CrysisRenders.energySwirl(getOverlayTexture(entity), f * -0.01F, f * 0.005F));
        this.model.renderToBuffer(matrixStack, vertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);

        matrixStack.popPose();
    }

    public ResourceLocation getOverlayTexture(AbstractClientPlayerEntity entity) {
        try {
            INanosuitCapability nanosuitCapability = entity.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
            if (entity.hasEffect(Effects.INVISIBILITY)) {
                return new ResourceLocation("crysis", "textures/effects/stealth.png");
            }
            if (nanosuitCapability.getMode().equals(NanosuitModes.ARMOR)) {
                return new ResourceLocation("crysis", "textures/effects/armor.png");
            }
        } catch (Exception ignored){

        }

        return new ResourceLocation("crysis", "textures/effects/stealth.png");
    }
}
