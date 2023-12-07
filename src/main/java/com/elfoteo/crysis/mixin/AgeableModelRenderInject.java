package com.elfoteo.crysis.mixin;

import com.elfoteo.crysis.AgeableModelInterface;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableModel.class)
public abstract class AgeableModelRenderInject<E extends Entity> extends EntityModel<E> implements AgeableModelInterface {
    @Shadow protected abstract Iterable<ModelRenderer> bodyParts();

    @Shadow protected abstract Iterable<ModelRenderer> headParts();

    @Shadow @Final private boolean scaleHead;

    @Shadow @Final private float babyHeadScale;

    @Shadow @Final private float yHeadOffset;

    @Shadow @Final private float zHeadOffset;

    @Shadow @Final private float babyBodyScale;

    @Shadow @Final private float bodyYOffset;

    @Inject(
            method = "renderToBuffer",
            at = @At("HEAD"),
            cancellable = true)
    protected void renderToBuffer(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (this.young) {
            matrixStack.pushPose();

            if (this.scaleHead) {
                float scaleFactor = 1.5F / this.babyHeadScale;
                matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);
            }

            matrixStack.translate(0.0D, (double) (this.yHeadOffset / 16.0F), (double) (this.zHeadOffset / 16.0F));

            this.headParts().forEach((part) -> {
                part.render(matrixStack, vertexBuilder, light, overlay, red, green, blue, alpha);
            });

            matrixStack.popPose();
            matrixStack.pushPose();

            float bodyScaleFactor = 1.0F / this.babyBodyScale;
            matrixStack.scale(bodyScaleFactor, bodyScaleFactor, bodyScaleFactor);
            matrixStack.translate(0.0D, (double) (this.bodyYOffset / 16.0F), 0.0D);

            this.bodyParts().forEach((part) -> {
                part.render(matrixStack, vertexBuilder, light, overlay, red, green, blue, alpha);

            });

            matrixStack.popPose();
        } else {
            this.headParts().forEach((part) -> {
                part.render(matrixStack, vertexBuilder, light, overlay, red, green, blue, alpha);
            });

            this.bodyParts().forEach((part) -> {
                part.render(matrixStack, vertexBuilder, light, overlay, red, green, blue, alpha);
            });
        }

        ci.cancel();
    }

    public Iterable<ModelRenderer> getBodyParts(){
        return this.bodyParts();
    }

    public Iterable<ModelRenderer> getHeadParts(){
        return this.headParts();
    }

    // Getter for scaleHead
    public boolean isScaleHead() {
        return scaleHead;
    }

    // Getter for babyHeadScale
    public float getBabyHeadScale() {
        return babyHeadScale;
    }

    // Getter for yHeadOffset
    public float getYHeadOffset() {
        return yHeadOffset;
    }

    // Getter for zHeadOffset
    public float getZHeadOffset() {
        return zHeadOffset;
    }

    // Getter for babyBodyScale
    public float getBabyBodyScale() {
        return babyBodyScale;
    }

    // Getter for bodyYOffset
    public float getBodyYOffset() {
        return bodyYOffset;
    }
}
