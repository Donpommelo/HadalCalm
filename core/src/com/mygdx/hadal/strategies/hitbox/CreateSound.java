package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.SoundEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy creates an attached sound when the attached hbox is created
 * @author Smelgslinger Slarpaccio
 */
public class CreateSound extends HitboxStrategy {

	//this is the sound effect that will be played
	private final SoundEffect effect;
	
	//this is the volume the sound will be played at
	private final float volume;
	
	//this is the pitch that the sound will get played at. (default is no change. change using factory method.)
	private float pitch = 1.0f;

	private SyncType syncType = SyncType.CREATESYNC;

	//does the sound effect loop or not?
	private final boolean looped;
	
	public CreateSound(PlayState state, Hitbox proj, BodyData user, SoundEffect effect, float volume, boolean looped) {
		super(state, proj, user);
		this.effect = effect;
		this.volume = volume;
		this.looped = looped;
	}
	
	@Override
	public void create() {
		//this is the sound entity that plays the sound and is attached to the hbox
		SoundEntity sound = new SoundEntity(state, hbox, effect, 0.0f, volume, pitch, looped, true, syncType);
		if (!state.isServer()) {
			((ClientState) state).addEntity(sound.getEntityID(), sound, false, ObjectLayer.EFFECT);
		}
	}
	
	public CreateSound setPitch(float pitch) {
		this.pitch = pitch;
		return this;
	}

	public CreateSound setSyncType(SyncType syncType) {
		this.syncType = syncType;
		return this;
	}
}
