package com.elfoteo.crysis.capability;

import com.elfoteo.crysis.NanosuitModes;

public class NanosuitModeCapability implements INanosuitModeCapability {

    private NanosuitModes currentMode;

    public NanosuitModeCapability() {
        setCurrentMode(NanosuitModes.IDLE);
    }

    @Override
    public NanosuitModes getMode() {
        return currentMode;
    }

    public void cycleMode(){
        currentMode = intToMode(modeToInt(currentMode)+1);
    }

    @Override
    public void setCurrentMode(NanosuitModes newMode) {
        currentMode = newMode;
    }

    public static NanosuitModes intToMode(int mode){
        switch (mode){
            case 1:
                return NanosuitModes.STEALTH;
            case 2:
                return NanosuitModes.ARMOR;
            case 3:
                return NanosuitModes.SPEED;
            case 4:
                return NanosuitModes.VISOR;
            default:
                return NanosuitModes.IDLE;
        }
    }

    public static int modeToInt(NanosuitModes nanosuitMode) {
        switch (nanosuitMode) {
            case STEALTH:
                return 1;
            case ARMOR:
                return 2;
            case SPEED:
                return 3;
            case VISOR:
                return 4;
            default:
                return 0; // or any other default value as needed
        }
    }
}