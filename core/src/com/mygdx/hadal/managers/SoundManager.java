package com.mygdx.hadal.managers;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.managers.loaders.SoundLoader;
import com.mygdx.hadal.requests.SoundLoad;
import com.mygdx.hadal.states.PlayState;

/**
 * SoundManager loads sounds.
 * Logic is delegated to Loader to make it easier for headless server to have different logic
 */
public class SoundManager {

    private static SoundLoader loader;

    public static void initLoader(SoundLoader loader) {
        SoundManager.loader = loader;
    }

    public static long play(SoundEffect soundEffect) {
        return SoundManager.loader.play(null, new SoundLoad(soundEffect));
    }

    public static long play(PlayState state, SoundEffect soundEffect) {
        return SoundManager.loader.play(state, new SoundLoad(soundEffect));
    }

    public static long play(SoundLoad soundLoad) {
        return SoundManager.loader.play(null, soundLoad);
    }

    public static long play(PlayState state, SoundLoad soundLoad) {
        return SoundManager.loader.play(state, soundLoad);
    }

    public static void playUniversal(PlayState state, SoundLoad soundLoad) {
        SoundManager.loader.playUniversal(state, soundLoad);
    }

    public static void updateSoundLocation(PlayState state, SoundEffect sound, Vector2 worldPos, float volume, long soundId) {
        SoundManager.loader.updateSoundLocation(state, sound, worldPos, volume, soundId);
    }
}