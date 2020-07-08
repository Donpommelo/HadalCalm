package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates an attached sound when the attached hbox is created
 * @author Zachary Tu
 */
public class CreateSound extends HitboxStrategy {
	
	//this is the sound entity that plays the sound and is attached to the hbox
	private SoundEntity sound;
	
	//this is the sound effect that will be played
	private SoundEffect effect;
	
	//this is the volume the sound will be played at
	private float volume;
	
	//does the sound effect loop or not?
	private boolean looped;
	
	public CreateSound(PlayState state, Hitbox proj, BodyData user, SoundEffect effect, float volume, boolean looped) {
		super(state, proj, user);
		this.effect = effect;
		this.volume = volume;
		this.looped = looped;
	}
	
	@Override
	public void create() {
		sound = new SoundEntity(state, creator.getSchmuck(), effect, volume, looped, true, soundSyncType.TICKSYNC);
	}
	
	@Override
	public void die() {
		sound.terminate();
	}
}
