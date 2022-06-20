package com.mygdx.hadal.save;

/**
 * A CosmeticSlot is a single slot in a player's cosmetic options. Each cosmetic item has a designated slot and equipping
 * an item will unequip any equipped items with the same slot.
 * @author Brarlando Beshire
 */
public enum CosmeticSlot {

    HEAD("HEAD", 0),
    EYE("EYE", 1),
    HAT1("HAT1", 2),
    HAT2("HAT2", 3),
    MOUTH1("MOUTH1", 4),
    MOUTH2("MOUTH2", 5),
    NOSE("NOSE", 6),
    DECAL_HEAD("HEAD DECAL", 8),
    DECAL_BODY("BODY DECAL", 9),

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
