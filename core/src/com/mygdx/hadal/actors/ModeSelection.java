package com.mygdx.hadal.actors;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.states.PlayState;

public class ModeSelection {

    private static Table tableOuter, tableInfo, tableSettings;
    private static ScrollPane settings;
    private static SelectBox<String> modeOptions;

    private static Text titleInfo, startOption, backOption;

    private static final float tableX = HadalGame.CONFIG_WIDTH;
    private static final float tableY = 50.0f;

    public static final int titleHeight = 60;
    public static final int titlePadding = 25;

    private static final int optionsWidthOuter = 720;
    private static final int optionsHeightOuter = 600;
    private static final int optionsHeightInner = 520;
    private static final int optionsWidth = 320;
    public static final int optionsHeight = 40;
    public static final int optionHeight = 35;
    public static final int optionHeightLarge = 45;
    public static final int optionPad = 3;
    private static final int scrollWidth = 330;

    private static final float titleScale = 0.8f;
    private static final float optionScale = 0.5f;

    private static final float detailHeight = 35.0f;
    private static final float detailPad = 10.0f;

    public static void addTable(PlayState state, UnlockLevel level) {

        tableOuter = new WindowTable();
        tableInfo = new Table();
        tableSettings = new Table();

        tableOuter.setTouchable(Touchable.enabled);

        titleInfo = new Text(level.toString(), 0, 0, false);
        titleInfo.setScale(titleScale);

        String[] compliantModes = new String[level.getModes().length];
        for (int i = 0; i < compliantModes.length; i++) {
            compliantModes[i] = level.getModes()[i].toString();
        }

        modeOptions = new SelectBox<>(GameStateManager.getSkin());
        modeOptions.setItems(compliantModes);

        modeOptions.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeEvent event, Actor actor) {

                //TODO: refresh setting options

            }
        });

        startOption = new Text("START MATCH", 0, 0, true);
        startOption.setScale(optionScale);

        startOption.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent e, float x, float y) {
                if (state.isServer()) {

                    GameMode chosenMode = level.getModes()[modeOptions.getSelectedIndex()];

                    state.loadLevel(level, chosenMode, PlayState.TransitionState.NEWLEVEL, "");

                    //play a particle when the player uses this event
                    Player user = state.getPlayer();
                    new ParticleEntity(state, user, Particle.TELEPORT, 0.0f, 3.0f, true,
                        ParticleEntity.particleSyncType.CREATESYNC, new Vector2(0, -user.getSize().y / 2));
                }
            }
        });

        backOption = new Text("BACK", 0, 0, true);
        backOption.setScale(optionScale);

        backOption.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent e, float x, float y) {
                leave(state);
                //TODO: hub menu re-enter
            }
        });

        tableInfo.add(modeOptions).height(detailHeight).pad(detailPad).row();
        tableInfo.add(startOption).height(detailHeight).pad(detailPad).row();
        tableInfo.add(backOption).height(detailHeight).pad(detailPad).row();

        tableOuter.add(titleInfo).pad(titlePadding).height(titleHeight).colspan(2);
        tableOuter.row();

        tableOuter.add(tableInfo).bottom();
        tableOuter.add(tableSettings).width(optionsWidth).height(optionsHeightInner);

        tableOuter.setPosition(tableX, tableY);
        tableOuter.setSize(optionsWidthOuter, optionsHeightOuter);

        state.getStage().setScrollFocus(settings);
        state.getStage().addActor(tableOuter);

        tableOuter.addAction(Actions.moveTo(tableX - optionsWidthOuter, tableY, .5f, Interpolation.pow5Out));
    }

    public static void leave(PlayState state) {
        tableOuter.addAction(Actions.moveTo(tableX, tableY, .5f, Interpolation.pow5Out));

        if (state.getStage() != null) {
            if (state.getStage().getScrollFocus() == settings) {
                state.getStage().setScrollFocus(null);
            }
            state.getStage().setKeyboardFocus(null);
        }
    }

    public static void loadSettings() {

    }
}
