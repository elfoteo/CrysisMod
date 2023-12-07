package com.elfoteo.crysis.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class NanosuitModeProvider implements ICapabilitySerializable<INBT> {

    @CapabilityInject(INanosuitModeCapability.class)
    public static final Capability<INanosuitModeCapability> NANOSUIT_CAPABILITY = null;

    private LazyOptional<INanosuitModeCapability> instance = LazyOptional.of(NANOSUIT_CAPABILITY::getDefaultInstance);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == NANOSUIT_CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return NANOSUIT_CAPABILITY.getStorage().writeNBT(NANOSUIT_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        NANOSUIT_CAPABILITY.getStorage().readNBT(NANOSUIT_CAPABILITY, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);
    }

}