package com.elfoteo.crysis;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class NanosuitArmorMaterial implements IArmorMaterial {
    private static final int[] ARMOR_VALUES = {3, 6, 8, 3};
    public static final NanosuitArmorMaterial NANOSUIT = new NanosuitArmorMaterial();

    @Override
    public int getDurabilityForSlot(EquipmentSlotType slotIn) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlotType slotIn) {
        return ARMOR_VALUES[slotIn.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_CHAIN;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return null;
    }

    @Override
    public String getName() {
        return "crysis:nanosuit";
    }

    @Override
    public float getToughness() {
        return 2.0F;
    }

    @Override
    public float getKnockbackResistance() {
        return 0.0F;
    }
}
