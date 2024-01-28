package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.equip.Equippable;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.equip.misc.NothingWeapon;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.utils.UnlocktoItem;

import java.util.Arrays;

public class LoadoutEquipHelper {

    private Player player;

    //This is a list of the player's weapons
    private Equippable[] multitools;

    private Equippable currentTool;

    //This is the slot number of the player's currently selected weapon
    private int currentSlot;

    //This is the player's last used slot. (Used for switch-to-last-slot button)
    private int lastSlot = 1;

    public LoadoutEquipHelper(Player player) {
        this.player = player;

        //Acquire weapons from loadout
        this.multitools = new Equippable[Loadout.MAX_WEAPON_SLOTS];
        Arrays.fill(multitools, UnlocktoItem.getUnlock(UnlockEquip.NOTHING, player));
        syncEquip(getActiveLoadout().multitools);
    }

    public void initEquips() {
        //Acquire weapons from loadout
        this.multitools = new Equippable[Loadout.MAX_WEAPON_SLOTS];
        Arrays.fill(multitools, UnlocktoItem.getUnlock(UnlockEquip.NOTHING, player));
        syncEquip(getActiveLoadout().multitools);
    }

    /**
     * This syncs a player's equipment
     * @param equip: the player's new set of equippables
     */
    public void syncEquip(UnlockEquip[] equip) {
        for (int i = 0; i < Loadout.MAX_WEAPON_SLOTS; i++) {
            multitools[i] = UnlocktoItem.getUnlock(equip[i], player);
            getActiveLoadout().multitools[i] = equip[i];
        }
        setEquip();
    }

    public void updateOldEquips(Player newPlayer) {
        this.player = newPlayer;
        for (Equippable e : multitools) {
            e.setUser(player);
        }
    }

    /**
     * Player picks up new weapon.
     * @param equip: The new equip to switch in. Replaces current slot if inventory is full.
     */
    public Equippable pickup(Equippable equip) {

        UnlockEquip unlock = UnlockEquip.getUnlockFromEquip(equip.getClass());

        int slotToReplace = currentSlot;

        //if we are picking up "nothing" in the armory, we skip "undesirable weapon check"
        if (!(equip instanceof NothingWeapon)) {
            for (int i = 0; i < getNumWeaponSlots(); i++) {
                if (multitools[i] instanceof NothingWeapon || multitools[i].isOutofAmmo()) {
                    slotToReplace = i;
                    break;
                }
            }
        }

        Equippable old = multitools[slotToReplace];

        multitools[slotToReplace] = equip;
        multitools[slotToReplace].setUser(player);
        currentSlot = slotToReplace;
        setEquip();

        getActiveLoadout().multitools[slotToReplace] = unlock;
        if (player.getState().isServer()) {
            syncServerEquipChange(getActiveLoadout().multitools);
        } else {
            syncClientEquipChange(getActiveLoadout().multitools);
        }
        return old;
    }

    /**
     * This helper function is called when weapon switching to ensure the correct weapon sprite is drawn and that the
     * current weapon is kept track of.
     */
    public void setEquip() {
        if (null == player.getPlayerData()) { return; }

        if (currentTool != null) {
            currentTool.unequip(player.getState());
        }
        currentTool = multitools[currentSlot];
        player.setToolSprite(currentTool.getWeaponSprite().getFrame());

        currentTool.equip(player.getState());

        //This recalcs stats that are tied to weapons. ex: "player receives 50% more damage when x is equipped".
        player.getPlayerData().calcStats();

        //play sounds for weapon switching
        SoundEffect.LOCKANDLOAD.playExclusive(player.getState(), null, player, 0.5f, true);
    }

    /**
     * Player switches to a specified weapon slot
     * @param slot: new weapon slot.
     */
    public void switchWeapon(int slot) {
        if (getNumWeaponSlots() >= slot && slot - 1 != currentSlot) {
            if (!(multitools[slot - 1] instanceof NothingWeapon)) {
                lastSlot = currentSlot;
                currentSlot = slot - 1;
                setEquip();
            }
        }
    }

    /**
     * Player switches to last used weapon slot
     */
    public void switchToLast() {
        if (lastSlot < getNumWeaponSlots()) {
            if (!(multitools[lastSlot] instanceof NothingWeapon)) {
                int tempSlot = lastSlot;
                lastSlot = currentSlot;
                currentSlot = tempSlot;
                setEquip();
            }
        }
    }

    /**
     * Player switches to a weapon slot above current slot, wrapping to end of slots if at first slot. (ignore empty slots)
     * This is also called automatically when running out of a consumable equip.
     */
    public void switchDown() {
        for (int i = 1; i <= getNumWeaponSlots(); i++) {
            if (!(multitools[(currentSlot + i) % getNumWeaponSlots()] instanceof NothingWeapon)) {
                lastSlot = currentSlot;
                currentSlot = (currentSlot + i) % getNumWeaponSlots();
                setEquip();
                return;
            }
        }
    }

    /**
     * Player switches to a weapon slot below current slot, wrapping to end of slots if at last slot. (ignore empty slots)
     */
    public void switchUp() {
        for (int i = 1; i <= getNumWeaponSlots(); i++) {
            if (!(multitools[(getNumWeaponSlots() + (currentSlot - i)) % getNumWeaponSlots()] instanceof NothingWeapon)) {
                lastSlot = currentSlot;
                currentSlot = (getNumWeaponSlots() + (currentSlot - i)) % getNumWeaponSlots();
                setEquip();
                return;
            }
        }
    }

    public void postCalcStats() {
        //check current slot in case stat change affects slot number
        if (currentSlot >= getNumWeaponSlots()) {
            currentSlot = getNumWeaponSlots() - 1;
            setEquip();
        }

        if (currentTool instanceof RangedWeapon ranged) {
            ranged.setClipLeft();
            ranged.setAmmoLeft();
        }
    }

    public void syncServerEquipChange(UnlockEquip[] equip) {
        HadalGame.server.sendToAllTCP(new PacketsLoadout.SyncEquipServer(player.getUser().getConnID(), equip));
    }

    public void syncServerEquipChangeEcho(int connID, UnlockEquip[] equip) {
        HadalGame.server.sendToAllExceptTCP(connID, new PacketsLoadout.SyncEquipServer(player.getUser().getConnID(), equip));
    }

    public void syncClientEquipChange(UnlockEquip[] equip) {
        HadalGame.client.sendTCP(new PacketsLoadout.SyncEquipClient(equip));
    }

    private Loadout getActiveLoadout() {
        return player.getUser().getLoadoutManager().getActiveLoadout();
    }

    /**
     * This returns the number of weapon slots after modifications
     */
    public int getNumWeaponSlots() {
        return Math.min((int) (Loadout.BASE_WEAPON_SLOTS + player.getPlayerData().getStat(Stats.WEAPON_SLOTS)), Loadout.MAX_WEAPON_SLOTS);
    }

    public Equippable[] getMultitools() { return multitools; }

    public Equippable getCurrentTool() { return currentTool; }

    public void setCurrentTool(Equippable currentTool) { this.currentTool = currentTool; }

    public int getCurrentSlot() { return currentSlot; }

    public void setCurrentSlot(int currentSlot) { this.currentSlot = currentSlot; }
}
