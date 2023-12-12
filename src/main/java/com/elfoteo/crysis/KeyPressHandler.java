package com.elfoteo.crysis;

import com.elfoteo.crysis.capability.INanosuitCapability;
import com.elfoteo.crysis.capability.NanosuitModeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = CrysisMod.MOD_ID, value = Dist.CLIENT, bus = Bus.FORGE)
public class KeyPressHandler {

    private static boolean cyclePowerKeyPressed = false;
    private static boolean toggleVisorKeyPressed = false;
    private static NanosuitModes oldMode = NanosuitModes.IDLE;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (CrysisMod.TOGGLE_VISOR.isDown()){
                toggleVisorKeyPressed = true;
                toggleVisorDown();
            }
            else{
                // On key released
                if (toggleVisorKeyPressed) {
                    PlayerEntity player = Minecraft.getInstance().player;
                    if (player != null) {
                        INanosuitCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
                        nanosuitCapability.setMode(oldMode);
                    }
                }
                toggleVisorKeyPressed = false;
                if (CrysisMod.CYCLE_POWER.isDown()) {
                    // Key is pressed
                    if (!cyclePowerKeyPressed) {
                        // Key was not pressed in the previous tick
                        cyclePowerPress();
                    }
                    cyclePowerKeyPressed = true;
                } else {
                    // Key is not pressed
                    cyclePowerKeyPressed = false;
                }
            }
        }
    }

    private static void toggleVisorDown() {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null){
            INanosuitCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
            if (nanosuitCapability.getMode() != NanosuitModes.VISOR){
                oldMode = nanosuitCapability.getMode();
            }
            if (nanosuitCapability.getEnergy() >= 1){
                nanosuitCapability.setMode(NanosuitModes.VISOR);
            }
        }
    }

    private static void cyclePowerPress() {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null){
            INanosuitCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
            nanosuitCapability.cycleMode();
        }
    }
}
