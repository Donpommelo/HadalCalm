package com.mygdx.hadal.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.battle.WeaponUtils;
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

    private static final float scrapMultiplier = 0.33f;
    private static final int baseScrapDrop = 1;

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageSource source, DamageTag... tags) {
        //null check in case this is an "extra kill" to give summoner kill credit for a summon
        if (vic != null) {
            if (vic.getUser() != null) {

                //upon death, lose eggplants and drop them according to how many you have
                SavedPlayerFields field = vic.getUser().getScores();
                int score = (int) (field.getScore() * scrapMultiplier);
                if (score < 0) {
                    score = 0;
                }
                state.getMode().processPlayerScoreChange(state, vic, -score);
                WeaponUtils.spawnScrap(state, score + baseScrapDrop, vic.getPixelPosition(), true, true);
            }
        }
    }

    private static final float searchRadius = 50.0f;
    private static final float eggplantDesireMultiplier = 0.05f;
    @Override
    public void processAIPath(PlayState state, PlayerBot bot, Vector2 playerLocation,
                              Array<RallyPoint.RallyPointMultiplier> path) {
        state.getWorld().QueryAABB((fixture -> {
            //check for eggplants in thte bot's vinicity and find a path towards a random one
            if (fixture.getUserData() instanceof final EventData eventData) {
                if (eventData.getEvent() instanceof final Scrap scrap) {

                    bot.getBotController().setEventTarget(scrap);
                    path.add(new RallyPoint.RallyPointMultiplier(BotManager.getNearestPoint(bot, scrap.getPosition()), eggplantDesireMultiplier));
                    return false;
                }
            }
            return true;
        }), playerLocation.x - searchRadius, playerLocation.y - searchRadius,
        playerLocation.x + searchRadius, playerLocation.y + searchRadius);
        if (path.isEmpty()) {
            bot.getBotController().setEventTarget(null);
        }
    }
}
