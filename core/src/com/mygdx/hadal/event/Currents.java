package com.mygdx.hadal.event;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.constants.Constants;
import com.mygdx.hadal.constants.ObjectLayer;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.entities.HadalEntity;
import com.mygdx.hadal.schmucks.entities.Ragdoll;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * Currents are an event that apply a continuous force to all schmucks inside of it.
 * <p>
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * <p>
 * Fields:
 * N/A
 * 
 * @author Frularbus Fortrand
 */
public class Currents extends Event {

	private static final Vector2 RAGDOLL_SIZE = new Vector2(48, 48);

	private final Vector2 vec = new Vector2();

	//This keeps track of engine timer.
	private float controllerCount;
	
	//these keep track of spawned particle dummies inside the current
	private float currBubbleSpawnTimer;
	private final float spawnTimerLimit;
	
	public Currents(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec) {
		super(state, startPos, size);
		this.vec.set(vec);
		spawnTimerLimit = 5120 / (size.x * size.y);
	}
	
	public Currents(PlayState state, Vector2 startPos, Vector2 size, Vector2 vec, float duration) {
		super(state, startPos, size, duration);
		this.vec.set(vec);
		spawnTimerLimit = 2560 / (size.x * size.y);

		setIndependent(true);
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR,
				(short) (BodyConstants.BIT_PLAYER | BodyConstants.BIT_ENEMY | BodyConstants.BIT_PROJECTILE | BodyConstants.BIT_SENSOR),	(short) 0)
				.setBodyType(BodyDef.BodyType.StaticBody)
				.addToWorld(world);
	}
	
	private final Vector2 entityLocation = new Vector2();
	private final Vector2 randLocation = new Vector2();
	private final Vector2 ragdollVelo = new Vector2();
	@Override
	public void controller(float delta) {
		super.controller(delta);
		
		controllerCount += delta;
		while (controllerCount >=  Constants.INTERVAL) {
			controllerCount -=  Constants.INTERVAL;
			
			//push is done through damage so that +knockback resistance will reduce the push.
			for (HadalEntity entity : eventData.getSchmucks()) {
				entity.getHadalData().receiveDamage(0.0f, new Vector2(vec), state.getWorldDummy().getBodyData(),
						false,null, DamageSource.CURRENTS, DamageTag.DEFLECT);
			}
		}
		
		entityLocation.set(getPixelPosition());
		
		//spawn a dummy with a particle attached. Dummies are moved by current to give visual effect.
		currBubbleSpawnTimer += delta;
		while (currBubbleSpawnTimer >= spawnTimerLimit) {
			currBubbleSpawnTimer -= spawnTimerLimit;
			int randX = (int) ((MathUtils.random() * size.x) - (size.x / 2) + entityLocation.x);
			int randY = (int) ((MathUtils.random() * size.y) - (size.y / 2) + entityLocation.y);
			EffectEntityManager.getParticle(state, new ParticleCreate(Particle.CURRENT_TRAIL,
					new Ragdoll(state, randLocation.set(randX, randY), RAGDOLL_SIZE, Sprite.NOTHING,
							ragdollVelo, 0.25f, 0.0f, false, true, false))
					.setVelocity(vec.angleRad())
					.setAngle(vec.angleRad() + 90 * MathUtils.degreesToRadians));
		}
	}
	
	@Override
	public void clientController(float delta) {
		super.controller(delta);
		
		controllerCount += delta;
		while (controllerCount >=  Constants.INTERVAL) {
			controllerCount -=  Constants.INTERVAL;
			
			//push is done through damage so that +knockback resistance will reduce the push.
			for (HadalEntity entity : eventData.getSchmucks()) {
				entity.getHadalData().receiveDamage(0.0f, randLocation.set(vec), state.getWorldDummy().getBodyData(),
						false, null, DamageSource.CURRENTS, DamageTag.DEFLECT);
			}
		}
		
		entityLocation.set(getPixelPosition());
		
		//spawn a dummy with a particle attached. Dummies are moved by current to give visual effect.
		currBubbleSpawnTimer += delta;
		while (currBubbleSpawnTimer >= spawnTimerLimit) {
			currBubbleSpawnTimer -= spawnTimerLimit;
			int randX = (int) ((MathUtils.random() * size.x) - (size.x / 2) + entityLocation.x);
			int randY = (int) ((MathUtils.random() * size.y) - (size.y / 2) + entityLocation.y);
			
			Ragdoll ragdoll = new Ragdoll(state, randLocation.set(randX, randY), RAGDOLL_SIZE, Sprite.NOTHING, ragdollVelo,
					0.25f, 0.0f, false, true, false);
			EffectEntityManager.getParticle(state, new ParticleCreate(Particle.CURRENT_TRAIL, ragdoll)
					.setVelocity(vec.angleRad())
					.setAngle(vec.angleRad() + 90 * MathUtils.degreesToRadians));
			((ClientState) state).addEntity(ragdoll.getEntityID(), ragdoll, false, ObjectLayer.STANDARD);
		}
	}
}
