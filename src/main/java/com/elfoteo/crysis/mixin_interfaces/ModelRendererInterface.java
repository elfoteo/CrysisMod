package com.elfoteo.crysis.mixin_interfaces;

import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.model.ModelRenderer;

public interface  ModelRendererInterface {
    ObjectList<ModelRenderer.ModelBox> forge1_16_5Test$getCubes();
    ObjectList<ModelRenderer> forge1_16_5Test$getChildren();
}
