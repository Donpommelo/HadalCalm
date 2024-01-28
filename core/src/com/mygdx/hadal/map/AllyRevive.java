package com.mygdx.hadal.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.bots.BotManager;
import com.mygdx.hadal.bots.RallyPoint;
import com.mygdx.hadal.event.modes.ReviveGravestone;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.users.ScoreManager;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.Transition;
import com.mygdx.hadal.users.User;

import static com.mygdx.hadal.users.Transition.LONG_FADE_DELAY;

public class AllyRevive extends ModeSetting {

    @Override
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageSource source, DamageTag... tags) {
        String resultsText = "";

        //null check in case this is an "extra kill" to give summoner kill credit for a summon
        if (vic != null) {
            User user = vic.getUser();
            if (user != null) {

                //check if all players are out
                boolean allded = true;
                AlignmentFilter winningTeam = AlignmentFilter.NONE;

                Array<User> users = HadalGame.usm.getUsers().values().toArray();
                if (SettingTeamMode.TeamMode.COOP.equals(mode.getTeamMode()) || users.size <= 1) {
                    resultsText = UIText.SETTING_LIVES_OUT.text();

                    //coop levels end when all players are dead
                    for (User user2 : users) {
                        if (!user2.isSpectator()) {
                            if (user2.getPlayer() != null) {
                                if (user2.getPlayer().isAlive()) {
                                    allded = false;
                                    break;
                                }
                            }
                        }
                    }
                } else {

                    //for team mode, keep track of which teams still have a living player
                    short factionLeft = -1;
                    for (User user2 : users) {
                        if (!user2.isSpectator()) {
                            if (user2.getPlayer() != null) {
                                if (user2.getPlayer().isAlive()) {
                                    Player playerLeft = user2.getPlayer();
                                    if (playerLeft != null) {

                                        //if team mode, living players qualify their team for a win (or themselves if on a solo-team)
                                        if (!AlignmentFilter.NONE.equals(user2.getLoadoutManager().getActiveLoadout().team)) {
                                            resultsText = UIText.PLAYER_WINS.text(user2.getLoadoutManager().getActiveLoadout().team.getTeamName());
                                            winningTeam = user2.getTeamFilter();
                                        } else {
                                            resultsText = UIText.PLAYER_WINS.text(playerLeft.getName());
                                            winningTeam = user2.getHitboxFilter();
                                        }

                                        //players or teams that "qualify" for a win only win if they are the only one(s) alive
                                        if (factionLeft == -1) {
                                            factionLeft = playerLeft.getHitboxFilter();
                                        } else {
                                            if (factionLeft != playerLeft.getHitboxFilter()) {
                                                allded = false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //if the match is over (all players dead in co-op or all but one team dead in pvp), all players go to results screen
                if (allded) {
                    for (User user2 : users) {
                        if (!user2.isSpectator()) {
                            ScoreManager score = user2.getScoreManager();
                            if (!AlignmentFilter.NONE.equals(winningTeam)) {
                                if (user2.getHitboxFilter().equals(winningTeam) || user2.getTeamFilter().equals(winningTeam)) {
                                    score.win();
                                }
                            }
                        }
                    }
                    state.transitionToResultsState(resultsText, LONG_FADE_DELAY);
                } else {
                    user.getTransitionManager().beginTransition(state,
                            new Transition()
                                    .setNextState(PlayState.TransitionState.RESPAWN)
                                    .setFadeDelay(-1));

                    float reviveTimer = numReviveTimer(vic.getUser().getScoreManager().getExtraModeScore());
                    vic.getUser().getScoreManager().setExtraModeScore(vic.getUser().getScoreManager().getExtraModeScore() + 1);

                    if (DamageSource.MAP_FALL.equals(source)) {
                        new ReviveGravestone(state, vic.getStart().getPixelPosition(), vic.getUser(), reviveTimer, vic.getStart());
                    } else {
                        new ReviveGravestone(state, vic.getPixelPosition(), vic.getUser(), reviveTimer, vic.getStart());
                    }
                }
            }
        }
    }

    private static final float searchRadius = 50.0f;
    private static final float reviveDesireMultiplier = 0.01f;
    @Override
    public void processAIPath(PlayState state, PlayerBot bot, Vector2 playerLocation,
                              Array<RallyPoint.RallyPointMultiplier> path) {
        state.getWorld().QueryAABB((fixture -> {
                    //check for allied grave markers in thte bot's vinicity and find a path towards a random one
                    if (fixture.getUserData() instanceof final EventData eventData) {
                        if (eventData.getEvent() instanceof final ReviveGravestone grave) {
                            if (grave.getGraveTeam() == bot.getUser().getLoadoutManager().getActiveLoadout().team) {
                                bot.getBotController().setEventTarget(grave);
                                path.add(new RallyPoint.RallyPointMultiplier(BotManager.getNearestPoint(bot, grave.getPosition()),
                                        grave, reviveDesireMultiplier));
                                return false;
                            }
                        }
                    }
                    return true;
                }), playerLocation.x - searchRadius, playerLocation.y - searchRadius,
                playerLocation.x + searchRadius, playerLocation.y + searchRadius);
        if (path.isEmpty()) {
            bot.getBotController().setEventTarget(null);
        }
    }

    private float numReviveTimer(int numDeaths) {
        switch (numDeaths) {
            case 0 -> {
                return 1.0f;
            }
            case 1 -> {
                return 2.0f;
            }
            case 2 -> {
                return 4.0f;
            }
            case 3 -> {
                return 8.0f;
            }
            case 4 -> {
                return 15.0f;
            }
            case 5 -> {
                return 30.0f;
            }
            default -> {
                return 60.0f;
            }
        }
    }
}
