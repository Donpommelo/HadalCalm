package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 *  This modifier shrinks the bodies of all players. Additionally, cameras are zoomed in more.
 *  @author Gicciatello Grumpernickel
 */
public class PlayerMini extends ModeModifier {

    private static final String settingTag = "player_mini";
    private static final String uiText = "SMALL PLAYERS?";
    private static final String name = "MINI";

    private static final float playerScale = 0.5f;
    private static final float zoomModifier = 0.5f;

    public PlayerMini() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state, GameMode mode) {
        state.setZoomModifier(state.getZoomModifier() * zoomModifier);
    }

    @Override
    public void modifyNewPlayer(PlayState state, GameMode mode, Loadout newLoadout, Player p, short hitboxFilter) {
        p.setScaleModifier(playerScale);
    }
}
