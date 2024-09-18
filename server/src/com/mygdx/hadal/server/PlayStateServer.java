package com.mygdx.hadal.server;

import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.map.GameMode;
import com.mygdx.hadal.save.UnlockLevel;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.users.UserManager;

public class PlayStateServer extends PlayState {

    private Lobby lobby;

    public PlayStateServer(HadalGame app, UnlockLevel level, GameMode mode, boolean reset, String startID, Lobby lobby) {
        super(app, level, mode, reset, startID);
        this.lobby = lobby;
    }

    @Override
    public UserManager getUserManager() { return lobby.getUserManager(); }

    public Lobby getLobby() { return lobby; }
}
