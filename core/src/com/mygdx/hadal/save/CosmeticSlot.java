package com.mygdx.hadal.save;

public enum CosmeticSlot {

    HAT1("HAT1", 0),
    HAT2("HAT2", 1),
    EYE("EYE", 2),
    NOSE("NOSE", 3),
    MOUTH("MOUTH", 4),

    ;

    private final String slotName;
    private final int slotNumber;

    CosmeticSlot(String slotName, int slotNumber) {
        this.slotName = slotName;
        this.slotNumber = slotNumber;
    }

    public String getSlotName() { return slotName; }

    public int getSlotNumber() { return slotNumber; }
}
