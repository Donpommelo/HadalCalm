package com.mygdx.hadal.event;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * @author Zachary Tu
 *
 */
public class HealingArea extends Event {
	
	private float controllerCount = 0;
	
	//Damage done by the poison
	private float heal;
	
	//If created by an dude, this is that dude
	private Schmuck perp;
	
	private float currPoisonSpawnTimer = 0f, spawnTimerLimit;
	private short filter;
	
	private final static float damageInterval = 1 / 60f;
	
	/**
	 * This constructor is used for when this event is created temporarily.
	 */
	public HealingArea(PlayState state, Vector2 startPos, Vector2 size, float heal, short filter) {
		super(state, startPos, size);
		this.heal = heal;
		this.filter = filter;
		this.perp = state.getWorldDummy();
		spawnTimerLimit = 4096f/(size.x * size.y);
	}
	
	/**
	 * This constructor is used for when this event is created temporarily.
	 */
	public HealingArea(PlayState state, Vector2 startPos, Vector2 size, float heal, float duration, Schmuck perp, short filter) {
		super(state,  startPos, size, duration);
		this.heal = heal;
		this.filter = filter;
		this.perp = perp;
		spawnTimerLimit = 4096f/(size.x * size.y);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 0, 0, false, false, Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY), filter, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
			controllerCount += delta;
			while (controllerCount >= damageInterval) {
				controllerCount -= damageInterval;
				
				for (HadalEntity entity : eventData.getSchmucks()) {
					if (entity instanceof Schmuck) {
						((Schmuck)entity).getBodyData().receiveDamage(-heal, new Vector2(), perp.getBodyData(), true, DamageTypes.REGEN);
					}
				}
			}
			
			currPoisonSpawnTimer += delta;
			while (currPoisonSpawnTimer >= spawnTimerLimit) {
				currPoisonSpawnTimer -= spawnTimerLimit;
				int randX = (int) ((Math.random() * size.x) - (size.x / 2) + getPixelPosition().x);
				int randY = (int) ((Math.random() * size.y) - (size.y / 2) + getPixelPosition().y);
				new ParticleEntity(state, new Vector2(randX, randY), Particle.REGEN, 1.5f, true, particleSyncType.NOSYNC);
			}
		super.controller(delta);
	}
	
	/**
	 * Client Poison should randomly spawn poison particles itself to avoid overhead.
	 */
	@Override
	public void clientController(float delta) {
		currPoisonSpawnTimer += delta;
		while (currPoisonSpawnTimer >= spawnTimerLimit) {
			currPoisonSpawnTimer -= spawnTimerLimit;
			int randX = (int) ((Math.random() * size.x) - (size.x / 2) + getPixelPosition().x);
			int randY = (int) ((Math.random() * size.y) - (size.y / 2) + getPixelPosition().y);
			ParticleEntity poison = new ParticleEntity(state, new Vector2(randX, randY), Particle.REGEN, 1.5f, true, particleSyncType.NOSYNC);
			((ClientState)state).addEntity(poison.getEntityID().toString(), poison, ObjectSyncLayers.STANDARD);
		}
	}
	
	/**
	 * When server creates poison, clients are told to create the poison in their own worlds
	 */
	@Override
	public Object onServerCreate() {
		if (blueprint == null) {
			blueprint = new RectangleMapObject(getPixelPosition().x - size.x / 2, getPixelPosition().y - size.y / 2, size.x, size.y);
			blueprint.setName("Heal");
			return new Packets.CreateEvent(entityID.toString(), blueprint);
		} else {
			return new Packets.CreateEvent(entityID.toString(), blueprint);
		}
	}
}
