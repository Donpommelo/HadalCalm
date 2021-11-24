package com.mygdx.hadal.event;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.server.EventDto;
import com.mygdx.hadal.server.packets.Packets;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.ClientState.ObjectSyncLayers;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * This event heals all schmucks inside of it. It is usually created temporarily from the effects 
 * of attacks
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * 
 * Fields:
 * heal: float heal per 1/60f done by this event
 * filter: short of who this event affects. Default: 0 (all units).
 * 
 * @author Zospus Ziwick
 */
public class HealingArea extends Event {
	
	private float controllerCount;
	
	private final float heal;
	private final short filter;

	//If created by an dude, this is that dude
	private final Schmuck perp;
	
	//timers manage the rate of particle spawn
	private float currCrossSpawnTimer;
	private final float spawnTimerLimit;

	//the frequency that the heal is applied
	private float particleLifespan = 1.5f;
	private static final float healInterval = 1 / 60f;
	
	/**
	 * This constructor is used for when this event is created temporarily.
	 */
	public HealingArea(PlayState state, Vector2 startPos, Vector2 size, float heal, short filter) {
		super(state, startPos, size);
		this.heal = heal;
		this.filter = filter;
		this.perp = state.getWorldDummy();
		spawnTimerLimit = 4096f / (size.x * size.y);
	}
	
	/**
	 * This constructor is used for when this event is created temporarily.
	 */
	public HealingArea(PlayState state, Vector2 startPos, Vector2 size, float heal, float duration, Schmuck perp, short filter) {
		super(state,  startPos, size, duration);
		this.heal = heal;
		this.filter = filter;
		this.perp = perp;
		spawnTimerLimit = 4096f / (size.x * size.y);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 0, 0, false, false,
				Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY), filter, true, eventData);
	}
	
	private final Vector2 entityLocation = new Vector2();
	private final Vector2 randLocation = new Vector2();
	@Override
	public void controller(float delta) {
		super.controller(delta);

		controllerCount += delta;
		while (controllerCount >= healInterval) {
			controllerCount -= healInterval;
			
			for (HadalEntity entity : eventData.getSchmucks()) {
				if (entity instanceof Schmuck schmuck) {
					schmuck.getBodyData().regainHp(heal, perp.getBodyData(), true, DamageTypes.REGEN);
				}
			}
		}
		
		entityLocation.set(getPixelPosition());
		
		//spawn particles periodically
		currCrossSpawnTimer += delta;
		while (currCrossSpawnTimer >= spawnTimerLimit) {
			currCrossSpawnTimer -= spawnTimerLimit;
			int randX = (int) ((MathUtils.random() * size.x) - (size.x / 2) + entityLocation.x);
			int randY = (int) ((MathUtils.random() * size.y) - (size.y / 2) + entityLocation.y);
			new ParticleEntity(state, randLocation.set(randX, randY), Particle.REGEN, particleLifespan, true, particleSyncType.NOSYNC);
		}
	}
	
	/**
	 * Client healing area should randomly spawn regen particles itself to reduce overhead.
	 */
	@Override
	public void clientController(float delta) {
		super.controller(delta);

		entityLocation.set(getPixelPosition());
		
		//spawn particles periodically
		currCrossSpawnTimer += delta;
		while (currCrossSpawnTimer >= spawnTimerLimit) {
			currCrossSpawnTimer -= spawnTimerLimit;
			int randX = (int) ((MathUtils.random() * size.x) - (size.x / 2) + entityLocation.x);
			int randY = (int) ((MathUtils.random() * size.y) - (size.y / 2) + entityLocation.y);
			ParticleEntity heal = new ParticleEntity(state, randLocation.set(randX, randY), Particle.REGEN, 1.5f, true, particleSyncType.NOSYNC);
			((ClientState) state).addEntity(heal.getEntityID(), heal, false, ObjectSyncLayers.EFFECT);
		}
	}
	
	/**
	 * When server creates healing area, clients are told to create the healing area in their own worlds
	 */
	@Override
	public Object onServerCreate() {
		if (independent) { return null; }
		if (blueprint == null) {
			entityLocation.set(getPixelPosition());
			
			blueprint = new RectangleMapObject(entityLocation.x - size.x / 2, entityLocation.y - size.y / 2, size.x, size.y);
			blueprint.setName("HealTemp");
			blueprint.getProperties().put("duration", duration);
		}
		return new Packets.CreateEvent(entityID, new EventDto(blueprint), synced);
	}
	
	@Override
	public void loadDefaultProperties() {
		setSynced(true);
	}
}
