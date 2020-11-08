package com.mygdx.hadal.states;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.codedisaster.steamworks.SteamID;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.MenuWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;

import java.util.Map;

public class LobbyState extends GameState {

    //This table contains the ui elements of the pause screen
    private Table options, details;

    //options that the player can view
    private Text hostOption, searchOption, exitOption;
    private Text notifications;

    //Dimensions of the setting menu
    private static final int optionsX = 25;
    private static final int optionsY = 100;
    private static final int optionsWidth = 300;
    private static final int optionsHeight = 600;

    private static final int detailsX = 320;
    private static final int detailsY = 100;
    private static final int detailsWidth = 800;
    private static final int detailsHeight = 600;

    private static final float optionsScale = 0.5f;
    private static final float optionHeight = 35.0f;
    private static final float optionPad = 15.0f;
    private static final float detailsScale = 0.3f;

    private static final float titlePad = 25.0f;
    private static final int detailsTextWidth = 750;

    //this state's background shader
    private final Shader shaderBackground;
    private final TextureRegion bg;

    public LobbyState(final GameStateManager gsm) {
        super(gsm);

        shaderBackground = Shader.SPLASH;
        shaderBackground.loadDefaultShader();
        this.bg = new TextureRegion((Texture) HadalGame.assetManager.get(AssetList.BACKGROUND2.toString()));
    }

    @Override
    public void show() {

        final LobbyState me = this;

        stage = new Stage() {
            {
                addActor(new MenuWindow(optionsX, optionsY, optionsWidth, optionsHeight));
                addActor(new MenuWindow(detailsX, detailsY, detailsWidth, detailsHeight));

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

                exitOption = new Text("EXIT?", 0, 0, true);
                exitOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        SoundEffect.NEGATIVE.play(gsm, 1.0f, false);
                        gsm.getApp().fadeOut();
                        gsm.getApp().setRunAfterTransition(() -> gsm.removeState(LobbyState.class));
                    }
                });
                exitOption.setScale(optionsScale);

                notifications = new Text("", 800, 300, false);
                notifications.setScale(0.5f);

                options.add(hostOption).height(optionHeight).pad(optionPad).row();
                options.add(searchOption).height(optionHeight).pad(optionPad).row();
                options.add(exitOption).height(optionHeight).pad(optionPad).expand().row();

                addActor(notifications);
            }
        };
        app.newMenu(stage);
        gsm.getApp().fadeIn();

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

    }

    private float timer;
    @Override
    public void render(float delta) {
        timer += delta;

        batch.begin();

        shaderBackground.getShaderProgram().bind();
        shaderBackground.shaderDefaultUpdate(timer);
        batch.setShader(shaderBackground.getShaderProgram());

        batch.draw(bg, 0, 0, HadalGame.CONFIG_WIDTH, HadalGame.CONFIG_HEIGHT);

        batch.setShader(null);

        batch.end();
    }

    public void setNotification(String notification) { notifications.setText(notification); }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
