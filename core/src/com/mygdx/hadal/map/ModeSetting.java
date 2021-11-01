package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

/**
 * This is a single setting for a mode.
 * A setting includes things like "this mode is pvp" to "this mode allows the player to change the time limit and will
 * end after that limit is reached
 * @author Snimbly Stucker
 */
public class ModeSetting {

    /**
     * This is run when a mode is selected in the navigations hub.
     * Here, we populate a settings table with the options available to that mode (time/lives limits)
     */
    public void setSetting(PlayState state, GameMode mode, Table table) {}

    /**
     * When we begin a match, this is run to save the mode-specific settings in the player's setting save file
     */
    public void saveSetting(PlayState state, GameMode mode) {}

    /**
     * This is run when the match begins. It sets up all mode-specific events that must be run at the start of the match.
     * It returns csv string consisting of the ids of all the events.
     */
    public String loadSettingStart(PlayState state, GameMode mode) { return ""; }

    /**
     * This is run when the match begins. It sets up all the ui tags that will used for the mode.
     * It returns csv string consisting of the ui tags to be displayed.
     */
    public String loadUIStart(PlayState state, GameMode mode) { return ""; }

    /**
     * This is run when the match begins. It sets up all mode-specific events that must be run when a player spawns.
     * It returns csv string consisting of the ids of all the events.
     */
    public String loadSettingSpawn(PlayState state, GameMode mode) { return ""; }

    /**
     * This is run when the match begins. It sets up all the miscellaneous functions of the mode that do not entail creating
     * events that must be connected to a global event
     */
    public void loadSettingMisc(PlayState state, GameMode mode) {}

    /**
     * This is run when the playstate is initiated. It sets up anything that must be donw prior to team assignment
     */
    public void processNewPlayState(PlayState state, GameMode mode) {}

    /**
     * This is run when creating a new player
     * @param newLoadout: new player's loadout. edit it to set defaults
     * @param connID: the id of the user we are creating a player for
     */
    public void processNewPlayerLoadout(PlayState state, GameMode mode, Loadout newLoadout, int connID) {}

    /**
     * This is run immediately after creating a new player
     * @param newLoadout: new player's loadout. edit it to set defaults
     * @param p: the player of the user we just created
     * @param hitboxFilter: the "team alignment" of the new player
     */
    public void modifyNewPlayer(PlayState state, GameMode mode, Loadout newLoadout, Player p, short hitboxFilter) {}

    /**
     * This is run when a player dies.
     * @param perp: the schmuck (not necessarily player) that killed
     * @param vic: the player that died
     */
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageTypes... tags) {}

    /**
     * This is run when a player's score changes
     * @param p: the player whose score is changing
     * @param scoreIncrement: the amount to change the score by
     */
    public void processPlayerScoreChange(PlayState state, GameMode mode, Player p, int scoreIncrement) {}

    /**
     * This is run when a team's score changes
     * @param teamIndex: the index of the team we are changing the score of
     * @param scoreIncrement: The amount to change the score by
     */
    public void processTeamScoreChange(PlayState state, GameMode mode, int teamIndex, int scoreIncrement) {}

    /**
     * This is run when a player runs out of lives
     * @param p: The player whose lives ran out
     */
    public void processPlayerLivesOut(PlayState state,  GameMode mode,Player p) {}
}
