package com.mygdx.hadal.schmucks.entities.helpers;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.save.UnlockActives;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.packets.PacketsLoadout;
import com.mygdx.hadal.utils.UnlocktoItem;

public class LoadoutMagicHelper {

    private Player player;

    //This is the player's active item
    private ActiveItem magic;

    public LoadoutMagicHelper(Player player) {
        this.player = player;
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

    public void updateOldMagic(Player newPlayer) {
        this.player = newPlayer;
        magic.setUser(newPlayer);
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
        HadalGame.server.sendToAllTCP(new PacketsLoadout.SyncActiveServer(player.getUser().getConnID(), active));
    }

    public void syncServerMagicChangeEcho(int connID, UnlockActives active) {
        HadalGame.server.sendToAllExceptTCP(connID, new PacketsLoadout.SyncActiveServer(player.getUser().getConnID(), active));
    }

    public void syncClientMagicChange(UnlockActives active) {
        HadalGame.client.sendTCP(new PacketsLoadout.SyncActiveClient(active));
    }

    private Loadout getActiveLoadout() {
        return player.getUser().getLoadoutManager().getActiveLoadout();
    }

    public ActiveItem getMagic() { return magic; }
}
