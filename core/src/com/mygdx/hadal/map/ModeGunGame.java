package com.mygdx.hadal.map;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ResultsState;
import com.mygdx.hadal.utils.UnlocktoItem;

import static com.mygdx.hadal.save.UnlockEquip.*;
import static com.mygdx.hadal.users.Transition.LONG_FADE_DELAY;

/**
 * This mode makes all players cycle through weapons when killing.
 * The player that cycles through all weapons first is the winner.
 * it process the mode's weapon switching and win condition
 * @author Quamilton Quirfitticelli
 */
public class ModeGunGame extends ModeSetting {

    //this is an ordered list of weapons the player cycles through upon getting kills
    public static final UnlockEquip[] weaponOrder = {TORPEDO_LAUNCHER, CR4PCANNON, CHARGE_BEAM, BOUNCING_BLADE,
            SNIPER_RIFLE, BANANA, ICEBERG, LASER_RIFLE, BOILER, MINIGUN, LASER_GUIDED_ROCKET, BATTERING_RAM,
            MORAYGUN, PARTY_POPPER, TRICK_GUN, DUELING_CORKGUN, TESLA_COIL, FISTICUFFS};

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID) {
        User user = HadalGame.usm.getUsers().get(connID);

        //when a player respawns, set their weapon to their last held weapon, determined by score
        if (null != user) {
            int currentGunIndex = Math.min(user.getScoreManager().getScore(), weaponOrder.length - 1);
            newLoadout.multitools[0] = weaponOrder[currentGunIndex];
        }
    }

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageSource source, DamageTag... tags) {

        //Note; we don't worry about suicides, b/c the perp counts as the world dummy, not the player themselves
        if (perp instanceof Player player) {
            state.getMode().processPlayerScoreChange(state, player, 1);

            if (null != player.getUser()) {
                int currentGunIndex = player.getUser().getScoreManager().getScore();
                if (currentGunIndex < weaponOrder.length) {

                    Loadout loadout = player.getUser().getLoadoutManager().getActiveLoadout();

                    //this sets the player's weapon to the new one and syncs client loadouts
                    player.getEquipHelper().getMultitools()[0] = UnlocktoItem.getUnlock(weaponOrder[currentGunIndex], player);
                    loadout.multitools[0] = weaponOrder[currentGunIndex];
                    player.getEquipHelper().setEquip();
                    player.getEquipHelper().syncServerEquipChange(loadout.multitools);

                    String message = weaponOrder[currentGunIndex].getName() + ": " + currentGunIndex + "/" + weaponOrder.length;
                    state.getUIManager().getKillFeed().sendNotification(message, player);
                } else {
                    //upon finishing all weapons, we end the game
                    state.getEndgameManager().levelEnd(ResultsState.MAGIC_WORD, false, true, LONG_FADE_DELAY);
                }
            }
        }
    }
}
