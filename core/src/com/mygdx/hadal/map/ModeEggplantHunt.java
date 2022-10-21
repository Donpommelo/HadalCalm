package com.mygdx.hadal.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.PickupUtils;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.bots.RallyPoint;
import com.mygdx.hadal.event.Scrap;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.states.PlayState;

/**
 * This mode setting is used for modes where eggplants are spawned.
 * @author Twonkeldebeast Twidah
 */
public class ModeEggplantHunt extends ModeSetting {

    private static final float SEARCH_RADIUS = 50.0f;
    private static final float EGGPLANT_DESIRE_MULTIPLIER = 0.05f;

    private static final float SCRAP_MULTIPLIER = 0.33f;
    private static final int BASE_SCRAP_DROP = 1;

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageSource source, DamageTag... tags) {
        //null check in case this is an "extra kill" to give summoner kill credit for a summon
        if (null != vic) {
            if (null != vic.getUser()) {

                //upon death, lose eggplants and drop them according to how many you have
                SavedPlayerFields field = vic.getUser().getScores();
                int score = (int) (field.getScore() * SCRAP_MULTIPLIER);
                if (0 > score) {
                    score = 0;
                }
                state.getMode().processPlayerScoreChange(state, vic, -score);
                PickupUtils.spawnScrap(state, vic, vic.getPixelPosition(), vic.getLinearVelocity(),
                        score + BASE_SCRAP_DROP, true, true);
            }
        }
    }

    @Override
    public void processAIPath(PlayState state, PlayerBot bot, Vector2 playerLocation,
                              Array<RallyPoint.RallyPointMultiplier> path) {
        state.getWorld().QueryAABB((fixture -> {
            //check for eggplants in the bot's vicinity and find a path towards a random one
            if (fixture.getUserData() instanceof EventData eventData) {
                if (eventData.getEvent() instanceof Scrap scrap) {

                    bot.getBotController().setEventTarget(scrap);
                    path.add(new RallyPoint.RallyPointMultiplier(BotManager.getNearestPoint(bot, scrap.getPosition()),
                            EGGPLANT_DESIRE_MULTIPLIER));
                    return false;
                }
            }
            return true;
        }), playerLocation.x - SEARCH_RADIUS, playerLocation.y - SEARCH_RADIUS,
        playerLocation.x + SEARCH_RADIUS, playerLocation.y + SEARCH_RADIUS);
        if (path.isEmpty()) {
            bot.getBotController().setEventTarget(null);
        }
    }
}
