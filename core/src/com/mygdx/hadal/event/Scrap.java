package com.mygdx.hadal.event;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 * A scrap event is a single currency unit that the player picks up if they touch it.
 * This does not have a blueprint and is not parsed from tiled.
 * 
 * If a client picks up scrap, it will be counted towards the host's money.
 */
public class Scrap extends Event {

	private Vector2 startVelo;
	
	private final static Vector2 baseSize = new Vector2(32, 32);
	
	//spread is for giving the initial scrap a random velocity
	private final static int spread = 120;
	private final static float veloAmp = 10.0f;
	private final static float lifespan = 7.5f;
	
	public Scrap(PlayState state, Vector2 startPos) {
		super(state, startPos, baseSize, lifespan);
		this.startVelo = new Vector2(0, 1);

		
		setEventSprite(Sprite.NASU);
		setScaleAlign("CENTER_STRETCH");
		setGravity(1.0f);
		addAmbientParticle(Particle.SPARKLE);
		setSynced(true);
	}

	private Vector2 newVelocity = new Vector2();
	@Override
	public void create() {

		this.eventData = new EventData(this) {
			
			@Override
			public void onTouch(HadalData fixB) {
				if (isAlive() && fixB instanceof PlayerBodyData) {
					event.queueDeletion();
					state.getGsm().getRecord().incrementScrap(1);
					state.getUiExtra().syncData();
					new ParticleEntity(state, fixB.getEntity(), Particle.SPARKLE, 0.0f, 1.0f, true, particleSyncType.CREATESYNC);
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, gravity, 1.0f, 0, false, false, Constants.BIT_SENSOR, (short)Constants.BIT_PLAYER, (short) 0, true, eventData);
		body.createFixture(FixtureBuilder.createFixtureDef(new Vector2(), size, false, 0, 0, 0.0f, 1.0f, Constants.BIT_SENSOR, Constants.BIT_WALL, (short) 0));
		
		float newDegrees = (float) (startVelo.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
		newVelocity.set(startVelo);
		setLinearVelocity(newVelocity.nor().scl(veloAmp).setAngle(newDegrees));
	}
}
