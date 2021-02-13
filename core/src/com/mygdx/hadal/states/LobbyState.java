package com.mygdx.hadal.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
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
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.server.Packets;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class LobbyState extends GameState {

    //This table contains the ui elements of the pause screen
    private Table joinLobby, lobbyTable, notificationTable, tableOptions, joinIP, host, tablePassword;
    private ScrollPane options;
    private VerticalGroup lobbyOptions;

    private TextField enterName, enterIP, enterPassword;

    //options that the player can view
    private Text hostOption, searchOption, exitOption, notifications;

    private static final int maxLobbyNameLength = 30;

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

                tableOptions = new WindowTable();
                lobbyTable = new WindowTable();

                Text joinTitle = new Text("JOIN", 0, 0, false);
                joinTitle.setScale(titleScale);

                Text lobbiesTitle = new Text("LOBBIES", 0, 0, false);
                lobbiesTitle.setScale(subtitleScale);

                lobbyOptions = new VerticalGroup().pad(optionPad).align(Align.topLeft);

                options = new ScrollPane(lobbyOptions, GameStateManager.getSkin());
                options.setFadeScrollBars(false);

                searchOption = new Text("REFRESH?", 0, 0, true);
                searchOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
                        //gsm.getApp().getLobbyManager().requestLobbyList(10, me);

                        retrieveLobbies();
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

                Text joinOptionIP = new Text("CONNECT TO IP?", 0, 0, true);
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

                                HadalGame.client.getClient().connect(8000, trimmedIp, gsm.getSetting().getPortNumber(), gsm.getSetting().getPortNumber());

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

                Text hostTitle = new Text("HOST", 0, 0, false);
                hostTitle.setScale(titleScale);

                Text enterNameText = new Text("SERVER NAME: ", 0, 0, true);
                enterNameText.setScale(subtitleScale);
                enterName = new TextField(gsm.getLoadout().getName() + "'s Lobby", GameStateManager.getSkin());
                enterName.setMaxLength(maxLobbyNameLength);
                enterName.setMessageText("ENTER NAME");

                Text serverSettings = new Text("CHANGE SERVER SETTINGS?", 0, 0, true);
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

                hostOption = new Text("CREATE SERVER?", 0, 0, true);
                hostOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {

                        if (inputDisabled) { return; }
                        inputDisabled = true;

                        setNotification("SERVER HOSTED");

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
                        GameStateManager.currentMode = GameStateManager.Mode.MULTI;

                        //Enter the Hub State.
                        gsm.getApp().setRunAfterTransition(() -> gsm.gotoHubState(LobbyState.class));
                        gsm.getApp().fadeOut();
                    }
                });
                hostOption.setScale(optionsScale);

                exitOption = new Text("RETURN?", 0, 0, true);
                exitOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        SoundEffect.NEGATIVE.play(gsm, 1.0f, false);

                        transitionOut(() -> gsm.removeState(LobbyState.class));
                    }
                });
                exitOption.setScale(optionsScale);

                notifications = new Text("", 0, 0, false, true, scrollWidth);
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

        HadalGame.socket.emit("end");
        retrieveLobbies();

        if (gsm.getApp().getFadeLevel() >= 1.0f) {
            gsm.getApp().fadeIn();
        }

        inputDisabled = true;
        transitionIn(() -> inputDisabled = false);
    }

    private final static String serverIP = "http://151.213.181.107";
    private final static String serverPort = "8080";
    public void connectSocket() {
        try {
            HadalGame.socket = IO.socket(serverIP + ":" + serverPort);
            HadalGame.socket.connect();
            if (!HadalGame.socket.connected()) {
                setNotification("COULD NOT FIND MATCHMAKING SERVER");
            }
        } catch (Exception e) {
            Gdx.app.log("LOBBY", "FAILED TO CONNECT SOCKET");
        }
    }

    public void configSocketEvents() {
        if (HadalGame.socket == null) return;

        HadalGame.socket.on(Socket.EVENT_CONNECT, args -> {
            Gdx.app.log("LOBBY", "CONNECTED");
        }).on(Socket.EVENT_DISCONNECT, args -> {
            Gdx.app.log("LOBBY", "DISCONNECTED");
        }).on("handshake", args -> {
            Gdx.app.log("LOBBY", "HANDSHAKE RECEIVED");
        }).on("receiveLobbies", args -> {
            Gdx.app.log("LOBBY", "LOBBIES RECEIVED " + args[0]);

            JSONArray lobbies = (JSONArray) args[0];
            updateLobbies(lobbies);
        });
    }

    public void retrieveLobbies() {
        if (HadalGame.socket == null) {
            connectSocket();
            configSocketEvents();
        } else if (!HadalGame.socket.connected()) {
            connectSocket();
            configSocketEvents();
        }
        HadalGame.socket.emit("getLobbies");
    }

    public void updateLobbies(JSONArray lobbies) {
        try {
            lobbyOptions.clear();
            for (int i = 0; i < lobbies.length(); i++) {
                String lobbyName = lobbies.getJSONObject(i).getString("name");
                String lobbyIP = lobbies.getJSONObject(i).getString("ip");
                int playerNum = lobbies.getJSONObject(i).getInt("playerNum");
                int playerCapacity = lobbies.getJSONObject(i).getInt("playerCapacity");

                Text lobbyOption = new Text(lobbyName + " " + playerNum + " / " + playerCapacity, 0, 0, true);
                lobbyOption.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent e, float x, float y) {
                        SoundEffect.UISWITCH1.play(gsm, 1.0f, false);
                        setNotification("JOINED LOBBY");

                        HadalGame.client.init();
                        GameStateManager.currentMode = GameStateManager.Mode.MULTI;
                        Gdx.app.postRunnable(() -> {

                            //Attempt for 500 milliseconds to connect to the ip. Then set notifications accordingly.
                            try {
                                HadalGame.client.getClient().connect(5000, String.valueOf(lobbyIP),
                                    gsm.getSetting().getPortNumber(), gsm.getSetting().getPortNumber());
                            } catch (IOException ex) {
                                Gdx.app.log("LOBBY", "FAILED TO JOIN: " + ex);
                            }
                        });
                    }
                });
                lobbyOption.setScale(optionScale);
                lobbyOption.setHeight(optionHeight);

                lobbyOptions.addActor(lobbyOption);
            }

            if (lobbies.length() == 0) {
                setNotification("NO LOBBIES FOUND");
            } else {
                setNotification("LOBBIES RETRIEVED");
            }

        } catch (JSONException e) {
            Gdx.app.log("LOBBY", "FAILED TO PARSE LOBBY LIST: " + e);
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
        notificationTable.addAction(Actions.moveTo(notificationX, notificationY, transitionDuration, intp));
        host.addAction(Actions.sequence(Actions.moveTo(hostX, hostY, transitionDuration, intp), Actions.run(runnable)));
    }

    private void transitionIn(Runnable runnable) {
        joinLobby.addAction(Actions.moveTo(joinXEnabled, joinYEnabled, transitionDuration, intp));
        joinIP.addAction(Actions.moveTo(ipXEnabled, ipYEnabled, transitionDuration, intp));
        notificationTable.addAction(Actions.moveTo(notificationXEnabled, notificationYEnabled, transitionDuration, intp));
        host.addAction(Actions.sequence(Actions.run(runnable), Actions.moveTo(hostXEnabled, hostYEnabled, transitionDuration, intp)));
    }

    public void setNotification(String notification) {
        if (!notification.equals("")) {
            notificationTable.addAction(Actions.sequence(
                Actions.moveTo(notificationX, notificationY, transitionDuration, intp),
                Actions.run(() -> notifications.setText(notification)),
                Actions.moveTo(notificationXEnabled, notificationYEnabled, transitionDuration, intp)));
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
