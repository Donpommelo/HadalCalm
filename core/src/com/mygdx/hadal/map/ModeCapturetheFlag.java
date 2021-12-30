package com.mygdx.hadal.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.actors.ObjectiveMarker;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.bots.RallyPoint;
import com.mygdx.hadal.event.modes.SpawnerFlag;
import com.mygdx.hadal.event.modes.FlagCapturable;
import com.mygdx.hadal.schmucks.bodies.PlayerBot;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 *  This modifier is used for the ctf mode and simply makes bots able to locate and pursue the flags
 * @author Slurplove Skitonio
 */
public class ModeCapturetheFlag extends ModeSetting {

    //this maps all flag spawners to their team so that bots can locate them
    private static final ObjectMap<AlignmentFilter, SpawnerFlag> flagSpawners = new ObjectMap<>();
    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        flagSpawners.clear();
    }

    private static final float flagAttackDesireMultiplier = 0.1f;
    private static final float flagDefendDesireMultiplier = 0.05f;
    private static final float flagReturnDesireMultiplier = 0.02f;
    private final Vector2 objectiveLocation = new Vector2();
    @Override
    public void processAIPath(PlayState state, GameMode mode, PlayerBot bot, Vector2 playerLocation, Vector2 playerVelocity,
            Array<RallyPoint.RallyPointMultiplier> path) {
        for (ObjectiveMarker objective: state.getUiObjective().getObjectives()) {
            objectiveLocation.set(objective.getObjectiveLocation()).scl(1 / PPM);
            if (objective.getObjectiveTarget() instanceof FlagCapturable flag) {
                if (flag.getTeamIndex() < AlignmentFilter.currentTeams.length) {
                    flagSpawners.put(AlignmentFilter.currentTeams[flag.getTeamIndex()], flag.getSpawner());

                    //if it is the bot's team's flag and is captured, we path towards it with high priority
                    if (bot.getPlayerData().getLoadout().team == AlignmentFilter.currentTeams[flag.getTeamIndex()]) {
                        if (flag.isCaptured() || flag.isAwayFromSpawn()) {
                            bot.getBotController().setEventTarget(flag);
                            path.add(new RallyPoint.RallyPointMultiplier(BotManager.getNearestPoint(bot, objectiveLocation), flagDefendDesireMultiplier));
                        }
                    } else {

                        //if this is the enemy's flag and the bot is capturing it, attempt to return home with high priority
                        if (flag.isCaptured()) {
                            if (bot.equals(flag.getTarget())) {
                                SpawnerFlag home = flagSpawners.get(bot.getPlayerData().getLoadout().team);
                                if (home != null) {
                                    bot.getBotController().setEventTarget(home);
                                    path.add(new RallyPoint.RallyPointMultiplier(BotManager.getNearestPoint(bot, home.getPosition()), flagReturnDesireMultiplier));
                                }
                            }
                        } else {

                            //otherwise, attempt to path towards an uncaptured enemy flag
                            bot.getBotController().setEventTarget(flag);
                            path.add(new RallyPoint.RallyPointMultiplier(BotManager.getNearestPoint(bot, objectiveLocation), flagAttackDesireMultiplier));
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
