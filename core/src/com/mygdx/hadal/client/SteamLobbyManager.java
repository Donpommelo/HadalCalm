package com.mygdx.hadal.client;

import com.badlogic.gdx.Gdx;
import com.codedisaster.steamworks.*;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.states.LobbyState;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.codedisaster.steamworks.SteamNativeHandle.getNativeHandle;

/**
 *
 * @author Falmham Fluham
 */
public class SteamLobbyManager {

    public boolean connectedToSteam;
    public String fug = "FUG";

    private SteamMatchmaking matchmaking;
    private final Map<Long, SteamID> lobbies = new HashMap<>();

    private static final String LobbyIpKey = "[ip-key]";
    private static final String LobbyNameKey = "[name-key]";
    private static final String LobbyDataKey = "[HADAL-key]";
    private static final String LobbyDataValue = "[HADAL-value]";

    private LobbyState lobbyState;

    private final SteamMatchmakingCallback matchmakingCallback = new SteamMatchmakingCallback() {

        @Override
        public void onFavoritesListChanged(int ip, int queryPort, int connPort, int appID, int flags, boolean add, int accountID) {

        }

        @Override
        public void onLobbyInvite(SteamID steamIDUser, SteamID steamIDLobby, long gameID) {
            lobbies.put(getNativeHandle(steamIDLobby), steamIDLobby);
            matchmaking.joinLobby(steamIDLobby);
        }

        @Override
        public void onLobbyEnter(SteamID steamIDLobby, int chatPermissions, boolean blocked, SteamMatchmaking.ChatRoomEnterResponse response) {
            lobbyState.setNotification("JOINED LOBBY");

            HadalGame.client.init();
            GameStateManager.currentMode = GameStateManager.Mode.MULTI;
            Gdx.app.postRunnable(() -> {

                //Attempt for 500 milliseconds to connect to the ip. Then set notifications accordingly.
                try {
                    String hostIP = matchmaking.getLobbyData(steamIDLobby, LobbyIpKey);

                    HadalGame.client.getClient().connect(5000, String.valueOf(hostIP), gsm.getSetting().getPortNumber(), gsm.getSetting().getPortNumber());
                } catch (IOException ex) {
                    lobbyState.setNotification("FAILED TO CONNECT TO SERVER: " );
                }
            });
        }

        @Override
        public void onLobbyDataUpdate(SteamID steamIDLobby, SteamID steamIDMember, boolean success) {

        }

        @Override
        public void onLobbyChatUpdate(SteamID steamIDLobby, SteamID steamIDUserChanged, SteamID steamIDMakingChange, SteamMatchmaking.ChatMemberStateChange stateChange) {

        }

        @Override
        public void onLobbyChatMessage(SteamID steamIDLobby, SteamID steamIDUser, SteamMatchmaking.ChatEntryType entryType, int chatID) {

        }

        @Override
        public void onLobbyGameCreated(SteamID steamIDLobby, SteamID steamIDGameServer, int ip, short port) {
//            HadalGame.client.init();
//            GameStateManager.currentMode = GameStateManager.Mode.MULTI;
//            Gdx.app.postRunnable(() -> {
//
//                //Attempt for 500 milliseconds to connect to the ip. Then set notifications accordingly.
//                try {
//                    String hostIP = matchmaking.getLobbyData(steamIDLobby, LobbyIpKey);
//
//                    HadalGame.client.getClient().connect(5000, String.valueOf(hostIP), port, port);
//                } catch (IOException ex) {
//                    lobbyState.setNotification("FAILED TO CONNECT TO SERVER: " );
//                }
//            });
        }

        @Override
        public void onLobbyMatchList(int lobbiesMatching) {
            lobbies.clear();
            for (int i = 0; i < lobbiesMatching; i++) {
                SteamID lobby = matchmaking.getLobbyByIndex(i);
                lobbies.put(getNativeHandle(lobby), lobby);
            }
            lobbyState.updateLobbies(lobbies);
        }

        @Override
        public void onLobbyKicked(SteamID steamIDLobby, SteamID steamIDAdmin, boolean kickedDueToDisconnect) {

        }

        @Override
        public void onLobbyCreated(SteamResult result, SteamID steamIDLobby) {

            if (result == SteamResult.OK) {
                lobbyState.setNotification("LOBY MAID");

                //Start up the server in multiplayer mode
                HadalGame.server.init(true);
                GameStateManager.currentMode = GameStateManager.Mode.MULTI;

                //Enter the Hub State.
                gsm.getApp().setRunAfterTransition(() -> gsm.gotoHubState(LobbyState.class));
                gsm.getApp().fadeOut();

                lobbies.put(getNativeHandle(steamIDLobby), steamIDLobby);
                matchmaking.setLobbyData(steamIDLobby, LobbyDataKey, LobbyDataValue);
                matchmaking.setLobbyData(steamIDLobby, LobbyIpKey, LobbyState.getPublicIp());
                matchmaking.setLobbyGameServer(steamIDLobby, 0, (short) gsm.getSetting().getPortNumber(), steamIDLobby);

            } else {
                lobbyState.setNotification("FAILED TO MAKE LOBBY: " + result);
            }
        }

        @Override
        public void onFavoritesListAccountsUpdated(SteamResult result) {}
    };

    private GameStateManager gsm;

    public SteamLobbyManager(GameStateManager gsm) {
        this.gsm = gsm;
    }

    public void createLobby(int maxMembers, LobbyState lobbyState) {
        matchmaking.createLobby(SteamMatchmaking.LobbyType.Public, maxMembers);
        lobbyState.setNotification("CREATING LOBBY");
        this.lobbyState = lobbyState;
    }

    public void requestLobbyList(int maxLobbies, LobbyState lobbyState) {
        if (connectedToSteam) {
            matchmaking.addRequestLobbyListResultCountFilter(maxLobbies);
            matchmaking.addRequestLobbyListStringFilter(LobbyDataKey, LobbyDataValue, SteamMatchmaking.LobbyComparison.Equal);

            matchmaking.requestLobbyList();
        } else {
            lobbyState.setNotification("NOT CONNECTED TO STEAM");
        }
        this.lobbyState = lobbyState;
    }

    public void joinLobby(long id, LobbyState lobbyState) {
        if (connectedToSteam) {
            matchmaking.joinLobby(lobbies.get(id));
        } else {
            lobbyState.setNotification("NOT CONNECTED TO STEAM");
        }

        this.lobbyState = lobbyState;
    }

    public boolean initializeLobbyManager() throws SteamException {

        if (connectedToSteam) { return true; }

        SteamAPI.loadLibraries();

        if (!SteamAPI.init()) {
            return false;
        }

        connectedToSteam = true;

        matchmaking = new SteamMatchmaking(matchmakingCallback);
        return true;
    }

    public void disposeLobbyManager() {
        if (connectedToSteam) {
            matchmaking.dispose();
        }
    }

    private float callbackAccumulator;
    private static final float callbackTime = 1 / 15f;
    public void controller(float delta) {

        if (!connectedToSteam) { return; }

        callbackAccumulator += delta;
        while (callbackAccumulator >= callbackTime) {
            callbackAccumulator -= callbackTime;
            SteamAPI.runCallbacks();
        }
    }
}
