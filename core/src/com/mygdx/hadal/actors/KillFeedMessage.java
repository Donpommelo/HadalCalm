package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.bodies.enemies.EnemyType;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.text.DeathTextUtil;
import com.mygdx.hadal.text.TextFilterUtil;

/**
 * A Kill Feed Message is a single actor displayed in the Kill Feed to represent a single death.
 * This is used both for kill messages i nthe upper right corner as well as notifications
 * @author Hapricorn Harbifferty
 */
public class KillFeedMessage extends AHadalActor {

    private final GlyphLayout layout;
    private final String message;

    private static final float padding = 10;
    private final float fontScale;
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

        //different stats depending on if this goes in the kill feed or notification feed
        if (killFeed) {
            this.fontScale = killFeedScale;
            this.targetWidth = killFeedWidth;
            this.lifespan = killFeedLifespan;
        } else {
            this.fontScale = notificationScale;
            this.targetWidth = notificationWidth;
            this.lifespan = notificationLifespan;
        }
        align = Align.left;

        message = text;
        layout = new GlyphLayout();
        HadalGame.FONT_UI_ALT.getData().setScale(fontScale);
        layout.setText(HadalGame.FONT_UI_ALT, message, Color.WHITE, targetWidth, align, true);
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

        HadalGame.FONT_UI_ALT.getData().setScale(fontScale);
        HadalGame.FONT_UI_ALT.setColor(Color.WHITE);
        HadalGame.FONT_UI_ALT.draw(batch, message, textX, getY() + getHeight() / 2 + layout.height / 2, targetWidth, align, true);
    }

    /**
     * This is called when the actor acts to keep track of when the message disappears
     */
    public boolean decrementLifespan(float delta) {
        lifespan -= delta;
        return lifespan <= 0;
    }
}
