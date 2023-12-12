package com.elfoteo.crysis.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

public class NanosuitModeStorage implements Capability.IStorage<INanosuitCapability> {

    @Override
    public INBT writeNBT(Capability<INanosuitCapability> capability, INanosuitCapability instance, Direction side) {
        CompoundNBT tag = new CompoundNBT();
        tag.putInt("nanosuitMode", NanosuitCapability.modeToInt(instance.getMode()));
        return tag;
    }

    @Override
    public void readNBT(Capability<INanosuitCapability> capability, INanosuitCapability instance, Direction side, INBT nbt) {
        CompoundNBT tag = new CompoundNBT();
        instance.setMode(NanosuitCapability.intToMode(tag.getInt("nanosuitMode")));
    }

}