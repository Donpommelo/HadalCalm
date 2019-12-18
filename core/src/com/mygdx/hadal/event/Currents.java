package com.mygdx.hadal.event;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.Ragdoll;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * Currents are an event that apply a continuous force to all schmucks inside of it.
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * 
 * Fields:
 * N/A
 * 
 * @author Zachary Tu
 *
 */
public class Currents extends Event {
	
	//force applied every 1/60 seconds
	private Vector2 vec;

	//This keeps track of engine timer.
	private float controllerCount = 0;
	
	private static final String name = "Current";

	private float currBubbleSpawnTimer = 0f, spawnTimerLimit;
	
	public Currents(PlayState state, int width, int height, int x, int y, Vector2 vec) {
		super(state, name, width, height, x, y);
		this.vec = vec;
		
		spawnTimerLimit = 4096f/(width * height);
	}
	
	public Currents(PlayState state, int width, int height, int x, int y, Vector2 vec, float duration) {
		super(state, name, width, height, x, y, duration);
		this.vec = vec;
		
		spawnTimerLimit = 2048f/(width * height);
	}
	
	@Override
	public void create() {

		this.eventData = new EventData(this);
		
		this.body = BodyBuilder.createBox(world, startX, startY, width, height, 1, 1, 0, true, true, Constants.BIT_SENSOR, 
				(short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_PROJECTILE | Constants.BIT_SENSOR),
				(short) 0, true, eventData);
	}
	
	@Override
	public void controller(float delta) {
		controllerCount+=delta;
		if (controllerCount >= 1/60f) {
			controllerCount = 0;
			
			for (HadalEntity entity : eventData.getSchmucks()) {
				entity.applyLinearImpulse(vec);
			}
		}
		
		currBubbleSpawnTimer += delta;
		while (currBubbleSpawnTimer >= spawnTimerLimit) {
			currBubbleSpawnTimer -= spawnTimerLimit;
			int randX = (int) ((Math.random() * width) - (width / 2) + getPosition().x * PPM);
			int randY = (int) ((Math.random() * height) - (height / 2) + getPosition().y * PPM);
			new ParticleEntity(state, new Ragdoll(state, 64, 64, randX, randY, null, new Vector2(0, 0), 0.5f, true), Particle.BUBBLE_TRAIL, 0.5f, 0.0f, true, particleSyncType.TICKSYNC);
		}
	}
	
	@Override
	public String getText() {
		return  name + " " + vec;
	}
	
}
