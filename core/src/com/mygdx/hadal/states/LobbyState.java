package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.codedisaster.steamworks.SteamID;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.WindowTable;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.Packets;

import java.io.IOException;
import java.util.Map;

public class LobbyState extends GameState {

    //This table contains the ui elements of the pause screen
    private Table joinLobby, tableOptions, joinIP, host, tablePassword;
    private ScrollPane options;

    private TextField enterName, enterIP, enterPassword;

    //options that the player can view
    private Text hostOption, searchOption, exitOption, notifications;

    //Dimensions of the setting menu
    private static final int joinX = -980;
    private static final int joinY = 160;
    private static final int joinXEnabled = 40;
    private static final int joinYEnabled = 180;
    private static final int joinWidth = 580;
    private static final int joinHeight = 500;
    private static final int scrollWidth = 550;
    private static final int scrollHeight = 250;

    private static final int ipX = -780;
    private static final int ipY = 40;
    private static final int ipXEnabled = 40;
    private static final int ipYEnabled = 40;
    private static final int ipWidth = 460;
    private static final int ipHeight = 100;
    private static final int textWidth = 260;

    private static final int hostX = 1650;
    private static final int hostY = 40;
    private static final int hostXEnabled = 660;
    private static final int hostYEnabled = 40;
    private static final int hostWidth = 580;
    private static final int hostHeight = 640;

    private static final int exitX = 20;
    private static final int exitY = 680;

    private static final int passwordX = 440;
    private static final int passwordY = 320;
    private static final int passwordWidth = 400;
    private static final int passwordHeight = 100;
    private static final float scaleSide = 0.25f;

    private static final float optionsScale = 0.5f;
    private static final float optionHeight = 35.0f;
    private static final float optionPad = 15.0f;
    private static final float detailsScale = 0.3f;

    private static final float titlePad = 25.0f;
    private static final float titleScale = 0.5f;
    private static final float subtitleScale = 0.25f;
    private static final int detailsTextWidth = 750;

    //this is the state underneath this state.
    private final GameState peekState;

    //This boolean determines if input is disabled. input is disabled if the player joins/hosts.
    private boolean inputDisabled;

    public LobbyState(final GameStateManager gsm,  GameState peekState) {
        super(gsm);
        this.peekState = peekState;
    }

    @Override
    public void show() {

        final LobbyState me = this;

        stage = new Stage() {
            {
                joinLobby = new WindowTable();
                joinLobby.setPosition(joinX, joinY);
                joinLobby.setSize(joinWidth, joinHeight);
                joinLobby.top();
                addActor(joinLobby);

                host = new WindowTable();
                host.setPosition(hostX, hostY);
                host.setSize(hostWidth, hostHeight);
                host.top();
                addActor(host);

                joinIP = new WindowTable();
                joinIP.setPosition(ipX, ipY);
                joinIP.setSize(ipWidth, ipHeight);
                addActor(joinIP);

                tableOptions = new WindowTable();

                Text title = new Text("JOIN", 0, 0, false);
                title.setScale(titleScale);

                Text lobbiesTitle = new Text("LOBBIES", 0, 0, false);
                lobbiesTitle.setScale(subtitleScale);

                options = new ScrollPane(tableOptions, GameStateManager.getSkin());
                options.setFadeScrollBars(false);

                searchOption = new Text("SEARCH", 0, 0, true);
                searchOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
                        gsm.getApp().getLobbyManager().requestLobbyList(10, me);
                    }
                });
                searchOption.setScale(optionsScale);

                Text ipDisplay = new Text("ENTER IP: ", 0, 0, false);
                ipDisplay.setScale(subtitleScale);

                enterIP = new TextField("", GameStateManager.getSkin());

                //retrieve last joined ip if existent
                if (gsm.getRecord().getLastIp().equals("")) {
                    enterIP.setMessageText("ENTER IP");
                } else {
                    enterIP.setText(gsm.getRecord().getLastIp());
                }

                Text joinOptionIP = new Text("CONNECT TO IP", 0, 0, true);
                joinOptionIP.setScale(subtitleScale);

                joinOptionIP.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {

                        if (inputDisabled) { return; }
                        inputDisabled = true;

                        SoundEffect.UISWITCH1.play(gsm, 1.0f, false);

                        //Start up the Client
                        HadalGame.client.init();
                        GameStateManager.currentMode = GameStateManager.Mode.MULTI;

                        setNotification("SEARCHING FOR SERVER!");
                        //Attempt to connect to the chosen ip
                        Gdx.app.postRunnable(() -> {

                            //Attempt for 500 milliseconds to connect to the ip. Then set notifications accordingly.
                            try {
                                //trim whitespace from ip
                                String trimmedIp = enterIP.getText().trim();

                                HadalGame.client.getClient().connect(5000, trimmedIp, gsm.getSetting().getPortNumber(), gsm.getSetting().getPortNumber());

                                //save last joined ip if successful
                                gsm.getRecord().setlastIp(trimmedIp);

                                setNotification("CONNECTED TO SERVER: " + trimmedIp);
                            } catch (IOException ex) {
                                setNotification("FAILED TO CONNECT TO SERVER!");

                                //Let the player attempt to connect again after finishing
                                inputDisabled = false;
                            }
                        });
                    }
                });

                hostOption = new Text("HOST", 0, 0, true);
                hostOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        SoundEffect.UISWITCH1.play(gsm, 1.0f, false);

                        if (gsm.getApp().getLobbyManager().connectedToSteam) {
                            gsm.getApp().getLobbyManager().createLobby(5, me);
                        } else {
                            notifications.setText("NOT CONNECTED TO STEAM");

                            HadalGame.server.init(true);
                            GameStateManager.currentMode = GameStateManager.Mode.MULTI;

                            //Enter the Hub State.
                            gsm.getApp().setRunAfterTransition(() -> gsm.gotoHubState(LobbyState.class));
                            gsm.getApp().fadeOut();
                        }
                    }
                });
                hostOption.setScale(optionsScale);

                exitOption = new Text("RETURN?", exitX, exitY, true);
                exitOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        SoundEffect.NEGATIVE.play(gsm, 1.0f, false);

                        transitionOut(() -> gsm.removeState(LobbyState.class));
                    }
                });
                exitOption.setScale(optionsScale);

                notifications = new Text("", 100, 100, false);
                notifications.setScale(optionsScale);

                joinLobby.add(title).colspan(2).height(optionHeight).pad(titlePad).row();
                joinLobby.add(lobbiesTitle).colspan(2).height(optionHeight).pad(titlePad).left().row();
                joinLobby.add(options).colspan(2).height(scrollHeight).width(scrollWidth).row();
                joinLobby.add(searchOption).height(optionHeight);

                joinIP.add(ipDisplay).height(optionHeight).pad(5);
                joinIP.add(enterIP).width(textWidth).height(optionHeight).row();
                joinIP.add(joinOptionIP).height(optionHeight);

                host.add(hostOption).colspan(2).height(optionHeight).pad(titlePad).row();

                addActor(exitOption);
                addActor(notifications);
            }
        };
        app.newMenu(stage);

        if (gsm.getApp().getFadeLevel() >= 1.0f) {
            gsm.getApp().fadeIn();
        }

        inputDisabled = true;
        transitionIn(() -> inputDisabled = false);
    }

    public void updateLobbies(Map<Long, SteamID> lobbies) {
        tableOptions.clear();

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
            tableOptions.add(lobbyOption).row();
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

    /**
     * When we connect to a password server, this is ran to bring up the password entering window
     */
    public void openPasswordRequest() {

        tablePassword = new WindowTable();
        tablePassword.setPosition(passwordX, passwordY);
        tablePassword.setSize(passwordWidth, passwordHeight);
        stage.addActor(tablePassword);

        Text password = new Text("PASSWORD: ", 0, 0, false);
        password.setScale(scaleSide);
        password.setHeight(optionHeight);

        enterPassword = new TextField("", GameStateManager.getSkin());
        enterPassword.setPasswordCharacter('*');
        enterPassword.setPasswordMode(true);
        enterPassword.setMessageText("PASSWORD");

        Text connect = new Text("CONNECT", 0, 0, true);
        connect.setScale(scaleSide);
        connect.setHeight(optionHeight);

        Text cancel = new Text("CANCEL", 0, 0, true);
        cancel.setScale(scaleSide);
        cancel.setHeight(optionHeight);

        connect.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent e, float x, float y) {
                SoundEffect.UISWITCH3.play(gsm, 1.0f, false);
                tablePassword.remove();
                tablePassword.setVisible(true);

                HadalGame.client.sendTCP(new Packets.PlayerConnect(true, enterName.getText(), HadalGame.Version, enterPassword.getText()));
            }
        });

        cancel.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent e, float x, float y) {
                SoundEffect.UISWITCH3.play(gsm, 1.0f, false);
                tablePassword.remove();
                tablePassword.setVisible(false);

                inputDisabled = false;
                HadalGame.client.getClient().stop();
            }
        });

        tablePassword.add(password).pad(5);
        tablePassword.add(enterPassword).width(textWidth).height(optionHeight).row();
        tablePassword.add(connect);
        tablePassword.add(cancel);
    }

    private static final float transitionDuration = 0.25f;
    private static final Interpolation intp = Interpolation.fastSlow;
    private void transitionOut(Runnable runnable) {
        joinLobby.addAction(Actions.moveTo(joinX, joinY, transitionDuration, intp));
        joinIP.addAction(Actions.moveTo(ipX, ipY, transitionDuration, intp));
        host.addAction(Actions.sequence(Actions.moveTo(hostX, hostY, transitionDuration, intp), Actions.run(runnable)));
    }

    private void transitionIn(Runnable runnable) {
        joinLobby.addAction(Actions.moveTo(joinXEnabled, joinYEnabled, transitionDuration, intp));
        joinIP.addAction(Actions.moveTo(ipXEnabled, ipYEnabled, transitionDuration, intp));
        host.addAction(Actions.sequence(Actions.run(runnable), Actions.moveTo(hostXEnabled, hostYEnabled, transitionDuration, intp)));
    }

    public void setNotification(String notification) { notifications.setText(notification); }

    public void setInputDisabled(boolean inputDisabled) { this.inputDisabled = inputDisabled; }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
