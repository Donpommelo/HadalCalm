package com.mygdx.hadal.event;

import static com.mygdx.hadal.utils.Constants.PPM;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Player;
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
 * This event damages all schmucks inside of it. It can be spawned as a hazard in a map or created temporarily fro mthe effects 
 * of attacks
 * 
 * Triggered Behavior: Toggle whether the poison is on or off
 * Triggering Behavior: N/A
 * 
 * Fields:
 * damage: float damage per 1/60f done by this event
 * startOn: boolean of whether this event starts on or off. Optional. Default: true.
 * 
 * @author Zachary Tu
 *
 */
public class Poison extends Event {
	
	private float controllerCount = 0;
	private float dps;
	private Schmuck perp;
	private boolean on, draw, randomParticles;
	
	private static final String name = "Poison";

	private float currPoisonSpawnTimer = 0f, spawnTimerLimit;
	private short filter;
	
	public Poison(PlayState state, int width, int height, int x, int y, float dps, boolean draw, short filter) {
		super(state, name, width, height, x, y);
		this.dps = dps;
		this.filter = filter;
		this.perp = state.getWorldDummy();
		this.draw = draw;
		this.on = true;
		
		spawnTimerLimit = 4096f/(width * height);
		
		randomParticles = width > 100;
		
		if (!randomParticles && draw) {
			new ParticleEntity(state, this, Particle.POISON, 0, 0, on, particleSyncType.CREATESYNC);
		}
	}
	
	/**
	 * This constructor is used for when this event is created temporarily.
	 */
	public Poison(PlayState state, int width, int height, int x, int y, float dps, float duration, Schmuck perp, boolean draw, short filter) {
		super(state, name, width, height, x, y, duration);
		this.dps = dps;
		this.filter = filter;
		this.perp = perp;
		this.draw = draw;
		this.on = true;
		spawnTimerLimit = 4096f/(width * height);
		
		randomParticles = width > 100;
		
		if (!randomParticles && draw) {
			new ParticleEntity(state, this, Particle.POISON, 1.5f, 0, on, particleSyncType.CREATESYNC);
		}
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onActivate(EventData activator, Player p) {
				on = !on;
			}
		};
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 0, 0, 0, false, false, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY),
				(short) filter, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		if (on) {
			controllerCount+=delta;
			if (controllerCount >= 1/60f) {
				controllerCount = 0;
				
				for (HadalEntity entity : eventData.getSchmucks()) {
					if (entity instanceof Schmuck) {
						((Schmuck)entity).getBodyData().receiveDamage(dps, new Vector2(0, 0), perp.getBodyData(), null, true, DamageTypes.POISON);
					}
				}
			}
			
			if (randomParticles && draw) {
				currPoisonSpawnTimer += delta;
				while (currPoisonSpawnTimer >= spawnTimerLimit) {
					currPoisonSpawnTimer -= spawnTimerLimit;
					int randX = (int) ((Math.random() * width) - (width / 2) + body.getPosition().x * PPM);
					int randY = (int) ((Math.random() * height) - (height / 2) + body.getPosition().y * PPM);
					new ParticleEntity(state, randX, randY, Particle.POISON, 1.5f, true, particleSyncType.NOSYNC);
				}
			}
		}
		super.controller(delta);
	}
	
	@Override
	public void clientController(float delta) {
		if (on) {
			if (randomParticles && draw) {
				currPoisonSpawnTimer += delta;
				while (currPoisonSpawnTimer >= spawnTimerLimit) {
					currPoisonSpawnTimer -= spawnTimerLimit;
					int randX = (int) ((Math.random() * width) - (width / 2) + body.getPosition().x * PPM);
					int randY = (int) ((Math.random() * height) - (height / 2) + body.getPosition().y * PPM);
					ParticleEntity poison = new ParticleEntity(state, randX, randY, Particle.POISON, 1.5f, true, particleSyncType.NOSYNC);
					((ClientState)state).addEntity(poison.getEntityID().toString(), poison, ObjectSyncLayers.STANDARD);
				}
			}
		}
	}
	
	@Override
	public Object onServerCreate() {
		if (blueprint == null) {
			return new Packets.CreatePoison(entityID.toString(), body.getPosition(), 
					new Vector2(width, height), draw);
		} else {
			return new Packets.CreateEvent(entityID.toString(), blueprint);
		}
	}
}
