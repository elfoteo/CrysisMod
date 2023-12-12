package com.elfoteo.crysis.mixin;
import com.elfoteo.crysis.mixin_interfaces.ModelRendererInterface;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelRenderer.class)
public abstract class ModelRenderInject implements ModelRendererInterface {
    @Shadow @Final private ObjectList<ModelRenderer> children;

    @Shadow @Final private ObjectList<ModelRenderer.ModelBox> cubes;
    @Unique
    public ObjectList<ModelRenderer.ModelBox> forge1_16_5Test$getCubes() {
        return this.cubes;
    }

    @Unique
    public ObjectList<ModelRenderer> forge1_16_5Test$getChildren() {
        return this.children;
    }
}
