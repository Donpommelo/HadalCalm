package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.enemies.EnemyType;
import com.mygdx.hadal.text.DeathTextUtil;
import com.mygdx.hadal.text.TextFilterUtil;

import static com.mygdx.hadal.managers.SkinManager.FONT_UI_ALT;

/**
 * A Kill Feed Message is a single actor displayed in the Kill Feed to represent a single death.
 * This is used both for kill messages i nthe upper right corner as well as notifications
 * @author Hapricorn Harbifferty
 */
public class KillFeedMessage extends AHadalActor {

    private static final float KILL_FEED_SCALE = 0.3f;
    private static final float KILL_FEED_WIDTH = 325;
    private static final float KILL_FEED_LIFESPAN = 18.0f;

    private static final float NOTIFICATION_SCALE = 0.5f;
    private static final float NOTIFICATION_WIDTH = 500;
    private static final float NOTIFICATION_LIFESPAN = 6.0f;

    private static final float PAD = 10;
    private static final int ALIGN = Align.left;

    private final GlyphLayout layout;
    private final String message;

    private final float fontScale;
    private final float targetWidth;
    private float lifespan;

    //grey region drawn underneath text to improve visibility
    private final TextureRegion grey;

    //The actor is drawn at an x-offset to make the text align left while still being constant distance from right side
    private final float textX;

    public KillFeedMessage(Player perp, Player vic, EnemyType type, DamageSource source, DamageTag... tags) {
        this(TextFilterUtil.filterGameText(DeathTextUtil.getDeathText(perp, vic, type, source, tags)), true);
    }

    public KillFeedMessage(String text, boolean killFeed) {

        //different stats depending on if this goes in the kill feed or notification feed
        if (killFeed) {
            this.fontScale = KILL_FEED_SCALE;
            this.targetWidth = KILL_FEED_WIDTH;
            this.lifespan = KILL_FEED_LIFESPAN;
        } else {
            this.fontScale = NOTIFICATION_SCALE;
            this.targetWidth = NOTIFICATION_WIDTH;
            this.lifespan = NOTIFICATION_LIFESPAN;
        }

        message = text;
        layout = new GlyphLayout();
        FONT_UI_ALT.getData().setScale(fontScale);
        layout.setText(FONT_UI_ALT, message, Color.WHITE, targetWidth, ALIGN, true);
        setWidth(layout.width);
        setHeight(layout.height);

        grey = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.GREY.toString()));

        if (killFeed) {
            textX = targetWidth - layout.width - PAD / 2;
        } else {
            textX = targetWidth / 2 - layout.width / 2 - PAD / 2;
        }
    }

    @Override
    public void draw(Batch batch, float alpha) {

        //draw grey box under message
        batch.draw(grey, textX - PAD / 2, getY() - PAD / 2, getWidth() + PAD, getHeight() + PAD);

        FONT_UI_ALT.getData().setScale(fontScale);
        FONT_UI_ALT.setColor(Color.WHITE);
        FONT_UI_ALT.draw(batch, message, textX, getY() + getHeight() / 2 + layout.height / 2, targetWidth, ALIGN, true);
    }

    /**
     * This is called when the actor acts to keep track of when the message disappears
     */
    public boolean decrementLifespan(float delta) {
        lifespan -= delta;
        return lifespan <= 0;
    }
}
