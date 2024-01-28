package com.mygdx.hadal.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.actors.ObjectiveMarker;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.bots.RallyPoint;
import com.mygdx.hadal.event.modes.TrickorTreatBucket;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.statuses.TrickOrTreating;

import static com.mygdx.hadal.constants.Constants.PPM;

/**
 */
public class ModeTrickorTreat extends ModeSetting {

    private static final float BOT_SCORE_AGGRO = 0.17f;
    private static final float SEARCH_RADIUS = 50.0f;
    private static final float GROUND_CANDY_DESIRE = 0.025f;
    private static final float RETURN_CANDY_DESIRE = 0.001f;

    private static final int STARTING_CANDY = 10;

    //this maps all flag spawners to their team so that bots can locate them
    private static final ObjectMap<AlignmentFilter, TrickorTreatBucket> BUCKETS = new ObjectMap<>();
    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        BUCKETS.clear();
        mode.setTeamStartScore(STARTING_CANDY);
        mode.setBotScoreAggroModifier(BOT_SCORE_AGGRO);
        state.addMapModifier(UnlockArtifact.TRICK_OR_TREAT);
    }

    private final Vector2 objectiveLocation = new Vector2();
    @Override
    public void processAIPath(PlayState state, PlayerBot bot, Vector2 playerLocation,
                              Array<RallyPoint.RallyPointMultiplier> path) {

        for (ObjectiveMarker objective : state.getUiObjective().getObjectives()) {
            objectiveLocation.set(objective.getObjectiveLocation()).scl(1 / PPM);
            if (objective.getObjectiveTarget() instanceof TrickorTreatBucket bucket) {
                if (bucket.getTeamIndex() < AlignmentFilter.currentTeams.length) {
                    //keep track of spawners. check if already added to avoid excessive "put" calls
                    if (!BUCKETS.containsKey(AlignmentFilter.currentTeams[bucket.getTeamIndex()])) {
                        BUCKETS.put(AlignmentFilter.currentTeams[bucket.getTeamIndex()], bucket);
                    }
                    Status candyStatus = bot.getPlayerData().getStatus(TrickOrTreating.class);
                    if (null != candyStatus) {
                        if (candyStatus instanceof TrickOrTreating trickOrTreating) {
                            if (bot.getUser().getLoadoutManager().getActiveLoadout().team == AlignmentFilter.currentTeams[bucket.getTeamIndex()]) {
                                if (0 < trickOrTreating.getCandyCount()) {
                                    bot.getBotController().setEventTarget(bucket);
                                    path.add(new RallyPoint.RallyPointMultiplier(BotManager.getNearestPoint(bot, objectiveLocation),
                                            bucket, RETURN_CANDY_DESIRE));
                                }
                            } else if (0 < AlignmentFilter.teamScores[bucket.getTeamIndex()]) {
                                bot.getBotController().setEventTarget(bucket);
                                path.add(new RallyPoint.RallyPointMultiplier(BotManager.getNearestPoint(bot, objectiveLocation),
                                        bucket, getStealMultiplier(trickOrTreating.getCandyCount())));
                            }
                        }
                    }
                }
            }
        }

        state.getWorld().QueryAABB((fixture -> {
                    //check for candy in the bot's vicinity and find a path towards a random one
                    if (fixture.getUserData() instanceof EventData eventData) {
                        if (eventData.getEvent().isBotModePickup()) {

                            bot.getBotController().setEventTarget(eventData.getEvent());
                            path.add(new RallyPoint.RallyPointMultiplier(BotManager.getNearestPoint(bot, eventData.getEvent().getPosition()),
                                    eventData.getEvent(), GROUND_CANDY_DESIRE));
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

    private static float getStealMultiplier(int candyCount) {
        return switch (candyCount) {
            case 0 -> 0.025f;
            case 1 -> 0.05f;
            case 2 -> 0.075f;
            case 3 -> 0.1f;
            case 4 -> 0.15f;
            default -> 10.0f;
        };
    }
}
