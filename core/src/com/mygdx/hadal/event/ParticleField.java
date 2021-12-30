package com.mygdx.hadal.event;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState.ObjectLayer;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;

/**
 * A Particle Field spawns a bunch of particles in its area. This is strictly for visual effect.
 * 
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * 
 * Fields:
 * particle: String name of the particle effect to use. Default: NOTHING
 * speed: float rate that particles are spawned. Default: 1.0f
 * duration: float duration of each particle effect. Default: 1.0f
 * scale: float size multiplier on particle size. Default: 1.0f
 * color: String name of color to tint particle. Default: NOTHING
 * teamColorIndex: int team. If not -1, particles will be colored equal to team color
 *
 * @author Peggplant Pottercups
 */
public class ParticleField extends Event {
	
	private final Particle particle;
	private final float duration;
	private float currParticleSpawnTimer;
	private final float spawnTimerLimit;
	private final float scale;
	private final HadalColor color;
	private final int teamColorIndex;
	
	public ParticleField(PlayState state, Vector2 startPos, Vector2 size, Particle particle, float speed, float duration, float scale,
				 String color, int teamColorIndex) {
		super(state, startPos, size);
		this.particle = particle;
		this.duration = duration;
		this.scale = scale;
		this.color = HadalColor.getByName(color);
		this.teamColorIndex = teamColorIndex;
		spawnTimerLimit = 4096f / (size.x * size.y) / speed;
	}
	
	@Override
	public void create() {
		this.eventData = new EventData(this);
		this.body = BodyBuilder.createBox(world, startPos, size, 0, 0, 0, false, false,
				Constants.BIT_SENSOR, (short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY), (short) 0, true, eventData);
	}
	
	private final Vector2 entityLocation = new Vector2();
	private final Vector2 randLocation = new Vector2();
	@Override
	public void controller(float delta) {
		
		entityLocation.set(getPixelPosition());
		
		//if specified, spawn random particles in the event's vicinity
		currParticleSpawnTimer += delta;
		while (currParticleSpawnTimer >= spawnTimerLimit) {
			currParticleSpawnTimer -= spawnTimerLimit;
			float randX = (MathUtils.random() * size.x) - (size.x / 2) + entityLocation.x;
			float randY = (MathUtils.random() * size.y) - (size.y / 2) + entityLocation.y;
			ParticleEntity particleEntity = new ParticleEntity(state, randLocation.set(randX, randY), particle, duration,
					true, SyncType.NOSYNC).setScale(scale);

			//tint particles according to input color or team color
			if (!color.equals(HadalColor.NOTHING)) {
				particleEntity.setColor(color);
			} else if (teamColorIndex != -1) {
				if (teamColorIndex < AlignmentFilter.currentTeams.length) {
					HadalColor teamColor = AlignmentFilter.currentTeams[teamColorIndex].getColor1();
					particleEntity.setColor(teamColor);
				}
			}
		}
	}
	
	/**
	 * Client particle field should randomly spawn particles itself to reduce overhead.
	 */
	@Override
	public void clientController(float delta) {
		
		entityLocation.set(getPixelPosition());

		currParticleSpawnTimer += delta;
		while (currParticleSpawnTimer >= spawnTimerLimit) {
			currParticleSpawnTimer -= spawnTimerLimit;
			float randX = (MathUtils.random() * size.x) - (size.x / 2) + entityLocation.x;
			float randY = (MathUtils.random() * size.y) - (size.y / 2) + entityLocation.y;
			ParticleEntity particleEntity = new ParticleEntity(state, randLocation.set(randX, randY), particle, duration,
					true, SyncType.NOSYNC);
			((ClientState) state).addEntity(particleEntity.getEntityID(), particleEntity, false, ObjectLayer.EFFECT);

			if (!color.equals(HadalColor.NOTHING)) {
				particleEntity.setColor(color);
			} else if (teamColorIndex != -1) {
				if (teamColorIndex < AlignmentFilter.currentTeams.length) {
					HadalColor teamColor = AlignmentFilter.currentTeams[teamColorIndex].getColor1();
					particleEntity.setColor(teamColor);
				}
			}
		}
	}
	
	@Override
	public void loadDefaultProperties() {
		setSyncType(eventSyncTypes.ALL);
	}
}
