package com.mygdx.hadal.server.util;

import com.badlogic.gdx.Gdx;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.constants.ServerConstants;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.StateManager;
import com.mygdx.hadal.states.LobbyState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.text.UIText;
import com.mygdx.hadal.utils.UPNPUtil;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SocketManager {

    //socket is used to connect to matchmaking server
    public static Socket socket;

    public static OkHttpClient client;

    private static boolean connectionAttempted;
    private static float connectionDuration;

    //we keep track of server name for host clients so we can send it to headless server upon connnection
    private static String serverName;

    public static void initializeSocket(LobbyState lobby) {
        if (socket == null) {
            connectSocket(lobby);
            configSocketEvents(lobby);
        } else if (!socket.connected()) {
            connectSocket(lobby);
            configSocketEvents(lobby);
        }
        if (socket != null) {
            socket.emit("end");
        }
        retrieveLobbies();
    }

    /**
     * This makes a request to the server for a list of current lobbies
     */
    public static void retrieveLobbies() {
        if (socket != null) {
            socket.emit("getLobbies");
        }
    }

    public static void makeOnlineServer(String serverName) {
        if (socket != null) {
            JSONObject lobbyData = new JSONObject();
            lobbyData.put("name", serverName);
            lobbyData.put("playerCapacity", JSONManager.setting.getMaxPlayers() + 1);
            socket.emit("makeLobbyOnline", lobbyData.toString());

            SocketManager.serverName = serverName;
        }
    }

    public static void makeLocalServer(String serverName) {
        if (socket != null) {
            JSONObject lobbyData = new JSONObject();
            lobbyData.put("ip", getPublicIP());
            lobbyData.put("name", serverName);
            lobbyData.put("playerCapacity", JSONManager.setting.getMaxPlayers() + 1);
            socket.emit("makeLobbyLocal", lobbyData.toString());
        }
    }

    public static void updateLobby(PlayState state) {
        if (socket != null) {
            if (socket.connected()) {
                JSONObject lobbyData = new JSONObject();
                try {
                    lobbyData.put("playerNum", HadalGame.usm.getNumPlayers());
                    lobbyData.put("playerCapacity", JSONManager.setting.getMaxPlayers() + 1);
                    lobbyData.put("gameMode", state.getMode().getName());
                    lobbyData.put("gameMap", state.getLevel().getName());

                    //host clients need to give server ip so we know which lobby to update.
                    if (!state.isServer()) {
                        lobbyData.put("instanceID", HadalGame.client.getInstanceID());
                    }
                } catch (JSONException jsonException) {
                    Gdx.app.log("LOBBY", "FAILED TO SEND LOBBY INFO " + jsonException);
                }
                socket.emit("updateLobby", lobbyData.toString());
            }
        }
    }

    public static void exit() {
        if (socket != null) {
            socket.emit("exit");
        }
    }

    public static void update(LobbyState lobby, float delta) {
        if (connectionAttempted && connectionDuration < ServerConstants.CONNECTION_TIMEOUT) {
            connectionDuration += delta;

            if (connectionDuration > ServerConstants.CONNECTION_TIMEOUT) {
                connectionAttempted = false;
                lobby.setNotification(UIText.CONNECTION_MM_FAILED.text());
            }
        }
    }

    public static void dispose() {
        if (socket != null) {
            socket.close();
            socket = null;

            if (client != null) {
                client.connectionPool().evictAll();
                client.dispatcher().cancelAll();
                client.dispatcher().executorService().shutdownNow();
            }
        }
    }

    private static void connectSocket(LobbyState lobby) {
        try {
            URI uri = URI.create(ServerConstants.LOBBY_IP);

            client = new OkHttpClient.Builder()
                    .dispatcher(new Dispatcher())
                    .readTimeout(1, TimeUnit.MINUTES) // important for HTTP long-polling
                    .build();

            IO.Options options = new IO.Options();
            options.callFactory = client;
            options.webSocketFactory = client;

            socket = IO.socket(uri, options);

            socket.connect();

            connectionAttempted = true;
            connectionDuration = 0.0f;

            lobby.setNotification(UIText.SEARCHING_MM.text());
        } catch (Exception e) {
            Gdx.app.log("LOBBY", "FAILED TO CONNECT SOCKET: " + e);
        }
    }

    private static void configSocketEvents(LobbyState lobby) {
        if (socket == null) { return; }

        socket.on(Socket.EVENT_CONNECT, args -> {
            Gdx.app.log("LOBBY", "CONNECTED ");
            connectionAttempted = false;
        })
        .on(Socket.EVENT_CONNECT_ERROR, args -> Gdx.app.log("LOBBY", "CONNECTION ERROR " + Arrays.toString(args)))
        .on(Socket.EVENT_DISCONNECT, args -> Gdx.app.log("LOBBY", "DISCONNECTED"))
        .on("handshake", args -> Gdx.app.log("LOBBY", "HANDSHAKE RECEIVED"))
        .on("receiveLobbies", args -> {
            Gdx.app.log("LOBBY", "LOBBIES RECEIVED " + args[0]);
            JSONArray lobbies = (JSONArray) args[0];
            lobby.updateLobbies(lobbies);
        })
        .on("lobbyCreated", args -> {
            Gdx.app.log("LOBBY", "ONLINE LOBBY CREATED " + args[0]);

            HadalGame.client.init();
            StateManager.currentMode = StateManager.Mode.MULTI;
            Gdx.app.postRunnable(() -> {
                String serverIP = String.valueOf(args[0]);
                String instanceID = String.valueOf(args[1]);

                int retries = 0;
                while (retries < ServerConstants.MAX_RETRIES) {
                    try {
                        HadalGame.client.getClient().connect(ServerConstants.RETRY_DELAY, serverIP, ServerConstants.PORT, ServerConstants.PORT);
                        HadalGame.client.setInstanceID(instanceID);
                        break;
                    } catch (IOException ex) {
                        Gdx.app.log("LOBBY", "FAILED TO JOIN: " + ex);
                        try {
                            Thread.sleep(ServerConstants.RETRY_DELAY);
                        } catch (InterruptedException interrupt) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                    retries++;
                }
                if (retries >= ServerConstants.MAX_RETRIES) {
                    lobby.setNotification(UIText.CONNECTION_FAILED.text());
                }
                lobby.setInputDisabled(false);
            });
        });
    }

    /**
     * @return returns the player's public ip for hosting servers
     */
    private static String getPublicIP() {

        //if the player has already retrieved their ip when enabling upnp, this step is unnecessary.
        if (!"".equals(UPNPUtil.myIP)) {
            return UPNPUtil.myIP;
        }

        BufferedReader in = null;
        try {
            URI uri = URI.create(ServerConstants.IP_URL);
            URL whatismyip = uri.toURL();
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            UPNPUtil.myIP = in.readLine();
            return UPNPUtil.myIP;
        } catch (IOException ioException) {
            Gdx.app.error("ERROR RETRIEVING IP: ", ioException.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e2) {
                    Gdx.app.error("ERROR RETRIEVING IP: ", e2.getMessage());
                }
            }
        }
        return null;
    }

    public static String getServerName() { return SocketManager.serverName; }
}
