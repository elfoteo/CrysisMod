package com.elfoteo.crysis;

import com.elfoteo.crysis.capability.*;
import com.elfoteo.crysis.capability.INanosuitCapability;
import com.elfoteo.crysis.capability.NanosuitCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@Mod("crysis")
public class CrysisMod {
    public static final String MOD_ID = "crysis";
    public static final KeyBinding CYCLE_POWER = new KeyBinding("key.crysis.cycle_power", GLFW.GLFW_KEY_K, "key.category.crysis");
    public static final KeyBinding TOGGLE_VISOR = new KeyBinding("key.crysis.toggle_visor", GLFW.GLFW_KEY_LEFT_ALT, "key.category.crysis");
    public static final float MARKER_CUBE_SIZE = .25f;
    private static final Map<String, NanosuitModes> nanosuitModeMap = new HashMap<>();
    public static final Color passive = new Color(7, 224, 56);
    public static final Color neutral = new Color(255, 166, 0);
    public static final Color aggressive = new Color(245, 52, 10);
    public static double gammaWithoutNanosuit = Minecraft.getInstance().options.gamma;
    public static void setNanosuitMode(PlayerEntity player, NanosuitModes nanosuitMode) {
        nanosuitModeMap.put(player.getStringUUID(), nanosuitMode);
    }
    public static NanosuitModes getNanosuitMode(PlayerEntity player) {
        return nanosuitModeMap.getOrDefault(player.getStringUUID(), NanosuitModes.IDLE); // Return IDLE if player not found
    }

    public CrysisMod() {
        // Register the mod for modloading
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.register(this);
        modEventBus.addListener(CrysisMod::onCommonSetup);

        // Register the event handler
        MinecraftForge.EVENT_BUS.register(NanosuitHandler.class);
        MinecraftForge.EVENT_BUS.register(ModItems.class);
        //MinecraftForge.EVENT_BUS.register(ForgeEventSubscriber.class);
        MinecraftForge.EVENT_BUS.register(this.getClass());
        ModItems.RegisterArmor(modEventBus);
        ClientRegistry.registerKeyBinding(CYCLE_POWER);
        ClientRegistry.registerKeyBinding(TOGGLE_VISOR);
    }
    public static boolean isWearingFullNanosuit(PlayerEntity player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlotType.HEAD);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlotType.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlotType.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlotType.FEET);

        return helmet.getItem() instanceof ArmorItem && ((ArmorItem) helmet.getItem()).getMaterial() == NanosuitArmorMaterial.NANOSUIT &&
                chestplate.getItem() instanceof ArmorItem && ((ArmorItem) chestplate.getItem()).getMaterial() == NanosuitArmorMaterial.NANOSUIT &&
                leggings.getItem() instanceof ArmorItem && ((ArmorItem) leggings.getItem()).getMaterial() == NanosuitArmorMaterial.NANOSUIT &&
                boots.getItem() instanceof ArmorItem && ((ArmorItem) boots.getItem()).getMaterial() == NanosuitArmorMaterial.NANOSUIT;
    }

    @SubscribeEvent
    public static void onCommonSetup(final FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(INanosuitCapability.class, new NanosuitModeStorage(), NanosuitCapability::new);
    }


    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            event.addCapability(new ResourceLocation(CrysisMod.MOD_ID, "nanosuit"), new NanosuitModeProvider());
        }
    }
}
