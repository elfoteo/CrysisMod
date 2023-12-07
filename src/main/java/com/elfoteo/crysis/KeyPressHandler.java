package com.elfoteo.crysis;

import com.elfoteo.crysis.capability.INanosuitModeCapability;
import com.elfoteo.crysis.capability.NanosuitModeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod(CrysisMod.MOD_ID)
@EventBusSubscriber(modid = CrysisMod.MOD_ID, value = Dist.CLIENT, bus = Bus.FORGE)
public class KeyPressHandler {

    private static boolean isKeyPressed = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            // Check for keypress, for example, the 'K' key (change as needed)
            if (CrysisMod.CYCLE_POWER.isDown()) {
                // Key is pressed
                if (!isKeyPressed) {
                    // Key was not pressed in the previous tick
                    onKeyPress();
                }
                isKeyPressed = true;
            } else {
                // Key is not pressed
                isKeyPressed = false;
            }
        }
    }

    private static void onKeyPress() {
        // Handle the keypress here
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null){
            INanosuitModeCapability nanosuitCapability = player.getCapability(NanosuitModeProvider.NANOSUIT_CAPABILITY).orElse(null);
            nanosuitCapability.cycleMode();
        }
    }
}
