package com.mygdx.hadal.event;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.PlayState.ObjectLayer;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * This event heals all schmucks inside of it. It is usually created temporarily from the effects 
 * of attacks
 * <p>
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * heal: float heal per 1/60f done by this event
 * filter: short of who this event affects. Default: 0 (all units).
 * 
 * @author Zospus Ziwick
 */
public class HealingArea extends Event {

	private static final float PARTICLE_LIFESPAN = 1.5f;

	private float controllerCount;
	
	private final float heal;
	private final short filter;

	//If created by an dude, this is that dude
	private final Schmuck perp;
	
	//timers manage the rate of particle spawn
	private float currCrossSpawnTimer;
	private final float spawnTimerLimit;

	/**
	 * This constructor is used for when this event is created temporarily.
	 */
	public HealingArea(PlayState state, Vector2 startPos, Vector2 size, float heal, short filter) {
		super(state, startPos, size);
		this.heal = heal;
		this.filter = filter;
		this.perp = state.getWorldDummy();
		spawnTimerLimit = 4096f / (size.x * size.y);

		setBotHealthPickup(true);
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

		setIndependent(true);
		setBotHealthPickup(true);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR,
				(short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY), filter)
				.setBodyType(BodyDef.BodyType.StaticBody)
				.addToWorld(world);
	}
	
	private final Vector2 entityLocation = new Vector2();
	private final Vector2 randLocation = new Vector2();
	@Override
	public void controller(float delta) {
		super.controller(delta);

		controllerCount += delta;
		while (controllerCount >= Constants.INTERVAL) {
			controllerCount -= Constants.INTERVAL;
			
			for (HadalEntity entity : eventData.getSchmucks()) {
				if (entity instanceof Schmuck schmuck) {
					schmuck.getBodyData().regainHp(heal * schmuck.getBodyData().getStat(Stats.MAX_HP) / 100.0f, perp.getBodyData(),
							true, DamageTag.REGEN);
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
			ParticleEntity heal = new ParticleEntity(state, randLocation.set(randX, randY), Particle.REGEN, PARTICLE_LIFESPAN,
					true, SyncType.NOSYNC);

			if (!state.isServer()) {
				((PlayStateClient) state).addEntity(heal.getEntityID(), heal, false, ObjectLayer.EFFECT);
			}
		}
	}
	
	/**
	 * Client healing area should randomly spawn regen particles itself to reduce overhead.
	 */
	@Override
	public void clientController(float delta) {
		controller(delta);
	}
}
