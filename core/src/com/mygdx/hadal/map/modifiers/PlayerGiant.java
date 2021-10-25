package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

/**
 *  This modifier increases the sizes of all players.
 *  @author Linjsalt Lonkkoff
 */
public class PlayerGiant extends ModeModifier {

    private static final String settingTag = "player_giant";
    private static final String uiText = "GIANT PLAYERS?";
    private static final String name = "GIANT";

    private static final float playerScale = 1.8f;

    public PlayerGiant() {
        super(settingTag, uiText, name);
    }

    @Override
    public void modifyNewPlayer(PlayState state, GameMode mode, Loadout newLoadout, Player p, short hitboxFilter) {
        p.setScaleModifier(playerScale);
    }
}
