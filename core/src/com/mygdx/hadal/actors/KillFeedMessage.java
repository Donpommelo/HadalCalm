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
 * This is used both for kill messages i nthe upper right corner as well as notifications
 * @author Hapricorn Harbifferty
 */
public class KillFeedMessage extends AHadalActor {

    private final BitmapFont font;
    private final Color color;
    private final GlyphLayout layout;

    private final String message;

    private static final float padding = 10;

    private final float scale;
    private final float targetWidth;
    private float lifespan;

    //grey region drawn underneath text to improve visibility
    private final TextureRegion grey;

    //The actor is drawn at an x-offset to make the text align left while still being constant distance from right side
    private final float textX;

    private final int align;

    private static final float killFeedScale = 0.3f;
    private static final float killFeedWidth = 325;
    private static final float killFeedLifespan = 18.0f;

    private static final float notificationScale = 0.5f;
    private static final float notificationWidth = 500;
    private static final float notificationLifespan = 6.0f;

    public KillFeedMessage(PlayState ps, Player perp, Player vic, EnemyType type, DamageTypes... tags) {
        this(TextFilterUtil.filterGameText(ps.getGsm(), DeathTextUtil.getDeathText(ps.getGsm(), perp, vic, type, tags)), true);
    }

    public KillFeedMessage(String text, boolean killFeed) {

        if (killFeed) {
            this.scale = killFeedScale;
            this.targetWidth = killFeedWidth;
            this.lifespan = killFeedLifespan;
        } else {
            this.scale = notificationScale;
            this.targetWidth = notificationWidth;
            this.lifespan = notificationLifespan;
        }
        align = Align.left;

        font = HadalGame.SYSTEM_FONT_UI_SMALL;
        font.getData().setScale(scale);

        color = Color.WHITE;
        message = text;
        layout = new GlyphLayout();
        layout.setText(font, message, color, targetWidth, align, true);
        setWidth(layout.width);
        setHeight(layout.height);

        grey = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.GREY.toString()));

        if (killFeed) {
            textX = targetWidth - layout.width - padding / 2;
        } else {
            textX = targetWidth / 2 - layout.width / 2 - padding / 2;
        }
    }

    @Override
    public void draw(Batch batch, float alpha) {

        //draw grey box under message
        batch.draw(grey, textX - padding / 2, getY() - padding / 2, getWidth() + padding, getHeight() + padding);

        font.getData().setScale(scale);
        font.setColor(color);
        font.draw(batch, message, textX, getY() + getHeight() / 2 + layout.height / 2, targetWidth, align, true);
    }

    /**
     * This is called when the actor acts to keep track of when the message disappears
     */
    public boolean decrementLifespan(float delta) {
        lifespan -= delta;
        return lifespan <= 0;
    }
}
