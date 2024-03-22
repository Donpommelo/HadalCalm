package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import static com.mygdx.hadal.managers.SkinManager.SIMPLE_PATCH;

/**
 * A window table is just a table with a window sprite drawn around it automatically
 * @author Snerabeau Shikola
 */
public class TableWindow extends Table {

    @Override
    public void draw(Batch batch, float parentAlpha) {
        SIMPLE_PATCH.draw(batch, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }
}
