package com.mygdx.hadal.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.bots.RallyPoint;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.PlayerBot;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayState;

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
     * This is run when a mode is selected in the navigations hub.
     * Here, we populate a settings table with the modifiers available to that mode
     */
    public void setModifiers(PlayState state, GameMode mode, Table table) {}

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
    public String loadSettingSpawn(PlayState state) { return ""; }

    /**
     * This is run when the match begins. It sets up all the miscellaneous functions of the mode that do not entail creating
     * events that must be connected to a global event
     */
    public void loadSettingMisc(PlayState state, GameMode mode) {}

    /**
     * This is run when initiating a new player's respawn
     * @param newLoadout: new player's loadout. edit it to set defaults
     * @param connID: the id of the user we are creating a player for
     */
    public void processNewPlayerAlignment(PlayState state, GameMode mode, Loadout newLoadout, int connID) {}

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
     * This is run after a player is created; for effects that require the player's body to exist
     */
    public void postCreatePlayer(PlayState state, GameMode mode, Player p) {}

    /**
     * This is run when a player dies.
     * @param perp : the schmuck (not necessarily player) that killed
     * @param vic : the player that died
     */
    public void processPlayerDeath(PlayState state, GameMode mode, Schmuck perp, Player vic, DamageSource source, DamageTag... tags) {}

    /**
     * This is run when a player's score changes
     * @param scoreIncrement : the amount to change the score by
     */
    public void processPlayerScoreChange(PlayState state, int scoreIncrement) {}

    /**
     * This is run when a team's score changes
     * @param scoreIncrement : The amount to change the score by
     */
    public void processTeamScoreChange(PlayState state, int scoreIncrement) {}

    /**
     * This is run when a player runs out of lives
     * @param p: The player whose lives ran out
     */
    public void processPlayerLivesOut(PlayState state, GameMode mode, Player p) {}

    /**
     * This is run regularly by bots to find an optimal path through the map accounting for mode-specific events
     * @param bot : Bot player doing the pathfinding
     * @param playerLocation : loaction of the bot player
     * @param path : current list of rally points that we will find paths towards
     */
    public void processAIPath(PlayState state, PlayerBot bot, Vector2 playerLocation,
                              Array<RallyPoint.RallyPointMultiplier> path) {}

    /**
     * This is run when the game ends. Atm, this just cleans up bot pathfinding threads
     */
    public void processGameEnd(PlayState state) {}
}
