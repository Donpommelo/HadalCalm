package com.mygdx.hadal.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.actors.ObjectiveMarker;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.bots.RallyPath;
import com.mygdx.hadal.event.SpawnerFlag;
import com.mygdx.hadal.schmucks.bodies.PlayerBot;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.hitbox.FlagCapturable;

import static com.mygdx.hadal.utils.Constants.PPM;

/**
 *  This modifier is used for the ctf mode and simply makes bots able to locate and pursue the flags
 */
public class ModeCapturetheFlag extends ModeSetting {

    //this maps all flag spawners to their team so that bots can locate them
    private static final ObjectMap<AlignmentFilter, SpawnerFlag> flagSpawners = new ObjectMap<>();
    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        flagSpawners.clear();
    }

    private static final float flagAttackDesireMultiplier = 0.2f;
    private static final float flagDefendDesireMultiplier = 0.05f;
    private static final float flagReturnDesireMultiplier = 0.02f;
    private final Vector2 objectiveLocation = new Vector2();
    @Override
    public RallyPath processAIPath(PlayState state, GameMode mode, PlayerBot p, Vector2 playerLocation, Vector2 playerVelocity) {
        for (ObjectiveMarker objective: state.getUiObjective().getObjectives()) {
            objectiveLocation.set(objective.getObjectiveLocation()).scl(1 / PPM);
            if (objective.getObjectiveTarget() instanceof Hitbox flag) {
                if (flag.getStrategies().size >= 2) {

                    //this is kinda sketchy code that relies on the fact that capturable flags are only created in 1 place
                    //and that they are always added as the second strategy
                    if (flag.getStrategies().get(1) instanceof FlagCapturable capture) {
                        if (capture.getTeamIndex() < AlignmentFilter.currentTeams.length) {
                            flagSpawners.put(AlignmentFilter.currentTeams[capture.getTeamIndex()], capture.getSpawner());

                            //if it is the bot's team's flag and is captured, we path towards it with high priority
                            if (p.getPlayerData().getLoadout().team == AlignmentFilter.currentTeams[capture.getTeamIndex()]) {
                                if (capture.isCaptured()) {
                                    RallyPath tempPath = BotManager.getShortestPathBetweenLocations(p, playerLocation, objectiveLocation, playerVelocity);
                                    if (tempPath != null) {
                                        p.getBotController().setEventTarget(flag);
                                        return new RallyPath(tempPath.getPath(), tempPath.getDistance() * flagDefendDesireMultiplier);
                                    }
                                }
                            } else {

                                //if this is the enemy's flag and the bot is capturing it, attempt to return home with high priority
                                if (capture.isCaptured()) {
                                    if (p.equals(capture.getTarget())) {
                                        SpawnerFlag home = flagSpawners.get(p.getPlayerData().getLoadout().team);
                                        if (home != null) {
                                            RallyPath tempPath = BotManager.getShortestPathBetweenLocations(p, playerLocation,
                                                    home.getPosition(), playerVelocity);
                                            if (tempPath != null) {
                                                p.getBotController().setEventTarget(home);
                                                return new RallyPath(tempPath.getPath(), tempPath.getDistance() * flagReturnDesireMultiplier);
                                            }
                                        }
                                    }
                                } else {

                                    //otherwise, attempt to path towards an uncaptured enemy flag
                                    RallyPath tempPath = BotManager.getShortestPathBetweenLocations(p, playerLocation, objectiveLocation, playerVelocity);
                                    if (tempPath != null) {
                                        p.getBotController().setEventTarget(flag);
                                        return new RallyPath(tempPath.getPath(), tempPath.getDistance() * flagAttackDesireMultiplier);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
