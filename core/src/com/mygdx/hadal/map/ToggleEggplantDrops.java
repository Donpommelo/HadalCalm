package com.mygdx.hadal.map;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

/**
 * This mode setting is used for modes where eggplants are spawned.
 * @author Twonkeldebeast Twidah
 */
public class ToggleEggplantDrops extends ModeSetting {

    private static final float scrapMultiplier = 0.33f;
    private static final int baseScrapDrop = 1;

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageTypes... tags) {
        User user = HadalGame.server.getUsers().get(vic.getConnID());
        if (user != null) {
            SavedPlayerFields field = user.getScores();
            int score = (int) (field.getScore() * scrapMultiplier);
            if (score < 0) {
                score = 0;
            }
            state.getMode().processPlayerScoreChange(state, vic, -score);
            WeaponUtils.spawnScrap(state, score + baseScrapDrop, vic.getPixelPosition(), true, true);
        }
    }
}
