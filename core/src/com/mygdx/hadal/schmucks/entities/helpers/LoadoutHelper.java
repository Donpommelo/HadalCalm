package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.packets.PacketsLoadout;

/**
 * LoadoutHelper manages the player's loadout.
 * The "true" active loadout is now part of User's loadout manager instead
 * This contains player-specific loadout utility functions that run when player is createed, modified etc
 */
public class LoadoutHelper {

    private final Player player;

    public LoadoutHelper(Player player) {
        this.player = player;
    }

    /**
     * This is called when creating a brand new player with a reset loadout
     */
    public void initLoadout() {
        player.getPlayerData().clearStatuses();

        player.getEquipHelper().initEquips();
        player.getArtifactHelper().syncArtifact(getActiveLoadout().artifacts, false, false);
        player.getMagicHelper().syncMagic(getActiveLoadout().activeItem);

        player.getCosmeticsHelper().syncCosmetics(getActiveLoadout().cosmetics, getActiveLoadout().character);
        player.getCosmeticsHelper().setCharacter(getActiveLoadout().character);
        player.getCosmeticsHelper().setTeam(getActiveLoadout().team);
    }

    /**
     * This is called by the client for players that receive a new loadout from the server.
     * We give the player the new loadout information.
     * @param loadout: The new loadout for the player
     * @param override: should this sync override settings artifact slot limit?
     * @param save: should this loadout be saved to the client's saved loadout?
     */
    public void syncLoadout(Loadout loadout, boolean override, boolean save) {
        Loadout newLoadout = new Loadout(loadout);

        player.getEquipHelper().syncEquip(newLoadout.multitools);
        player.getArtifactHelper().syncArtifact(newLoadout.artifacts, override, save);
        player.getMagicHelper().syncMagic(newLoadout.activeItem);

        player.getCosmeticsHelper().syncCosmetics(newLoadout.cosmetics, loadout.character);
        player.getCosmeticsHelper().setCharacter(newLoadout.character);
        player.getCosmeticsHelper().setTeam(newLoadout.team);

        player.getUser().getLoadoutManager().setActiveLoadout(newLoadout);

        if (player.getUser() != null) {
            player.getUser().setTeamFilter(newLoadout.team);
        }
    }

    /**
     * This is run when transitioning the player into a new map/world or respawning
     * @param newPlayer: the new player that this data belongs to.
     */
    public void updateOldData(PlayerBodyData newPlayer) {
        player.getEquipHelper().updateOldEquips(newPlayer);
        player.getMagicHelper().updateOldMagic(newPlayer);
    }

    public void syncServerWholeLoadoutChange() {
        HadalGame.server.sendToAllTCP(new PacketsLoadout.SyncWholeLoadout(player.getUser().getConnID(), getActiveLoadout(), false));
    }

    private Loadout getActiveLoadout() {
        return player.getUser().getLoadoutManager().getActiveLoadout();
    }
}
