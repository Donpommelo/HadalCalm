package com.mygdx.hadal.actors;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.event.hub.NavigationsMultiplayer;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.map.ModeSetting;
import com.mygdx.hadal.states.PlayState;

public class ModeSelection {

    private static Table tableOuter;
    private static ScrollPane settings;

    private static final float tableX = HadalGame.CONFIG_WIDTH;
    private static final float tableInX = 160;
    private static final float tableY = 240.0f;

    public static final int titleHeight = 60;
    public static final int titlePadding = 25;

    private static final int optionsWidthOuter = 400;
    private static final int optionsHeightOuter = 400;
    public static final int optionsWidth = 390;
    public static final int optionHeight = 30;
    public static final int optionPad = 3;

    private static final float titleScale = 0.8f;
    private static final float optionScale = 0.5f;

    public static final float detailHeight = 35.0f;
    public static final float detailPad = 10.0f;

    public static final float detailsScale = 0.25f;

    public static void addTable(PlayState state, GameMode mode, NavigationsMultiplayer nav) {

        tableOuter = new WindowTable();
        final Table tableInfo = new Table();
        final Table tableSettings = new Table();

        tableOuter.setTouchable(Touchable.enabled);

        final Text titleInfo = new Text("SETTINGS", 0, 0, false);
        titleInfo.setScale(titleScale);

        final Text backOption = new Text("BACK", 0, 0, true);
        backOption.setScale(optionScale);

        backOption.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent e, float x, float y) {
                leave(state);
                state.getUiHub().leave();
                nav.enter();
            }
        });

        tableSettings.clear();
        for (ModeSetting setting: mode.getSettings()) {
            setting.setSetting(state, mode, tableSettings);
        }

        settings = new ScrollPane(tableSettings, GameStateManager.getSkin());
        settings.setFadeScrollBars(false);

        settings.addListener(new InputListener() {

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                state.getStage().setScrollFocus(settings);
            }
        });

        tableInfo.add(backOption).height(detailHeight).pad(detailPad).row();

        tableOuter.add(titleInfo).pad(titlePadding).height(titleHeight).colspan(2);
        tableOuter.row();

        tableOuter.add(settings).expandY().width(optionsWidth).row();
        tableOuter.add(tableInfo).bottom();

        tableOuter.setPosition(tableX, tableY);
        tableOuter.setSize(optionsWidthOuter, optionsHeightOuter);

        state.getStage().addActor(tableOuter);

        tableOuter.addAction(Actions.moveTo(tableInX, tableY, .5f, Interpolation.pow5Out));
    }

    public static void leave(PlayState state) {
        if (tableOuter != null) {
            tableOuter.addAction(Actions.moveTo(tableX, tableY, .5f, Interpolation.pow5Out));
        }

        if (state.getStage() != null) {
            if (state.getStage().getScrollFocus() == settings) {
                state.getStage().setScrollFocus(null);
            }
            state.getStage().setKeyboardFocus(null);
        }
    }
}
