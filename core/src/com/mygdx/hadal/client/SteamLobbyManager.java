package com.mygdx.hadal.client;

import com.codedisaster.steamworks.*;
import com.mygdx.hadal.states.LobbyState;

import java.util.HashMap;
import java.util.Map;

import static com.codedisaster.steamworks.SteamNativeHandle.getNativeHandle;

public class SteamLobbyManager {

    public boolean connectedToSteam;
    public String fug = "FUG";

    private SteamMatchmaking matchmaking;
    private final Map<Long, SteamID> lobbies = new HashMap<>();

    private static final String LobbyDataKey = "[test-key]";
    private static final String LobbyDataValue = "[test-value]";

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
            String hostAddress = matchmaking.getLobbyData(steamIDLobby, LobbyDataKey);
            lobbyState.setNotification("MEEP");
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
                lobbies.put(getNativeHandle(steamIDLobby), steamIDLobby);
                matchmaking.setLobbyData(steamIDLobby, LobbyDataKey, LobbyDataValue);

                lobbyState.setNotification("LOBY MAID");
            }
        }

        @Override
        public void onFavoritesListAccountsUpdated(SteamResult result) {}
    };

    public SteamLobbyManager() {

    }

    public void createLobby(int maxMembers, LobbyState lobbyState) {
        if (connectedToSteam) {
            matchmaking.createLobby(SteamMatchmaking.LobbyType.Public, maxMembers);
        } else {
            lobbyState.setNotification("NOT CONNECTED TO STEAM");
        }
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
            SteamAPI.printDebugInfo(System.err);
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
