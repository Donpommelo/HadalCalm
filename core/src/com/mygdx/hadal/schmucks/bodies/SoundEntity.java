package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

/**
 * A SoundEntity is like a ParticleEntity except for Sound. It attaches to another entity and plays sound fro mthat entity's location
 * It also helps sync the sound between server and client.
 * @author Zachary Tu
 */
public class SoundEntity extends HadalEntity {

	//This is the sound effect that will be played
	private SoundEffect sound;
	
	//this is the sound id of the instance of the sound
	private long soundId;
	
	//this is the rate at which the sound volume changes (default: 0, -x for fading out and +x for fading in)
	private float fade;

	//the volume of the sound and the max volume the sound will fade in to.
	private float volume, maxVolume;
	
	//the pitch of the sound;
	private float pitch;
	
	//should the sound loop after playing? Should the sound start off playing?
	private boolean looped, on;
	
	//Is this entity following another entity? If so, what is the entity's id (used by client)
	private HadalEntity attachedEntity;
	private String attachedId;
	
	//how is this entity synced? (this works identically to particle entities
	private soundSyncType sync;
	
	//Has the attached entity despawned yet?
	private boolean despawn;
	
	//default values for sound fading
	private static final float defaultFadeInSpeed = 2.0f;
	private static final float defaultFadeOutSpeed = -2.0f;
	
	public SoundEntity(PlayState state, HadalEntity entity, SoundEffect sound, float volume, float pitch, boolean looped, boolean startOn, soundSyncType sync) {
		super(state, new Vector2(), new Vector2());
		this.attachedEntity = entity;
		this.sound = sound;
		this.maxVolume = volume;
		this.volume = volume;
		this.pitch = pitch;
		this.looped = looped;
		this.on = startOn;
		this.sync = sync;
		
		//if we start off attached to an entity, play the sound and update its volume/pan based on its location
		if (startOn && attachedEntity != null) {
			this.soundId = sound.playSourced(state, new Vector2(attachedEntity.getPixelPosition().x, attachedEntity.getPixelPosition().y), volume, pitch, false);
			sound.updateSoundLocation(state, attachedEntity.getPixelPosition(), volume, soundId);
		} else {
			//otherwise, we just get the sound id and pause it.
			this.soundId = sound.play(state.getGsm(), volume, pitch, false);
			sound.getSound().pause(soundId);
		}
		
		//set the looping of the sound
		sound.getSound().setLooping(soundId, looped);
	}

	@Override
	public void create() {}

	//This is the rate that the sound will sync its volume/pan based on its moving position.
	//No need to update every tick.
	private float syncAccumulator = 0.0f;
	private final static float syncTime = 0.01f;
	@Override
	public void controller(float delta) {
		
		//process sound fading. Gradually change sound volume until it reaches 0.0 or max volume.
		if (fade != 0) {
			volume += delta * fade;
			
			//when a sound finishes fading out, pause it and delete it, if it is set to despawn
			if (volume <= 0.0f) {
				volume = 0.0f;
				sound.getSound().pause(soundId);
				fade = 0.0f;
				on = false;
				
				if (despawn) {
					this.queueDeletion();
				}
			}
			if (volume >= maxVolume) {
				volume = maxVolume;
				fade = 0.0f;
			}
		}
		
		syncAccumulator += delta;
		
		if (syncAccumulator >= syncTime) {
			syncAccumulator = 0;
			
			//If attached to a living unit, this entity tracks its movement. If attached to a unit that has died, we despawn.
			if (attachedEntity != null) {
				if (attachedEntity.isAlive() && attachedEntity.getBody() != null) {
					sound.updateSoundLocation(state, attachedEntity.getPixelPosition(), volume, soundId);
				} else {
					turnOff();
					despawn = true;
				}
			}
		}
	}

	/**
	 * Client NoiseEntites will run normally if set to Create or No Sync
	 * If attached to an entity that hasn't been sent over yet, wait until it exists and then attach and resume sound
	 */
	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		
		if (sync.equals(soundSyncType.CREATESYNC) || sync.equals(soundSyncType.NOSYNC)) {
			controller(delta);			
		}
		if (attachedEntity == null && attachedId != null) {
			attachedEntity = ((ClientState) state).findEntity(attachedId);
			if (on) {
				sound.getSound().resume(soundId);
			}
		}
	}
	
	/**
	 * When created on the server, tell clients to create if create or tick sync.
	 */
	@Override
	public Object onServerCreate() {
		if (sync.equals(soundSyncType.CREATESYNC) || sync.equals(soundSyncType.TICKSYNC)) {
			if (attachedEntity != null) {
				return new Packets.CreateSound(entityID.toString(), attachedEntity.getEntityID().toString(), sound.toString(), volume, pitch, looped, on, sync.equals(soundSyncType.TICKSYNC));
			}
		}
		return null;
	}
	
	@Override
	public Object onServerDelete() { 
		if (sync.equals(soundSyncType.TICKSYNC)) {
			return new Packets.DeleteEntity(entityID.toString(), state.getTimer()); 
		} else {
			return null;
		}
	}

	/**
	 * For sounds that are tick synced, send over location and volume to clients as well as whether it is on or not
	 */
	private Vector2 newPos = new Vector2();
	@Override
	public void onServerSync() {
		if (sync.equals(soundSyncType.TICKSYNC)) {
			if (attachedEntity != null) {
				if (attachedEntity.getBody() != null) {
					newPos.set(attachedEntity.getPixelPosition().x, attachedEntity.getPixelPosition().y);
					state.getSyncPackets().add(new Packets.SyncSound(entityID.toString(), newPos, volume, on, entityAge, state.getTimer()));
				}
			}
		}
	}
	
	/**
	 * For Client sound entities, sync position, volume and on if the server sends over the packets (if Tick synced)
	 */
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncSound) {
			Packets.SyncSound p = (Packets.SyncSound) o;
			volume = p.volume;
			if (p.on) {
				turnOn();
			}
			if (!p.on) {
				turnOff();
			}
		} else {
			super.onClientSync(o);
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {}
	
	@Override
	public void dispose() {
		if (!destroyed) {
			sound.getSound().stop(soundId);
		}
		super.dispose();
	}
	
	public void turnOn() {
		on = true;
		fade = defaultFadeInSpeed;
		sound.getSound().resume(soundId);
	}
	
	public void turnOff() {
		fade = defaultFadeOutSpeed;
		on = false;
	}
	
	/**
	 * This turns a sound off and sets it to despawn after fading
	 */
	public void terminate() {
		fade = defaultFadeOutSpeed;
		on = false;
		despawn = true;
	}
	
	public void setAttachedEntity(HadalEntity attachedEntity) { this.attachedEntity = attachedEntity; }

	public void setAttachedId(String attachedId) { this.attachedId = attachedId; }

	public enum soundSyncType {
		NOSYNC,
		CREATESYNC,
		TICKSYNC
	}
}
