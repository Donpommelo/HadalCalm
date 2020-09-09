package com.mygdx.hadal.event;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.managers.GameStateManager.Mode;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.ProcTime;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 * A scrap event is a single currency unit that the player picks up if they touch it.
 * This does not have a blueprint and is not parsed from tiled.
 * 
 * If a client picks up scrap, it will be counted towards the host's money.
 * @author Zachary Tu
 */
public class Scrap extends Event {

	private Vector2 startVelo;
	
	private final static Vector2 baseSize = new Vector2(32, 32);
	
	//spread is for giving the initial scrap a random velocity
	private final static int spread = 90;
	private final static float veloAmp = 7.5f;
	private final static float lifespan = 9.0f;
	
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
					
					state.getGsm();
					
					//in single player, scrap gives the player 1 unit of currency
					if (GameStateManager.currentMode == Mode.SINGLE) {
						state.getGsm().getRecord().incrementScrap(1);
					}
					
					//in eggplant mode, we increase the players score by 1
					if (state.getGsm().getSetting().getPVPMode() == 1) {
						state.getUiExtra().changeFields(((PlayerBodyData) fixB).getPlayer(), 1, 0, 0.0f, 0.0f, false);
					}
					
					state.getUiExtra().syncData();
					new ParticleEntity(state, fixB.getEntity(), Particle.SPARKLE, 1.0f, 1.0f, true, particleSyncType.CREATESYNC);
					
					//activate effects that activate upon picking up scrap
					((PlayerBodyData) fixB).statusProcTime(new ProcTime.ScrapPickup());
					
					SoundEffect.COIN3.playExclusive(state, getPixelPosition(), ((PlayerBodyData) fixB).getPlayer(), 1.0f, false);
				}
			}
		};
		
		this.body = BodyBuilder.createBox(world, startPos, size, gravity, 1.0f, 0, false, true, Constants.BIT_SENSOR, (short) Constants.BIT_PLAYER, (short) 0, true, eventData);
		FixtureBuilder.createFixtureDef(body, new Vector2(), size, false, 0, 0, 0.0f, 1.0f, Constants.BIT_SENSOR, (short) (Constants.BIT_WALL | Constants.BIT_DROPTHROUGHWALL), (short) 0);
		
		float newDegrees = (float) (startVelo.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
		newVelocity.set(startVelo);
		setLinearVelocity(newVelocity.nor().scl(veloAmp).setAngle(newDegrees));
	}
}
