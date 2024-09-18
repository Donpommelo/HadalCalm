package com.mygdx.hadal.server;

import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;

public class LobbyInfoDynamic {

    private int playerNum, playerCapacity, hostID;
    private String hostName;
    private GameMode mode;
    private UnlockLevel level;

    public LobbyInfoDynamic() {}

    public LobbyInfoDynamic(int playerNum, int playerCapacity, int hostID, String hostName, GameMode mode, UnlockLevel level) {
        this.playerNum = playerNum;
        this.playerCapacity = playerCapacity;
        this.hostID = hostID;
        this.hostName = hostName;
        this.mode = mode;
        this.level = level;
    }

    public LobbyInfoDynamic(int playerNum, int playerCapacity, GameMode mode, UnlockLevel level) {
        this.playerNum = playerNum;
        this.playerCapacity = playerCapacity;
        this.mode = mode;
        this.level = level;
    }

    public int getPlayerNum() { return playerNum; }

    public int getPlayerCapacity() { return playerCapacity; }

    public int getHostID() { return hostID; }

    public String getHostName() { return hostName; }

    public GameMode getMode() { return mode; }

    public UnlockLevel getLevel() { return level; }
}
