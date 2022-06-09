package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 * This modifiers makes the bodies of all players invisible. Invislbe players will become partially visible periodically
 * @author Yenerd Yecaster
 */
public class PlayerInvisible extends ModeModifier {

    private static final String settingTag = "player_invisible";
    private static final UIText uiText = UIText.MODIFIER_INVIS_UI;
    private static final UIText name = UIText.MODIFIER_INVIS;

    public PlayerInvisible() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) { state.addMapModifier(UnlockArtifact.PLAYER_INVISIBLE); }
}
