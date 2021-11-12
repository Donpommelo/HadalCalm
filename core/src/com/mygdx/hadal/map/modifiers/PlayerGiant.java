package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.HText;

/**
 *  This modifier increases the sizes of all players.
 *  @author Linjsalt Lonkkoff
 */
public class PlayerGiant extends ModeModifier {

    private static final String settingTag = "player_giant";
    private static final HText uiText = HText.MODIFIER_LARGE_UI;
    private static final HText name = HText.MODIFIER_LARGE;

    private static final float playerScale = 1.8f;

    public PlayerGiant() {
        super(settingTag, uiText, name);
    }

    @Override
    public void modifyNewPlayer(PlayState state, GameMode mode, Loadout newLoadout, Player p, short hitboxFilter) {
        p.setScaleModifier(playerScale);
    }
}
