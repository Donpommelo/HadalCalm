package com.mygdx.hadal.managers.loaders;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.JSONManager;
import com.mygdx.hadal.managers.PacketManager;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.PlayState;

public class SoundLoader {

    public long play(PlayState state, SoundLoad soundLoad) {
        if (soundLoad.isSingleton()) {
            soundLoad.getSound().loadSound().stop();
        }
        float volume = soundLoad.getVolume();
        if (!soundLoad.isNoModifiers()) {
            volume = volume * JSONManager.setting.getSoundVolume() * JSONManager.setting.getMasterVolume();
        }

        long soundID = soundLoad.getSound().loadSound().play(volume, soundLoad.getPitch(), 0.0f);

        if (state != null && !soundLoad.getPosition().isZero()) {
            updateSoundLocation(state, soundLoad.getSound(), soundLoad.getPosition(), soundLoad.getVolume(), soundID);
        }

        return soundID;
    }

    public void playUniversal(PlayState state, SoundLoad soundLoad) {
        //Send a packet to the client and play the sound
        if (state.isServer()) {
            PacketManager.serverUDPAll(new Packets.SyncSoundSingle(soundLoad.getSound(), soundLoad.getPosition(), soundLoad.getVolume(), soundLoad.getPitch(), soundLoad.isSingleton()));
        }

        play(state, soundLoad);
    }

    //maxDist is the largest distance the player can hear sounds from.
    //Further sounds will be quieter.
    private static final float MAX_DIST = 3500.0f;
    /**
     * updateSoundLocation updates the volume and pan of single instance of a sound.
     * This is done based on the sound's location relative to the player.
     * This is used for sounds attached to entities.
     */
    private final Vector2 playerPosition = new Vector2();
    public void updateSoundLocation(PlayState state, SoundEffect sound, Vector2 worldPos, float volume, long soundId) {
        Player player = HadalGame.usm.getOwnPlayer();

        //this avoids playing sounds at default volume if player has not loaded in yet
        if (null == player) {
            sound.loadSound().setPan(soundId, 0.0f, volume * JSONManager.setting.getSoundVolume() * JSONManager.setting.getMasterVolume());
            return;
        }

        //check if player exists and is alive (to avoid sudden sound change on death)
        if (null != player.getBody() && player.isAlive()) {
            playerPosition.set(player.getPixelPosition());
        }

        //as a spectator, the center of the camera is treated as the player location
        if (state.getSpectatorManager().isSpectatorMode()) {
            playerPosition.set(state.getCamera().position.x, state.getCamera().position.y);
        }

        float xDist = worldPos.x - playerPosition.x;
        float yDist = worldPos.y - playerPosition.y;
        float dist = Math.abs(xDist) + Math.abs(yDist);

        float pan;
        float newVolume;

        //sound will be played from right/left headphone depending on relative x-coordinate
        if (MAX_DIST < xDist) {
            pan = 1.0f;
        } else if (-MAX_DIST > xDist) {
            pan = -1.0f;
        } else {
            pan = xDist / MAX_DIST;
        }

        //sound volume scales inversely to distance from sound
        if (MAX_DIST < dist) {
            newVolume = 0.0f;
        } else if (0 >= dist) {
            newVolume = 1.0f;
        } else {
            newVolume = (MAX_DIST - dist) / MAX_DIST;
        }

        sound.loadSound().setPan(soundId, pan, newVolume * volume * JSONManager.setting.getSoundVolume() * JSONManager.setting.getMasterVolume());
    }
}
