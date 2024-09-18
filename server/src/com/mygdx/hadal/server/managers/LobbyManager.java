package com.mygdx.hadal.server.managers;

import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.server.Lobby;
import com.mygdx.hadal.server.LobbyInfo;
import com.mygdx.hadal.server.LobbyInfoDynamic;
import com.mygdx.hadal.users.User;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LobbyManager {

    private static final ObjectMap<Integer, Lobby> lobbies = new ObjectMap<>();
    private static final ObjectMap<Integer, Lobby> clients = new ObjectMap<>();
    private static ExecutorService executor;

    public static void init() {
        int numberOfThreads = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(numberOfThreads);
    }

    public static void controller(float delta) {
        for (Lobby lobby : lobbies.values()) {
            executor.submit(() -> lobby.update(delta));
        }
    }

    public static LobbyInfo[] getLobbyInfo() {
        LobbyInfo[] lobbyInfos = new LobbyInfo[lobbies.size];
        for (int i = 0; i < lobbies.size; i++) {
            lobbyInfos[i] = lobbies.get(i).getLobbyInfo();
        }
        return lobbyInfos;
    }

    private static int nextLobbyID = 1;
    public static void addLobby(int connID, String lobbyName, String hostName, int playerCapacity) {

        //if this client is hosting any other servers, do not create a new server.
        if (clients.containsKey(connID)) {
            return;
        }

        LobbyInfoDynamic lobbyInfoDynamic = new LobbyInfoDynamic(0, playerCapacity, connID, hostName,
                GameMode.HUB, UnlockLevel.HUB_MULTI);
        LobbyInfo lobbyInfo = new LobbyInfo(nextLobbyID, lobbyName, lobbyInfoDynamic);
        Lobby lobby = new Lobby(lobbyInfo);

        lobbies.put(nextLobbyID, lobby);
        nextLobbyID++;
    }

    public static void removeLobby(Lobby lobby) {
        lobbies.remove(lobby.getLobbyInfo().getLobbyID());

        for (Integer clientID : lobby.getUserManager().getUsers().keys()) {
            clients.remove(clientID);
        }
    }

    public static void addClientToLobby(int connID, Lobby lobby) {
        clients.put(connID, lobby);

        //TODO: populate with user information
        lobby.getUserManager().getUsers().put(connID, new User(connID, "", new Loadout()));
    }

    public static void removeClientFromLobby(int connID, Lobby lobby) {
        clients.remove(connID);
        lobby.getUserManager().getUsers().remove(connID);
        if (lobby.getUserManager().getUsers().size == 0) {
            removeLobby(lobby);
        }
    }

    public static Lobby getLobbyForConnection(int connID) {
        return clients.get(connID);
    }

    public static void dispose() throws IOException {
        if (executor != null) {
            executor.shutdown();
        }
    }

    public static ObjectMap<Integer, Lobby> getLobbies() {
        return lobbies;
    }
}
