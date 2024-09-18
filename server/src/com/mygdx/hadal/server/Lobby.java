package com.mygdx.hadal.server;

import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.users.User;
import com.mygdx.hadal.users.UserManager;

public class Lobby {

    //These keep track of all connected users mapped to their connection id
    private final UserManager userManager = new UserManager();

    private LobbyInfo lobbyInfo;

    private PlayState state;

    public Lobby(LobbyInfo lobbyInfo) {
        this.lobbyInfo = lobbyInfo;
        int connID = lobbyInfo.getLobbyInfoDynamic().getHostID();
        userManager.getUsers().put(connID, new User(connID, lobbyInfo.getLobbyInfoDynamic().getHostName(), new Loadout()));

        state = new PlayStateServer(null, UnlockLevel.HUB_MULTI, GameMode.HUB, true, "", this);
        state.initializeMap();
    }

    public synchronized void update(float delta) {
        state.update(delta);
    }

    public UserManager getUserManager() { return userManager; }

    public LobbyInfo getLobbyInfo() { return lobbyInfo; }
}
