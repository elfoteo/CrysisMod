package com.elfoteo.crysis;

import net.minecraft.client.renderer.model.ModelRenderer;

public interface AgeableModelInterface {
    public Iterable<ModelRenderer> getBodyParts();
    public Iterable<ModelRenderer> getHeadParts();
    boolean isScaleHead();
    float getBabyHeadScale();
    float getYHeadOffset();
    float getZHeadOffset();
    float getBabyBodyScale();
    float getBodyYOffset();
}
