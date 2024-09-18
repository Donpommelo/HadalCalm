package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.UIHub;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.TransitionManager.TransitionState;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.TooltipManager;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.users.ScoreManager;
import com.mygdx.hadal.users.Transition;
import com.mygdx.hadal.users.User;

import static com.mygdx.hadal.managers.SkinManager.SKIN;
import static com.mygdx.hadal.users.Transition.LONG_FADE_DELAY;

/**
 * This mode setting is used for modes where the host can designate the team mode.
 * Auto-assign means that all players are randomly put into 1 of 2 teams at the start.
 * manual assign means each player will be aligned with the color they choose in the hub
 * @author Jergarita Jisrael
 */
public class SettingTeamMode extends ModeSetting {

    private SelectBox<String> teamsOptions;
    private SelectBox<String> teamsNumOptions;

    private TeamMode lockedTeamMode;
    private boolean teamModeChoice;

    /**
     * Constructor for modes that give the player thte option to choose between team modes
     */
    public SettingTeamMode() { teamModeChoice = true; }

    /**
     * Constructor for modes that lock 1 specific team mode
     */
    public SettingTeamMode(TeamMode lockedTeamMode) {
        this.lockedTeamMode = lockedTeamMode;
    }

    @Override
    public void setSetting(PlayState state, GameMode mode, Table table) {
        if (teamModeChoice) {
            String[] teamChoices = UIText.SETTING_TEAM_MODE_OPTIONS.text().split(",");
            Text team = new Text(UIText.SETTING_TEAM_MODE.text());
            team.setScale(UIHub.DETAILS_SCALE);
            TooltipManager.addTooltip(team, UIText.SETTING_TEAM_MODE_DESC.text());

            teamsOptions = new SelectBox<>(SKIN);
            teamsOptions.setItems(teamChoices);
            teamsOptions.setWidth(UIHub.OPTIONS_WIDTH);
            teamsOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, SettingSave.TEAM_MODE));

            String[] teamNumChoices = UIText.SETTING_TEAM_NUM_OPTIONS.text().split(",");
            Text teamNum = new Text(UIText.SETTING_TEAM_NUM.text());
            teamNum.setScale(UIHub.DETAILS_SCALE);
            TooltipManager.addTooltip(teamNum, UIText.SETTING_TEAM_NUM_DESC.text());

            teamsNumOptions = new SelectBox<>(SKIN);
            teamsNumOptions.setItems(teamNumChoices);
            teamsNumOptions.setWidth(UIHub.OPTIONS_WIDTH);
            teamsNumOptions.setSelectedIndex(JSONManager.setting.getModeSetting(mode, SettingSave.TEAM_NUMBER));

            //team number option is disabled outside of auto-assigned teams
            teamsNumOptions.setDisabled(teamsOptions.getSelectedIndex() != 1);
            teamsOptions.addListener(new ChangeListener() {

                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    teamsNumOptions.setDisabled(teamsOptions.getSelectedIndex() != 1);
                }
            });

            table.add(team);
            table.add(teamsOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();
            table.add(teamNum);
            table.add(teamsNumOptions).height(UIHub.DETAIL_HEIGHT).pad(UIHub.DETAIL_PAD).row();
        }
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        if (teamModeChoice) {
            JSONManager.setting.setModeSetting(mode, SettingSave.TEAM_MODE, teamsOptions.getSelectedIndex());
            JSONManager.setting.setModeSetting(mode, SettingSave.TEAM_NUMBER, teamsNumOptions.getSelectedIndex());
        }
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        if (teamModeChoice) {
            mode.setTeamMode(indexToTeamMode(JSONManager.setting.getModeSetting(mode, SettingSave.TEAM_MODE)));
            mode.setTeamNum(indexToTeamNum(JSONManager.setting.getModeSetting(mode, SettingSave.TEAM_NUMBER)));
        } else {
            mode.setTeamMode(lockedTeamMode);
        }
    }

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID) {
        if (state.isServer()) {
            User user = HadalGame.usm.getUsers().get(connID);
            if (user != null) {

                //on auto-assign team mode, player teams are set to their "override" value
                if (mode.isTeamDesignated()) {
                    if (!user.isTeamAssigned()) {
                        AlignmentFilter.assignNewPlayerToTeam(user);
                    }
                    newLoadout.team = user.getTeamFilter();
                } else {

                    //otherwise, we use this line to set their "team" value to their chosen color
                    user.setTeamFilter(newLoadout.team);
                }
            }
        }
    }

    @Override
    public void modifyNewPlayer(PlayState state, GameMode mode, Loadout newLoadout, Player p, short hitboxFilter) {
        if (!TeamMode.COOP.equals(mode.getTeamMode())) {
            if (TeamMode.FFA.equals(mode.getTeamMode())) {
                p.setHitboxFilter(hitboxFilter);
            } else {
                if (AlignmentFilter.NONE.equals(newLoadout.team)) {
                    p.setHitboxFilter(hitboxFilter);
                } else {
                    p.setHitboxFilter(newLoadout.team.getFilter());
                }
            }
        }
    }

    @Override
    public void processPlayerLivesOut(PlayState state, GameMode mode, Player p) {
        String resultsText = "";
        //check if all players are out
        boolean allded = true;
        AlignmentFilter winningTeam = AlignmentFilter.NONE;

        Array<User> users = HadalGame.usm.getUsers().values().toArray();
        if (TeamMode.COOP.equals(mode.getTeamMode()) || users.size <= 1) {
            resultsText = UIText.SETTING_LIVES_OUT.text();

            //coop levels end when all players are dead
            for (User user : users) {
                if (!user.isSpectator()) {
                    if (user.getScoreManager().getLives() > 0) {
                        allded = false;
                        break;
                    }
                }
            }
        } else {

            //for team mode, keep track of which teams still have a living player
            short factionLeft = -1;
            for (User user : users) {
                if (!user.isSpectator()) {
                    if (user.getScoreManager().getLives() > 0) {
                        Player playerLeft = user.getPlayer();
                        if (playerLeft != null) {

                            //in free-for-all, living players are qualified to win
                            if (TeamMode.FFA.equals(mode.getTeamMode())) {
                                resultsText = UIText.PLAYER_WINS.text(playerLeft.getName());
                            } else {
                                //if team mode, living players qualify their team for a win (or themselves if on a solo-team)
                                if (!AlignmentFilter.NONE.equals(user.getLoadoutManager().getActiveLoadout().team)) {
                                    resultsText = UIText.PLAYER_WINS.text(user.getLoadoutManager().getActiveLoadout().team.getTeamName());
                                    winningTeam = user.getTeamFilter();
                                } else {
                                    resultsText = UIText.PLAYER_WINS.text(playerLeft.getName());
                                    winningTeam = user.getHitboxFilter();
                                }
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

        //if the match is over (all players dead in co-op or all but one team dead in pvp), all players go to results screen
        if (allded) {
            for (User user : users) {
                if (!user.isSpectator()) {
                    ScoreManager score = user.getScoreManager();
                    if (TeamMode.FFA.equals(mode.getTeamMode())) {
                        if (user.getScoreManager().getLives() > 0) {
                            score.win();
                        }
                    } else {
                        if (!AlignmentFilter.NONE.equals(winningTeam)) {
                            if (user.getHitboxFilter().equals(winningTeam) || user.getTeamFilter().equals(winningTeam)) {
                                score.win();
                            }
                        }
                    }
                }
            }

            if (SettingArcade.arcade) {
                SettingArcade.processEndOfRound(state, mode);
            } else {
                state.getEndgameManager().transitionToResultsState(resultsText, LONG_FADE_DELAY);
            }
        } else {

            //the player that dies respawns if there are still others left and becomes a spectator otherwise
            User dedUser = p.getUser();
            if (dedUser != null) {
                if (dedUser.getScoreManager().getLives() > 0) {
                    dedUser.getTransitionManager().beginTransition(state,
                            new Transition()
                                    .setNextState(TransitionState.RESPAWN)
                                    .setFadeDelay(state.getRespawnTime(p)));
                } else {
                    dedUser.getTransitionManager().beginTransition(state,
                            new Transition()
                                    .setNextState(TransitionState.SPECTATOR)
                                    .setFadeDelay(LONG_FADE_DELAY));
                }
            }
        }
    }

    private TeamMode indexToTeamMode(int index) {
        return switch (index) {
            case 1 -> TeamMode.TEAM_AUTO;
            case 2 -> TeamMode.TEAM_MANUAL;
            case 3 -> TeamMode.HUMANS_VS_BOTS;
            default -> TeamMode.FFA;
        };
    }

    private int indexToTeamNum(int index) {
        return switch (index) {
            case 1 -> 3;
            case 2 -> 4;
            case 3-> 5;
            default -> 2;
        };
    }

    public enum TeamMode {
        COOP,
        FFA,
        TEAM_AUTO,
        TEAM_MANUAL,
        HUMANS_VS_BOTS
    }
}
