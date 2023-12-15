package com.elfoteo.crysis;

import com.elfoteo.crysis.capability.INanosuitCapability;
import com.elfoteo.crysis.capability.NanosuitModeProvider;
import com.elfoteo.crysis.utils.CrysisUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrysisMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NanosuitOverlay {

    private static final ResourceLocation FRAME = new ResourceLocation(CrysisMod.MOD_ID, "textures/overlay/progress_bar_empty.png");
    private static final ResourceLocation FILLED = new ResourceLocation(CrysisMod.MOD_ID, "textures/overlay/progress_bar_filled.png");
    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null) {
            if (!CrysisMod.isWearingFullNanosuit(player)) {
                return;
            }

            // Check if it's the correct overlay event (in this case, POST)
            if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
                return;
            }

            // Get Minecraft instance
            Minecraft mc = Minecraft.getInstance();
            FontRenderer fontRenderer = mc.font;

            try{
                INanosuitCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
                String text = CrysisUtils.modeToString(nanosuitCapability.getMode());

                int x = 75;
                int y = event.getWindow().getGuiScaledHeight() - 10 - fontRenderer.lineHeight;

                fontRenderer.draw(event.getMatrixStack(), text, x, y, 0xFFFFFF);

                // Render the texture
                // Calculate progress value between 0.0 and 1.0 (e.g., 0.5 for 50% progress)
                float progress = Math.round(((float) nanosuitCapability.getEnergy() / nanosuitCapability.getMaxEnergy()) * 55) / 55.0f;

                int xOffset = 5;
                int yOffset = event.getWindow().getGuiScaledHeight() - 24;

                RenderSystem.disableDepthTest();
                RenderSystem.depthMask(false);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                mc.getTextureManager().bind(FRAME);
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuilder();
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.vertex(0.0D + xOffset, 18 + yOffset, -90.0D).uv(0.0F, 1.0F).endVertex();
                bufferbuilder.vertex(66.0D + xOffset, 18 + yOffset, -90.0D).uv(1.0F, 1.0F).endVertex();
                bufferbuilder.vertex(66.0D + xOffset, 0.0D + yOffset, -90.0D).uv(1.0F, 0.0F).endVertex();
                bufferbuilder.vertex(0.0D + xOffset, 0.0D + yOffset, -90.0D).uv(0.0F, 0.0F).endVertex();
                tessellator.end();

                // Calculate the width based on progress
                double width = 55 * progress;

                mc.getTextureManager().bind(FILLED);
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.vertex(0.0D + xOffset + 11, 12 + yOffset + 3, -90.0D).uv(0.0F, 1.0F).endVertex();
                bufferbuilder.vertex(width + xOffset + 11, 12 + yOffset + 3, -90.0D).uv(progress, 1.0F).endVertex();  // Adjust uv for progress
                bufferbuilder.vertex(width + xOffset + 11, 0.0D + yOffset + 3, -90.0D).uv(progress, 0.0F).endVertex();  // Adjust uv for progress
                bufferbuilder.vertex(0.0D + xOffset + 11, 0.0D + yOffset + 3, -90.0D).uv(0.0F, 0.0F).endVertex();

                tessellator.end();

                RenderSystem.depthMask(true);
                RenderSystem.enableDepthTest();
                RenderSystem.disableBlend();
            }
            catch (Exception ex){

            }
        }
    }
}
