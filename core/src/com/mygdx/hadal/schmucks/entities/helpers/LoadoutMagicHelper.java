package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.utils.UnlocktoItem;

/**
 * LoadoutMagicHelper manages a player's Magic
 */
public class LoadoutMagicHelper {

    private final Player player;

    //This is the player's active item
    private ActiveItem magic;

    public LoadoutMagicHelper(Player player) {
        this.player = player;

        //Acquire magic from loadout. Do this on initializingg to avoid null magic for ui purposes
        this.magic = UnlocktoItem.getUnlock(UnlockActives.NOTHING, player);
    }

    /**
     * This syncs a player's active item
     * @param active: the player's new active item
     */
    public void syncMagic(UnlockActives active) {
        this.magic = UnlocktoItem.getUnlock(active, player);
        getActiveLoadout().activeItem = active;
    }

    /**
     * This is run when using old playerData to create new player.
     * Take old magic and apply to new player
     */
    public void updateOldMagic(PlayerBodyData newPlayer) {
        magic = newPlayer.getPlayer().getMagicHelper().getMagic();
        magic.setUser(player);
    }

    /**
     * Player picks up a new Active Item.
     * @param item: Old item if nonempty and a Nothing Item otherwise
     */
    public void pickup(ActiveItem item) {

        UnlockActives unlock = UnlockActives.getUnlockFromActive(item.getClass());
        getActiveLoadout().activeItem = unlock;

        magic = item;
        magic.setUser(player);

        //active items start off charged in the hub
        if (player.getState().getMode().isHub()) {
            magic.setCurrentChargePercent(1.0f);
        } else {
            magic.setCurrentChargePercent(player.getPlayerData().getStat(Stats.STARTING_CHARGE));
        }

        if (player.getState().isServer()) {
            syncServerMagicChange(unlock);
        } else {
            syncClientMagicChange(unlock);
        }
    }

    public void syncServerMagicChange(UnlockActives active) {
        PacketManager.serverTCPAll(new PacketsLoadout.SyncActiveServer(player.getUser().getConnID(), active));
    }

    public void syncServerMagicChangeEcho(int connID, UnlockActives active) {
        PacketManager.serverTCPAllExcept(connID, new PacketsLoadout.SyncActiveServer(player.getUser().getConnID(), active));
    }

    public void syncClientMagicChange(UnlockActives active) {
        PacketManager.clientTCP(new PacketsLoadout.SyncActiveClient(active));
    }

    private Loadout getActiveLoadout() {
        return player.getUser().getLoadoutManager().getActiveLoadout();
    }

    public ActiveItem getMagic() { return magic; }
}
