package com.mygdx.hadal.save;

import com.mygdx.hadal.text.UIText;

/**
 * A CosmeticSlot is a single slot in a player's cosmetic options. Each cosmetic item has a designated slot and equipping
 * an item will unequip any equipped items with the same slot.
 * @author Brarlando Beshire
 */
public enum CosmeticSlot {

    HEAD(UIText.SLOT_HEAD, 0),
    EYE(UIText.SLOT_EYE, 1),
    HAT1(UIText.SLOT_HAT1, 2),
    HAT2(UIText.SLOT_HAT2, 3),
    MOUTH1(UIText.SLOT_MOUTH1, 4),
    MOUTH2(UIText.SLOT_MOUTH2, 5),
    NOSE(UIText.SLOT_NOSE, 6),
    DECAL_HEAD(UIText.SLOT_DECAL_HEAD, 8),
    DECAL_BODY(UIText.SLOT_DECAL_BODY, 9),

    ;

    private final String slotName;
    private final int slotNumber;

    CosmeticSlot(UIText slotName, int slotNumber) {
        this.slotName = slotName.text();
        this.slotNumber = slotNumber;
    }

    public String getSlotName() { return slotName; }

    public int getSlotNumber() { return slotNumber; }
}
