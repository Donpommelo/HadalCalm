package com.mygdx.hadal.users;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.save.SavedLoadout;
import com.mygdx.hadal.save.UnlockEquip;

/**
 * LoadoutManager keeps track of the user's loadout.
 * There are 2 types of Loadout
 * savedLoadout: This is set when the player changes their loadout in the hub. (any time their save file is updated)
 * This is used when respawning a player and received by the server when the client has their player created.
 * Clients don't need to know anyone else's savedLoadout, since server is responsible for respawning.
 * <p>
 * activeLoadout: This is the user's player's current loadout (formerly loadout in playerBodyData)
 */
public class LoadoutManager {

    private Loadout savedLoadout, activeLoadout, arcadeLoadout;

    //player's primary weapon in their last saved loadout. Only used for clients for the effect of a single artifact
    private UnlockEquip lastEquippedPrimary = UnlockEquip.NOTHING;

    public LoadoutManager(Loadout savedLoadout) {
        this.savedLoadout = savedLoadout;
        this.activeLoadout = savedLoadout;
        this.arcadeLoadout = new Loadout(SavedLoadout.createNewLoadout());
    }

    public Loadout getSavedLoadout() { return savedLoadout; }

    public void setSavedLoadout(Loadout savedLoadout) { this.savedLoadout = savedLoadout; }

    public Loadout getActiveLoadout() { return activeLoadout; }

    public void setActiveLoadout(Loadout activeLoadout) { this.activeLoadout = activeLoadout; }

    public Loadout getArcadeLoadout() { return arcadeLoadout; }

    public void setArcadeLoadout(Loadout arcadeLoadout) { this.arcadeLoadout = arcadeLoadout; }

    public UnlockEquip getLastEquippedPrimary() { return lastEquippedPrimary; }

    public void setLastEquippedPrimary(UnlockEquip lastEquippedPrimary) { this.lastEquippedPrimary = lastEquippedPrimary; }
}
