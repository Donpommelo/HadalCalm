package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.DeathTextUtil;
import com.mygdx.hadal.utils.TextFilterUtil;

/**
 * A Kill Feed Message is a single actor displayed in the Kill Feed to represent a single death.
 * @author Hapricorn Harbifferty
 */
public class KillFeedMessage extends AHadalActor {

    private final BitmapFont font;
    private final Color color;
    private final GlyphLayout layout;

    private final String message;

    private static final float scale = 0.3f;
    private static final float targetWidth = 325;
    private static final float padding = 10;

    //kill messages go away after 10 seconds
    private float lifespan = 18.0f;

    //grey region drawn underneath text to improve visibility
    private final TextureRegion grey;

    //The actor is drawn at an x-offset to make the text align left while still being constant distance from right side
    private final float textX;

    public KillFeedMessage(PlayState ps, Player perp, Player vic, EnemyType type, DamageTypes... tags) {
        font = HadalGame.SYSTEM_FONT_UI_SMALL;
        font.getData().setScale(scale);

        color = Color.WHITE;
        message = TextFilterUtil.filterGameText(ps.getGsm(), DeathTextUtil.getDeathText(ps.getGsm(), perp, vic, type, tags));
        layout = new GlyphLayout();
        layout.setText(font, message, color, targetWidth, Align.left, true);
        setWidth(layout.width);
        setHeight(layout.height);

        grey = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.GREY.toString()));
        textX = targetWidth - layout.width - padding / 2;
    }

    @Override
    public void draw(Batch batch, float alpha) {

        //draw grey box under message
        batch.draw(grey, textX - padding / 2, getY() - padding / 2, getWidth() + padding, getHeight() + padding);

        font.getData().setScale(scale);
        font.setColor(color);
        font.draw(batch, message, textX, getY() + getHeight() / 2 + layout.height / 2, targetWidth, Align.left, true);
    }

    /**
     * This is called when the actor acts to keep track of when the message disappears
     */
    public boolean decrementLifespan(float delta) {
        lifespan -= delta;
        return lifespan <= 0;
    }
}
