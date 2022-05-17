package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.actors.WindowTable;
import com.mygdx.hadal.audio.MusicTrackType;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.text.HText;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import static com.mygdx.hadal.utils.Constants.*;

public class LobbyState extends GameState {

    //This table contains the ui elements of the pause screen
    private Table joinLobby, lobbyTable, notificationTable, joinIP, host, tablePassword;
    private ScrollPane options;
    private VerticalGroup lobbyOptions;

    private TextField enterName, enterIP, enterPassword;

    //options that the player can view
    private Text hostOption, searchOption, exitOption, notifications;

    //Dimensions of the setting menu
    private static final int joinX = 1650;
    private static final int joinY = 240;
    private static final int joinXEnabled = 660;
    private static final int joinYEnabled = 240;
    private static final int joinWidth = 580;
    private static final int joinHeight = 440;
    private static final int scrollWidth = 550;
    private static final int scrollHeight = 250;

    private static final int ipX = 1540;
    private static final int ipY = 100;
    private static final int ipXEnabled = 720;
    private static final int ipYEnabled = 100;
    private static final int ipWidth = 460;
    private static final int ipHeight = 100;
    private static final int textWidth = 260;

    private static final int hostX = -980;
    private static final int hostY = 100;
    private static final int hostXEnabled = 40;
    private static final int hostYEnabled = 100;
    private static final int hostWidth = 580;
    private static final int hostHeight = 580;

    private static final int notificationX = 40;
    private static final int notificationY = -240;
    private static final int notificationXEnabled = 40;
    private static final int notificationYEnabled = 40;
    private static final int notificationWidth = 580;
    private static final int notificationHeight = 60;

    private static final int passwordX = 440;
    private static final int passwordY = 320;
    private static final int passwordWidth = 400;
    private static final int passwordHeight = 100;
    private static final float scaleSide = 0.25f;

    private static final float optionsScale = 0.5f;
    private static final float optionHeight = 35.0f;
    private static final float optionScale = 0.3f;
    private static final float optionPad = 10.0f;

    private static final float titlePad = 25.0f;
    private static final float titleScale = 0.5f;
    private static final float subtitleScale = 0.25f;

    //this is the state underneath this state.
    private final GameState peekState;

    //This boolean determines if input is disabled. input is disabled if the player joins/hosts.
    private boolean inputDisabled;

    private static final float refreshCd = 4.0f;
    private float refreshCdCount = refreshCd;

    private boolean connectionAttempted;
    private static final float connectionTimeout = 10.0f;
    private float connectionDuration;

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

                notificationTable = new WindowTable();
                notificationTable.setPosition(notificationX, notificationY);
                notificationTable.setSize(notificationWidth, notificationHeight);
                addActor(notificationTable);

                lobbyTable = new WindowTable();

                Text joinTitle = new Text(HText.JOIN.text());
                joinTitle.setScale(titleScale);

                Text lobbiesTitle = new Text(HText.LOBBIES.text());
                lobbiesTitle.setScale(subtitleScale);

                lobbyOptions = new VerticalGroup().pad(optionPad).align(Align.topLeft);

                options = new ScrollPane(lobbyOptions, GameStateManager.getSkin());
                options.setFadeScrollBars(false);

                searchOption = new Text(HText.REFRESH.text()).setButton(true);
                searchOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        if (refreshCdCount >= refreshCd) {
                            SoundEffect.UISWITCH1.play(gsm, 1.0f, false);

                            retrieveLobbies();
                            refreshCdCount = 0.0f;
                        }
                    }
                });
                searchOption.setScale(optionsScale);

                Text ipDisplay = new Text(HText.ENTER_IP.text());
                ipDisplay.setScale(subtitleScale);

                enterIP = new TextField("", GameStateManager.getSkin());

                //retrieve last joined ip if existent
                if (!gsm.getRecord().getLastIp().equals("")) {
                    enterIP.setText(gsm.getRecord().getLastIp());
                }

                Text joinOptionIP = new Text(HText.CONNECT_IP.text()).setButton(true);
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

                        setNotification(HText.SEARCHING_SERVER.text());
                        //Attempt to connect to the chosen ip
                        Gdx.app.postRunnable(() -> {

                            //Attempt for 500 milliseconds to connect to the ip. Then set notifications accordingly.
                            try {
                                //trim whitespace from ip
                                String trimmedIp = enterIP.getText().trim();

                                HadalGame.client.getClient().connect(8000, trimmedIp, gsm.getSetting().getPortNumber(), gsm.getSetting().getPortNumber());

                                //save last joined ip if successful
                                gsm.getRecord().setlastIp(trimmedIp);

                                setNotification(HText.CONNECTED.text(trimmedIp));
                            } catch (IOException ex) {
                                setNotification(HText.CONNECTION_FAILED.text());

                                //Let the player attempt to connect again after finishing
                                inputDisabled = false;
                            }
                        });
                    }
                });

                Text hostTitle = new Text(HText.HOST.text());
                hostTitle.setScale(titleScale);

                Text enterNameText = new Text(HText.SERVER_NAME.text());
                enterNameText.setScale(subtitleScale);
                enterName = new TextField(HText.SERVER_NAME_DEFAULT.text(gsm.getLoadout().getName()),
                        GameStateManager.getSkin());
                enterName.setMaxLength(MAX_NAME_LENGTH_LONG);
                enterName.setMessageText(HText.ENTER_NAME.text());

                Text serverSettings = new Text(HText.SERVER_SETTING_CHANGE.text()).setButton(true);
                serverSettings.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {

                        if (inputDisabled) { return; }
                        inputDisabled = true;

                        SoundEffect.UISWITCH1.play(gsm, 1.0f, false);

                        //Enter the Setting State.
                        transitionOut(() -> getGsm().addState(GameStateManager.State.SETTING, me));
                    }
                });
                serverSettings.setScale(optionsScale);

                hostOption = new Text(HText.SERVER_CREATE.text()).setButton(true);
                hostOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {

                        if (inputDisabled) { return; }
                        inputDisabled = true;

                        setNotification(HText.HOSTED.text());

                        if (HadalGame.socket != null) {
                            JSONObject lobbyData = new JSONObject();
                            try {
                                lobbyData.put("ip", getPublicIp());
                                lobbyData.put("name", enterName.getText());
                                lobbyData.put("playerNum", 1);
                                lobbyData.put("playerCapacity", gsm.getSetting().getMaxPlayers() + 1);
                            } catch (JSONException jsonException) {
                                Gdx.app.log("LOBBY", "FAILED TO CREATE LOBBY " + jsonException);
                            }
                            HadalGame.socket.emit("makeLobby", lobbyData.toString());
                        }

                        SoundEffect.UISWITCH1.play(gsm, 1.0f, false);

                        //Start up the server in multiplayer mode
                        HadalGame.server.init(true);
                        HadalGame.server.setServerName(enterName.getText());
                        GameStateManager.currentMode = GameStateManager.Mode.MULTI;

                        //Enter the Hub State.
                        gsm.getApp().setRunAfterTransition(() -> gsm.gotoHubState(LobbyState.class));
                        gsm.getApp().fadeOut();
                    }
                });
                hostOption.setScale(optionsScale);

                exitOption = new Text(HText.RETURN.text()).setButton(true);
                exitOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        SoundEffect.NEGATIVE.play(gsm, 1.0f, false);

                        transitionOut(() -> gsm.removeState(LobbyState.class));
                    }
                });
                exitOption.setScale(optionsScale);

                notifications = new Text("").setWrap(scrollWidth);
                notifications.setScale(subtitleScale);

                joinLobby.add(joinTitle).colspan(2).height(optionHeight).pad(titlePad).row();
                joinLobby.add(lobbiesTitle).height(optionHeight).pad(titlePad).left();
                joinLobby.add(searchOption).height(optionHeight).pad(titlePad).right().row();

                lobbyTable.add(options);
                joinLobby.add(lobbyTable).colspan(2).height(scrollHeight).width(scrollWidth).pad(optionPad).row();

                joinIP.add(ipDisplay).height(optionHeight).pad(5);
                joinIP.add(enterIP).width(textWidth).height(optionHeight).row();
                joinIP.add(joinOptionIP).height(optionHeight);

                notificationTable.add(notifications).pad(optionPad).expandX().left();

                host.add(hostTitle).colspan(2).height(optionHeight).pad(titlePad).row();
                host.add(enterNameText).height(optionHeight).pad(titlePad);
                host.add(enterName).width(textWidth).height(optionHeight).pad(titlePad).row();
                host.add(serverSettings).colspan(2).height(optionHeight).pad(optionPad).row();
                host.add(hostOption).colspan(2).height(optionHeight).pad(optionPad).row();
                host.add(exitOption).height(optionHeight).pad(optionPad).expandY().bottom().left().row();
            }
        };
        app.newMenu(stage);
        stage.setScrollFocus(options);

        if (HadalGame.socket == null) {
            connectSocket();
            configSocketEvents();
        } else if (!HadalGame.socket.connected()) {
            connectSocket();
            configSocketEvents();
        }

        if (HadalGame.socket != null) {
            HadalGame.socket.emit("end");
        }

        retrieveLobbies();

        if (gsm.getApp().getFadeLevel() >= 1.0f) {
            gsm.getApp().fadeIn();
        }

        HadalGame.musicPlayer.playSong(MusicTrackType.TITLE, 1.0f);

        inputDisabled = true;
        transitionIn(() -> inputDisabled = false);
    }

    private final static String serverIP = "https://hadalcalm-lobby-server.herokuapp.com/";
    public void connectSocket() {
        try {
            HadalGame.socket = IO.socket(serverIP);
            HadalGame.socket.connect();

            connectionAttempted = true;
            connectionDuration = 0.0f;

            setNotification(HText.SEARCHING_MM.text());
        } catch (Exception e) {
            Gdx.app.log("LOBBY", "FAILED TO CONNECT SOCKET: " + e);
        }
    }

    public void configSocketEvents() {
        if (HadalGame.socket == null) { return; }

        HadalGame.socket.on(Socket.EVENT_CONNECT, args -> {
            Gdx.app.log("LOBBY", "CONNECTED");
            connectionAttempted = false;
        })
            .on(Socket.EVENT_DISCONNECT, args -> Gdx.app.log("LOBBY", "DISCONNECTED"))
            .on("handshake", args -> Gdx.app.log("LOBBY", "HANDSHAKE RECEIVED"))
            .on("receiveLobbies", args -> { Gdx.app.log("LOBBY", "LOBBIES RECEIVED " + args[0]);

            JSONArray lobbies = (JSONArray) args[0];
            updateLobbies(lobbies);
        });
    }

    /**
     * This makes a request to the server for a list of current lobbies
     */
    public void retrieveLobbies() {
        if (HadalGame.socket != null) {
            HadalGame.socket.emit("getLobbies");
        }
    }

    /**
     * This is run when we receive lobbies from server to display in the ui window
     * @param lobbies: the currently active lobbies
     */
    public void updateLobbies(JSONArray lobbies) {
        try {
            lobbyOptions.clear();
            for (int i = 0; i < lobbies.length(); i++) {
                String lobbyName = lobbies.getJSONObject(i).getString("name");
                String lobbyIP = lobbies.getJSONObject(i).getString("ip");

                int playerNum = lobbies.getJSONObject(i).getInt("playerNum");
                int playerCapacity = lobbies.getJSONObject(i).getInt("playerCapacity");

                Text lobbyOption = new Text(lobbyName + " " + playerNum + " / " + playerCapacity).setButton(true);
                lobbyOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {

                        if (inputDisabled) { return; }
                        inputDisabled = true;

                        SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
                        setNotification(HText.JOINING.text());

                        HadalGame.client.init();
                        GameStateManager.currentMode = GameStateManager.Mode.MULTI;
                        Gdx.app.postRunnable(() -> {

                            //Attempt for 500 milliseconds to connect to the ip. Then set notifications accordingly.
                            try {
                                HadalGame.client.getClient().connect(5000, String.valueOf(lobbyIP),
                                    gsm.getSetting().getPortNumber(), gsm.getSetting().getPortNumber());
                            } catch (IOException ex) {
                                Gdx.app.log("LOBBY", "FAILED TO JOIN: " + ex);
                                setNotification(HText.CONNECTION_FAILED.text());
                                inputDisabled = false;
                            }
                        });
                    }
                });
                lobbyOption.setScale(optionScale);
                lobbyOption.setHeight(optionHeight);

                lobbyOptions.addActor(lobbyOption);
            }

            if (lobbies.length() == 0) {
                setNotification(HText.NO_LOBBIES.text());
            } else {
                setNotification(HText.LOBBIES_RETRIEVED.text());
            }
        } catch (JSONException e) {
            Gdx.app.log("LOBBY", "FAILED TO PARSE LOBBY LIST: " + e);
        }
    }

    @Override
    public void update(float delta) {
        peekState.update(delta);

        if (refreshCdCount < refreshCd) {
            refreshCdCount += delta;
        }

        if (connectionAttempted && connectionDuration < connectionTimeout) {
            connectionDuration += delta;

            if (connectionDuration > connectionTimeout) {
                connectionAttempted = false;
                setNotification(HText.CONNECTION_MM_FAILED.text());
            }
        }
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

        Text password = new Text(HText.PASSWORD.text());
        password.setScale(scaleSide);
        password.setHeight(optionHeight);

        enterPassword = new TextField("", GameStateManager.getSkin());
        enterPassword.setPasswordCharacter('*');
        enterPassword.setPasswordMode(true);

        Text connect = new Text(HText.PASSWORD_ENTER.text()).setButton(true);
        connect.setScale(scaleSide);
        connect.setHeight(optionHeight);

        Text cancel = new Text(HText.RETURN.text()).setButton(true);
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

    private void transitionOut(Runnable runnable) {
        joinLobby.addAction(Actions.moveTo(joinX, joinY, TRANSITION_DURATION, INTP_FASTSLOW));
        joinIP.addAction(Actions.moveTo(ipX, ipY, TRANSITION_DURATION, INTP_FASTSLOW));
        notificationTable.addAction(Actions.moveTo(notificationX, notificationY, TRANSITION_DURATION, INTP_FASTSLOW));
        host.addAction(Actions.sequence(Actions.moveTo(hostX, hostY, TRANSITION_DURATION, INTP_FASTSLOW), Actions.run(runnable)));
    }

    private void transitionIn(Runnable runnable) {
        joinLobby.addAction(Actions.moveTo(joinXEnabled, joinYEnabled, TRANSITION_DURATION, INTP_FASTSLOW));
        joinIP.addAction(Actions.moveTo(ipXEnabled, ipYEnabled, TRANSITION_DURATION, INTP_FASTSLOW));
        notificationTable.addAction(Actions.moveTo(notificationXEnabled, notificationYEnabled, TRANSITION_DURATION, INTP_FASTSLOW));
        host.addAction(Actions.sequence(Actions.run(runnable), Actions.moveTo(hostXEnabled, hostYEnabled, TRANSITION_DURATION, INTP_FASTSLOW)));
    }

    public void setNotification(String notification) {
        if (!notification.equals("")) {
            notificationTable.addAction(Actions.sequence(
                Actions.moveTo(notificationX, notificationY, TRANSITION_DURATION, INTP_FASTSLOW),
                Actions.run(() -> notifications.setText(notification)),
                Actions.moveTo(notificationXEnabled, notificationYEnabled, TRANSITION_DURATION, INTP_FASTSLOW)));
        }
    }

    public void setInputDisabled(boolean inputDisabled) { this.inputDisabled = inputDisabled; }

    @Override
    public void dispose() {
        stage.dispose();

        if (HadalGame.socket != null) {
            HadalGame.socket.disconnect();
        }
    }

    /**
     * @return returns the player's public ip for hosting servers
     */
    public static String getPublicIp() {

        //if the player has already retrieved their ip when enabling upnp, this step is unnecessary.
        if (!HadalGame.myIp.equals("")) {
            return HadalGame.myIp;
        }

        BufferedReader in = null;
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            HadalGame.myIp = in.readLine();
            return HadalGame.myIp;
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return null;
    }
}
