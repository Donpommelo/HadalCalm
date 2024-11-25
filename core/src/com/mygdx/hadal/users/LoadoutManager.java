package com.mygdx.hadal.users;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.save.SavedLoadout;

/**
 * LoadoutManager keeps track of the user's loadout.
 * There are 3 types of Loadout
 * savedLoadout: This is set when the player changes their loadout in the hub. (any time their save file is updated)
 * This is used when respawning a player and received by the server when the client has their player created.
 * Clients don't need to know anyone else's savedLoadout, since server is responsible for respawning.
 * <p>
 * activeLoadout: This is the user's player's current loadout (formerly loadout in playerBodyData)
 * arcadeLoadout: exclusive to arcade mode; includes artifacts bought from thte vending machine
 */
public class LoadoutManager {

    private Loadout savedLoadout, activeLoadout, arcadeLoadout;

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
}
