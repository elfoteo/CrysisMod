package com.elfoteo.crysis;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Objects;

public class CrysisRenders {
    private static final RenderState.CullState NO_CULL = new RenderState.CullState(false);
    private static final RenderState.DepthTestState NO_DEPTH_TEST = new RenderState.DepthTestState("always", 519);
    private static final RenderState.AlphaState DEFAULT_ALPHA = new RenderState.AlphaState(0.003921569F);

    private static final RenderState.TexturingState OUTLINE_TEXTURING = new RenderState.TexturingState("outline_texturing", RenderSystem::setupOutline, RenderSystem::teardownOutline);

    private static final RenderState.FogState NO_FOG = new RenderState.FogState("no_fog", () -> {
    }, () -> {
    });

    private static final RenderState.TargetState OUTLINE_TARGET = new RenderState.TargetState("outline_target", () -> {
        Objects.requireNonNull(Minecraft.getInstance().levelRenderer.entityTarget()).bindWrite(false);
    }, () -> {
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    });
    public static final RenderType GLOWING = RenderType.create("outline", DefaultVertexFormats.POSITION_COLOR, 7, 256,
            RenderType.State.builder()
                    .setCullState(NO_CULL)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setAlphaState(DEFAULT_ALPHA)
                    .setTexturingState(OUTLINE_TEXTURING)
                    .setFogState(NO_FOG)
                    .setOutputState(OUTLINE_TARGET)
                    .createCompositeState(true));
    protected static final RenderState.FogState BLACK_FOG = new RenderState.FogState("black_fog", () -> {
        RenderSystem.fog(2918, 0.0F, 0.0F, 0.0F, 1.0F);
        RenderSystem.enableFog();
    }, () -> {
        FogRenderer.levelFogColor();
        RenderSystem.disableFog();
    });
    protected static final RenderState.TransparencyState ADDITIVE_TRANSPARENCY = new RenderState.TransparencyState("additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderState.DiffuseLightingState DIFFUSE_LIGHTING = new RenderState.DiffuseLightingState(true);
    protected static final RenderState.OverlayState OVERLAY = new RenderState.OverlayState(true);
    protected static final RenderState.LightmapState LIGHTMAP = new RenderState.LightmapState(true);
    protected static final RenderState.TransparencyState NO_TRANSPARENCY = new RenderState.TransparencyState("no_transparency", () -> {
        RenderSystem.disableBlend();
    }, () -> {
    });
    protected static final RenderState.TransparencyState TRANSLUCENT_TRANSPARENCY = new RenderState.TransparencyState("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    public static RenderType energySwirl(ResourceLocation p_228636_0_, float p_228636_1_, float p_228636_2_) {
        return RenderType.create("energy_swirl", DefaultVertexFormats.NEW_ENTITY, 7,
                256, false, true,
                RenderType.State.builder().setTextureState(
                        new RenderState.TextureState(p_228636_0_, false, false))
                        .setTexturingState(new RenderState.OffsetTexturingState(p_228636_1_, p_228636_2_))
                        .setFogState(BLACK_FOG)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setDiffuseLightingState(DIFFUSE_LIGHTING)
                        .setAlphaState(DEFAULT_ALPHA)
                        .setCullState(NO_CULL)
                        .setLightmapState(LIGHTMAP)
                        .setOverlayState(OVERLAY)
                        .createCompositeState(false));
    }
}
