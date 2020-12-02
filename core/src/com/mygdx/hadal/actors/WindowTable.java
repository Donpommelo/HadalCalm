package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.hadal.managers.GameStateManager;

public class WindowTable extends Table {

    @Override
    public void draw (Batch batch, float parentAlpha) {
        GameStateManager.getSimplePatch().draw(batch, getX(), getY(), getWidth(), getHeight());
        super.draw(batch, parentAlpha);
    }
}
