package com.mygdx.hadal.event;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.mygdx.hadal.constants.BodyConstants;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.server.AlignmentFilter;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.b2d.HadalBody;

/**
 * A Particle Field spawns a bunch of particles in its area. This is strictly for visual effect.
 * <p>
 * Triggered Behavior: N/A
 * Triggering Behavior: N/A
 * <p>
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
		this.body = new HadalBody(eventData, startPos, size, BodyConstants.BIT_SENSOR, (short) 0, (short) 0)
				.setBodyType(BodyDef.BodyType.StaticBody)
				.addToWorld(world);
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

			ParticleCreate particleCreate = new ParticleCreate(particle, randLocation.set(randX, randY))
					.setLifespan(duration)
					.setScale(scale);

			//tint particles according to input color or team color
			if (!HadalColor.NOTHING.equals(color)) {
				particleCreate.setColor(color);
			} else if (teamColorIndex != -1) {
				if (teamColorIndex < AlignmentFilter.currentTeams.length) {
					HadalColor teamColor = AlignmentFilter.currentTeams[teamColorIndex].getPalette().getIcon();
					particleCreate.setColor(teamColor);
				}
			}
			EffectEntityManager.getParticle(state, particleCreate);
		}
	}
	
	/**
	 * Client particle field should randomly spawn particles itself to reduce overhead.
	 */
	@Override
	public void clientController(float delta) {
		controller(delta);
	}
}
