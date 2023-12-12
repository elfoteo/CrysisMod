package com.elfoteo.crysis.mixin;

import com.elfoteo.crysis.mixin_interfaces.AgeableModelInterface;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
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
    public Iterable<ModelRenderer> forge1_16_5Test$getBodyParts(){
        return this.bodyParts();
    }

    public Iterable<ModelRenderer> forge1_16_5Test$getHeadParts(){
        return this.headParts();
    }

    // Getter for scaleHead
    public boolean forge1_16_5Test$isScaleHead() {
        return scaleHead;
    }

    // Getter for babyHeadScale
    public float forge1_16_5Test$getBabyHeadScale() {
        return babyHeadScale;
    }

    // Getter for yHeadOffset
    public float forge1_16_5Test$getYHeadOffset() {
        return yHeadOffset;
    }

    // Getter for zHeadOffset
    public float forge1_16_5Test$getZHeadOffset() {
        return zHeadOffset;
    }

    // Getter for babyBodyScale
    public float forge1_16_5Test$getBabyBodyScale() {
        return babyBodyScale;
    }

    // Getter for bodyYOffset
    public float forge1_16_5Test$getBodyYOffset() {
        return bodyYOffset;
    }
}
