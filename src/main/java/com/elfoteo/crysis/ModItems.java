package com.elfoteo.crysis;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = CrysisMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "crysis");

    public static final RegistryObject<Item> NANOSUIT_HELMET = ITEMS.register("nanosuit_helmet",
            () -> new ArmorItem(NanosuitArmorMaterial.NANOSUIT, EquipmentSlotType.HEAD, new Item.Properties().tab(ItemGroup.TAB_COMBAT)));

    public static final RegistryObject<Item> NANOSUIT_CHESTPLATE = ITEMS.register("nanosuit_chestplate",
            () -> new ArmorItem(NanosuitArmorMaterial.NANOSUIT, EquipmentSlotType.CHEST, new Item.Properties().tab(ItemGroup.TAB_COMBAT)));

    public static final RegistryObject<Item> NANOSUIT_LEGGINGS = ITEMS.register("nanosuit_leggings",
            () -> new ArmorItem(NanosuitArmorMaterial.NANOSUIT, EquipmentSlotType.LEGS, new Item.Properties().tab(ItemGroup.TAB_COMBAT)));

    public static final RegistryObject<Item> NANOSUIT_BOOTS = ITEMS.register("nanosuit_boots",
            () -> new ArmorItem(NanosuitArmorMaterial.NANOSUIT, EquipmentSlotType.FEET, new Item.Properties().tab(ItemGroup.TAB_COMBAT)));

    public static void RegisterArmor(IEventBus event){
        ITEMS.register(event);
    }
}
