package com.mygdx.hadal.text;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * TooltipManager contains methods to attach tooltips to scene2d actors
 */
public class TooltipManager {

    private static final float TOOLTIP_WIDTH = 350.0f;

    public static void addTooltip(Actor a, String text, float tooltipWidth) {
        TextTooltip.TextTooltipStyle style = new TextTooltip.TextTooltipStyle();
        style.wrapWidth = tooltipWidth;
        TextTooltip tooltip = new TextTooltip(text, GameStateManager.getSkin());

        //tooltip instantly appears. (note that tooltip wrap width is set in uiskin.json)
        tooltip.setInstant(true);
        a.addListener(tooltip);
    }

    public static void addTooltip(Actor a, String text) {
        addTooltip(a, text, TOOLTIP_WIDTH);
    }
}
