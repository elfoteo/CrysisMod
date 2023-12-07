package com.elfoteo.crysis.mixin;
import com.elfoteo.crysis.CrysisMod;
import com.elfoteo.crysis.ModelRendererInterface;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelRenderer.class)
public abstract class ModelRenderInject implements ModelRendererInterface {
    @Shadow public boolean visible;
    @Shadow public abstract void translateAndRotate(MatrixStack p_228307_1_);

    @Shadow @Final private ObjectList<ModelRenderer> children;

    @Shadow @Final private ObjectList<ModelRenderer.ModelBox> cubes;

    @Shadow protected abstract void compile(MatrixStack.Entry matrixStackEntry, IVertexBuilder vertexBuilder, int light, int overlay, float red, float green, float blue, float alpha);



    @Inject(
            method = "render(Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;IIFFFF)V",
            at = @At("HEAD"),
            cancellable = true)
    protected void render(MatrixStack matrixStack, IVertexBuilder vertexBuilder, int light, int overlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (this.visible) {
            if (!this.cubes.isEmpty() || !this.children.isEmpty()) {
                matrixStack.pushPose();
                this.translateAndRotate(matrixStack);
                this.compile(matrixStack.last(), vertexBuilder, light, overlay, red, green, blue, alpha);

                for (ModelRenderer modelRenderer : this.children) {
                    modelRenderer.render(matrixStack, vertexBuilder, light, overlay, red, green, blue, alpha);
                    // TODO: add here the custom glowing
                    // for (ModelRenderer.ModelBox cube : this.cubes) {
                    //     CrysisMod.renderSpecialSquare(matrixStack, vertexBuilder, cube.minX, cube.minY, cube.minZ, cube.maxX, cube.maxY, cube.maxZ, 255, 0, 0);
                    // }
                }
                matrixStack.popPose();
            }
        }
        ci.cancel();
    }


    @Unique
    public ObjectList<ModelRenderer.ModelBox> forge1_16_5Test$getCubes() {
        return this.cubes;
    }

    @Unique
    public ObjectList<ModelRenderer> forge1_16_5Test$getChildren() {
        return this.children;
    }
}
