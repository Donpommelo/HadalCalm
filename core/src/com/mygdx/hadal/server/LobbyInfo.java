package com.mygdx.hadal.server;

public class LobbyInfo {

    private int lobbyID;
    private String name, ip;
    private boolean isLocal;

    private LobbyInfoDynamic lobbyInfoDynamic;

    public LobbyInfo() {}

    public LobbyInfo(int lobbyID, String name, LobbyInfoDynamic lobbyInfoDynamic) {
        this.lobbyID = lobbyID;
        this.name = name;
        this.lobbyInfoDynamic = lobbyInfoDynamic;
    }

    public LobbyInfo(int lobbyID, String name, String ip, LobbyInfoDynamic lobbyInfoDynamic) {
        this.lobbyID = lobbyID;
        this.name = name;
        this.ip = ip;
        this.lobbyInfoDynamic = lobbyInfoDynamic;

        this.isLocal = true;
    }

    public int getLobbyID() { return lobbyID; }

    public String getName() { return name; }

    public String getIp() { return ip; }

    public boolean isLocal() { return isLocal; }

    public LobbyInfoDynamic getLobbyInfoDynamic() { return lobbyInfoDynamic; }
}
