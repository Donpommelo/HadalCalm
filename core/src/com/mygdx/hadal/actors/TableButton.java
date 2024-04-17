package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import static com.mygdx.hadal.managers.SkinManager.SIMPLE_PATCH;

/**
 * A button table is just a table with a window sprite drawn around it when moused over
 * @author Snerabeau Shikola
 */
public class TableButton extends Table {

    //is this actor being moused over?
    private boolean mouseOver;

    public TableButton() {
        final TableButton me = this;
        this.addListener(new InputListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                me.mouseOver = true;
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                me.mouseOver = false;
            }
        });
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (mouseOver) {
            SIMPLE_PATCH.draw(batch, getX(), getY(), getWidth(), getHeight());
        }
        super.draw(batch, parentAlpha);
    }
}
