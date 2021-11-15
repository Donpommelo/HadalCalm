package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.ModeSettingSelection;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.User;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;

import java.util.Collection;

import static com.mygdx.hadal.states.PlayState.defaultFadeOutSpeed;
import static com.mygdx.hadal.states.PlayState.longFadeDelay;

/**
 * This mode setting is used for modes where the host can designate the team mode.
 * Auto-assign means that all players are randomly put into 1 of 2 teams at the start.
 * manual assign means each player will be aligned with the color they choose in the hub
 * @author Jergarita Jisrael
 */
public class SettingTeamMode extends ModeSetting {

    private static final String settingTag1 = "team_mode";
    private static final Integer defaultValue1 = 0;
    private static final String settingTag2 = "team_num";
    private static final Integer defaultValue2 = 0;

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
            String[] teamChoices = HText.SETTING_TEAM_MODE_OPTIONS.text().split(",");
            Text team = new Text(HText.SETTING_TEAM_MODE.text(), 0, 0, false);
            team.setScale(ModeSettingSelection.detailsScale);

            teamsOptions = new SelectBox<>(GameStateManager.getSkin());
            teamsOptions.setItems(teamChoices);
            teamsOptions.setWidth(ModeSettingSelection.optionsWidth);
            teamsOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag1, defaultValue1));

            String[] teamNumChoices = HText.SETTING_TEAM_NUM_OPTIONS.text().split(",");
            Text teamNum = new Text(HText.SETTING_TEAM_NUM.text(), 0, 0, false);
            teamNum.setScale(ModeSettingSelection.detailsScale);

            teamsNumOptions = new SelectBox<>(GameStateManager.getSkin());
            teamsNumOptions.setItems(teamNumChoices);
            teamsNumOptions.setWidth(ModeSettingSelection.optionsWidth);
            teamsNumOptions.setSelectedIndex(state.getGsm().getSetting().getModeSetting(mode, settingTag2, defaultValue2));

            table.add(team);
            table.add(teamsOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).row();
            table.add(teamNum);
            table.add(teamsNumOptions).height(ModeSettingSelection.detailHeight).pad(ModeSettingSelection.detailPad).row();
        }
    }

    @Override
    public void saveSetting(PlayState state, GameMode mode) {
        if (teamModeChoice) {
            state.getGsm().getSetting().setModeSetting(mode, settingTag1, teamsOptions.getSelectedIndex());
            state.getGsm().getSetting().setModeSetting(mode, settingTag2, teamsNumOptions.getSelectedIndex());
        }
    }

    @Override
    public void loadSettingMisc(PlayState state, GameMode mode) {
        if (teamModeChoice) {
            mode.setTeamMode(indexToTeamMode(state.getGsm().getSetting().getModeSetting(mode, settingTag1, defaultValue1)));
            mode.setTeamNum(indexToTeamNum(state.getGsm().getSetting().getModeSetting(mode, settingTag2, defaultValue2)));
        } else {
            mode.setTeamMode(lockedTeamMode);
        }
    }

    @Override
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID) {
        if (state.isServer()) {
            User user = HadalGame.server.getUsers().get(connID);
            if (user != null) {

                //on auto-assign team mode, player teams are set to their "override" value
                if (mode.isTeamDesignated()) {
                    newLoadout.team = user.getTeamFilter();
                } else {

                    //otherwise, we use this line to set their "team" value to their chosen color
                    user.setTeamFilter(newLoadout.team);
                }
            }
        }
    }

    @Override
    public void modifyNewPlayer(PlayState state,  GameMode mode, Loadout newLoadout, Player p, short hitboxFilter) {
        if (!mode.getTeamMode().equals(TeamMode.COOP)) {
            if (mode.getTeamMode().equals(TeamMode.FFA)) {
                p.setHitboxfilter(hitboxFilter);
            } else {
                if (newLoadout.team.equals(AlignmentFilter.NONE)) {
                    p.setHitboxfilter(hitboxFilter);
                } else {
                    p.setHitboxfilter(newLoadout.team.getFilter());
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

        Collection<User> users = HadalGame.server.getUsers().values();
        if (mode.getTeamMode().equals(TeamMode.COOP) || users.size() <= 1) {
            resultsText = HText.SETTING_LIVES_OUT.text();

            //coop levels end when all players are dead
            for (User user : users) {
                if (!user.isSpectator()) {
                    if (user.getScores().getLives() > 0) {
                        allded = false;
                        break;
                    }
                }
            }
        } else {

            //for team mode, keep track of which teams still have a living player
            short factionLeft = -1;
            for (User user: users) {
                if (!user.isSpectator()) {
                    if (user.getScores().getLives() > 0) {
                        Player playerLeft = user.getPlayer();
                        if (playerLeft != null) {

                            //in free-for-all, living players are qualified to win
                            if (mode.getTeamMode().equals(TeamMode.FFA)) {
                                resultsText = HText.PLAYER_WINS.text(playerLeft.getName());
                            } else {
                                //if team mode, living players qualify their team for a win (or themselves if on a solo-team)
                                if (!playerLeft.getPlayerData().getLoadout().team.equals(AlignmentFilter.NONE)) {
                                    resultsText = HText.PLAYER_WINS.text(playerLeft.getPlayerData().getLoadout().team.toString());
                                    winningTeam = user.getTeamFilter();
                                } else {
                                    resultsText = HText.PLAYER_WINS.text(playerLeft.getName());
                                    winningTeam = user.getHitBoxFilter();
                                }
                            }

                            //players or teams that "qualify" for a win only win if they are the only one(s) alive
                            if (factionLeft == -1) {
                                factionLeft = playerLeft.getHitboxfilter();
                            } else {
                                if (factionLeft != playerLeft.getHitboxfilter()) {
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
                    SavedPlayerFields score = user.getScores();
                    if (mode.getTeamMode().equals(TeamMode.FFA)) {
                        if (user.getScores().getLives() > 0) {
                            score.win();
                        }
                    } else {
                        if (winningTeam != AlignmentFilter.NONE) {
                            if (user.getHitBoxFilter().equals(winningTeam) || user.getTeamFilter().equals(winningTeam)) {
                                score.win();
                            }
                        }
                    }
                }
            }
            state.transitionToResultsState(resultsText, PlayState.longFadeDelay);
        } else {

            //the player that dies respawns if there are still others left and becomes a spectator otherwise
            User dedUser = p.getUser();
            if (dedUser.getScores().getLives() > 0) {
                dedUser.beginTransition(state, PlayState.TransitionState.RESPAWN, false, defaultFadeOutSpeed, state.getRespawnTime());
            } else {
                dedUser.beginTransition(state, PlayState.TransitionState.SPECTATOR, false, defaultFadeOutSpeed, longFadeDelay);
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
