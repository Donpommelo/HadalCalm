package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates a sound when the attached hbox dies
 * @author Ghuvarius Greatrice
 *
 */
public class DieSound extends HitboxStrategy {
	
	//this is the sound effect that gets played
	private final SoundEffect sound;
	
	//this is the volume that the sound will get played at.
	private final float volume;
	
	//this is the pitch that the sound will get played at. (default is no change. change using factory method.)
	private float pitch = 1.0f;

	//Does the server notify the client of this sound?
	private boolean synced = true;

	//if true, this will not play if the hbox dies by timing out
	private boolean ignoreOnTimeout;

	public DieSound(PlayState state, Hitbox proj, BodyData user, SoundEffect sound, float volume) {
		super(state, proj, user);
		this.sound = sound;
		this.volume = volume;
	}
	
	@Override
	public void die() {

		if (ignoreOnTimeout && hbox.getLifeSpan() <= 0.0f) { return; }

		if (synced) {
			sound.playUniversal(state, hbox.getPixelPosition(), volume, pitch, false);
		} else {
			sound.playSourced(state, hbox.getPixelPosition(), volume, pitch);
		}
	}
	
	public DieSound setPitch(float pitch) {
		this.pitch = pitch;
		return this;
	}

	public DieSound setSynced(boolean synced) {
		this.synced = synced;
		return this;
	}

	public DieSound setIgnoreOnTimeout(boolean ignoreOnTimeout) {
		this.ignoreOnTimeout = ignoreOnTimeout;
		return this;
	}
}
