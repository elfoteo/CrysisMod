package com.elfoteo.crysis;

import com.elfoteo.crysis.capability.INanosuitModeCapability;
import com.elfoteo.crysis.capability.NanosuitModeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CrysisMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NanosuitOverlay {

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null){
            if (!CrysisMod.isWearingFullNanosuit(player)){
                return;
            }
            // Check if it's the correct overlay event (in this case, TEXT)
            if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) {
                return;
            }

            // Get Minecraft instance
            Minecraft mc = Minecraft.getInstance();
            FontRenderer fontRenderer = mc.font;

            // Your text to be displayed
            INanosuitModeCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
            String text = CrysisMod.modeToString(nanosuitCapability.getMode());

            // Calculate position at the bottom left
            int x = 5; // Adjust as needed
            int y = event.getWindow().getGuiScaledHeight() - 10 - fontRenderer.lineHeight;

            // Render the text
            fontRenderer.draw(event.getMatrixStack(), text, x, y, 0xFFFFFF);
        }
    }
}
