package com.elfoteo.crysis.capability;

import com.elfoteo.crysis.NanosuitModes;

public interface INanosuitCapability {
    NanosuitModes getMode();
    int getMaxEnergy();
    int getEnergy();
    void cycleMode();
    void setMode(NanosuitModes newMode);
    void setEnergy(int newEnergy);
    float getInvisTransition();
    void stepInvisTransition();
}