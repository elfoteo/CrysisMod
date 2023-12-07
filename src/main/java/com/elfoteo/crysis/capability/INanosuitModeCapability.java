package com.elfoteo.crysis.capability;

import com.elfoteo.crysis.NanosuitModes;

public interface INanosuitModeCapability {
    NanosuitModes getMode();
    void cycleMode();
    void setCurrentMode(NanosuitModes newMode);
}