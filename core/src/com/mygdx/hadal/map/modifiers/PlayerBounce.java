package com.mygdx.hadal.map.modifiers;

import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;

/**
 * This modifiers makes the bodies of all players bouncy
 *  @author Fulfram Frarbhead
 */
public class PlayerBounce extends ModeModifier {

    private static final String settingTag = "player_bounce";
    private static final UIText uiText = UIText.MODIFIER_BOUNCE_UI;
    private static final UIText name = UIText.MODIFIER_BOUNCE;

    public PlayerBounce() {
        super(settingTag, uiText, name);
    }

    @Override
    public void executeModifier(PlayState state) { state.addMapModifier(UnlockArtifact.PLAYER_BOUNCE); }
}
