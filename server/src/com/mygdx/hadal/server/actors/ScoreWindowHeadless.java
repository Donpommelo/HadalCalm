package com.mygdx.hadal.server.actors;

import com.mygdx.hadal.actors.ScoreWindow;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;

public class ScoreWindowHeadless extends ScoreWindow {

    public ScoreWindowHeadless(PlayState state) {
        super(state);
    }

    @Override
    public void syncScoreTable() {}

    @Override
    public void syncSettingTable() {
        PacketManager.serverTCPAll(new Packets.SyncSharedSettings(JSONManager.sharedSetting));
    }
}
