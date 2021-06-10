package com.mygdx.hadal.map;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
}
