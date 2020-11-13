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
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.DeathTextUtil;
import com.mygdx.hadal.utils.TextFilterUtil;

public class KillFeedMessage extends AHadalActor {

    private final BitmapFont font;
    private final Color color;
    private final GlyphLayout layout;

    private final String message;

    private static final float scale = 0.25f;
    private static final float targetWidth = 325;
    private static final float padding = 10;

    private float lifespan = 5.0f;

    private final TextureRegion grey;
    private final float textX;

    public KillFeedMessage(PlayState ps, Schmuck perp, Player player, DamageTypes... tags) {
        font = HadalGame.SYSTEM_FONT_UI_SMALL;
        font.getData().setScale(scale);

        color = Color.WHITE;
        message = TextFilterUtil.filterGameText(ps.getGsm(), DeathTextUtil.getDeathText(ps.getGsm(), perp, player, tags));
        layout = new GlyphLayout();
        layout.setText(font, message, color, targetWidth, Align.left, true);
        setWidth(layout.width);
        setHeight(layout.height);

        grey = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.GREY.toString()));
        textX = targetWidth - layout.width - padding / 2;
    }

    @Override
    public void draw(Batch batch, float alpha) {

        batch.draw(grey, textX - padding / 2, getY() - padding / 2, getWidth() + padding, getHeight() + padding);

        font.getData().setScale(scale);
        font.setColor(color);
        font.draw(batch, message, textX, getY() + getHeight() / 2 + layout.height / 2, targetWidth, Align.left, true);
    }

    public boolean decrementLifespan(float delta) {
        lifespan -= delta;
        return lifespan <= 0;
    }
}
