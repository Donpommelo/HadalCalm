package com.mygdx.hadal.schmucks.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

import java.util.UUID;

/**
 * A SoundEntity is like a ParticleEntity except for Sound. It attaches to another entity and plays sound from that entity's location
 * It also helps sync the sound between server and client.
 * @author Scelazar Scampbell
 */
public class SoundEntity extends HadalEntity {

	//This is the sound effect that will be played
	private final SoundEffect sound;
	
	//this is the sound id of the instance of the sound
	private final long soundId;

	private float lifespan;
	private final boolean temp;

	//this is the rate at which the sound volume changes (default: 0, -x for fading out and +x for fading in)
	private float fade;

	//the volume of the sound and the max volume the sound will fade in to.
	private float volume;
	private final float maxVolume;
	
	//the pitch of the sound
	private final float pitch;
	
	//should the sound loop after playing? Should the sound start off playing?
	private final boolean looped;
	private boolean on;
	
	//Is this entity following another entity? If so, what is the entity's id (used by client)
	private HadalEntity attachedEntity;
	private UUID attachedId;
	
	//how is this entity synced? (this works identically to particle entities)
	private final SyncType sync;
	
	//Has the attached entity despawned yet?
	private boolean despawn;
	
	//default values for sound fading
	private static final float defaultFadeInSpeed = 2.0f;
	private static final float defaultFadeOutSpeed = -2.0f;
	
	public SoundEntity(PlayState state, HadalEntity entity, SoundEffect sound, float lifespan, float volume, float pitch,
					   boolean looped, boolean startOn, SyncType sync) {
		super(state, new Vector2(), new Vector2());
		this.attachedEntity = entity;
		this.sound = sound;
		this.maxVolume = volume;
		this.volume = volume;
		this.pitch = pitch;
		this.looped = looped;
		this.on = startOn;
		this.sync = sync;

		temp = lifespan != 0;
		this.lifespan = lifespan;

		//if we start off attached to an entity, play the sound and update its volume/pan based on its location
		if (startOn && attachedEntity != null) {
			this.soundId = sound.playSourced(state, new Vector2(attachedEntity.getPixelPosition().x, attachedEntity.getPixelPosition().y), volume, pitch);
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
	private static final float syncTime = 0.01f;
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
					if (state.isServer()) {
						this.queueDeletion();
					} else {
						((ClientState) state).removeEntity(entityID);
					}
				}
			}
			if (volume >= maxVolume) {
				volume = maxVolume;
				fade = 0.0f;
			}
		}

		if (temp) {
			lifespan -= delta;
			if (lifespan <= 0) {
				sound.getSound().pause(soundId);
				if (state.isServer()) {
					this.queueDeletion();
				} else {
					((ClientState) state).removeEntity(entityID);
				}
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
	 * Client Noise Entities will run normally if set to Create or No Sync
	 * If attached to an entity that hasn't been sent over yet, wait until it exists and then attach and resume sound
	 */
	@Override
	public void clientController(float delta) {
		super.clientController(delta);
		
		if (sync.equals(SyncType.CREATESYNC) || sync.equals(SyncType.NOSYNC)) {
			controller(delta);			
		}
		if (attachedEntity == null && attachedId != null) {
			attachedEntity = state.findEntity(attachedId);
			if (on) {
				sound.getSound().resume(soundId);
			}
		}
	}
	
	/**
	 * When created on the server, tell clients to create if create or tick sync.
	 */
	@Override
	public Object onServerCreate(boolean catchup) {
		if (sync.equals(SyncType.CREATESYNC) || sync.equals(SyncType.TICKSYNC)) {
			if (attachedEntity != null) {
				return new Packets.CreateSound(entityID, attachedEntity.getEntityID(), sound, lifespan, volume, pitch, looped, on, sync.equals(SyncType.TICKSYNC));
			}
		}
		return null;
	}
	
	@Override
	public Object onServerDelete() { 
		if (sync.equals(SyncType.TICKSYNC)) {
			return new Packets.DeleteEntity(entityID, state.getTimer());
		} else {
			return null;
		}
	}

	/**
	 * For sounds that are tick synced, send over volume to clients as well as whether it is on or not
	 */
	@Override
	public void onServerSync() {
		if (sync.equals(SyncType.TICKSYNC)) {
			if (attachedEntity != null) {
				if (attachedEntity.getBody() != null) {
					state.getSyncPackets().add(new Packets.SyncSound(entityID, volume, on, entityAge, state.getTimer()));
				}
			}
		}
	}
	
	/**
	 * For Client sound entities, sync position, volume and on if the server sends over the packets (if Tick synced)
	 */
	@Override
	public void onClientSync(Object o) {
		if (o instanceof Packets.SyncSound p) {
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

	public void setAttachedId(UUID attachedId) { this.attachedId = attachedId; }
}