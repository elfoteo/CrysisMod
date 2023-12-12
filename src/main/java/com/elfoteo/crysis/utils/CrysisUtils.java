package com.elfoteo.crysis.utils;

import com.elfoteo.crysis.CrysisMod;
import com.elfoteo.crysis.NanosuitModes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.awt.*;

public class CrysisUtils {
    public static String modeToString(NanosuitModes mode) {
        if (mode == NanosuitModes.STEALTH) {
            return "Stealth";
        } else if (mode == NanosuitModes.ARMOR) {
            return "Armor";
        } else if (mode == NanosuitModes.SPEED) {
            return "Speed";
        } else if (mode == NanosuitModes.VISOR) {
            return "Visor";
        } else {
            return "Idle";
        }
    }

    public static boolean canEntityAttack(LivingEntity entity) {
        if (entity != null) {
            // Check if the entity has non-zero attack damage attribute
            ModifiableAttributeInstance attribute = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            if (attribute != null) {
                for (AttributeModifier modifier : attribute.getModifiers()) {
                    if (modifier.getAmount() > 0.0D) {
                        return true;
                    }
                }
            }
            return entity instanceof MonsterEntity;
        }
        return false;
    }

    public static Color getGlowColorForEntity(LivingEntity entity){
        Color color;
        if (canEntityAttack(entity)){
            color = CrysisMod.aggressive;
        }
        else{
            color = CrysisMod.passive;
        }
        if (entity instanceof PlayerEntity){
            color = CrysisMod.neutral;
        }
        return color;
    }
}
