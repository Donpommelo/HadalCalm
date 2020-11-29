package com.mygdx.hadal.states;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.codedisaster.steamworks.SteamID;
import com.mygdx.hadal.actors.MenuWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.GameStateManager;

import java.util.Map;

public class LobbyState extends GameState {

    //This table contains the ui elements of the pause screen
    private Table options, details;
    private MenuWindow windowOptions, windowDetails;

    //options that the player can view
    private Text hostOption, searchOption, exitOption;
    private Text notifications;

    //Dimensions of the setting menu
    private static final int optionsX = -1025;
    private static final int optionsY = 100;
    private static final int optionsXEnabled = 25;
    private static final int optionsYEnabled = 100;
    private static final int optionsWidth = 300;
    private static final int optionsHeight = 600;

    private static final int detailsX = -730;
    private static final int detailsY = 100;
    private static final int detailsXEnabled = 320;
    private static final int detailsYEnabled = 100;
    private static final int detailsWidth = 800;
    private static final int detailsHeight = 600;

    private static final float optionsScale = 0.5f;
    private static final float optionHeight = 35.0f;
    private static final float optionPad = 15.0f;
    private static final float detailsScale = 0.3f;

    private static final float titlePad = 25.0f;
    private static final int detailsTextWidth = 750;

    //this is the state underneath this state.
    private final GameState peekState;

    public LobbyState(final GameStateManager gsm,  GameState peekState) {
        super(gsm);
        this.peekState = peekState;
    }

    @Override
    public void show() {

        final LobbyState me = this;

        stage = new Stage() {
            {
                windowOptions = new MenuWindow(optionsX, optionsY, optionsWidth, optionsHeight);
                windowDetails = new MenuWindow(detailsX, detailsY, detailsWidth, detailsHeight);
                addActor(windowOptions);
                addActor(windowDetails);

                options = new Table();
                options.setPosition(optionsX, optionsY);
                options.setSize(optionsWidth, optionsHeight);
                options.top();
                addActor(options);

                details = new Table();
                details.setPosition(detailsX, detailsY);
                details.setSize(detailsWidth, detailsHeight);
                details.top();
                addActor(details);

                hostOption = new Text("HOST", 0, 0, true);
                hostOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
                        gsm.getApp().getLobbyManager().createLobby(5, me);
                    }
                });
                hostOption.setScale(optionsScale);

                searchOption = new Text("SEARCH", 0, 0, true);
                searchOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
                        gsm.getApp().getLobbyManager().requestLobbyList(10, me);
                    }
                });
                searchOption.setScale(optionsScale);

                exitOption = new Text("RETURN?", 0, 0, true);
                exitOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        SoundEffect.NEGATIVE.play(gsm, 1.0f, false);

                        transitionOut(() -> gsm.removeState(LobbyState.class));
                    }
                });
                exitOption.setScale(optionsScale);

                notifications = new Text("", 500, 120, false);
                notifications.setScale(0.5f);

                options.add(hostOption).height(optionHeight).pad(optionPad).row();
                options.add(searchOption).height(optionHeight).pad(optionPad).row();
                options.add(exitOption).height(optionHeight).pad(optionPad).expand().row();

                addActor(notifications);
            }
        };
        app.newMenu(stage);
        transitionIn();

        notifications.setText(gsm.getApp().getLobbyManager().fug);
    }

    public void updateLobbies(Map<Long, SteamID> lobbies) {
        details.clear();

        final LobbyState me = this;

        for (Map.Entry<Long, SteamID> lobby : lobbies.entrySet()) {
            Text lobbyOption = new Text(Long.toHexString(lobby.getKey()) + " ", 0, 0, false);
            lobbyOption.addListener(new ClickListener() {

                @Override
                public void clicked(InputEvent e, float x, float y) {
                    SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
                    gsm.getApp().getLobbyManager().joinLobby(lobby.getKey(), me);
                }
            });
            details.add(lobbyOption).row();
        }
    }

    @Override
    public void update(float delta) {
        peekState.update(delta);
    }

    @Override
    public void render(float delta) {
        peekState.render(delta);
        peekState.stage.getViewport().apply();
        peekState.stage.act();
        peekState.stage.draw();
    }

    private static final float transitionDuration = 0.4f;
    private static final Interpolation intp = Interpolation.fastSlow;
    private void transitionOut(Runnable runnable) {
        options.addAction(Actions.moveTo(optionsX, optionsY, transitionDuration, intp));
        windowOptions.addAction(Actions.moveTo(optionsX, optionsY, transitionDuration, intp));

        details.addAction(Actions.moveTo(detailsX, detailsY, transitionDuration, intp));
        windowDetails.addAction(Actions.sequence(Actions.moveTo(detailsX, detailsY, transitionDuration, intp), Actions.run(runnable)));
    }

    private void transitionIn() {
        options.addAction(Actions.moveTo(optionsXEnabled, optionsYEnabled, transitionDuration, intp));
        windowOptions.addAction(Actions.moveTo(optionsXEnabled, optionsYEnabled, transitionDuration, intp));

        details.addAction(Actions.moveTo(detailsXEnabled, detailsYEnabled, transitionDuration, intp));
        windowDetails.addAction(Actions.moveTo(detailsXEnabled, detailsYEnabled, transitionDuration, intp));
    }

    public void setNotification(String notification) { notifications.setText(notification); }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
