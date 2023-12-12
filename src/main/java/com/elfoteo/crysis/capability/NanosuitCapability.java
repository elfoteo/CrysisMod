package com.elfoteo.crysis.capability;

import com.elfoteo.crysis.NanosuitModes;

public class NanosuitCapability implements INanosuitCapability {

    private NanosuitModes currentMode;
    private int energy = 0;
    private float invisTransition = 0;
    private static final int MAX_ENERGY = 1000;

    public NanosuitCapability() {
        setMode(NanosuitModes.IDLE);
    }

    @Override
    public NanosuitModes getMode() {
        return currentMode;
    }

    @Override
    public int getMaxEnergy() {
        return MAX_ENERGY;
    }

    @Override
    public int getEnergy() {
        return energy;
    }

    public void cycleMode(){
        currentMode = intToMode(modeToInt(currentMode)+1);
        if (currentMode == NanosuitModes.VISOR){
            currentMode = intToMode(modeToInt(currentMode)+1);
        }
    }

    @Override
    public void setMode(NanosuitModes newMode) {
        currentMode = newMode;
    }

    @Override
    public void setEnergy(int newEnergy) {
        energy = Math.max(0, Math.min(getMaxEnergy(), newEnergy));
    }

    @Override
    public float getInvisTransition() {
        if (getMode() != NanosuitModes.STEALTH && Math.abs(invisTransition - 1) < 0.0001){
            return 0;
        }
        return invisTransition;
    }

    @Override
    public void stepInvisTransition() {
        if (getMode() != NanosuitModes.STEALTH){
            invisTransition = (float) Math.max(0, Math.min(1, invisTransition+0.02));
        }
        else{
            invisTransition = (float) Math.max(0, Math.min(1, invisTransition-0.2));
        }
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