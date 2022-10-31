package com.mygdx.hadal.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.actors.ObjectiveMarker;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.bots.RallyPoint;
import com.mygdx.hadal.event.modes.FlagCapturable;
import com.mygdx.hadal.event.modes.FlagSpawner;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 *  This modifier is used for the ctf mode and simply makes bots able to locate and pursue the flags
 * @author Slurplove Skitonio
 */
public class ModeCapturetheFlag extends ModeSetting {

    //this maps all flag spawners to their team so that bots can locate them
    private static final ObjectMap<AlignmentFilter, FlagSpawner> FLAG_SPAWNERS = new ObjectMap<>();
    private static final float FLAG_ATTACK_DESIRE_MULTIPLIER = 0.1f;
    private static final float FLAG_DEFEND_DESIRE_MULTIPLIER = 0.02f;
    private static final float FLAG_RETURN_DESIRE_MULTIPLIER = 0.04f;

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        FLAG_SPAWNERS.clear();
    }

    private final Vector2 objectiveLocation = new Vector2();
    @Override
    public void processAIPath(PlayState state, PlayerBot bot, Vector2 playerLocation,
                              Array<RallyPoint.RallyPointMultiplier> path) {
        for (ObjectiveMarker objective : state.getUiObjective().getObjectives()) {
            objectiveLocation.set(objective.getObjectiveLocation()).scl(1 / PPM);
            if (objective.getObjectiveTarget() instanceof FlagCapturable flag) {
                if (flag.getTeamIndex() < AlignmentFilter.currentTeams.length) {

                    //keep track of spawners. check if already added to avoid excessive "put" calls
                    if (!FLAG_SPAWNERS.containsKey(AlignmentFilter.currentTeams[flag.getTeamIndex()])) {
                        FLAG_SPAWNERS.put(AlignmentFilter.currentTeams[flag.getTeamIndex()], flag.getSpawner());
                    }

                    //if it is the bot's team's flag and is captured, we path towards it with high priority
                    if (bot.getPlayerData().getLoadout().team == AlignmentFilter.currentTeams[flag.getTeamIndex()]) {
                        if (flag.isCaptured() || flag.isAwayFromSpawn()) {
                            bot.getBotController().setEventTarget(flag);
                            path.add(new RallyPoint.RallyPointMultiplier(BotManager.getNearestPoint(bot, objectiveLocation),
                                    flag, FLAG_DEFEND_DESIRE_MULTIPLIER));
                        }
                    } else {

                        //if this is the enemy's flag and the bot is capturing it, attempt to return home with high priority
                        if (flag.isCaptured()) {
                            if (bot.equals(flag.getTarget())) {
                                FlagSpawner home = FLAG_SPAWNERS.get(bot.getPlayerData().getLoadout().team);
                                if (null != home) {
                                    bot.getBotController().setEventTarget(home);
                                    path.add(new RallyPoint.RallyPointMultiplier(BotManager.getNearestPoint(bot, home.getPosition()),
                                            home, FLAG_RETURN_DESIRE_MULTIPLIER));
                                }
                            }
                        } else {

                            //otherwise, attempt to path towards an uncaptured enemy flag
                            bot.getBotController().setEventTarget(flag);
                            path.add(new RallyPoint.RallyPointMultiplier(BotManager.getNearestPoint(bot, objectiveLocation),
                                    flag, FLAG_ATTACK_DESIRE_MULTIPLIER));
                        }
                    }
                }
            }
        }
        if (path.isEmpty()) {
            bot.getBotController().setEventTarget(null);
        }
    }
}
