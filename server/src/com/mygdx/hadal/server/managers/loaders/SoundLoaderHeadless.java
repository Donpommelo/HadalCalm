package com.mygdx.hadal.server.managers.loaders;

import com.mygdx.hadal.server.util.PacketManager;
import com.mygdx.hadal.managers.loaders.SoundLoader;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;

public class SoundLoaderHeadless extends SoundLoader {

    @Override
    public long play(PlayState state, SoundLoad soundLoad) { return 0L; }

    @Override
    public void playUniversal(PlayState state, SoundLoad soundLoad) {
        PacketManager.serverUDPAll(new Packets.SyncSoundSingle(soundLoad.getSound(), soundLoad.getPosition(), soundLoad.getVolume(), soundLoad.getPitch(), soundLoad.isSingleton()));
    }
}
