package com.mygdx.hadal.save;

/**
 * A CosmeticSlot is a single slot in a player's cosmetic options. Each cosmetic item has a designated slot and equipping
 * an item will unequip any equipped items with the same slot.
 * @author Brarlando Beshire
 */
public enum CosmeticSlot {

    HEAD("HEAD", 0),
    HAT1("HAT1", 1),
    HAT2("HAT2", 2),
    EYE("EYE", 3),
    NOSE("NOSE", 4),
    MOUTH("MOUTH", 5),
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
