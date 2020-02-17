package com.mygdx.hadal.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.hadal.managers.GameStateManager;

public enum SoundEffect {

	UISWITCH1("sound/switch5.wav"),
	UISWITCH2("sound/switch7.wav"),
	UISWITCH3("sound/switch10.wav"),
	;
	
	private String soundId;
	private Sound sound;
	
	SoundEffect(String soundId) {
		this.soundId = soundId;
	}
	
	public Sound getSound() {
		
		if (sound == null) {
			sound = Gdx.audio.newSound(Gdx.files.internal(soundId));
		}
		return sound;
	}
	
	public long play(GameStateManager gsm) {
		return getSound().play(gsm.getSetting().getSoundVolume() * gsm.getSetting().getMasterVolume());
	}
}
