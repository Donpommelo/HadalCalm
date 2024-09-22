package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.save.UnlockCharacter;
import com.mygdx.hadal.save.UnlockCosmetic;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.packets.PacketsLoadout;

/**
 * LoadoutCosmeticsHelper manages a player's equipped cosmetics.
 * This includes cosmetics, character skin and color
 */
public class LoadoutCosmeticsHelper {

    private final Player player;

    public LoadoutCosmeticsHelper(Player player) {
        this.player = player;
    }

    /**
     * This syncs a player's cosmetics.
     * Copy the new cosmetics into user active loadout and ensure no incompatible cosmetics are used
     */
    public void syncCosmetics(UnlockCosmetic[] cosmetics, UnlockCharacter character) {
        System.arraycopy(cosmetics, 0, getActiveLoadout().cosmetics, 0, Loadout.MAX_COSMETIC_SLOTS);
        for (int i = 0; i < Loadout.MAX_COSMETIC_SLOTS; i++) {
            if (getActiveLoadout().cosmetics[i].checkCompatibleCharacters(character)) {
                getActiveLoadout().cosmetics[i] = UnlockCosmetic.NOTHING_HAT1;
            }
        }
    }

    public void setCosmetic(UnlockCosmetic cosmetic) {
        getActiveLoadout().cosmetics[cosmetic.getCosmeticSlot().getSlotNumber()] = cosmetic;
    }

    /**
     * This is called when switching teams.
     */
    public void setTeam(AlignmentFilter team) {
        getActiveLoadout().team = team;
        player.setBodySprite(null, team);
        if (player.getUser() != null) {
            player.getUser().setTeamFilter(team);
        }
    }

    /**
     * This is called when switching characters.
     */
    public void setCharacter(UnlockCharacter character) {
        getActiveLoadout().character = character;
        player.setBodySprite(character, null);

        //sync cosmetics to unequip hats that are not compatible with new character
        syncCosmetics(getActiveLoadout().cosmetics, character);
    }

    public void syncServerCharacterChange(UnlockCharacter character) {
        PacketManager.serverTCPAll(player.getState(), new PacketsLoadout.SyncCharacterServer(player.getUser().getConnID(), character));
    }

    public void syncServerTeamChange(AlignmentFilter team) {
        PacketManager.serverTCPAll(player.getState(), new PacketsLoadout.SyncTeamServer(player.getUser().getConnID(), team));
    }

    public void syncServerCosmeticChange(UnlockCosmetic cosmetic) {
        PacketManager.serverTCPAll(player.getState(), new PacketsLoadout.SyncCosmeticServer(player.getUser().getConnID(), cosmetic));
    }

    private Loadout getActiveLoadout() {
        return player.getUser().getLoadoutManager().getActiveLoadout();
    }
}
