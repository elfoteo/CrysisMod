package com.elfoteo.crysis.mixin_interfaces;

import net.minecraft.client.renderer.model.ModelRenderer;

public interface AgeableModelInterface {
    public Iterable<ModelRenderer> forge1_16_5Test$getBodyParts();
    public Iterable<ModelRenderer> forge1_16_5Test$getHeadParts();
    boolean forge1_16_5Test$isScaleHead();
    float forge1_16_5Test$getBabyHeadScale();
    float forge1_16_5Test$getYHeadOffset();
    float forge1_16_5Test$getZHeadOffset();
    float forge1_16_5Test$getBabyBodyScale();
    float forge1_16_5Test$getBodyYOffset();
}
