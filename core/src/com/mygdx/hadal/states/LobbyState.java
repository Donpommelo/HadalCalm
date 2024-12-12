package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.TableButton;
import com.mygdx.hadal.actors.TableWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.audio.MusicPlayer;
import com.mygdx.hadal.audio.MusicTrackType;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.ServerConstants;
import com.mygdx.hadal.managers.FadeManager;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.SoundManager;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.server.util.SocketManager;
import com.mygdx.hadal.text.UIText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.mygdx.hadal.constants.Constants.*;
import static com.mygdx.hadal.managers.SkinManager.SKIN;

public class LobbyState extends GameState {

    //Dimensions of the setting menu
    private static final int JOIN_X = 1650;
    private static final int JOIN_Y = 240;
    private static final int JOIN_X_ENABLED = 620;
    private static final int JOIN_Y_ENABLED = 240;
    private static final int JOIN_WIDTH = 620;
    private static final int JOIN_HEIGHT = 440;
    private static final int SCROLL_WIDTH = 590;
    private static final int SCROLL_HEIGHT = 250;

    private static final int IP_X = 1540;
    private static final int IP_Y = 100;
    private static final int IP_X_ENABLED = 700;
    private static final int IP_Y_ENABLED = 100;
    private static final int IP_WIDTH = 460;
    private static final int IP_HEIGHT = 100;
    private static final int TEXT_WIDTH = 260;

    private static final int HOST_X = -980;
    private static final int HOST_Y = 100;
    private static final int HOST_X_ENABLED = 40;
    private static final int HOST_Y_ENABLED = 100;
    private static final int HOST_WIDTH = 540;
    private static final int HOST_HEIGHT = 580;

    private static final int NOTIFICATION_X = 40;
    private static final int NOTIFICATION_Y = -240;
    private static final int NOTIFICATION_X_ENABLED = 40;
    private static final int NOTIFICATION_Y_ENABLED = 40;
    private static final int NOTIFICATION_WIDTH = 540;
    private static final int NOTIFICATION_HEIGHT = 60;

    private static final int PASSWORD_X = 440;
    private static final int PASSWORD_Y = 320;
    private static final int PASSWORD_WIDTH = 400;
    private static final int PASSWORD_HEIGHT = 100;
    private static final float SCALE_SIDE = 0.25f;

    private static final float OPTIONS_SCALE = 0.5f;
    private static final float OPTION_HEIGHT = 35.0f;
    private static final float OPTION_SCALE = 0.225f;
    private static final float OPTION_PAD = 10.0f;

    private static final float TITLE_PAD = 25.0f;
    private static final float TITLE_SCALE = 0.5f;
    private static final float SUBTITLE_SCALE = 0.25f;

    private static final float REFRESH_CD = 4.0f;

    //This table contains the ui elements of the pause screen
    private Table joinLobby, lobbyOptions, lobbyTable, notificationTable, joinIP, host, tablePassword;
    private ScrollPane options;

    private TextField enterName, enterIP, enterPassword;

    //options that the player can view
    private Text hostOption, hostOptionLocal, searchOption, exitOption, notifications;

    //this is the state underneath this state.
    private final GameState peekState;

    //This boolean determines if input is disabled. input is disabled if the player joins/hosts.
    private boolean inputDisabled;

    private float refreshCdCount = REFRESH_CD;

    public LobbyState(HadalGame app, GameState peekState) {
        super(app);
        this.peekState = peekState;
    }

    @Override
    public void show() {

        final LobbyState me = this;
        stage = new Stage() {
            {
                joinLobby = new TableWindow();
                joinLobby.setPosition(JOIN_X, JOIN_Y);
                joinLobby.setSize(JOIN_WIDTH, JOIN_HEIGHT);
                joinLobby.top();
                addActor(joinLobby);
                host = new TableWindow();
                host.setPosition(HOST_X, HOST_Y);
                host.setSize(HOST_WIDTH, HOST_HEIGHT);
                host.top();
                addActor(host);

                joinIP = new TableWindow();
                joinIP.setPosition(IP_X, IP_Y);
                joinIP.setSize(IP_WIDTH, IP_HEIGHT);
                addActor(joinIP);

                notificationTable = new TableWindow();
                notificationTable.setPosition(NOTIFICATION_X, NOTIFICATION_Y);
                notificationTable.setSize(NOTIFICATION_WIDTH, NOTIFICATION_HEIGHT);
                addActor(notificationTable);

                lobbyTable = new TableWindow();

                Text joinTitle = new Text(UIText.JOIN.text());
                joinTitle.setScale(TITLE_SCALE);

                Text lobbiesTitle = new Text(UIText.LOBBIES.text());
                lobbiesTitle.setScale(SUBTITLE_SCALE);

                lobbyOptions = new Table().align(Align.top);

                options = new ScrollPane(lobbyOptions, SKIN);
                options.setFadeScrollBars(false);
                options.setScrollingDisabled(true, false);

                searchOption = new Text(UIText.REFRESH.text()).setButton(true);
                searchOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        if (refreshCdCount >= REFRESH_CD) {
                            SoundManager.play(SoundEffect.UISWITCH1);

                            SocketManager.retrieveLobbies();
                            refreshCdCount = 0.0f;
                        }
                    }
                });
                searchOption.setScale(OPTIONS_SCALE);

                Text ipDisplay = new Text(UIText.ENTER_IP.text());
                ipDisplay.setScale(SUBTITLE_SCALE);

                enterIP = new TextField("", SKIN);

                //retrieve last joined ip if existent
                if (!"".equals(JSONManager.record.getLastIp())) {
                    enterIP.setText(JSONManager.record.getLastIp());
                }

                Text joinOptionIP = new Text(UIText.CONNECT_IP.text()).setButton(true);
                joinOptionIP.setScale(SUBTITLE_SCALE);

                joinOptionIP.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {

                        if (inputDisabled) { return; }
                        inputDisabled = true;

                        SoundManager.play(SoundEffect.UISWITCH1);

                        //Start up the Client
                        HadalGame.client.init();
                        StateManager.currentMode = StateManager.Mode.MULTI;

                        setNotification(UIText.SEARCHING_SERVER.text());
                        //Attempt to connect to the chosen ip
                        Gdx.app.postRunnable(() -> {

                            //Attempt for 500 milliseconds to connect to the ip. Then set notifications accordingly.
                            try {
                                //trim whitespace from ip
                                String trimmedIP = enterIP.getText().trim();

                                HadalGame.client.getClient().connect(8000, trimmedIP, ServerConstants.PORT, ServerConstants.PORT);

                                //save last joined ip if successful
                                JSONManager.record.setLastIP(trimmedIP);

                                setNotification(UIText.CONNECTED.text(trimmedIP));
                            } catch (IOException ex) {
                                setNotification(UIText.CONNECTION_FAILED.text());

                                //Let the player attempt to connect again after finishing
                                inputDisabled = false;
                            }
                        });
                    }
                });

                Text hostTitle = new Text(UIText.HOST.text());
                hostTitle.setScale(TITLE_SCALE);

                Text enterNameText = new Text(UIText.SERVER_NAME.text());
                enterNameText.setScale(SUBTITLE_SCALE);
                enterName = new TextField(UIText.SERVER_NAME_DEFAULT.text(JSONManager.loadout.getName()), SKIN);
                enterName.setMaxLength(MAX_NAME_LENGTH_LONG);
                enterName.setMessageText(UIText.ENTER_NAME.text());

                Text serverSettings = new Text(UIText.SERVER_SETTING_CHANGE.text()).setButton(true);
                serverSettings.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {

                        if (inputDisabled) { return; }
                        inputDisabled = true;

                        SoundManager.play(SoundEffect.UISWITCH1);

                        //Enter the Setting State.
                        transitionOut(() -> StateManager.addState(app, StateManager.State.SETTING, me));
                    }
                });
                serverSettings.setScale(OPTIONS_SCALE);

                hostOption = new Text(UIText.SERVER_CREATE.text()).setButton(true);
                hostOption.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent e, float x, float y) {

                        if (inputDisabled) { return; }
                        inputDisabled = true;

                        setNotification(UIText.HOSTED.text());
                        SocketManager.makeOnlineServer(enterName.getText());
                        SoundManager.play(SoundEffect.UISWITCH1);
                    }
                });
                hostOption.setScale(OPTIONS_SCALE);

                hostOptionLocal = new Text(UIText.SERVER_CREATE_LOCAL.text()).setButton(true);
                hostOptionLocal.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {

                        if (inputDisabled) { return; }
                        inputDisabled = true;

                        setNotification(UIText.HOSTED.text());
                        SocketManager.makeLocalServer(enterName.getText());
                        SoundManager.play(SoundEffect.UISWITCH1);

                        //Start up the server in multiplayer mode
                        HadalGame.server.init(true, false);
                        HadalGame.server.setServerName(enterName.getText());
                        StateManager.currentMode = StateManager.Mode.MULTI;

                        //Enter the Hub State.
                        FadeManager.setRunAfterTransition(() -> StateManager.gotoHubState(app, LobbyState.class));
                        FadeManager.fadeOut();
                    }
                });
                hostOptionLocal.setScale(OPTIONS_SCALE);

                exitOption = new Text(UIText.RETURN.text()).setButton(true);
                exitOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        SoundManager.play(SoundEffect.NEGATIVE);

                        transitionOut(() -> StateManager.removeState(LobbyState.class));
                    }
                });
                exitOption.setScale(OPTIONS_SCALE);

                notifications = new Text("").setWrap(SCROLL_WIDTH);
                notifications.setScale(SUBTITLE_SCALE);

                joinLobby.add(joinTitle).colspan(2).height(OPTION_HEIGHT).pad(TITLE_PAD).row();
                joinLobby.add(lobbiesTitle).height(OPTION_HEIGHT).pad(TITLE_PAD).left();
                joinLobby.add(searchOption).height(OPTION_HEIGHT).pad(TITLE_PAD).right().row();

                lobbyTable.add(options).grow().top();
                joinLobby.add(lobbyTable).colspan(2).height(SCROLL_HEIGHT).width(SCROLL_WIDTH).pad(OPTION_PAD).row();

                joinIP.add(ipDisplay).height(OPTION_HEIGHT).pad(5);
                joinIP.add(enterIP).width(TEXT_WIDTH).height(OPTION_HEIGHT).row();
                joinIP.add(joinOptionIP).height(OPTION_HEIGHT);

                notificationTable.add(notifications).pad(OPTION_PAD).expandX().left();

                host.add(hostTitle).colspan(2).height(OPTION_HEIGHT).pad(TITLE_PAD).row();
                host.add(enterNameText).height(OPTION_HEIGHT).pad(TITLE_PAD);
                host.add(enterName).width(TEXT_WIDTH).height(OPTION_HEIGHT).pad(TITLE_PAD).row();
                host.add(hostOption).colspan(2).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
                host.add(hostOptionLocal).colspan(2).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
                host.add(serverSettings).colspan(2).height(OPTION_HEIGHT).pad(OPTION_PAD).row();
                host.add(exitOption).height(OPTION_HEIGHT).pad(OPTION_PAD).expandY().bottom().left().row();
            }
        };
        app.newMenu(stage);
        stage.setScrollFocus(options);

        SocketManager.initializeSocket(this);

        if (FadeManager.getFadeLevel() >= 1.0f) {
            FadeManager.fadeIn();
        }

        MusicPlayer.playSong(MusicTrackType.TITLE, 1.0f);

        inputDisabled = true;
        transitionIn(() -> inputDisabled = false);
    }

    /**
     * This is run when we receive lobbies from server to display in the ui window
     * @param lobbies: the currently active lobbies
     */
    public void updateLobbies(JSONArray lobbies) {
        try {
            lobbyOptions.clear();
            for (int i = 0; i < lobbies.length(); i++) {
                JSONObject roomInfo = lobbies.getJSONObject(i);
                String lobbyName = roomInfo.optString("name");
                String lobbyIP = roomInfo.optString("ip");
                String instanceID = roomInfo.optString("instanceID");

                int playerNum = roomInfo.optInt("playerNum");
                int playerCapacity = roomInfo.optInt("playerCapacity");
                String gameMode = roomInfo.optString("gameMode");
                String gameMap = roomInfo.optString("gameMap");

                Table lobbyOption = new TableButton();

                Text nameAndCapacity = new Text(lobbyName + " (" + playerNum + " / " + playerCapacity + ")");
                nameAndCapacity.setScale(OPTION_SCALE);
                Text modeAndMap = new Text(gameMode + ": " + gameMap);
                modeAndMap.setScale(OPTION_SCALE);

                lobbyOption.add(nameAndCapacity).pad(OPTION_PAD).grow().left();
                lobbyOption.add(modeAndMap).pad(OPTION_PAD).grow().right();
                lobbyOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {

                        if (inputDisabled) { return; }
                        inputDisabled = true;

                        SoundManager.play(SoundEffect.UISWITCH1);
                        setNotification(UIText.JOINING.text());

                        HadalGame.client.init();
                        StateManager.currentMode = StateManager.Mode.MULTI;
                        Gdx.app.postRunnable(() -> {

                            //Attempt for 500 milliseconds to connect to the ip. Then set notifications accordingly.
                            try {
                                HadalGame.client.setInstanceID(instanceID);
                                HadalGame.client.getClient().connect(5000, lobbyIP, ServerConstants.PORT, ServerConstants.PORT);
                            } catch (IOException ex) {
                                Gdx.app.log("LOBBY", "FAILED TO JOIN: " + ex);
                                setNotification(UIText.CONNECTION_FAILED.text());
                                inputDisabled = false;
                            }
                        });
                    }
                });
                lobbyOptions.add(lobbyOption).padTop(OPTION_PAD).width(SCROLL_WIDTH).height(OPTION_HEIGHT).row();
            }

            if (lobbies.isEmpty()) {
                setNotification(UIText.NO_LOBBIES.text());
            } else {
                setNotification(UIText.LOBBIES_RETRIEVED.text());
            }
        } catch (JSONException e) {
            Gdx.app.log("LOBBY", "FAILED TO PARSE LOBBY LIST: " + e);
        }
    }

    @Override
    public void update(float delta) {
        peekState.update(delta);

        if (refreshCdCount < REFRESH_CD) {
            refreshCdCount += delta;
        }

        SocketManager.update(this, delta);
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

        tablePassword = new TableWindow();
        tablePassword.setPosition(PASSWORD_X, PASSWORD_Y);
        tablePassword.setSize(PASSWORD_WIDTH, PASSWORD_HEIGHT);
        stage.addActor(tablePassword);

        Text password = new Text(UIText.PASSWORD.text());
        password.setScale(SCALE_SIDE);
        password.setHeight(OPTION_HEIGHT);

        enterPassword = new TextField("", SKIN);
        enterPassword.setPasswordCharacter('*');
        enterPassword.setPasswordMode(true);

        Text connect = new Text(UIText.PASSWORD_ENTER.text()).setButton(true);
        connect.setScale(SCALE_SIDE);
        connect.setHeight(OPTION_HEIGHT);

        Text cancel = new Text(UIText.RETURN.text()).setButton(true);
        cancel.setScale(SCALE_SIDE);
        cancel.setHeight(OPTION_HEIGHT);

        connect.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent e, float x, float y) {
                SoundManager.play(SoundEffect.UISWITCH3);
                tablePassword.remove();
                tablePassword.setVisible(true);

                PacketManager.clientTCP(new Packets.PlayerConnect(true, enterName.getText(), HadalGame.VERSION, enterPassword.getText()));
            }
        });

        cancel.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent e, float x, float y) {
                SoundManager.play(SoundEffect.UISWITCH3);
                tablePassword.remove();
                tablePassword.setVisible(false);

                inputDisabled = false;
                HadalGame.client.getClient().stop();
            }
        });

        tablePassword.add(password).pad(5);
        tablePassword.add(enterPassword).width(TEXT_WIDTH).height(OPTION_HEIGHT).row();
        tablePassword.add(connect);
        tablePassword.add(cancel);
    }

    private void transitionOut(Runnable runnable) {
        joinLobby.addAction(Actions.moveTo(JOIN_X, JOIN_Y, TRANSITION_DURATION, INTP_FASTSLOW));
        joinIP.addAction(Actions.moveTo(IP_X, IP_Y, TRANSITION_DURATION, INTP_FASTSLOW));
        notificationTable.addAction(Actions.moveTo(NOTIFICATION_X, NOTIFICATION_Y, TRANSITION_DURATION, INTP_FASTSLOW));
        host.addAction(Actions.sequence(Actions.moveTo(HOST_X, HOST_Y, TRANSITION_DURATION, INTP_FASTSLOW), Actions.run(runnable)));
    }

    private void transitionIn(Runnable runnable) {
        joinLobby.addAction(Actions.moveTo(JOIN_X_ENABLED, JOIN_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW));
        joinIP.addAction(Actions.moveTo(IP_X_ENABLED, IP_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW));
        notificationTable.addAction(Actions.moveTo(NOTIFICATION_X_ENABLED, NOTIFICATION_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW));
        host.addAction(Actions.sequence(Actions.run(runnable), Actions.moveTo(HOST_X_ENABLED, HOST_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW)));
    }

    public void setNotification(String notification) {
        if (!"".equals(notification)) {
            notificationTable.addAction(Actions.sequence(
                Actions.moveTo(NOTIFICATION_X, NOTIFICATION_Y, TRANSITION_DURATION, INTP_FASTSLOW),
                Actions.run(() -> notifications.setText(notification)),
                Actions.moveTo(NOTIFICATION_X_ENABLED, NOTIFICATION_Y_ENABLED, TRANSITION_DURATION, INTP_FASTSLOW)));
        }
    }

    public void setInputDisabled(boolean inputDisabled) { this.inputDisabled = inputDisabled; }

    @Override
    public void dispose() {
        stage.dispose();
        SocketManager.dispose();;
    }
}
