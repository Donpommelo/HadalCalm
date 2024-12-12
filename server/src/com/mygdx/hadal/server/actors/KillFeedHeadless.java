package com.mygdx.hadal.server.actors;

import com.mygdx.hadal.actors.KillFeed;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.enemies.EnemyType;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;

public class KillFeedHeadless extends KillFeed {

    public KillFeedHeadless(PlayState ps) {
        super(ps);
    }

    @Override
    public void initActors() {}

    @Override
    public void addMessage(Player perp, Player vic, EnemyType type, DamageSource source, DamageTag... tags) {}

    @Override
    public void addNotification(String text, boolean global) {
        if (global) {
            PacketManager.serverTCPAll(new Packets.SyncNotification(text));
        }
    }
}
