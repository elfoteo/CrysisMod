package com.elfoteo.crysis;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;

public class NanosuitArmorItem extends ArmorItem {
    public NanosuitArmorItem(IArmorMaterial material, EquipmentSlotType slot, Item.Properties properties) {
        super(material, slot, properties);
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }
}
