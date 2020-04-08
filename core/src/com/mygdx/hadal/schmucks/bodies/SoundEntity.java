package com.mygdx.hadal.schmucks.bodies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;

public class SoundEntity extends HadalEntity {

	private SoundEffect sound;
	private long soundId;
	private float fade;

	private float volume;
	
	private boolean looped, on;
	
	//Is this entity following another entity?
	private HadalEntity attachedEntity;
	private String attachedId;
	
	private soundSyncType sync;
	
	//Has the attached entity despawned yet?
	private boolean despawn;
	
	private static final float defaultFadeInSpeed = 2.0f;
	private static final float defaultFadeOutSpeed = -1.0f;
	
	public SoundEntity(PlayState state, HadalEntity entity, SoundEffect sound, float volume, boolean looped, boolean startOn, soundSyncType sync) {
		super(state, new Vector2(), new Vector2());
		this.attachedEntity = entity;
		this.sound = sound;
		this.volume = volume;
		this.looped = looped;
		this.on = startOn;
		this.sync = sync;
		
		if (startOn && attachedEntity != null) {
			this.soundId = sound.playSourced(state, new Vector2(attachedEntity.getPixelPosition().x, attachedEntity.getPixelPosition().y), volume);
			sound.updateSoundLocation(state, attachedEntity.getPixelPosition(), volume, soundId);
		} else {
			this.soundId = sound.play(state.getGsm(), volume);
			sound.getSound().pause(soundId);
		}
		sound.getSound().setLooping(soundId, looped);
	}

	@Override
	public void create() {}

	private float syncAccumulator = 0.0f;
	private final static float syncTime = 0.1f;
	@Override
	public void controller(float delta) {
		
		syncAccumulator += delta;
		
		if (syncAccumulator >= syncTime) {
			syncAccumulator = 0;
			
			//If attached to a living unit, this entity tracks its movement. If attached to a unit that has died, we despawn.
			if (attachedEntity != null && !despawn) {
				if (attachedEntity.isAlive() && attachedEntity.getBody() != null) {
					sound.updateSoundLocation(state, attachedEntity.getPixelPosition(), volume, soundId);
				} else {
					turnOff();
					despawn = true;
				}
			}
		}

		if (fade != 0) {
			volume += delta * fade;
			if (volume <= 0.0f) {
				volume = 0.0f;
				sound.getSound().pause(soundId);
				fade = 0.0f;
				on = false;
				
				if (despawn) {
					this.queueDeletion();
				}
			}
			if (volume >= 1.0f) {
				volume = 1.0f;
				fade = 0.0f;
			}
		}
	}

	@Override
	public void clientController(float delta) {
		
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
	
	@Override
	public Object onServerCreate() {
		
		if (sync.equals(soundSyncType.CREATESYNC) || sync.equals(soundSyncType.TICKSYNC)) {
			if (attachedEntity != null) {
				return new Packets.CreateSound(entityID.toString(), attachedEntity.getEntityID().toString(), sound.toString(), volume, looped, on);
			}
		}
		return null;
	}
	
	private Vector2 newPos = new Vector2();
	@Override
	public void onServerSync() {
		if (sync.equals(soundSyncType.TICKSYNC)) {
			if (attachedEntity != null) {
				if (attachedEntity.getBody() != null) {
					newPos.set(attachedEntity.getPixelPosition().x, attachedEntity.getPixelPosition().y);
					HadalGame.server.sendToAllUDP(new Packets.SyncSound(entityID.toString(), newPos, volume, on));
				}
			}
		}
	}
	
	@Override
	public void onClientSync(Object o) {
		Packets.SyncSound p = (Packets.SyncSound) o;
		volume = p.volume;
		if (p.on) {
			turnOn();
		}
		if (!p.on) {
			turnOff();
		}
	}
	
	@Override
	public void render(SpriteBatch batch) {}
	
	public void turnOn() {
		on = true;
		fade = defaultFadeInSpeed;
		sound.getSound().resume(soundId);
	}
	
	public void turnOff() {
		fade = defaultFadeOutSpeed;
		on = false;
	}
	
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
