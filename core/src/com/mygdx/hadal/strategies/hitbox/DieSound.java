package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates an explosion when the attached hbox dies
 * @author Zachary Tu
 *
 */
public class DieSound extends HitboxStrategy {
	
	private SoundEffect sound;
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
