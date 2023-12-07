package com.elfoteo.crysis.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class NanosuitModeStorage implements Capability.IStorage<INanosuitModeCapability> {

    @Override
    public INBT writeNBT(Capability<INanosuitModeCapability> capability, INanosuitModeCapability instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("nanosuitMode", NanosuitModeCapability.modeToInt(instance.getMode()));
        return tag;
    }

    @Override
    public void readNBT(Capability<INanosuitModeCapability> capability, INanosuitModeCapability instance, Direction side, INBT nbt) {
        CompoundNBT tag = new CompoundNBT();
        instance.setCurrentMode(NanosuitModeCapability.intToMode(tag.getInt("nanosuitMode")));
    }

}