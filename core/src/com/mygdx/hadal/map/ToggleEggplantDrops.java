package com.mygdx.hadal.map;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.bots.RallyPath;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.event.Scrap;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.PlayerBot;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.server.SavedPlayerFields;
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
        SavedPlayerFields field = vic.getUser().getScores();
        int score = (int) (field.getScore() * scrapMultiplier);
        if (score < 0) {
            score = 0;
        }
        state.getMode().processPlayerScoreChange(state, vic, -score);
        WeaponUtils.spawnScrap(state, score + baseScrapDrop, vic.getPixelPosition(), true, true);
    }

    private static final float searchRadius = 300.0f;
    private static final float eggplantDesireMultiplier = 0.1f;
    @Override
    public RallyPath processAIPath(PlayState state, GameMode mode, PlayerBot p, Vector2 playerLocation, Vector2 playerVelocity) {
        final RallyPath[] bestPath = new RallyPath[1];
        state.getWorld().QueryAABB((fixture -> {
            if (bestPath[0] == null) {
                if (fixture.getUserData() instanceof final EventData eventData) {
                    if (eventData.getEvent() instanceof final Scrap scrap) {
                        RallyPath tempPath = BotManager.getShortestPathBetweenLocations(state.getWorld(), playerLocation,
                                scrap.getPosition(), playerVelocity);
                        if (tempPath != null) {
                            p.getBotController().setMoveTarget(scrap);
                            bestPath[0] = tempPath;
                        }
                    }
                }
            }
            return true;
        }), playerLocation.x - searchRadius, playerLocation.y - searchRadius,
        playerLocation.x + searchRadius, playerLocation.y + searchRadius);

        if (bestPath[0] != null) {
            bestPath[0].setDistance(bestPath[0].getDistance() * eggplantDesireMultiplier);
        }

        return bestPath[0];
    }
}
