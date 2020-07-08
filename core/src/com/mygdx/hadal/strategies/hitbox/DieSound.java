package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates a sound when the attached hbox dies
 * @author Zachary Tu
 *
 */
public class DieSound extends HitboxStrategy {
	
	//this is the sound effect that gets played
	private SoundEffect sound;
	
	//this is the volume that the sound will get played at.
	private float volume;
	
	public DieSound(PlayState state, Hitbox proj, BodyData user, SoundEffect sound, float volume) {
		super(state, proj, user);
		this.sound = sound;
		this.volume = volume;
	}
	
	@Override
	public void die() {
		sound.playUniversal(state, hbox.getPixelPosition(), volume, false);
	}
}
